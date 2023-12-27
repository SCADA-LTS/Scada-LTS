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

import com.serotonin.bacnet4j.Network;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.obj.ObjectCovSubscription;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.bacnet.BACnetIPPointLocatorVO;
import com.serotonin.util.IpAddressUtils;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
public class BACnetIPPointLocatorRT extends PointLocatorRT {
    private static int NEXT_COV_ID = 1;

    private final BACnetIPPointLocatorVO vo;
    private RemoteDevice remoteDevice;
    private final ObjectIdentifier oid;
    private final PropertyIdentifier pid;
    private final int covId;

    public BACnetIPPointLocatorRT(BACnetIPPointLocatorVO vo) {
        this.vo = vo;
        oid = new ObjectIdentifier(new ObjectType(vo.getObjectTypeId()), vo.getObjectInstanceNumber());
        pid = new PropertyIdentifier(vo.getPropertyIdentifierId());

        if (vo.isUseCovSubscription())
            covId = NEXT_COV_ID++;
        else
            covId = -1;
    }

    public Address getAddress(int defaultPort) {
        int port = defaultPort;
        if (vo.getRemoteDevicePort() != 0)
            port = vo.getRemoteDevicePort();
        return new Address(IpAddressUtils.toIpAddress(vo.getRemoteDeviceIp()), port);
    }

    public Network getNetwork() {
        if (!StringUtils.isEmpty(vo.getNetworkAddress()))
            return new Network(vo.getNetworkNumber(), vo.getNetworkAddress());
        return null;
    }

    public int getRemoteDeviceInstanceNumber() {
        return vo.getRemoteDeviceInstanceNumber();
    }

    public boolean isInitialized() {
        return remoteDevice != null;
    }

    public boolean isUseCovSubscription() {
        return vo.isUseCovSubscription() && ObjectCovSubscription.supportedObjectType(oid.getObjectType());
    }

    public void setRemoteDevice(RemoteDevice remoteDevice) {
        this.remoteDevice = remoteDevice;
    }

    public RemoteDevice getRemoteDevice() {
        return remoteDevice;
    }

    public ObjectIdentifier getOid() {
        return oid;
    }

    public PropertyIdentifier getPid() {
        return pid;
    }

    public int getCovId() {
        return covId;
    }

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    @Override
    public boolean isRelinquishable() {
        return vo.isRelinquishable();
    }

    public int getWritePriority() {
        return vo.getWritePriority();
    }
}
