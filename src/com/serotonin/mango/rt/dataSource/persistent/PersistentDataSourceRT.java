package com.serotonin.mango.rt.dataSource.persistent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.EventDataSource;
import com.serotonin.mango.rt.publish.persistent.Packet;
import com.serotonin.mango.rt.publish.persistent.PacketType;
import com.serotonin.mango.rt.publish.persistent.PayloadReadTimeoutException;
import com.serotonin.mango.rt.publish.persistent.PersistentAbortException;
import com.serotonin.mango.rt.publish.persistent.PersistentProtocolException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.persistent.PersistentDataSourceVO;
import com.serotonin.mango.vo.dataSource.persistent.PersistentPointLocatorVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;
import com.serotonin.util.ArrayUtils;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.i18n.LocalizableMessage;

public class PersistentDataSourceRT extends EventDataSource implements Runnable {
    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;

    final Log log = LogFactory.getLog(PersistentDataSourceRT.class);
    final PersistentDataSourceVO vo;
    volatile ServerSocket serverSocket;
    final Map<String, DataPointRT> pointXids = new ConcurrentHashMap<String, DataPointRT>();
    final List<ConnectionHandler> connectionHandlers = new CopyOnWriteArrayList<ConnectionHandler>();

    public PersistentDataSourceRT(PersistentDataSourceVO vo) {
        super(vo);
        this.vo = vo;
    }

    public int getConnectionCount() {
        return connectionHandlers.size();
    }

