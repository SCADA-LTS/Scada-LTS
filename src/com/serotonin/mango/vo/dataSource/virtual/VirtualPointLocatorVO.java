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
package com.serotonin.mango.vo.dataSource.virtual;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.virtual.ChangeTypeRT;
import com.serotonin.mango.rt.dataSource.virtual.VirtualPointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.IntMessagePair;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class VirtualPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    private static final Log LOG = LogFactory.getLog(VirtualPointLocatorVO.class);

    public LocalizableMessage getConfigurationDescription() {
        ChangeTypeVO changeType = getChangeType();
        if (changeType == null)
            throw new ShouldNeverHappenException("unknown change type");
        return changeType.getDescription();
    }

    private ChangeTypeVO getChangeType() {
        if (changeTypeId == ChangeTypeVO.Types.ALTERNATE_BOOLEAN)
            return alternateBooleanChange;
        if (changeTypeId == ChangeTypeVO.Types.BROWNIAN)
            return brownianChange;
        if (changeTypeId == ChangeTypeVO.Types.INCREMENT_ANALOG)
            return incrementAnalogChange;
        if (changeTypeId == ChangeTypeVO.Types.INCREMENT_MULTISTATE)
            return incrementMultistateChange;
        if (changeTypeId == ChangeTypeVO.Types.NO_CHANGE)
            return noChange;
        if (changeTypeId == ChangeTypeVO.Types.RANDOM_ANALOG)
            return randomAnalogChange;
        if (changeTypeId == ChangeTypeVO.Types.RANDOM_BOOLEAN)
            return randomBooleanChange;
        if (changeTypeId == ChangeTypeVO.Types.RANDOM_MULTISTATE)
            return randomMultistateChange;
        if (changeTypeId == ChangeTypeVO.Types.ANALOG_ATTRACTOR)
            return analogAttractorChange;

        LOG.error("Failed to resolve changeTypeId " + changeTypeId + " for virtual point locator");
        return alternateBooleanChange;
    }

    public PointLocatorRT createRuntime() {
        ChangeTypeRT changeType = getChangeType().createRuntime();
        String startValue = getChangeType().getStartValue();
        MangoValue startObject;
        if (dataTypeId == DataTypes.BINARY)
            startObject = BinaryValue.parseBinary(startValue);
        else if (dataTypeId == DataTypes.MULTISTATE) {
            try {
                startObject = MultistateValue.parseMultistate(startValue);
            }
            catch (NumberFormatException e) {
                startObject = new MultistateValue(0);
            }
        }
        else if (dataTypeId == DataTypes.NUMERIC) {
            try {
                startObject = NumericValue.parseNumeric(startValue);
            }
            catch (NumberFormatException e) {
                startObject = new NumericValue(0);
            }
        }
        else {
            if (startValue == null)
                startObject = new AlphanumericValue("");
            else
                startObject = new AlphanumericValue(startValue);
        }
        return new VirtualPointLocatorRT(changeType, startObject, isSettable());
    }

    public void validate(DwrResponseI18n response) {
        if (!DataTypes.CODES.isValidId(dataTypeId))
            response.addContextualMessage("dataTypeId", "validate.invalidValue");

        // Alternate boolean
        if (changeTypeId == ChangeTypeVO.Types.ALTERNATE_BOOLEAN) {
            if (StringUtils.isEmpty(alternateBooleanChange.getStartValue()))
                response.addContextualMessage("alternateBooleanChange.startValue", "validate.required");
        }

        // Brownian
        else if (changeTypeId == ChangeTypeVO.Types.BROWNIAN) {
            if (brownianChange.getMin() >= brownianChange.getMax())
                response.addContextualMessage("brownianChange.max", "validate.maxGreaterThanMin");
            if (brownianChange.getMaxChange() <= 0)
                response.addContextualMessage("brownianChange.maxChange", "validate.greaterThanZero");
            if (StringUtils.isEmpty(brownianChange.getStartValue()))
                response.addContextualMessage("brownianChange.startValue", "validate.required");
        }

        // Increment analog
        else if (changeTypeId == ChangeTypeVO.Types.INCREMENT_ANALOG) {
            if (incrementAnalogChange.getMin() >= incrementAnalogChange.getMax())
                response.addContextualMessage("incrementAnalogChange.max", "validate.maxGreaterThanMin");
            if (incrementAnalogChange.getChange() <= 0)
                response.addContextualMessage("incrementAnalogChange.change", "validate.greaterThanZero");
            if (StringUtils.isEmpty(incrementAnalogChange.getStartValue()))
                response.addContextualMessage("incrementAnalogChange.startValue", "validate.required");
        }

        // Increment multistate
        else if (changeTypeId == ChangeTypeVO.Types.INCREMENT_MULTISTATE) {
            if (StringUtils.isEmpty(incrementMultistateChange.getValues()))
                response.addContextualMessage("incrementMultistateChange.values", "validate.atLeast1");
            if (StringUtils.isEmpty(incrementMultistateChange.getStartValue()))
                response.addContextualMessage("incrementMultistateChange.startValue", "validate.required");
        }

        // No change
        else if (changeTypeId == ChangeTypeVO.Types.NO_CHANGE) {
            if (StringUtils.isEmpty(noChange.getStartValue()) && dataTypeId != DataTypes.ALPHANUMERIC)
                response.addContextualMessage("noChange.startValue", "validate.required");
        }

        // Random analog
        else if (changeTypeId == ChangeTypeVO.Types.RANDOM_ANALOG) {
            if (randomAnalogChange.getMin() >= randomAnalogChange.getMax())
                response.addContextualMessage("randomAnalogChange.max", "validate.maxGreaterThanMin");
            if (StringUtils.isEmpty(randomAnalogChange.getStartValue()))
                response.addContextualMessage("randomAnalogChange.startValue", "validate.required");
        }

        // Random boolean
        else if (changeTypeId == ChangeTypeVO.Types.RANDOM_BOOLEAN) {
            if (StringUtils.isEmpty(randomBooleanChange.getStartValue()))
                response.addContextualMessage("randomBooleanChange.startValue", "validate.required");
        }

        // Random multistate
        else if (changeTypeId == ChangeTypeVO.Types.RANDOM_MULTISTATE) {
            if (StringUtils.isEmpty(randomMultistateChange.getValues()))
                response.addContextualMessage("randomMultistateChange.values", "validate.atLeast1");
            if (StringUtils.isEmpty(randomMultistateChange.getStartValue()))
                response.addContextualMessage("randomMultistateChange.startValue", "validate.required");
        }

        // Analog attractor
        else if (changeTypeId == ChangeTypeVO.Types.ANALOG_ATTRACTOR) {
            if (analogAttractorChange.getMaxChange() <= 0)
                response.addContextualMessage("analogAttractorChange.maxChange", "validate.greaterThanZero");
            if (analogAttractorChange.getVolatility() < 0)
                response.addContextualMessage("analogAttractorChange.volatility", "validate.cannotBeNegative");
            if (analogAttractorChange.getAttractionPointId() < 1)
                response.addContextualMessage("analogAttractorChange.attractionPointId", "validate.required");
            if (StringUtils.isEmpty(analogAttractorChange.getStartValue()))
                response.addContextualMessage("analogAttractorChange.startValue", "validate.required");
        }

        else
            response.addContextualMessage("changeTypeId", "validate.invalidChoice");

        ChangeTypeVO changeType = getChangeType();
        if (changeType != null) {
            boolean found = false;
            for (IntMessagePair imp : ChangeTypeVO.getChangeTypes(dataTypeId)) {
                if (imp.getKey() == changeTypeId) {
                    found = true;
                    break;
                }
            }

            if (!found)
                response.addGenericMessage("validate.changeType.incompatible");
        }
    }

    private int dataTypeId = DataTypes.BINARY;
    private int changeTypeId = ChangeTypeVO.Types.ALTERNATE_BOOLEAN;
    @JsonRemoteProperty
    private boolean settable;
    private AlternateBooleanChangeVO alternateBooleanChange = new AlternateBooleanChangeVO();
    private BrownianChangeVO brownianChange = new BrownianChangeVO();
    private IncrementAnalogChangeVO incrementAnalogChange = new IncrementAnalogChangeVO();
    private IncrementMultistateChangeVO incrementMultistateChange = new IncrementMultistateChangeVO();
    private NoChangeVO noChange = new NoChangeVO();
    private RandomAnalogChangeVO randomAnalogChange = new RandomAnalogChangeVO();
    private RandomBooleanChangeVO randomBooleanChange = new RandomBooleanChangeVO();
    private RandomMultistateChangeVO randomMultistateChange = new RandomMultistateChangeVO();
    private AnalogAttractorChangeVO analogAttractorChange = new AnalogAttractorChangeVO();

    public int getChangeTypeId() {
        return changeTypeId;
    }

    public void setChangeTypeId(int changeTypeId) {
        this.changeTypeId = changeTypeId;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public AlternateBooleanChangeVO getAlternateBooleanChange() {
        return alternateBooleanChange;
    }

    public BrownianChangeVO getBrownianChange() {
        return brownianChange;
    }

    public IncrementAnalogChangeVO getIncrementAnalogChange() {
        return incrementAnalogChange;
    }

    public IncrementMultistateChangeVO getIncrementMultistateChange() {
        return incrementMultistateChange;
    }

    public NoChangeVO getNoChange() {
        return noChange;
    }

    public RandomAnalogChangeVO getRandomAnalogChange() {
        return randomAnalogChange;
    }

    public RandomBooleanChangeVO getRandomBooleanChange() {
        return randomBooleanChange;
    }

    public RandomMultistateChangeVO getRandomMultistateChange() {
        return randomMultistateChange;
    }

    public boolean isSettable() {
        return settable;
    }

    public void setSettable(boolean settable) {
        this.settable = settable;
    }

    public AnalogAttractorChangeVO getAnalogAttractorChange() {
        return analogAttractorChange;
    }

    public void setAlternateBooleanChange(AlternateBooleanChangeVO alternateBooleanChange) {
        this.alternateBooleanChange = alternateBooleanChange;
    }

    public void setBrownianChange(BrownianChangeVO brownianChange) {
        this.brownianChange = brownianChange;
    }

    public void setIncrementAnalogChange(IncrementAnalogChangeVO incrementAnalogChange) {
        this.incrementAnalogChange = incrementAnalogChange;
    }

    public void setIncrementMultistateChange(IncrementMultistateChangeVO incrementMultistateChange) {
        this.incrementMultistateChange = incrementMultistateChange;
    }

    public void setNoChange(NoChangeVO noChange) {
        this.noChange = noChange;
    }

    public void setRandomAnalogChange(RandomAnalogChangeVO randomAnalogChange) {
        this.randomAnalogChange = randomAnalogChange;
    }

    public void setRandomBooleanChange(RandomBooleanChangeVO randomBooleanChange) {
        this.randomBooleanChange = randomBooleanChange;
    }

    public void setRandomMultistateChange(RandomMultistateChangeVO randomMultistateChange) {
        this.randomMultistateChange = randomMultistateChange;
    }

    public void setAnalogAttractorChange(AnalogAttractorChangeVO analogAttractorChange) {
        this.analogAttractorChange = analogAttractorChange;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.settable", settable);
        AuditEventType.addDataTypeMessage(list, "dsEdit.pointDataType", dataTypeId);
        AuditEventType.addExportCodeMessage(list, "dsEdit.virtual.changeType", ChangeTypeVO.CHANGE_TYPE_CODES,
                changeTypeId);
        getChangeType().addProperties(list);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        VirtualPointLocatorVO from = (VirtualPointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.settable", from.settable, settable);
        AuditEventType.maybeAddDataTypeChangeMessage(list, "dsEdit.pointDataType", from.dataTypeId, dataTypeId);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.virtual.changeType",
                ChangeTypeVO.CHANGE_TYPE_CODES, from.changeTypeId, changeTypeId);
        if (from.changeTypeId == changeTypeId)
            getChangeType().addPropertyChanges(list, from.getChangeType());
        else
            getChangeType().addProperties(list);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        out.writeInt(dataTypeId);
        out.writeInt(changeTypeId);
        out.writeBoolean(settable);
        out.writeObject(alternateBooleanChange);
        out.writeObject(brownianChange);
        out.writeObject(incrementAnalogChange);
        out.writeObject(incrementMultistateChange);
        out.writeObject(noChange);
        out.writeObject(randomAnalogChange);
        out.writeObject(randomBooleanChange);
        out.writeObject(randomMultistateChange);
        out.writeObject(analogAttractorChange);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            dataTypeId = in.readInt();
            changeTypeId = in.readInt();
            settable = in.readBoolean();
            alternateBooleanChange = (AlternateBooleanChangeVO) in.readObject();
            brownianChange = (BrownianChangeVO) in.readObject();
            incrementAnalogChange = (IncrementAnalogChangeVO) in.readObject();
            incrementMultistateChange = (IncrementMultistateChangeVO) in.readObject();
            noChange = (NoChangeVO) in.readObject();
            randomAnalogChange = (RandomAnalogChangeVO) in.readObject();
            randomBooleanChange = (RandomBooleanChangeVO) in.readObject();
            randomMultistateChange = (RandomMultistateChangeVO) in.readObject();
            analogAttractorChange = (AnalogAttractorChangeVO) in.readObject();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        Integer value = deserializeDataType(json, DataTypes.IMAGE);
        if (value != null)
            dataTypeId = value;

        JsonObject ctjson = json.getJsonObject("changeType");
        if (ctjson == null)
            throw new LocalizableJsonException("emport.error.missingObject", "changeType");

        String text = ctjson.getString("type");
        if (text == null)
            throw new LocalizableJsonException("emport.error.missing", "type", ChangeTypeVO.CHANGE_TYPE_CODES
                    .getCodeList());

        changeTypeId = ChangeTypeVO.CHANGE_TYPE_CODES.getId(text);
        if (changeTypeId == -1)
            throw new LocalizableJsonException("emport.error.invalid", "changeType", text,
                    ChangeTypeVO.CHANGE_TYPE_CODES.getCodeList());

        reader.populateObject(getChangeType(), ctjson);
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        serializeDataType(map);
        map.put("changeType", getChangeType());
    }
}
