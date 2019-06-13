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
import java.util.List;

import com.serotonin.bacnet4j.type.enumerated.LifeSafetyState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;

/**
 * @author Matthew Lohbihler
 */
public class BACnetObjectBean {
    private int objectTypeId;
    private int instanceNumber;
    private String objectTypeDescription;
    private String objectName;
    private String presentValue;
    private boolean cov;

    // Default values for points
    private int dataTypeId;
    private List<String> unitsDescription = new ArrayList<String>();

    public String getPrettyPresentValue() {
        if (objectTypeId == ObjectType.binaryInput.intValue() || objectTypeId == ObjectType.binaryOutput.intValue()
                || objectTypeId == ObjectType.binaryValue.intValue()) {
            if ("0".equals(presentValue) && unitsDescription.size() > 0)
                return unitsDescription.get(0);
            if ("1".equals(presentValue) && unitsDescription.size() > 1)
                return unitsDescription.get(1);
        }
        else if (objectTypeId == ObjectType.multiStateInput.intValue()
                || objectTypeId == ObjectType.multiStateOutput.intValue()
                || objectTypeId == ObjectType.multiStateValue.intValue()) {
            try {
                int index = Integer.parseInt(presentValue) - 1;
                if (index >= 0 && index < unitsDescription.size())
                    return unitsDescription.get(index);
            }
            catch (NumberFormatException e) {
                // no op
            }
        }
        else if (objectTypeId == ObjectType.lifeSafetyPoint.intValue()
                || objectTypeId == ObjectType.lifeSafetyZone.intValue()) {
            try {
                int index = Integer.parseInt(presentValue);
                return new LifeSafetyState(index).toString();
            }
            catch (NumberFormatException e) {
                // no op
            }
        }
        else if (unitsDescription.size() > 0)
            return presentValue + " " + unitsDescription.get(0);

        return presentValue;
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public String getObjectTypeDescription() {
        return objectTypeDescription;
    }

    public void setObjectTypeDescription(String objectTypeDescription) {
        this.objectTypeDescription = objectTypeDescription;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public List<String> getUnitsDescription() {
        return unitsDescription;
    }

    public void setUnitsDescription(List<String> unitsDescription) {
        this.unitsDescription = unitsDescription;
    }

    public String getPresentValue() {
        return presentValue;
    }

    public void setPresentValue(String presentValue) {
        this.presentValue = presentValue;
    }

    public boolean isCov() {
        return cov;
    }

    public void setCov(boolean cov) {
        this.cov = cov;
    }
}
