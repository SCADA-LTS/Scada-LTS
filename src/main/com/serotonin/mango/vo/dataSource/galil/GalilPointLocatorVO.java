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
package com.serotonin.mango.vo.dataSource.galil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.galil.GalilPointLocatorRT;
import com.serotonin.mango.rt.dataSource.galil.PointTypeRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class GalilPointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    private static final Log LOG = LogFactory.getLog(GalilPointLocatorVO.class);

    public LocalizableMessage getConfigurationDescription() {
        PointTypeVO pointType = getPointType();
        if (pointType == null)
            return new LocalizableMessage("common.unknown");
        return pointType.getDescription();
    }

    private PointTypeVO getPointType() {
        if (pointTypeId == PointTypeVO.Types.COMMAND)
            return commandPointType;
        if (pointTypeId == PointTypeVO.Types.INPUT)
            return inputPointType;
        if (pointTypeId == PointTypeVO.Types.OUTPUT)
            return outputPointType;
        if (pointTypeId == PointTypeVO.Types.TELL_POSITION)
            return tellPositionPointType;
        if (pointTypeId == PointTypeVO.Types.VARIABLE)
            return variablePointType;
        LOG.error("Failed to resolve pointTypeId " + pointTypeId + " for Galil point locator");
        return null;
    }

    public PointLocatorRT createRuntime() {
        PointTypeRT changeType = getPointType().createRuntime();
        return new GalilPointLocatorRT(changeType);
    }

    public void validate(DwrResponseI18n response) {
        // Command
        PointTypeVO pointType = getPointType();
        if (pointType == null)
            response.addContextualMessage("pointTypeId", "validate.invalidChoice");
        else
            pointType.validate(response);
    }

    public int getDataTypeId() {
        return getPointType().getDataTypeId();
    }

    public boolean isSettable() {
        return getPointType().isSettable();
    }

    private int pointTypeId = PointTypeVO.Types.COMMAND;
    private CommandPointTypeVO commandPointType = new CommandPointTypeVO();
    private InputPointTypeVO inputPointType = new InputPointTypeVO();
    private OutputPointTypeVO outputPointType = new OutputPointTypeVO();
    private TellPositionPointTypeVO tellPositionPointType = new TellPositionPointTypeVO();
    private VariablePointTypeVO variablePointType = new VariablePointTypeVO();

    public int getPointTypeId() {
        return pointTypeId;
    }

    public void setPointTypeId(int pointTypeId) {
        this.pointTypeId = pointTypeId;
    }

    public CommandPointTypeVO getCommandPointType() {
        return commandPointType;
    }

    public InputPointTypeVO getInputPointType() {
        return inputPointType;
    }

    public OutputPointTypeVO getOutputPointType() {
        return outputPointType;
    }

    public TellPositionPointTypeVO getTellPositionPointType() {
        return tellPositionPointType;
    }

    public VariablePointTypeVO getVariablePointType() {
        return variablePointType;
    }

    public void setCommandPointType(CommandPointTypeVO commandPointType) {
        this.commandPointType = commandPointType;
    }

    public void setInputPointType(InputPointTypeVO inputPointType) {
        this.inputPointType = inputPointType;
    }

    public void setOutputPointType(OutputPointTypeVO outputPointType) {
        this.outputPointType = outputPointType;
    }

    public void setTellPositionPointType(TellPositionPointTypeVO tellPositionPointType) {
        this.tellPositionPointType = tellPositionPointType;
    }

    public void setVariablePointType(VariablePointTypeVO variablePointType) {
        this.variablePointType = variablePointType;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addExportCodeMessage(list, "dsEdit.galil.pointType", PointTypeVO.POINT_TYPE_CODES, pointTypeId);
        getPointType().addProperties(list);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        GalilPointLocatorVO from = (GalilPointLocatorVO) o;
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.galil.pointType", PointTypeVO.POINT_TYPE_CODES,
                from.pointTypeId, pointTypeId);
        if (from.pointTypeId == pointTypeId)
            getPointType().addPropertyChanges(list, from.getPointType());
        else
            getPointType().addProperties(list);
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
        out.writeInt(pointTypeId);
        out.writeObject(commandPointType);
        out.writeObject(inputPointType);
        out.writeObject(outputPointType);
        out.writeObject(tellPositionPointType);
        out.writeObject(variablePointType);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            pointTypeId = in.readInt();
            commandPointType = (CommandPointTypeVO) in.readObject();
            inputPointType = (InputPointTypeVO) in.readObject();
            outputPointType = (OutputPointTypeVO) in.readObject();
            tellPositionPointType = (TellPositionPointTypeVO) in.readObject();
            variablePointType = (VariablePointTypeVO) in.readObject();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        JsonObject ptjson = json.getJsonObject("pointType");
        if (ptjson == null)
            throw new LocalizableJsonException("emport.error.missingObject", "pointType");

        String text = ptjson.getString("type");
        if (text == null)
            throw new LocalizableJsonException("emport.error.pointType.missing", "type", PointTypeVO.POINT_TYPE_CODES
                    .getCodeList());

        pointTypeId = PointTypeVO.POINT_TYPE_CODES.getId(text);
        if (pointTypeId == -1)
            throw new LocalizableJsonException("emport.error.pointType.invalid", "pointType", text,
                    PointTypeVO.POINT_TYPE_CODES.getCodeList());

        reader.populateObject(getPointType(), ptjson);
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("pointType", getPointType());
    }
}
