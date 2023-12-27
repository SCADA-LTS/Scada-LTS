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
package com.serotonin.mango.web.dwr.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.Network;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DefaultDeviceEventListener;
import com.serotonin.bacnet4j.event.ExceptionListener;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.obj.ObjectCovSubscription;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.DataTypes;
import com.serotonin.util.queue.ByteQueue;
import com.serotonin.web.i18n.I18NUtils;

/**
 * @author Matthew Lohbihler
 */
public class BACnetDiscovery extends DefaultDeviceEventListener implements TestingUtility, ExceptionListener {
    private static final Log LOG = LogFactory.getLog(BACnetDiscovery.class);

    final ResourceBundle bundle;
    private int removeDeviceIndex = 1;
    private final LocalDevice localDevice;
    private final AutoShutOff autoShutOff;
    private final List<IntValuePair> iamsReceived = new ArrayList<IntValuePair>();
    String message;
    private boolean finished;

    private int deviceDetailsIndex;
    private List<BACnetObjectBean> deviceDetails;

    public BACnetDiscovery(ResourceBundle bundle, int deviceId, String broadcastAddress, int port, int timeout,
            int segTimeout, int segWindow, int retries, int whoIsPort, int maxReadMultipleReferencesSegmented,
            int maxReadMultipleReferencesNonsegmented) {
        this.bundle = bundle;

        autoShutOff = new AutoShutOff() {
            @Override
            void shutOff() {
                message = I18NUtils.getMessage(BACnetDiscovery.this.bundle, "dsEdit.bacnetIp.tester.auto");
                BACnetDiscovery.this.cleanup();
            }
        };

        LocalDevice.setExceptionListener(this);

        localDevice = new LocalDevice(deviceId, broadcastAddress);
        localDevice.setPort(port);
        localDevice.setTimeout(timeout);
        localDevice.setSegTimeout(segTimeout);
        localDevice.setSegWindow(segWindow);
        localDevice.setRetries(retries);
        localDevice.getEventHandler().addListener(this);
        localDevice.setMaxReadMultipleReferencesSegmented(maxReadMultipleReferencesSegmented);
        localDevice.setMaxReadMultipleReferencesNonsegmented(maxReadMultipleReferencesNonsegmented);

        try {
            localDevice.initialize();
        }
        catch (Exception e) {
            LOG.warn("", e);
            message = e.getMessage();
            cleanup();
            return;
        }

        if (whoIsPort == 0)
            whoIsPort = port;

        WhoIsRequest whoIs = new WhoIsRequest();
        try {
            localDevice.sendBroadcast(whoIsPort, whoIs);
        }
        catch (BACnetException e) {
            LOG.warn("", e);
            message = e.getMessage();
            cleanup();
            return;
        }

        message = I18NUtils.getMessage(bundle, "dsEdit.bacnetIp.tester.listening");
    }

    public void addUpdateInfo(Map<String, Object> result) {
        autoShutOff.update();

        result.put("devices", iamsReceived);
        result.put("message", message);
        result.put("finished", finished);

        List<BACnetObjectBean> details = deviceDetails;
        deviceDetails = null;

        if (details != null) {
            RemoteDevice d = localDevice.getRemoteDeviceByUserData(deviceDetailsIndex);

            result.put("deviceIndex", deviceDetailsIndex);
            result.put("deviceAddress", d.getAddress().toIpString());
            result.put("deviceName", d.getName());
            result.put("deviceIp", d.getAddress().toIpString());
            result.put("devicePort", d.getAddress().getPort());

            Network network = d.getNetwork();
            if (network != null) {
                result.put("deviceNetworkNumber", network.getNetworkNumber());
                result.put("deviceNetworkAddress", network.getNetworkAddressDottedString());
            }

            result.put("deviceInstanceNumber", d.getObjectIdentifier().getInstanceNumber());
            result.put("deviceDescription", getDeviceDescription(d));
            result.put("deviceDetails", details);
        }
    }

    public void cancel() {
        message = I18NUtils.getMessage(bundle, "dsEdit.bacnetIp.tester.cancelled");
        cleanup();
    }

    void cleanup() {
        if (!finished) {
            finished = true;
            autoShutOff.cancel();
            localDevice.terminate();
            LocalDevice.setExceptionListener(null);
        }
    }

    public void receivedException(Exception e) {
        message = e.getMessage();
        // cleanup();
    }

    public void receivedThrowable(Throwable t) {
        message = t.getMessage();
        // cleanup();
    }

    @Override
    public void unimplementedVendorService(UnsignedInteger vendorId, UnsignedInteger serviceNumber, ByteQueue queue) {
        // TODO localize
        message = "Received unimplemented vendor service: vendor id=" + vendorId + ", service number=" + serviceNumber
                + ", bytes (with context id)=" + queue;
    }

    @Override
    public void iAmReceived(RemoteDevice d) {
        int index;
        synchronized (this) {
            index = removeDeviceIndex++;
        }
        d.setUserData(index);
        iamsReceived.add(new IntValuePair(index, getDeviceDescription(d)));
    }

    public static String getDeviceDescription(RemoteDevice d) {
        String description = d.getAddress().toIpString();
        if (d.getNetwork() != null)
            description += " - " + d.getNetwork().getNetworkNumber() + "/"
                    + d.getNetwork().getNetworkAddressDottedString();
        return description;
    }

    @Override
    public void listenerException(Throwable e) {
        message = e.getMessage();
        cleanup();
    }

    public void getDeviceDetails(int index) {
        deviceDetails = null;
        try {
            deviceDetails = getDetails(index);
        }
        catch (Exception e) {
            LOG.warn("", e);
            message = e.getMessage();
        }
    }

