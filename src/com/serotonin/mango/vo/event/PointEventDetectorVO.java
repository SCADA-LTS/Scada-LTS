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
package com.serotonin.mango.vo.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.detectors.AlphanumericStateDetectorRT;
import com.serotonin.mango.rt.event.detectors.AnalogHighLimitDetectorRT;
import com.serotonin.mango.rt.event.detectors.AnalogLowLimitDetectorRT;
import com.serotonin.mango.rt.event.detectors.BinaryStateDetectorRT;
import com.serotonin.mango.rt.event.detectors.MultistateStateDetectorRT;
import com.serotonin.mango.rt.event.detectors.NegativeCusumDetectorRT;
import com.serotonin.mango.rt.event.detectors.NoChangeDetectorRT;
import com.serotonin.mango.rt.event.detectors.NoUpdateDetectorRT;
import com.serotonin.mango.rt.event.detectors.PointChangeDetectorRT;
import com.serotonin.mango.rt.event.detectors.PointEventDetectorRT;
import com.serotonin.mango.rt.event.detectors.PositiveCusumDetectorRT;
import com.serotonin.mango.rt.event.detectors.StateChangeCountDetectorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class PointEventDetectorVO extends SimpleEventDetectorVO implements Cloneable, JsonSerializable,
        ChangeComparable<PointEventDetectorVO> {
    public static final String XID_PREFIX = "PED_";

    public static final int TYPE_ANALOG_HIGH_LIMIT = 1;
    public static final int TYPE_ANALOG_LOW_LIMIT = 2;
    public static final int TYPE_BINARY_STATE = 3;
    public static final int TYPE_MULTISTATE_STATE = 4;
    public static final int TYPE_POINT_CHANGE = 5;
    public static final int TYPE_STATE_CHANGE_COUNT = 6;
    public static final int TYPE_NO_CHANGE = 7;
    public static final int TYPE_NO_UPDATE = 8;
    public static final int TYPE_ALPHANUMERIC_STATE = 9;
    public static final int TYPE_POSITIVE_CUSUM = 10;
    public static final int TYPE_NEGATIVE_CUSUM = 11;

    private static List<ImplDefinition> definitions;

    public static List<ImplDefinition> getImplementations(int dataType) {
        if (definitions == null) {
            List<ImplDefinition> d = new ArrayList<ImplDefinition>();
            d.add(new ImplDefinition(TYPE_ANALOG_HIGH_LIMIT, null, "pointEdit.detectors.highLimit",
                    new int[] { DataTypes.NUMERIC }));
            d.add(new ImplDefinition(TYPE_ANALOG_LOW_LIMIT, null, "pointEdit.detectors.lowLimit",
                    new int[] { DataTypes.NUMERIC }));
            d.add(new ImplDefinition(TYPE_POINT_CHANGE, null, "pointEdit.detectors.change", new int[] {
                    DataTypes.BINARY, DataTypes.MULTISTATE, DataTypes.NUMERIC, DataTypes.ALPHANUMERIC }));
            d.add(new ImplDefinition(TYPE_BINARY_STATE, null, "pointEdit.detectors.state",
                    new int[] { DataTypes.BINARY }));
            d.add(new ImplDefinition(TYPE_MULTISTATE_STATE, null, "pointEdit.detectors.state",
                    new int[] { DataTypes.MULTISTATE }));
            d.add(new ImplDefinition(TYPE_ALPHANUMERIC_STATE, null, "pointEdit.detectors.state",
                    new int[] { DataTypes.ALPHANUMERIC }));
            d.add(new ImplDefinition(TYPE_STATE_CHANGE_COUNT, null, "pointEdit.detectors.changeCount", new int[] {
                    DataTypes.BINARY, DataTypes.MULTISTATE, DataTypes.ALPHANUMERIC }));
            d.add(new ImplDefinition(TYPE_NO_CHANGE, null, "pointEdit.detectors.noChange", new int[] {
                    DataTypes.BINARY, DataTypes.MULTISTATE, DataTypes.NUMERIC, DataTypes.ALPHANUMERIC }));
            d.add(new ImplDefinition(TYPE_NO_UPDATE, null, "pointEdit.detectors.noUpdate",
                    new int[] { DataTypes.BINARY, DataTypes.MULTISTATE, DataTypes.NUMERIC, DataTypes.ALPHANUMERIC,
                            DataTypes.IMAGE }));
            d.add(new ImplDefinition(TYPE_POSITIVE_CUSUM, null, "pointEdit.detectors.posCusum",
                    new int[] { DataTypes.NUMERIC }));
            d.add(new ImplDefinition(TYPE_NEGATIVE_CUSUM, null, "pointEdit.detectors.negCusum",
                    new int[] { DataTypes.NUMERIC }));
            definitions = d;
        }

        List<ImplDefinition> impls = new ArrayList<ImplDefinition>();
        for (ImplDefinition def : definitions) {
            if (def.supports(dataType))
                impls.add(def);
        }
        return impls;
    }

    private int id;
    private String xid;
    @JsonRemoteProperty
    private String alias;
    private DataPointVO dataPoint;
    private int detectorType;
    private int alarmLevel;
    private double limit;
    private int duration;
    private int durationType = Common.TimePeriods.SECONDS;
    private boolean binaryState;
    private int multistateState;
    private int changeCount = 2;
    private String alphanumericState;
    private double weight;

    public EventTypeVO getEventType() {
        return new EventTypeVO(EventType.EventSources.DATA_POINT, dataPoint.getId(), id, getDescription(), alarmLevel,
                getEventDetectorKey());
    }

    public ImplDefinition getDef() {
        // Ensure that definitions is not null.
        if (definitions == null)
            getImplementations(0);

        for (ImplDefinition def : definitions) {
            if (def.getId() == detectorType)
                return def;
        }
        return null;
    }

    public PointEventDetectorRT createRuntime() {
        switch (detectorType) {
        case TYPE_ANALOG_HIGH_LIMIT:
            return new AnalogHighLimitDetectorRT(this);
        case TYPE_ANALOG_LOW_LIMIT:
            return new AnalogLowLimitDetectorRT(this);
        case TYPE_BINARY_STATE:
            return new BinaryStateDetectorRT(this);
        case TYPE_MULTISTATE_STATE:
            return new MultistateStateDetectorRT(this);
        case TYPE_POINT_CHANGE:
            return new PointChangeDetectorRT(this);
        case TYPE_STATE_CHANGE_COUNT:
            return new StateChangeCountDetectorRT(this);
        case TYPE_NO_CHANGE:
            return new NoChangeDetectorRT(this);
        case TYPE_NO_UPDATE:
            return new NoUpdateDetectorRT(this);
        case TYPE_ALPHANUMERIC_STATE:
            return new AlphanumericStateDetectorRT(this);
        case TYPE_POSITIVE_CUSUM:
            return new PositiveCusumDetectorRT(this);
        case TYPE_NEGATIVE_CUSUM:
            return new NegativeCusumDetectorRT(this);
        }
        throw new ShouldNeverHappenException("Unknown detector type: " + detectorType);
    }

    public boolean isRtnApplicable() {
        return detectorType != TYPE_POINT_CHANGE;
    }

    @Override
    public String getEventDetectorKey() {
        return SimpleEventDetectorVO.POINT_EVENT_DETECTOR_PREFIX + id;
    }

    public LocalizableMessage getDescription() {
        if (!StringUtils.isEmpty(alias))
            return new LocalizableMessage("common.default", alias);
        return getConfigurationDescription();
    }

    private LocalizableMessage getConfigurationDescription() {
        LocalizableMessage message;
        LocalizableMessage durationDesc = getDurationDescription();
        if (detectorType == TYPE_ANALOG_HIGH_LIMIT) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.highLimit", dataPoint.getTextRenderer().getText(
                        limit, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.highLimitPeriod", dataPoint.getTextRenderer()
                        .getText(limit, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else if (detectorType == TYPE_ANALOG_LOW_LIMIT) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.lowLimit", dataPoint.getTextRenderer().getText(
                        limit, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.lowLimitPeriod", dataPoint.getTextRenderer()
                        .getText(limit, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else if (detectorType == TYPE_BINARY_STATE) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.state", dataPoint.getTextRenderer().getText(
                        binaryState, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.statePeriod", dataPoint.getTextRenderer().getText(
                        binaryState, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else if (detectorType == TYPE_MULTISTATE_STATE) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.state", dataPoint.getTextRenderer().getText(
                        multistateState, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.statePeriod", dataPoint.getTextRenderer().getText(
                        multistateState, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else if (detectorType == TYPE_POINT_CHANGE)
            message = new LocalizableMessage("event.detectorVo.change");
        else if (detectorType == TYPE_STATE_CHANGE_COUNT)
            message = new LocalizableMessage("event.detectorVo.changeCount", changeCount, durationDesc);
        else if (detectorType == TYPE_NO_CHANGE)
            message = new LocalizableMessage("event.detectorVo.noChange", durationDesc);
        else if (detectorType == TYPE_NO_UPDATE)
            message = new LocalizableMessage("event.detectorVo.noUpdate", durationDesc);
        else if (detectorType == TYPE_ALPHANUMERIC_STATE) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.state", dataPoint.getTextRenderer().getText(
                        alphanumericState, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.statePeriod", dataPoint.getTextRenderer().getText(
                        alphanumericState, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else if (detectorType == TYPE_POSITIVE_CUSUM) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.posCusum", dataPoint.getTextRenderer().getText(
                        limit, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.posCusumPeriod", dataPoint.getTextRenderer()
                        .getText(limit, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else if (detectorType == TYPE_NEGATIVE_CUSUM) {
            if (durationDesc == null)
                message = new LocalizableMessage("event.detectorVo.negCusum", dataPoint.getTextRenderer().getText(
                        limit, TextRenderer.HINT_SPECIFIC));
            else
                message = new LocalizableMessage("event.detectorVo.negCusumPeriod", dataPoint.getTextRenderer()
                        .getText(limit, TextRenderer.HINT_SPECIFIC), durationDesc);
        }
        else
            throw new ShouldNeverHappenException("Unknown detector type: " + detectorType);

        return message;
    }

    public LocalizableMessage getDurationDescription() {
        if (duration == 0)
            return null;
        return Common.getPeriodDescription(durationType, duration);
    }

    public PointEventDetectorVO copy() {
        try {
            return (PointEventDetectorVO) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    @Override
    public String getTypeKey() {
        return "event.audit.pointEventDetector";
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "common.xid", xid);
        AuditEventType.addPropertyMessage(list, "pointEdit.detectors.alias", alias);
        AuditEventType.addPropertyMessage(list, "pointEdit.detectors.type", getDef().getNameKey());
        AuditEventType.addPropertyMessage(list, "common.alarmLevel", AlarmLevels.getAlarmLevelMessage(alarmLevel));
        AuditEventType.addPropertyMessage(list, "common.configuration", getConfigurationDescription());
        AuditEventType.addPropertyMessage(list, "pointEdit.detectors.weight", weight);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, PointEventDetectorVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, xid);
        AuditEventType.maybeAddPropertyChangeMessage(list, "pointEdit.detectors.alias", from.alias, alias);
        if (from.detectorType != detectorType)
            AuditEventType.addPropertyChangeMessage(list, "pointEdit.detectors.type", from.getDef().getNameKey(),
                    getDef().getNameKey());
        AuditEventType.maybeAddAlarmLevelChangeMessage(list, "common.alarmLevel", from.alarmLevel, alarmLevel);
        if (from.limit != limit || from.duration != duration || from.durationType != durationType
                || from.binaryState != binaryState || from.multistateState != multistateState
                || from.changeCount != changeCount || from.alphanumericState != alphanumericState)
            AuditEventType.maybeAddPropertyChangeMessage(list, "common.configuration", from
                    .getConfigurationDescription(), getConfigurationDescription());
        AuditEventType.maybeAddPropertyChangeMessage(list, "pointEdit.detectors.weight", from.weight, weight);
    }

    public DataPointVO njbGetDataPoint() {
        return dataPoint;
    }

    public void njbSetDataPoint(DataPointVO dataPoint) {
        this.dataPoint = dataPoint;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isBinaryState() {
        return binaryState;
    }

    public void setBinaryState(boolean binaryState) {
        this.binaryState = binaryState;
    }

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }

    public int getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(int detectorType) {
        this.detectorType = detectorType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDurationType() {
        return durationType;
    }

    public void setDurationType(int durationType) {
        this.durationType = durationType;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public int getMultistateState() {
        return multistateState;
    }

    public void setMultistateState(int multistateState) {
        this.multistateState = multistateState;
    }

    public String getAlphanumericState() {
        return alphanumericState;
    }

    public void setAlphanumericState(String alphanumericState) {
        this.alphanumericState = alphanumericState;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    private static final ExportCodes TYPE_CODES = new ExportCodes();
    static {
        TYPE_CODES.addElement(TYPE_ANALOG_HIGH_LIMIT, "HIGH_LIMIT");
        TYPE_CODES.addElement(TYPE_ANALOG_LOW_LIMIT, "LOW_LIMIT");
        TYPE_CODES.addElement(TYPE_BINARY_STATE, "BINARY_STATE");
        TYPE_CODES.addElement(TYPE_MULTISTATE_STATE, "MULTISTATE_STATE");
        TYPE_CODES.addElement(TYPE_POINT_CHANGE, "POINT_CHANGE");
        TYPE_CODES.addElement(TYPE_STATE_CHANGE_COUNT, "STATE_CHANGE_COUNT");
        TYPE_CODES.addElement(TYPE_NO_CHANGE, "NO_CHANGE");
        TYPE_CODES.addElement(TYPE_NO_UPDATE, "NO_UPDATE");
        TYPE_CODES.addElement(TYPE_ALPHANUMERIC_STATE, "ALPHANUMERIC_STATE");
        TYPE_CODES.addElement(TYPE_POSITIVE_CUSUM, "POSITIVE_CUSUM");
        TYPE_CODES.addElement(TYPE_NEGATIVE_CUSUM, "NEGATIVE_CUSUM");
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("type");
        if (text == null)
            throw new LocalizableJsonException("emport.error.ped.missing", "type", TYPE_CODES.getCodeList());

        detectorType = TYPE_CODES.getId(text);
        if (!TYPE_CODES.isValidId(detectorType))
            throw new LocalizableJsonException("emport.error.ped.invalid", "type", text, TYPE_CODES.getCodeList());

        text = json.getString("alarmLevel");
        if (text != null) {
            alarmLevel = AlarmLevels.CODES.getId(text);
            if (!AlarmLevels.CODES.isValidId(alarmLevel))
                throw new LocalizableJsonException("emport.error.ped.invalid", "alarmLevel", text, AlarmLevels.CODES
                        .getCodeList());
        }

        switch (detectorType) {
        case TYPE_ANALOG_HIGH_LIMIT:
            limit = getDouble(json, "limit");
            updateDuration(json);
            break;
        case TYPE_ANALOG_LOW_LIMIT:
            limit = getDouble(json, "limit");
            updateDuration(json);
            break;
        case TYPE_BINARY_STATE:
            binaryState = getBoolean(json, "state");
            updateDuration(json);
            break;
        case TYPE_MULTISTATE_STATE:
            multistateState = getInt(json, "state");
            updateDuration(json);
            break;
        case TYPE_POINT_CHANGE:
            break;
        case TYPE_STATE_CHANGE_COUNT:
            changeCount = getInt(json, "changeCount");
            updateDuration(json);
            break;
        case TYPE_NO_CHANGE:
            updateDuration(json);
            break;
        case TYPE_NO_UPDATE:
            updateDuration(json);
            break;
        case TYPE_ALPHANUMERIC_STATE:
            alphanumericState = getString(json, "state");
            updateDuration(json);
            break;
        case TYPE_POSITIVE_CUSUM:
            limit = getDouble(json, "limit");
            weight = getDouble(json, "weight");
            updateDuration(json);
            break;
        case TYPE_NEGATIVE_CUSUM:
            limit = getDouble(json, "limit");
            weight = getDouble(json, "weight");
            updateDuration(json);
            break;
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("xid", xid);
        map.put("type", TYPE_CODES.getCode(detectorType));
        map.put("alarmLevel", AlarmLevels.CODES.getCode(alarmLevel));

        switch (detectorType) {
        case TYPE_ANALOG_HIGH_LIMIT:
            map.put("limit", limit);
            addDuration(map);
            break;
        case TYPE_ANALOG_LOW_LIMIT:
            map.put("limit", limit);
            addDuration(map);
            break;
        case TYPE_BINARY_STATE:
            map.put("state", binaryState);
            addDuration(map);
            break;
        case TYPE_MULTISTATE_STATE:
            map.put("state", multistateState);
            addDuration(map);
            break;
        case TYPE_POINT_CHANGE:
            break;
        case TYPE_STATE_CHANGE_COUNT:
            map.put("changeCount", changeCount);
            addDuration(map);
            break;
        case TYPE_NO_CHANGE:
            addDuration(map);
            break;
        case TYPE_NO_UPDATE:
            addDuration(map);
            break;
        case TYPE_ALPHANUMERIC_STATE:
            map.put("state", alphanumericState);
            addDuration(map);
            break;
        case TYPE_POSITIVE_CUSUM:
            map.put("limit", limit);
            map.put("weight", weight);
            addDuration(map);
            break;
        case TYPE_NEGATIVE_CUSUM:
            map.put("limit", limit);
            map.put("weight", weight);
            addDuration(map);
            break;
        }
    }

    private double getDouble(JsonObject json, String name) throws JsonException {
        Double d = json.getDouble(name);
        if (d == null)
            throw new LocalizableJsonException("emport.error.ped.missingAttr", name);
        return d;
    }

    private int getInt(JsonObject json, String name) throws JsonException {
        Integer i = json.getInt(name);
        if (i == null)
            throw new LocalizableJsonException("emport.error.ped.missingAttr", name);
        return i;
    }

    private void updateDuration(JsonObject json) throws JsonException {
        String text = json.getString("durationType");
        if (text == null)
            throw new LocalizableJsonException("emport.error.ped.missing", "durationType", Common.TIME_PERIOD_CODES
                    .getCodeList());

        durationType = Common.TIME_PERIOD_CODES.getId(text);
        if (!Common.TIME_PERIOD_CODES.isValidId(durationType))
            throw new LocalizableJsonException("emport.error.ped.invalid", "durationType", text,
                    Common.TIME_PERIOD_CODES.getCodeList());

        duration = getInt(json, "duration");
    }

    private boolean getBoolean(JsonObject json, String name) throws JsonException {
        Boolean b = json.getBoolean(name);
        if (b == null)
            throw new LocalizableJsonException("emport.error.ped.missingAttr", name);
        return b;
    }

    private String getString(JsonObject json, String name) throws JsonException {
        String s = json.getString(name);
        if (s == null)
            throw new LocalizableJsonException("emport.error.ped.missingAttr", name);
        return s;
    }

    private void addDuration(Map<String, Object> map) {
        map.put("durationType", Common.TIME_PERIOD_CODES.getCode(durationType));
        map.put("duration", duration);
    }
}
