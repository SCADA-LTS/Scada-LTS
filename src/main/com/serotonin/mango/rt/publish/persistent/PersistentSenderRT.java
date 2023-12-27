package com.serotonin.mango.rt.publish.persistent;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.PublisherEventType;
import com.serotonin.mango.rt.publish.PublishQueue;
import com.serotonin.mango.rt.publish.PublishedPointRT;
import com.serotonin.mango.rt.publish.PublisherRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.publish.persistent.PersistentPointVO;
import com.serotonin.mango.vo.publish.persistent.PersistentSenderVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.i18n.LocalizableMessage;

public class PersistentSenderRT extends PublisherRT<PersistentPointVO> {
    static final Log LOG = LogFactory.getLog(PersistentSenderRT.class);

    public static final int CONNECTION_FAILED_EVENT = 11;
    public static final int PROTOCOL_FAILURE_EVENT = 12;
    public static final int CONNECTION_ABORTED_EVENT = 13;
    public static final int CONNECTION_LOST_EVENT = 14;
    public static final int SYNC_COMPLETION_EVENT = 15;

    final EventType connectionFailedEventType = new PublisherEventType(getId(), CONNECTION_FAILED_EVENT);
    final EventType protocolFailureEventType = new PublisherEventType(getId(), PROTOCOL_FAILURE_EVENT);
    final EventType connectionAbortedEventType = new PublisherEventType(getId(), CONNECTION_ABORTED_EVENT);
    final EventType connectionLostEventType = new PublisherEventType(getId(), CONNECTION_LOST_EVENT);
    final EventType syncCompletionEventType = new PublisherEventType(getId(), SYNC_COMPLETION_EVENT);

    final PersistentSenderVO vo;
    private PersistentSendThread sendThread;

    public PersistentSenderRT(PersistentSenderVO vo) {
        super(vo);
        this.vo = vo;
    }

    PublishQueue<PersistentPointVO> getPublishQueue() {
        return queue;
    }

    List<PublishedPointRT<PersistentPointVO>> getPointRTs() {
        return pointRTs;
    }

    public int getPointCount() {
        return pointRTs.size();
    }

    public int getQueueSize() {
        return queue.getSize();
    }

    public boolean isConnected() {
        return sendThread.isConnected();
    }

    public int getConnectingIndex() {
        return sendThread.getConnectingIndex();
    }

    public int getPacketsToSend() {
        return sendThread.packetsToSend.size();
    }

    public int getSyncStatus() {
        SyncHandler syncHandler = sendThread.syncHandler;
        if (syncHandler == null)
            return -1;
        return syncHandler.getPointsCompleted();
    }

    public int getSyncRequestsSent() {
        SyncHandler syncHandler = sendThread.syncHandler;
        if (syncHandler == null)
            return -1;
        return syncHandler.getRequestsSent();
    }

    public boolean startSync() {
        return sendThread.startSync();
    }

    @Override
    protected void pointInitialized(PublishedPointRT<PersistentPointVO> rt) {
        super.pointInitialized(rt);

        DataPointRT pointRT = Common.ctx.getRuntimeManager().getDataPoint(rt.getVo().getDataPointId());
        if (pointRT != null) {
            updatePublishedPointVO(rt.getVo(), pointRT.getVO());

            // Send the updated point info.
            ByteQueue queue = new ByteQueue();
            queue.pushU2B(rt.getVo().getIndex());
            queue.push(rt.getVo().getSerializedDataPoint());
            Packet packet = Packet.borrowPacket(PacketType.POINT_UPDATE, queue);
            sendThread.sendPacket(packet);
        }
    }

    //
    //
    // Lifecycle
    //
    @Override
    public void initialize() {
        // Cache the data point VOs for use during runtime.
        DataPointDao dataPointDao = new DataPointDao();
        int index = 0;
        for (PersistentPointVO p : vo.getPoints()) {
            DataPointVO dpvo = dataPointDao.getDataPoint(p.getDataPointId());
            p.setIndex(index++);
            updatePublishedPointVO(p, dpvo);
        }

        sendThread = new PersistentSendThread(this);
        super.initialize(sendThread);
    }

    private void updatePublishedPointVO(PersistentPointVO ppvo, DataPointVO dpvo) {
        ppvo.setXid(dpvo.getXid());
        ppvo.setSerializedDataPoint(SerializationHelper.writeObjectToArray(dpvo));
    }

    void raiseConnectionEvent(EventType type, Exception e) {
        LocalizableMessage lm;
        if (e instanceof PersistentAbortException)
            lm = ((PersistentAbortException) e).getLocalizableMessage();
        else
            lm = new LocalizableMessage("common.default", e.getMessage());

        raiseConnectionEvent(type, lm);
    }

    void raiseConnectionEvent(EventType type, LocalizableMessage lm) {
        Common.ctx.getEventManager().raiseEvent(type, System.currentTimeMillis(), true, AlarmLevels.URGENT, lm,
                createEventContext());
    }

    void raiseSyncCompletionEvent(LocalizableMessage lm) {
        Common.ctx.getEventManager().raiseEvent(syncCompletionEventType, System.currentTimeMillis(), false,
                AlarmLevels.NONE, lm, createEventContext());
    }
}