    private List<BACnetObjectBean> getDetails(int index) throws Exception {
        RemoteDevice d = localDevice.getRemoteDeviceByUserData(index);
        if (d == null)
            throw new Exception(I18NUtils.getMessage(bundle, "dsEdit.bacnetIp.tester.indexNotFound"));

        deviceDetailsIndex = index;

        return getDetails(localDevice, d);
    }

    @SuppressWarnings("unchecked")
    public static List<BACnetObjectBean> getDetails(LocalDevice localDevice, RemoteDevice d) throws Exception {
        localDevice.getExtendedDeviceInformation(d);

        // Get the device's object list.
        List<ObjectIdentifier> oids = ((SequenceOf<ObjectIdentifier>) localDevice.sendReadPropertyAllowNull(d,
                d.getObjectIdentifier(), PropertyIdentifier.objectList)).getValues();

        // ReadPropertyRequest read = new ReadPropertyRequest(new ObjectIdentifier(ObjectType.device, deviceId),
        // PropertyIdentifier.objectList);

        PropertyReferences refs = new PropertyReferences();
        Map<ObjectIdentifier, BACnetObjectBean> objectProperties = new HashMap<ObjectIdentifier, BACnetObjectBean>();
        for (ObjectIdentifier oid : oids) {
            addPropertyReferences(refs, oid);

            BACnetObjectBean bean = new BACnetObjectBean();
            bean.setObjectTypeId(oid.getObjectType().intValue());
            bean.setObjectTypeDescription(oid.getObjectType().toString());
            bean.setInstanceNumber(oid.getInstanceNumber());
            bean.setCov(d.getServicesSupported().isSubscribeCov()
                    && ObjectCovSubscription.supportedObjectType(oid.getObjectType()));

            if (ObjectType.binaryInput.equals(oid.getObjectType())
                    || ObjectType.binaryOutput.equals(oid.getObjectType())
                    || ObjectType.binaryValue.equals(oid.getObjectType())) {
                bean.setDataTypeId(DataTypes.BINARY);
                bean.getUnitsDescription().add("");
                bean.getUnitsDescription().add("");
            }
            else if (ObjectType.multiStateInput.equals(oid.getObjectType())
                    || ObjectType.multiStateOutput.equals(oid.getObjectType())
                    || ObjectType.multiStateValue.equals(oid.getObjectType())
                    || ObjectType.lifeSafetyPoint.equals(oid.getObjectType())
                    || ObjectType.lifeSafetyZone.equals(oid.getObjectType())) {
                bean.setDataTypeId(DataTypes.MULTISTATE);
            }
            else
                bean.setDataTypeId(DataTypes.NUMERIC);

            objectProperties.put(oid, bean);
        }

        PropertyValues values = localDevice.readProperties(d, refs);

        for (ObjectPropertyReference value : values) {
            ObjectIdentifier oid = value.getObjectIdentifier();
            PropertyIdentifier pid = value.getPropertyIdentifier();

            BACnetObjectBean bean = objectProperties.get(oid);
            if (pid.equals(PropertyIdentifier.objectName))
                bean.setObjectName(values.getString(oid, pid));
            else if (pid.equals(PropertyIdentifier.presentValue))
                bean.setPresentValue(values.getString(oid, pid));
            else if (pid.equals(PropertyIdentifier.units))
                bean.getUnitsDescription().add(values.getString(oid, pid));
            else if (pid.equals(PropertyIdentifier.inactiveText)) {
                Encodable e = values.getNullOnError(oid, pid);
                bean.getUnitsDescription().set(0, e == null ? "0" : e.toString());
            }
            else if (pid.equals(PropertyIdentifier.activeText)) {
                Encodable e = values.getNullOnError(oid, pid);
                bean.getUnitsDescription().set(1, e == null ? "1" : e.toString());
            }
            else if (pid.equals(PropertyIdentifier.outputUnits))
                bean.getUnitsDescription().add(values.getString(oid, pid));
            else if (pid.equals(PropertyIdentifier.stateText)) {
                try {
                    SequenceOf<CharacterString> states = (SequenceOf<CharacterString>) values.get(oid, pid);
                    for (CharacterString state : states)
                        bean.getUnitsDescription().add(state.toString());
                }
                catch (PropertyValueException e) {
                    LOG.warn("Error in stateText result: " + e.getError());
                }
            }
        }

        return new ArrayList<BACnetObjectBean>(objectProperties.values());
    }

    private static void addPropertyReferences(PropertyReferences refs, ObjectIdentifier oid) {
        refs.add(oid, PropertyIdentifier.objectName);

        ObjectType type = oid.getObjectType();
        if (ObjectType.accumulator.equals(type)) {
            refs.add(oid, PropertyIdentifier.units);
        }
        else if (ObjectType.analogInput.equals(type) || ObjectType.analogOutput.equals(type)
                || ObjectType.analogValue.equals(type) || ObjectType.pulseConverter.equals(type)) {
            refs.add(oid, PropertyIdentifier.units);
        }
        else if (ObjectType.binaryInput.equals(type) || ObjectType.binaryOutput.equals(type)
                || ObjectType.binaryValue.equals(type)) {
            refs.add(oid, PropertyIdentifier.inactiveText);
            refs.add(oid, PropertyIdentifier.activeText);
        }
        else if (ObjectType.lifeSafetyPoint.equals(type)) {
            refs.add(oid, PropertyIdentifier.units);
        }
        else if (ObjectType.loop.equals(type)) {
            refs.add(oid, PropertyIdentifier.outputUnits);
        }
        else if (ObjectType.multiStateInput.equals(type) || ObjectType.multiStateOutput.equals(type)
                || ObjectType.multiStateValue.equals(type)) {
            refs.add(oid, PropertyIdentifier.stateText);
        }
        else
            return;

        refs.add(oid, PropertyIdentifier.presentValue);
    }
}
