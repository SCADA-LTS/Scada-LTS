package com.serotonin.mango.rt.publish.persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.publish.PublishedPointRT;
import com.serotonin.mango.util.DateUtils;
import com.serotonin.mango.vo.publish.persistent.PersistentPointVO;
import com.serotonin.sync.Synchronizer;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.i18n.LocalizableMessage;

class SyncHandler implements Runnable {
    static final Log LOG = LogFactory.getLog(SyncHandler.class);
    private static final String START_TIMES_KEY = "startTimes";

    final PersistentSendThread sendThread;

    final PointValueDao pointValueDao = new PointValueDao();
    long cutoff;

    int recordsSynced;
    final Set<Integer> targetOvercountPoints = new HashSet<Integer>();
    int responseErrors;
    Map<Integer, Long> startTimes;
    int requestsSent;
    int pointsCompleted;
    volatile boolean cancelled;

    List<PublishedPointRT<PersistentPointVO>> pointsToCheck;
    PointSync[] pointSyncs;

    SyncHandler(PersistentSendThread sendThread) {
        this.sendThread = sendThread;
    }

    public int getPointsCompleted() {
        return pointsCompleted;
    }

    public int getRequestsSent() {
        return requestsSent;
    }

    void cancel() {
        cancelled = true;

        if (pointSyncs != null) {
            for (PointSync pointSync : pointSyncs) {
                synchronized (pointSync) {
                    pointSync.notify();
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void run() {
        startTimes = (Map<Integer, Long>) sendThread.publisher.getPersistentData(START_TIMES_KEY);
        if (startTimes == null)
            startTimes = new HashMap<Integer, Long>();

        long start = System.currentTimeMillis();

        try {
            cutoff = System.currentTimeMillis() - 1000 * 60 * 60 * 2; // 2 hours.

            LOG.info("Sync handler running with cutoff: " + cutoff);

            // Create the list of points to be checked.
            pointsToCheck = new ArrayList<PublishedPointRT<PersistentPointVO>>(sendThread.publisher.getPointRTs());

            // The top byte of the request id int is the id of the point sync class that sent it, so there can not be 
            // more than 128 tasks.
            int tasks = 10;
            if (pointsToCheck.size() < tasks)
                tasks = pointsToCheck.size();

            Synchronizer<PointSync> sync = new Synchronizer<PointSync>();
            pointSyncs = new PointSync[tasks];
            for (int i = 0; i < tasks; i++) {
                pointSyncs[i] = new PointSync(i);
                sync.addTask(pointSyncs[i]);
            }
            sync.executeAndWait(Common.timer.getExecutorService());
        }
        finally {
            sendThread.endSyncHandler();
            LOG.info("Sync handler run completed");
        }

        LocalizableMessage lm = new LocalizableMessage("event.pb.persistent.syncCompleted.details",
                sendThread.publisher.getPointRTs().size(), requestsSent, recordsSynced, targetOvercountPoints.size(),
                responseErrors, DateUtils.getDuration(System.currentTimeMillis() - start));
        sendThread.publisher.raiseSyncCompletionEvent(lm);
    }

    void responseReceived(Packet packet) {
        int responseId = packet.getPayload().popU3B();
        int syncId = (responseId >> 16) & 0xFF;

        // Find the point sync to dispatch this to.
        if (syncId < 0 || syncId >= pointSyncs.length)
            LOG.info("Invalid sync id " + syncId);
        else
            pointSyncs[syncId].responseReceived(responseId & 0xFFFF, packet.popLong());
    }

    void sendRequest(int id, int requestId, int pointIndex, long from, long to) {
        int realRequestId = (id << 16) | requestId;

        ByteQueue queue = new ByteQueue();
        queue.pushU3B(realRequestId);
        queue.pushU2B(pointIndex);
        Packet.pushLong(queue, from);
        Packet.pushLong(queue, to);

        Packet packet = Packet.borrowPacket(PacketType.RANGE_COUNT, queue);
        sendThread.sendPacket(packet);
    }

    class PointSync implements Runnable {
        private final int id;

        private int nextRequestId;
        private volatile int responseId = -1;
        private volatile long responseCount;
        boolean pointUpdated;

        public PointSync(int id) {
            this.id = id;
        }

        public void run() {
            LOG.info("PointSync " + id + " started");

            while (true) {
                if (cancelled || !sendThread.isConnected())
                    break;

                PublishedPointRT<PersistentPointVO> point;
                synchronized (pointsToCheck) {
                    if (pointsToCheck.isEmpty())
                        break;

                    point = pointsToCheck.remove(0);
                }

                if (point.isPointEnabled())
                    checkPoint(point.getVo(), cutoff);
            }

            LOG.info("PointSync " + id + " completed");
        }

        void checkPoint(PersistentPointVO point, long to) {
            // Determine the date range that we need to check.
            Long from;
            synchronized (startTimes) {
                from = startTimes.get(point.getDataPointId());
            }
            if (from == null) {
                from = pointValueDao.getInceptionDate(point.getDataPointId());
                updatePointStartTime(point, from);
            }

            if (from == -1)
                // There are no values for this point yet, so ignore.
                return;

            if (from > to)
                // Nothing in the range we're interested in, so ignore.
                return;

            // Start recursing through the range
            pointUpdated = false;
            checkRangeImpl(point, from, to);

            pointsCompleted++;
        }

        /**
         * The recursive method that synchronizes range counts on both sides of the connection.
         */
        void checkRangeImpl(PersistentPointVO point, long from, long to) {
            // Send the packet
            responseId = -1;

            // Send the range check request.
            int requestId = nextRequestId++;
            if (nextRequestId > 0xFFFF)
                nextRequestId = 0;

            if (!sendThread.isConnected())
                // If we've lost the connection, give up.
                return;

            // Create the request.
            sendRequest(id, requestId, point.getIndex(), from, to);
            requestsSent++;

            // Check how many records are in that range.
            long count = pointValueDao.dateRangeCount(point.getDataPointId(), from, to);

            // Wait for the response ...
            synchronized (this) {
                // Just check to see if we should wait.
                if (cancelled)
                    return;

                // ... if we haven't received it already.
                if (responseId == -1) {
                    try {
                        // Wait up to 20 minutes
                        wait(20 * 60 * 1000);
                    }
                    catch (InterruptedException e) {
                        // no op
                    }
                }

                // Check to see if we were canceled.
                if (cancelled)
                    return;
            }

            // Check for a bad response id.
            if (responseId != requestId) {
                if (responseId == -1)
                    // This really shouldn't happen. At least, we'd like to prevent it from happening as much as
                    // possible, so let's make sure we know about it.
                    LOG.error("No response received for request id " + requestId);
                else
                    LOG.error("Request/response id mismatch: " + requestId);

                responseErrors++;
                pointUpdated = true;
                return;
            }

            if (responseCount == -1) {
                pointUpdated = true;
                // The point is unavailable. Just quit.
                return;
            }

            if (count == responseCount) {
                // Counts match. Done here.
                if (!pointUpdated)
                    updatePointStartTime(point, to + 1);
                return;
            }

            if (responseCount == 0) {
                // None of the records in this range exist in the target. So, send them by adding them to the send
                // queue.
                List<PointValueTime> pvts = pointValueDao.getPointValuesBetween(point.getDataPointId(), from, to + 1);
                if (LOG.isInfoEnabled())
                    LOG.info("Syncing records: count=" + count + ", queried=" + pvts.size() + ", point="
                            + point.getXid() + ", from=" + from + ", to=" + to);
                sendThread.publisher.publish(point, pvts);
                recordsSynced += pvts.size();
                pointUpdated = true;
                return;
            }

            if (count == 0) {
                if (LOG.isInfoEnabled())
                    LOG.info("Overcount detected: local=" + count + ", target=" + responseCount + ", point="
                            + point.getXid() + ", from=" + from + ", to=" + to);
                return;
            }

            if (count < responseCount)
                // This can happen if the source is only logging changes but sending updates, and the target is 
                // logging all. Reconciliation in this case means checking every record, so this is not a condition 
                // that is handled. Just noted.
                targetOvercountPoints.add(point.getDataPointId());

            if (from == to)
                // An overcount on the target. Most likely duplicate records. Ignore.
                return;

            // There are differences in this range. Split the range and recurse to find them.
            long mid = ((to - from) >> 1) + from;
            checkRangeImpl(point, from, mid);
            checkRangeImpl(point, mid + 1, to);
        }

        private void updatePointStartTime(PersistentPointVO point, long time) {
            synchronized (startTimes) {
                startTimes.put(point.getDataPointId(), time);
                sendThread.publisher.setPersistentData(START_TIMES_KEY, startTimes);
            }
        }

        void responseReceived(int responseId, long responseCount) {
            synchronized (this) {
                this.responseId = responseId;
                this.responseCount = responseCount;
                // Break the sync thread out of the wait.
                notify();
            }
        }
    }
}
