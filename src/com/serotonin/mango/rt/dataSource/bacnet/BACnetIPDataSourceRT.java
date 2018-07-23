/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.bacnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.Network;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.event.DeviceEventListener;
import com.serotonin.bacnet4j.event.ExceptionListener;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.ObjectProperties;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.ReinitializeDeviceRequest.ReinitializedStateOfDevice;
import com.serotonin.bacnet4j.service.confirmed.SubscribeCOVRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.BACnetError;
import com.serotonin.bacnet4j.type.constructed.Choice;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.LifeSafetyState;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.bacnet.BACnetIPDataSourceVO;
import com.serotonin.timer.FixedRateTrigger;
import com.serotonin.timer.TimerTask;
import com.serotonin.timer.TimerTrigger;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.DateFunctions;

/**
 * @author Matthew Lohbihler
 */
public class BACnetIPDataSourceRT extends PollingDataSource implements DeviceEventListener, ExceptionListener {
    public static final int INITIALIZATION_EXCEPTION_EVENT = 1;
    public static final int MESSAGE_EXCEPTION_EVENT = 2;
    public static final int DEVICE_EXCEPTION_EVENT = 3;

    final Log log = LogFactory.getLog(BACnetIPDataSourceRT.class);
    final BACnetIPDataSourceVO vo;
    private LocalDevice localDevice;
    private boolean initialized = false;
    final List<RemoteDevice> pollsInProgress = new ArrayList<RemoteDevice>();
    private CovResubscriptionTask covResubscriptionTask;

