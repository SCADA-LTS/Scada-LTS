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
package com.serotonin.mango.vo.link;

import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.PointLinkService;
import org.scada_lts.utils.XidUtils;
import org.scada_lts.web.beans.validation.script.ScriptProtect;
import org.scada_lts.web.beans.validation.script.ScriptValidatorUtils;
import org.scada_lts.web.beans.validation.xss.XssProtect;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class PointLinkVO implements ChangeComparable<PointLinkVO>, JsonSerializable {
    public static final String XID_PREFIX = "PL_";

    public static final int EVENT_UPDATE = 1;
    public static final int EVENT_CHANGE = 2;

    public static ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(EVENT_UPDATE, "UPDATE", "pointLinks.event.update");
        EVENT_CODES.addElement(EVENT_CHANGE, "CHANGE", "pointLinks.event.change");
    }

    private int id = Common.NEW_ID;
    @XssProtect
    private String xid;
    private int sourcePointId;
    private int targetPointId;
    @JsonRemoteProperty
    @ScriptProtect
    private String script;
    private int event;
    @JsonRemoteProperty
    private boolean disabled;

    public boolean isNew() {
        return id == Common.NEW_ID;
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

    public int getSourcePointId() {
        return sourcePointId;
    }

    public void setSourcePointId(int sourcePointId) {
        this.sourcePointId = sourcePointId;
    }

    public int getTargetPointId() {
        return targetPointId;
    }

    public void setTargetPointId(int targetPointId) {
        this.targetPointId = targetPointId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String getTypeKey() {
        return "event.audit.pointLink";
    }

    /**
     * Validates this PointLinkVO's configuration and records any validation messages.
     *
     * Performs the following checks and adds contextual messages to the provided response when a check fails:
     * - XID uniqueness
     * - presence of a source point
     * - presence of a target point
     * - that the source and target are not the same
     * - script content validity
     *
     * @param response the response object to receive contextual validation messages
     */
    public void validate(DwrResponseI18n response) {

        PointLinkService pointLinkService = new PointLinkService();
        XidUtils.validateXid(response, pointLinkService::isXidUnique, xid, id);

        if (sourcePointId == 0)
            response.addContextualMessage("sourcePointId", "pointLinks.validate.sourceRequired");
        if (targetPointId == 0)
            response.addContextualMessage("targetPointId", "pointLinks.validate.targetRequired");
        if (sourcePointId == targetPointId)
            response.addContextualMessage("targetPointId", "pointLinks.validate.samePoint");
        if(!ScriptValidatorUtils.validate(script))
            response.addContextualMessage("script", "validate.invalidValue");
    }

    /**
     * Adds this point link's audit properties to the provided list.
     *
     * Adds the XID, source and target point extended names, script, event code, and disabled flag as localized audit messages.
     *
     * @param list the list to which localized audit messages describing this object's properties will be added
     */
    @Override
    public void addProperties(List<LocalizableMessage> list) {
        DataPointDao dataPointDao = new DataPointDao();
        AuditEventType.addPropertyMessage(list, "common.xid", xid);
        AuditEventType.addPropertyMessage(list, "pointLinks.source", dataPointDao.getExtendedPointName(sourcePointId));
        AuditEventType.addPropertyMessage(list, "pointLinks.target", dataPointDao.getExtendedPointName(targetPointId));
        AuditEventType.addPropertyMessage(list, "pointLinks.script", script);
        AuditEventType.addExportCodeMessage(list, "pointLinks.event", EVENT_CODES, event);
        AuditEventType.addPropertyMessage(list, "common.disabled", disabled);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, PointLinkVO from) {
        DataPointDao dataPointDao = new DataPointDao();
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, xid);
        AuditEventType
                .maybeAddPropertyChangeMessage(list, "pointLinks.source",
                        dataPointDao.getExtendedPointName(from.sourcePointId),
                        dataPointDao.getExtendedPointName(sourcePointId));
        AuditEventType
                .maybeAddPropertyChangeMessage(list, "pointLinks.target",
                        dataPointDao.getExtendedPointName(from.targetPointId),
                        dataPointDao.getExtendedPointName(targetPointId));
        AuditEventType.maybeAddPropertyChangeMessage(list, "pointLinks.script", from.script, script);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "pointLinks.event", EVENT_CODES, from.event, event);
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.disabled", from.disabled, disabled);
    }

    //
    //
    // Serialization
    //
    public void jsonSerialize(Map<String, Object> map) {
        DataPointDao dataPointDao = new DataPointDao();

        map.put("xid", xid);

        DataPointVO dp = dataPointDao.getDataPoint(sourcePointId);
        if (dp != null)
            map.put("sourcePointId", dp.getXid());

        dp = dataPointDao.getDataPoint(targetPointId);
        if (dp != null)
            map.put("targetPointId", dp.getXid());

        map.put("event", EVENT_CODES.getCode(event));
    }

    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        DataPointDao dataPointDao = new DataPointDao();

        String xid = json.getString("sourcePointId");
        if (xid != null) {
            DataPointVO vo = dataPointDao.getDataPoint(xid);
            if (vo == null)
                throw new LocalizableJsonException("emport.error.missingPoint", xid);
            sourcePointId = vo.getId();
        }

        xid = json.getString("targetPointId");
        if (xid != null) {
            DataPointVO vo = dataPointDao.getDataPoint(xid);
            if (vo == null)
                throw new LocalizableJsonException("emport.error.missingPoint", xid);
            targetPointId = vo.getId();
        }

        String text = json.getString("event");
        if (text != null) {
            event = EVENT_CODES.getId(text);
            if (!EVENT_CODES.isValidId(event))
                throw new LocalizableJsonException("emport.error.link.invalid", "event", text,
                        EVENT_CODES.getCodeList());
        }
    }
}