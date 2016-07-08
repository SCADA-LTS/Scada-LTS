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
package com.serotonin.mango.vo.dataSource.vmstat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.vmstat.VMStatDataSourceRT;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class VMStatDataSourceVO extends DataSourceVO<VMStatDataSourceVO> {
    public static final Type TYPE = Type.VMSTAT;

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(VMStatDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource"), EventType.DuplicateHandling.IGNORE_SAME_MESSAGE, AlarmLevels.URGENT));
        ets
                .add(createEventType(VMStatDataSourceRT.PARSE_EXCEPTION_EVENT, new LocalizableMessage(
                        "event.ds.dataParse")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(VMStatDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(VMStatDataSourceRT.PARSE_EXCEPTION_EVENT, "PARSE_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    public interface OutputScale {
        int NONE = 1;
        int LOWER_K = 2;
        int UPPER_K = 3;
        int LOWER_M = 4;
        int UPPER_M = 5;
    }

    public static final ExportCodes OUTPUT_SCALE_CODES = new ExportCodes();
    static {
        OUTPUT_SCALE_CODES.addElement(OutputScale.NONE, "NONE", "dsEdit.vmstat.scale.none");
        OUTPUT_SCALE_CODES.addElement(OutputScale.LOWER_K, "LOWER_K", "dsEdit.vmstat.scale.k");
        OUTPUT_SCALE_CODES.addElement(OutputScale.UPPER_K, "UPPER_K", "dsEdit.vmstat.scale.K");
        OUTPUT_SCALE_CODES.addElement(OutputScale.LOWER_M, "LOWER_M", "dsEdit.vmstat.scale.m");
        OUTPUT_SCALE_CODES.addElement(OutputScale.UPPER_M, "UPPER_M", "dsEdit.vmstat.scale.M");
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("dsEdit.vmstat.dsconn", pollSeconds);
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new VMStatDataSourceRT(this);
    }

    @Override
    public VMStatPointLocatorVO createPointLocator() {
        return new VMStatPointLocatorVO();
    }

    @JsonRemoteProperty
    private int pollSeconds = 60;
    @JsonRemoteProperty
    private int outputScale = OutputScale.NONE;

    public int getPollSeconds() {
        return pollSeconds;
    }

    public void setPollSeconds(int pollSeconds) {
        this.pollSeconds = pollSeconds;
    }

    public int getOutputScale() {
        return outputScale;
    }

    public void setOutputScale(int outputScale) {
        this.outputScale = outputScale;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);

        if (pollSeconds < 1)
            response.addContextualMessage("pollSeconds", "validate.greaterThanZero", pollSeconds);

        if (!OUTPUT_SCALE_CODES.isValidId(outputScale))
            response.addContextualMessage("outputScale", "validate.invalidValue");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.vmstat.pollSeconds", pollSeconds);
        AuditEventType.addExportCodeMessage(list, "dsEdit.vmstat.outputScale", OUTPUT_SCALE_CODES, outputScale);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, VMStatDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.vmstat.pollSeconds", from.pollSeconds, pollSeconds);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.vmstat.outputScale", OUTPUT_SCALE_CODES,
                from.outputScale, outputScale);
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
        out.writeInt(pollSeconds);
        out.writeInt(outputScale);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            pollSeconds = in.readInt();
            outputScale = in.readInt();
        }
    }
}