    public BACnetIPDataSourceRT(BACnetIPDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    //
    // /
    // / Lifecycle
    // /
    //
    @Override
    public void initialize() {
        LocalDevice.setExceptionListener(this);

        localDevice = new LocalDevice(vo.getDeviceId(), vo.getBroadcastAddress());
        localDevice.setPort(vo.getPort());
        localDevice.setTimeout(vo.getTimeout());
        localDevice.setSegTimeout(vo.getSegTimeout());
        localDevice.setSegWindow(vo.getSegWindow());
        localDevice.setRetries(vo.getRetries());
        localDevice.getEventHandler().addListener(this);
        localDevice.setMaxReadMultipleReferencesSegmented(vo.getMaxReadMultipleReferencesSegmented());
        localDevice.setMaxReadMultipleReferencesNonsegmented(vo.getMaxReadMultipleReferencesNonsegmented());

        try {
            localDevice.initialize();

            // Deactivate any existing event.
            returnToNormal(INITIALIZATION_EXCEPTION_EVENT, System.currentTimeMillis());
        }
        catch (Exception e) {
            raiseEvent(INITIALIZATION_EXCEPTION_EVENT, System.currentTimeMillis(), true, new LocalizableMessage(
                    "event.initializationError", e.getMessage()));
            return;
        }

        super.initialize();

        // Let everyone know we're here.
        try {
            localDevice.sendBroadcast(localDevice.getIAm());
        }
        catch (BACnetException e) {
            fireMessageExceptionEvent("event.bacnet.iamError", e.getMessage());
        }

        // Find out who we're slummin with.
        try {
            localDevice.sendBroadcast(new WhoIsRequest());

            // Wait for responses to come in.
            try {
                Thread.sleep(vo.getTimeout() / 4);
            }
            catch (InterruptedException e) {
                // no op
            }
        }
        catch (BACnetException e) {
            fireMessageExceptionEvent("event.bacnet.whoisError", e.getMessage());
        }

        initialized = true;
    }

    @Override
    public void beginPolling() {
        if (!initialized)
            return;

        super.beginPolling();

        covResubscriptionTask = new CovResubscriptionTask(new FixedRateTrigger(0,
                vo.getCovSubscriptionTimeoutMinutes() * 60 * 1000 / 2));
        Common.timer.schedule(covResubscriptionTask);
    }

    @Override
    public void terminate() {
        if (covResubscriptionTask != null)
            covResubscriptionTask.cancel();

        super.terminate();
        if (localDevice != null) {
            synchronized (pollsInProgress) {
                while (!pollsInProgress.isEmpty()) {
                    try {
                        pollsInProgress.wait(200);
                    }
                    catch (InterruptedException e) {
                        // no op
                    }
                }
                localDevice.terminate();
            }
        }
        LocalDevice.setExceptionListener(null);
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        if (!initialized)
            return;

        BACnetIPPointLocatorRT locator = dataPoint.getPointLocator();
        Address address = locator.getAddress(vo.getPort());
        Network network = locator.getNetwork();

        // Check if the remote device is in the local list yet.
        RemoteDevice d = localDevice.getRemoteDevice(address, network);
        if (d == null) {
            // Send a whois to get remote device data.
            try {
                localDevice.sendUnconfirmed(address, null, new WhoIsRequest());
            }
            catch (BACnetException e) {
                fireMessageExceptionEvent("event.bacnet.whoisPoint", dataPoint.getVO().getName(), e.getMessage());
                disablePoint(dataPoint);
                return;
            }

            int sleep = vo.getTimeout() / 4;
            for (int i = 0; i < 4; i++) {
                // Wait a bit for the response.
                try {
                    if (sleep > 0)
                        Thread.sleep(sleep);
                }
                catch (InterruptedException e) {
                    // no op
                }

                // Check again for the device
                d = localDevice.getRemoteDevice(address, network);
                if (d != null)
                    break;
            }
        }

        if (d == null) {
            // If we still don't have the device, try to get it manually.
            try {
                d = localDevice.findRemoteDevice(address, network, locator.getRemoteDeviceInstanceNumber());
            }
            catch (BACnetException e) {
                // Ignore.
            }
            catch (PropertyValueException e) {
                // Shouldn't happen, so just log.
                log.error("Couldn't manually get segmentation and vendor id from device", e);
            }
        }

        if (d == null) {
            // If we still don't have the device, call it in.
            fireDeviceExceptionEvent("event.bacnet.deviceError", address.toIpString());
            disablePoint(dataPoint);
        }
        else {
            locator.setRemoteDevice(d);

            if (locator.isUseCovSubscription()) {
                if (!sendCovSubscription(dataPoint, false))
                    return;
            }

            super.addDataPoint(dataPoint);
        }
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        super.removeDataPoint(dataPoint);

        if (!initialized)
            return;

        BACnetIPPointLocatorRT locator = dataPoint.getPointLocator();
        if (locator.isInitialized() && locator.isUseCovSubscription())
            sendCovSubscription(dataPoint, true);
    }

    //
    // /
    // / PollingDataSource
    // /
    //
    @Override
    protected void doPoll(long time) {
        Map<RemoteDevice, List<DataPointRT>> devicePoints = new HashMap<RemoteDevice, List<DataPointRT>>();

        synchronized (pointListChangeLock) {
            for (DataPointRT dp : dataPoints) {
                BACnetIPPointLocatorRT locator = dp.getPointLocator();
                if (locator.isUseCovSubscription() && dp.getPointValue() != null)
                    continue;

                List<DataPointRT> points = devicePoints.get(locator.getRemoteDevice());
                if (points == null) {
                    points = new ArrayList<DataPointRT>();
                    devicePoints.put(locator.getRemoteDevice(), points);
                }

                points.add(dp);
            }
        }

        for (RemoteDevice d : devicePoints.keySet())
            Common.ctx.getBackgroundProcessing().addWorkItem(new DevicePoller(d, devicePoints.get(d), time));
    }

    class DevicePoller implements WorkItem {
        private final RemoteDevice d;
        private final List<DataPointRT> points;
        private final long time;

        public DevicePoller(RemoteDevice d, List<DataPointRT> points, long time) {
            this.d = d;
            this.points = points;
            this.time = time;
        }

        @Override
        public void execute() {
            synchronized (pollsInProgress) {
                if (pollsInProgress.contains(d)) {
                    // There is another poll still running for the device, so abort this one.
                    log.warn(vo.getName() + ": poll of " + d + " at " + DateFunctions.getFullSecondTime(time)
                            + " aborted because a previous poll is still running");
                    return;
                }

                pollsInProgress.add(d);
            }

            pollDevice(d, points, time);

            synchronized (pollsInProgress) {
                pollsInProgress.remove(d);
                pollsInProgress.notify();
            }
        }

        @Override
        public int getPriority() {
            return WorkItem.PRIORITY_HIGH;
        }
    }

    void pollDevice(RemoteDevice d, List<DataPointRT> points, long time) {
        // Gather the points into a list of point references.
        PropertyReferences refs = new PropertyReferences();
        for (DataPointRT dp : points) {
            BACnetIPPointLocatorRT locator = dp.getPointLocator();
            refs.add(locator.getOid(), locator.getPid());
        }

        try {
            // Send the read request.
            PropertyValues values = localDevice.readProperties(d, refs);

            // Dereference the property values back into the points.
            for (DataPointRT dp : points) {
                BACnetIPPointLocatorRT locator = dp.getPointLocator();
                Encodable encodable = values.getNoErrorCheck(locator.getOid(), locator.getPid());
                dereferencePoint(dp, encodable, time);
            }
        }
        catch (BACnetException e) {
            fireMessageExceptionEvent("event.bacnet.readDevice", d.getAddress().toIpString(), e.getMessage());
        }
    }

    @Override
    public void forcePointRead(DataPointRT dataPoint) {
        BACnetIPPointLocatorRT locator = dataPoint.getPointLocator();
        RemoteDevice d = locator.getRemoteDevice();

        try {
            // Send the read request.
            ReadPropertyRequest req = new ReadPropertyRequest(locator.getOid(), locator.getPid());
            ReadPropertyAck ack = (ReadPropertyAck) localDevice.send(d, req);
            dereferencePoint(dataPoint, ack.getValue(), System.currentTimeMillis());
        }
        catch (BACnetException e) {
            fireMessageExceptionEvent("event.bacnet.readDevice", d.getAddress().toIpString(), e.getMessage());
        }
    }

    private void dereferencePoint(DataPointRT dp, Encodable encodable, long time) {
        if (encodable == null)
            fireDeviceExceptionEvent("event.bacnet.readError", dp.getVO().getName(), "no value returned");
        else if (encodable instanceof BACnetError)
            fireDeviceExceptionEvent("event.bacnet.readError", dp.getVO().getName(),
                    ((BACnetError) encodable).getErrorCode());
        else {
            MangoValue value = encodableToValue(encodable, dp.getDataTypeId());
            dp.updatePointValue(new PointValueTime(value, time));
        }
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime pvt, SetPointSource source) {
        if (!initialized)
            return;

        try {
            BACnetIPPointLocatorRT locator = dataPoint.getPointLocator();
            if (!locator.isInitialized())
                return;

            WritePropertyRequest writeRequest = new WritePropertyRequest(locator.getOid(), locator.getPid(), null,
                    valueToEncodable(pvt.getValue(), locator.getOid().getObjectType(), locator.getPid()),
                    new UnsignedInteger(locator.getWritePriority()));
            localDevice.send(locator.getRemoteDevice(), writeRequest);
            dataPoint.setPointValue(pvt, source);
        }
        catch (Throwable t) {
            fireMessageExceptionEvent("event.setPointFailed", t.getMessage());
        }
    }

    @Override
    public void relinquish(DataPointRT dataPoint) {
        if (!initialized)
            return;

        try {
            BACnetIPPointLocatorRT locator = dataPoint.getPointLocator();
            if (!locator.isInitialized())
                return;

            WritePropertyRequest writeRequest = new WritePropertyRequest(locator.getOid(), locator.getPid(), null,
                    new Null(), new UnsignedInteger(locator.getWritePriority()));
            localDevice.send(locator.getRemoteDevice(), writeRequest);
            forcePointRead(dataPoint);
        }
        catch (Throwable t) {
            fireMessageExceptionEvent("event.relinquishFailed", t.getMessage());
        }
    }

    Boolean getPointListChangeLock() {
        return pointListChangeLock;
    }

    List<DataPointRT> getDataPoints() {
        return dataPoints;
    }

    //
    // COV subscriptions
    class CovResubscriptionTask extends TimerTask {
        public CovResubscriptionTask(TimerTrigger trigger) {
            super(trigger);
        }

        @Override
        public void run(long fireTime) {
            synchronized (getPointListChangeLock()) {
                for (DataPointRT dp : getDataPoints()) {
                    BACnetIPPointLocatorRT locator = dp.getPointLocator();
                    if (locator.isUseCovSubscription())
                        sendCovSubscription(dp, false);
                }
            }
        }
    }

    //
    // /
    // / DeviceEventListener
    // /
    //
    public void listenerException(Throwable e) {
        fireMessageExceptionEvent(e);
    }

    public boolean allowPropertyWrite(BACnetObject obj, PropertyValue pv) {
        return true;
    }

    public void iAmReceived(RemoteDevice d) {
        try {
            localDevice.getExtendedDeviceInformation(d);
        }
        catch (BACnetException e) {
            // This should always work since we just received the IAm from the device.
            throw new ShouldNeverHappenException(e);
        }
    }

    public void propertyWritten(BACnetObject obj, PropertyValue pv) {
        // no op
    }

    public void iHaveReceived(RemoteDevice d, RemoteObject o) {
        // no op
    }

    public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier, RemoteDevice initiatingDevice,
            ObjectIdentifier monitoredObjectIdentifier, UnsignedInteger timeRemaining,
            SequenceOf<PropertyValue> listOfValues) {

        int covId = subscriberProcessIdentifier.intValue();

        List<PropertyValue> values = listOfValues.getValues();
        for (PropertyValue pv : values) {
            // Find the point that cares.
            DataPointRT dataPoint = null;
            BACnetIPPointLocatorRT locator = null;
            synchronized (pointListChangeLock) {
                for (DataPointRT dp : dataPoints) {
                    locator = dp.getPointLocator();
                    if (locator.getCovId() == covId) {
                        dataPoint = dp;
                        break;
                    }
                }
            }

            if (dataPoint != null) {
                if (pv.getPropertyIdentifier().equals(locator.getPid())) {
                    MangoValue value = encodableToValue(pv.getValue(), dataPoint.getDataTypeId());
                    dataPoint.updatePointValue(new PointValueTime(value, System.currentTimeMillis()));
                }
            }
            else {
                // We got a cov notice for a point we don't have? Unsubscribe.
                try {
                    sendCovSubscriptionImpl(initiatingDevice, monitoredObjectIdentifier, covId, true);
                }
                catch (BACnetException e) { /* Ignore exceptions */
                }
            }
        }
    }