    public String getConnectionAddress(int index) {
        try {
            return connectionHandlers.get(index).getSocketAddress();
        }
        catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    public long getConnectionTime(int index) {
        try {
            return connectionHandlers.get(index).getConnectionTime();
        }
        catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public long getPacketsReceived(int index) {
        try {
            return connectionHandlers.get(index).getPacketsReceived();
        }
        catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    //
    //
    // Lifecycle
    //
    @Override
    public void initialize() {
        super.initialize();

        try {
            serverSocket = new ServerSocket(vo.getPort());
            serverSocket.setSoTimeout(2000);

            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        }
        catch (IOException e) {
            serverSocket = null;
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.initializationError", e.getMessage()));
        }
    }

    @Override
    public void terminate() {
        super.terminate();

        // Stop the server socket
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                // Ignore
            }
            serverSocket = null;
        }
    }

    @Override
    public void joinTermination() {
        super.joinTermination();

        while (!connectionHandlers.isEmpty()) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // no op
            }
        }
    }

    @Override
    public void beginPolling() {
        if (serverSocket != null)
            new Thread(this, "Persistent TCP data source").start();
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        super.addDataPoint(dataPoint);
        pointXids.put(dataPoint.getVO().getXid(), dataPoint);
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        super.removeDataPoint(dataPoint);
        pointXids.remove(dataPoint.getVO().getXid());
    }

    public void run() {
        try {
            while (serverSocket != null) {
                try {
                    Socket socket = serverSocket.accept();
                    log.info("Received socket from " + socket.getRemoteSocketAddress());
                    ConnectionHandler ch = new ConnectionHandler(socket);
                    connectionHandlers.add(ch);
                    Common.timer.execute(ch);
                }
                catch (SocketTimeoutException e) {
                    // no op
                }
            }
        }
        catch (IOException e) {
            // ignore
        }
    }

    class ConnectionHandler implements Runnable {
        private final Socket socket;
        private InputStream in;
        private OutputStream out;
        int version = 3;
        private final ByteQueue writeBuffer = new ByteQueue();
        private final List<String> indexedXids = new ArrayList<String>();
        final PointValueDao pointValueDao = new PointValueDao();
        private final long connectionTime;
        private long packetsReceived;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
            connectionTime = System.currentTimeMillis();
        }

        public String getSocketAddress() {
            return socket.getRemoteSocketAddress().toString();
        }

        public long getConnectionTime() {
            return connectionTime;
        }

        public long getPacketsReceived() {
            return packetsReceived;
        }

        public void run() {
            try {
                runImpl();
            }
            catch (IOException e) {
                log.warn("Connection handler exception", e);
            }
            catch (PersistentProtocolException e) {
                try {
                    Packet.pushString(writeBuffer, e.getMessage());
                    Packet.writePacket(out, version, PacketType.ABORT, writeBuffer);
                    log.warn("Connection handler exception", e);
                    sleepImpl();
                }
                catch (IOException e1) {
                    log.warn("Connection handler exception", e1);
                }
            }
            catch (DoAbortException e) {
                try {
                    Packet.pushString(writeBuffer, e.getLocalizableMessage().serialize());
                    Packet.writePacket(out, version, PacketType.ABORT, writeBuffer);
                    sleepImpl();
                }
                catch (IOException e1) {
                    log.warn("Connection handler exception", e1);
                }
            }
            catch (PersistentAbortException e) {
                log.warn("Connection handler exception", e);
            }
            finally {
                connectionHandlers.remove(this);
                try {
                    socket.close();
                }
                catch (IOException e) {
                    log.warn("Connection handler exception", e);
                }
            }
        }

        private void sleepImpl() {
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e1) {
                // no op
            }
        }

        private void runImpl() throws IOException, PersistentProtocolException, PersistentAbortException,
                DoAbortException {
            socket.setSoTimeout(Packet.DATA_SOURCE_SOCKET_TIMEOUT);
            in = socket.getInputStream();
            out = socket.getOutputStream();

            //
            // Version
            Packet packet = Packet.readPacket(in, 1);
            packetsReceived++;
            if (packet.getType() != PacketType.VERSION)
                throw new PersistentProtocolException("Expected version, got " + packet.getType());
            int requestedVersion = packet.getPayload().popU1B();
            packet.release();

            // Version response
            if (requestedVersion < version)
                version = requestedVersion;
            Packet.writePacket(out, 1, PacketType.VERSION, new byte[] { (byte) version });

            //
            // Authentication key
            packet = Packet.readPacket(in, version);
            packetsReceived++;
            if (packet.getType() != PacketType.AUTH_KEY)
                throw new PersistentProtocolException("Expected auth key, got " + packet.getType());
            String authKey = packet.popString();
            if (!authKey.equals(vo.getAuthorizationKey()))
                throw new DoAbortException(new LocalizableMessage("event.persistent.authKey"));
            packet.release();

            // Authentication key response
            Packet.writePacket(out, version, PacketType.AUTH_KEY, Packet.EMPTY);

            // Points
            while (true) {
                packet = Packet.readPacket(in, version);
                packetsReceived++;
                try {
                    if (packet.getType() != PacketType.POINT)
                        throw new PersistentProtocolException("Expected points, got " + packet.getType());

                    if (packet.getPayload().size() == 0)
                        // The end
                        break;

                    String xid = packet.popString();
                    indexedXids.add(xid);
                    ensurePoint(xid, packet.getPayload().popAll());

                    if (version < 3)
                        // As of version 3 we do not send this response.
                        Packet.writePacket(out, version, PacketType.POINT, Packet.EMPTY);
                }
                finally {
                    packet.release();
                }
            }

            // Points response
            Packet.writePacket(out, version, PacketType.POINT, Packet.EMPTY);

            //
            // Data
            ByteQueue payload;
            DataPointRT point;
            int dataType;
            MangoValue value;
            int imageType;
            byte[] imageData;
            long time;

            // Corruption tracking
            PacketInfo[] previousPackets = new PacketInfo[20];
            int nextPreviousIndex = 0;

            while (serverSocket != null) {
                try {
                    packet = Packet.readPacket(in, version);
                    packetsReceived++;
                }
                catch (SocketTimeoutException e) {
                    continue;
                }
                catch (PayloadReadTimeoutException e) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < previousPackets.length; i++) {
                        int index = (nextPreviousIndex + i) % previousPackets.length;
                        if (previousPackets[index] != null) {
                            sb.append('[');
                            sb.append(previousPackets[index].type).append(',');
                            sb.append(previousPackets[index].length).append(',');
                            sb.append(previousPackets[index].receivedTime).append(']');
                        }
                    }
                    log.error("Payload read timeout: type=" + e.getType().name() + ", len=" + e.getLength()
                            + ", payload=[" + ArrayUtils.toHexString(e.getPayload()) + "], previous=" + sb);
                    continue;
                }

                PacketInfo pi = new PacketInfo();
                pi.type = packet.getType().name();
                pi.length = packet.getPayload().size();
                pi.receivedTime = System.currentTimeMillis();
                previousPackets[nextPreviousIndex] = pi;
                nextPreviousIndex = (nextPreviousIndex + 1) % previousPackets.length;

                try {
                    if (packet.getType() == PacketType.CLOSE)
                        break;

                    if (packet.getType() == PacketType.TEST)
                        continue;

                    if (packet.getType() == PacketType.RANGE_COUNT) {
                        Common.timer.execute(new RangeCountHandler(packet, out));
                        continue;
                    }

                    if (packet.getType() == PacketType.POINT_UPDATE) {
                        int index = packet.getPayload().popU2B();
                        ensurePoint(indexedXids.get(index), packet.getPayload().popAll());
                        continue;
                    }

                    if (packet.getType() == PacketType.POINT_HIERARCHY) {
                        // Point hierarchy update.
                        int index = packet.getPayload().popU2B();
                        int len = packet.getPayload().popU2B();

                        List<String> path = new ArrayList<String>();
                        for (int i = 0; i < len; i++)
                            path.add(packet.popString());
                        updatePointHierarchy(indexedXids.get(index), path);
                        continue;
                    }

                    if (packet.getType() != PacketType.DATA)
                        throw new PersistentProtocolException("Expected data, got " + packet.getType());

                    payload = packet.getPayload();
                    point = getIndexedPoint(payload.popU2B());
                    if (point == null)
                        // Point is not enabled.
                        continue;

                    dataType = payload.popU1B();
                    switch (dataType) {
                    case 1:
                        value = new BinaryValue(payload.pop() != 0);
                        break;
                    case 2:
                        value = new MultistateValue(payload.popS4B());
                        break;
                    case 3:
                        value = new NumericValue(packet.popDouble());
                        break;
                    case 4:
                        value = new AlphanumericValue(packet.popString());
                        break;
                    case 5:
                        imageType = payload.popS4B();
                        imageData = new byte[payload.popS4B()];
                        payload.pop(imageData);
                        value = new ImageValue(imageData, imageType);
                        break;
                    default:
                        throw new PersistentProtocolException("Unknown data type: " + dataType);
                    }

                    time = packet.popLong();

                    // Save the value.
                    point.updatePointValue(new PointValueTime(value, time));
                }
                finally {
                    packet.release();
                }
            }
        }

        class PacketInfo {
            String type;
            int length;
            long receivedTime;
        }

        private DataPointVO unserialize(byte[] serializedData) throws DoAbortException {
            try {
                return (DataPointVO) SerializationHelper.readObjectFromArray(serializedData);
            }
            catch (Exception e) {
                log.error("Point deserialization error", e);
                throw new DoAbortException(new LocalizableMessage("event.persistent.pointDeserialization",
                        e.getMessage()));
            }
        }

        private void ensurePoint(String xid, byte[] serializedData) throws DoAbortException {
            DataPointVO newDpvo = unserialize(serializedData);

            // Check if the point is already in the list of loaded points.
            DataPointRT dprt = pointXids.get(xid);

            if (dprt != null) {
                // Already exists. Check that the data types match.
                if (dprt.getVO().getPointLocator().getDataTypeId() != newDpvo.getPointLocator().getDataTypeId()) {
                    // Data type mismatch. Abort
                    LocalizableMessage lm = new LocalizableMessage("event.persistent.dataTypeMismatch", xid,
                            newDpvo.getDataTypeMessage(), dprt.getVO().getDataTypeMessage());
                    throw new DoAbortException(lm);
                }

                // We're good.
                updatePoint(dprt.getVO(), newDpvo);
                return;
            }

            // Doesn't exist in the RT list. Check if it exists at all.
            DataPointVO oldDpvo = new DataPointDao().getDataPoint(xid);

            if (oldDpvo != null) {
                // The point exists. Make sure it belongs to this data source.
                if (oldDpvo.getDataSourceId() != vo.getId())
                    // Wrong data source.
                    throw new DoAbortException(new LocalizableMessage("event.persistent.dataSourceMismatch", xid));

                // The point is disabled (because otherwise it would be in the RT list).
                updatePoint(oldDpvo, newDpvo);
            }
            else {
                if (StringUtils.isLengthGreaterThan(xid, 50))
                    throw new DoAbortException(new LocalizableMessage("event.persistent.xidTooLong", xid));

                // The point does not exist. Create it.
                newDpvo.setId(Common.NEW_ID);
                newDpvo.setXid(xid);
                newDpvo.setDataSourceId(vo.getId());
                newDpvo.setEnabled(true);
                newDpvo.setPointFolderId(0);
                newDpvo.setEventDetectors(new ArrayList<PointEventDetectorVO>());
                newDpvo.setLoggingType(DataPointVO.LoggingTypes.ALL);
                PersistentPointLocatorVO locator = new PersistentPointLocatorVO();
                locator.setDataTypeId(newDpvo.getPointLocator().getDataTypeId());
                newDpvo.setPointLocator(locator);
                Common.ctx.getRuntimeManager().saveDataPoint(newDpvo);
            }
        }

        private void updatePoint(DataPointVO oldDpvo, DataPointVO newDpvo) {
            if (PersistentDataSourceRT.this.vo.isAcceptPointUpdates()) {
                oldDpvo.setName(newDpvo.getName());
                oldDpvo.setDeviceName(newDpvo.getDeviceName());
                oldDpvo.setEngineeringUnits(newDpvo.getEngineeringUnits());
                oldDpvo.setTextRenderer(newDpvo.getTextRenderer());
                oldDpvo.setChartRenderer(newDpvo.getChartRenderer());
                oldDpvo.setChartColour(newDpvo.getChartColour());
                Common.ctx.getRuntimeManager().saveDataPoint(oldDpvo);
            }
        }

        private void updatePointHierarchy(String xid, List<String> path) {
            // Only update if we accept point updates.
            if (PersistentDataSourceRT.this.vo.isAcceptPointUpdates()) {
                DataPointDao dataPointDao = new DataPointDao();

                // Get the point vo.
                DataPointRT dprt = pointXids.get(xid);
                DataPointVO dpvo;
                if (dprt == null)
                    // Not currently enabled.
                    dpvo = dataPointDao.getDataPoint(xid);
                else
                    dpvo = dprt.getVO();

                if (dpvo == null)
                    return;

                PointHierarchy pointHierarchy = dataPointDao.getPointHierarchy();

                // Get the current path to the point.
                List<PointFolder> folders = pointHierarchy.getFolderPath(dpvo.getId());
                PointFolder oldFolder = folders.get(folders.size() - 1);

                // Get the new folder for the point. Create new folders as necessary.
                PointFolder newFolder = pointHierarchy.getRoot();
                for (String s : path) {
                    PointFolder sub = newFolder.getSubfolder(s);
                    if (sub == null) {
                        // Add the folder
                        sub = new PointFolder();
                        sub.setName(s);
                        newFolder.addSubfolder(sub);
                    }
                    newFolder = sub;
                }

                if (oldFolder != newFolder) {
                    oldFolder.removeDataPoint(dpvo.getId());
                    newFolder.addDataPoint(new IntValuePair(dpvo.getId(), dpvo.getName()));

                    // Save the hierarchy
                    dataPointDao.savePointHierarchy(pointHierarchy.getRoot());
                }
            }
        }

        DataPointRT getIndexedPoint(int index) {
            try {
                return pointXids.get(indexedXids.get(index));
            }
            catch (IndexOutOfBoundsException e) {
                log.error("Received invalid point index: " + index);
                return null;
            }
        }

        class RangeCountHandler implements Runnable {
            private final int requestId;
            private final int index;
            private final long from;
            private final long to;
            private final OutputStream out;

            RangeCountHandler(Packet packet, OutputStream out) {
                requestId = packet.getPayload().popU3B();
                index = packet.getPayload().popU2B();
                from = packet.popLong();
                to = packet.popLong();
                this.out = out;
            }

            public void run() {
                long result;

                DataPointRT dprt = getIndexedPoint(index);
                if (dprt == null)
                    result = -1;
                else {
                    result = pointValueDao.dateRangeCount(dprt.getId(), from, to);
                }

                ByteQueue queue = new ByteQueue();
                queue.pushU3B(requestId);
                Packet.pushLong(queue, result);

                try {
                    synchronized (out) {
                        Packet.writePacket(out, version, PacketType.RANGE_COUNT, queue);
                    }
                }
                catch (IOException e) {
                    // no op
                }
            }
        }
    }
}