    public void eventNotificationReceived(UnsignedInteger processIdentifier, RemoteDevice initiatingDevice,
            ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp, UnsignedInteger notificationClass,
            UnsignedInteger priority, com.serotonin.bacnet4j.type.enumerated.EventType eventType,
            CharacterString messageText, NotifyType notifyType,
            com.serotonin.bacnet4j.type.primitive.Boolean ackRequired, EventState fromState, EventState toState,
            NotificationParameters eventValues) {
        // no op
    }

    public void textMessageReceived(RemoteDevice textMessageSourceDevice, Choice messageClass,
            MessagePriority messagePriority, CharacterString message) {
        // no op
    }

    public void privateTransferReceived(UnsignedInteger vendorId, UnsignedInteger serviceNumber,
            Encodable serviceParameters) {
        // no op
    }

    @Override
    public void reinitializeDevice(ReinitializedStateOfDevice reinitializedStateOfDevice) {
        // no op
    }

    @Override
    public void synchronizeTime(DateTime arg0, boolean arg1) {
        // no op
    }

    //
    // /
    // / ExceptionListener
    // /
    //
    public void receivedException(Exception e) {
        fireMessageExceptionEvent(e);
    }

    public void receivedThrowable(Throwable t) {
        fireMessageExceptionEvent(t);
    }

    public void unimplementedVendorService(UnsignedInteger vendorId, UnsignedInteger serviceNumber, ByteQueue queue) {
        log.warn("Received unimplemented vendor service: vendor id=" + vendorId + ", service number=" + serviceNumber
                + ", bytes (with context id)=" + queue);
    }

    //
    // /
    // / Convenience methods
    // /
    //
    boolean sendCovSubscription(DataPointRT dataPoint, boolean unsubscribe) {
        BACnetIPPointLocatorRT locator = dataPoint.getPointLocator();

        try {
            sendCovSubscriptionImpl(locator.getRemoteDevice(), locator.getOid(), locator.getCovId(), unsubscribe);
        }
        catch (BACnetException e) {
            // If we are unsubscribing a failure doesn't really matter since the lease will expire eventually anyway,
            // so ignore.
            if (!unsubscribe) {
                fireMessageExceptionEvent("event.bacnet.covFailed",
                        locator.getRemoteDevice().getAddress().toIpString(), e.getMessage());
                disablePoint(dataPoint);
                return false;
            }
        }

        return true;
    }

    private void sendCovSubscriptionImpl(RemoteDevice remoteDevice, ObjectIdentifier oid, int covId, boolean unsubscribe)
            throws BACnetException {
        com.serotonin.bacnet4j.type.primitive.Boolean confirm;
        UnsignedInteger lifetime;
        if (unsubscribe) {
            confirm = null;
            lifetime = null;
        }
        else {
            confirm = new com.serotonin.bacnet4j.type.primitive.Boolean(false);
            lifetime = new UnsignedInteger(vo.getCovSubscriptionTimeoutMinutes() * 60);
        }

        localDevice.send(remoteDevice, new SubscribeCOVRequest(new UnsignedInteger(covId), oid, confirm, lifetime));
    }

    private void fireMessageExceptionEvent(Throwable t) {
        fireMessageExceptionEvent("common.default", t.getMessage());
        log.info("", t);
    }

    private void fireMessageExceptionEvent(String key, Object... args) {
        raiseEvent(MESSAGE_EXCEPTION_EVENT, System.currentTimeMillis(), false, new LocalizableMessage(key, args));
    }

    private void fireDeviceExceptionEvent(String key, Object... args) {
        raiseEvent(DEVICE_EXCEPTION_EVENT, System.currentTimeMillis(), false, new LocalizableMessage(key, args));
    }

    private void disablePoint(DataPointRT dataPoint) {
        DataPointVO dataPointVO = dataPoint.getVO();
        dataPointVO.setEnabled(false);
        Common.ctx.getRuntimeManager().saveDataPoint(dataPointVO);
    }

    private MangoValue encodableToValue(Encodable encodable, int dataTypeId) {
        if (dataTypeId == DataTypes.BINARY) {
            if (encodable instanceof Enumerated)
                return new BinaryValue(((Enumerated) encodable).intValue() != 0);
            if (encodable instanceof Real)
                return new BinaryValue(((Real) encodable).floatValue() != 0);
            log.warn("Unexpected Encodable type for data type Binary: " + encodable.getClass().getName());
            return BinaryValue.ZERO;
        }
        else if (dataTypeId == DataTypes.MULTISTATE) {
            if (encodable instanceof UnsignedInteger)
                return new MultistateValue(((UnsignedInteger) encodable).intValue());
            if (encodable instanceof Enumerated)
                return new MultistateValue(((Enumerated) encodable).intValue());
            if (encodable instanceof Real)
                return new MultistateValue((int) ((Real) encodable).floatValue());
            log.warn("Unexpected Encodable type for data type Multistate: " + encodable.getClass().getName());
            return new MultistateValue(1);
        }
        else if (dataTypeId == DataTypes.NUMERIC) {
            if (encodable instanceof Enumerated)
                return new NumericValue(((Enumerated) encodable).intValue());
            if (encodable instanceof Real)
                return new NumericValue(((Real) encodable).floatValue());
            log.warn("Unexpected Encodable type for data type Numeric: " + encodable.getClass().getName());
            return new NumericValue(0);
        }
        else if (dataTypeId == DataTypes.ALPHANUMERIC) {
            return new AlphanumericValue(encodable.toString());
        }

        throw new ShouldNeverHappenException("Unknown data type id: " + dataTypeId);
    }

    private Encodable valueToEncodable(MangoValue value, ObjectType objectType, PropertyIdentifier pid) {
        Class<? extends Encodable> clazz = ObjectProperties.getPropertyTypeDefinition(objectType, pid).getClazz();

        if (value instanceof BinaryValue) {
            boolean b = value.getBooleanValue();
            if (clazz == BinaryPV.class) {
                if (b)
                    return BinaryPV.active;
                return BinaryPV.inactive;
            }

            if (clazz == UnsignedInteger.class)
                return new UnsignedInteger(b ? 1 : 0);

            if (clazz == LifeSafetyState.class)
                return new LifeSafetyState(b ? 1 : 0);

            if (clazz == Real.class)
                return new Real(b ? 1 : 0);
        }
        else if (value instanceof MultistateValue) {
            int i = ((MultistateValue) value).getIntegerValue();
            if (clazz == BinaryPV.class) {
                if (i != 0)
                    return BinaryPV.active;
                return BinaryPV.inactive;
            }

            if (clazz == UnsignedInteger.class)
                return new UnsignedInteger(i);

            if (clazz == LifeSafetyState.class)
                return new LifeSafetyState(i);

            if (clazz == Real.class)
                return new Real(i);
        }
        else if (value instanceof NumericValue) {
            double d = value.getDoubleValue();
            if (clazz == BinaryPV.class) {
                if (d != 0)
                    return BinaryPV.active;
                return BinaryPV.inactive;
            }

            if (clazz == UnsignedInteger.class)
                return new UnsignedInteger((int) d);

            if (clazz == LifeSafetyState.class)
                return new LifeSafetyState((int) d);

            if (clazz == Real.class)
                return new Real((float) d);
        }
        else if (value instanceof AlphanumericValue) {
            String s = value.getStringValue();
            if (clazz == BinaryPV.class) {
                if (BinaryValue.parseBinary(s).getBooleanValue())
                    return BinaryPV.active;
                return BinaryPV.inactive;
            }

            if (clazz == UnsignedInteger.class)
                return new UnsignedInteger(MultistateValue.parseMultistate(s).getIntegerValue());

            if (clazz == LifeSafetyState.class)
                return new LifeSafetyState(MultistateValue.parseMultistate(s).getIntegerValue());

            if (clazz == Real.class)
                return new Real(NumericValue.parseNumeric(s).getFloatValue());
        }

        throw new ShouldNeverHappenException("Unknown data type: " + value.getClass().getName());
    }
}
