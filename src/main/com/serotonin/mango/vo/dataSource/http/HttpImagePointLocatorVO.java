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
package com.serotonin.mango.vo.dataSource.http;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.rt.dataSource.http.HttpImagePointLocatorRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.AbstractPointLocatorVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class HttpImagePointLocatorVO extends AbstractPointLocatorVO implements JsonSerializable {
    public static final int SCALE_TYPE_NONE = 0;
    public static final int SCALE_TYPE_PERCENT = 1;
    public static final int SCALE_TYPE_BOX = 2;

    private static final ExportCodes SCALE_TYPE_CODES = new ExportCodes();
    static {
        SCALE_TYPE_CODES.addElement(SCALE_TYPE_NONE, "SCALE_TYPE_NONE", "dsEdit.httpImage.scalingType.none");
        SCALE_TYPE_CODES.addElement(SCALE_TYPE_PERCENT, "SCALE_TYPE_PERCENT", "dsEdit.httpImage.scalingType.percent");
        SCALE_TYPE_CODES.addElement(SCALE_TYPE_BOX, "SCALE_TYPE_BOX", "dsEdit.httpImage.scalingType.box");
    }

    public boolean isSettable() {
        return false;
    }

    public PointLocatorRT createRuntime() {
        return new HttpImagePointLocatorRT(this);
    }

    public LocalizableMessage getConfigurationDescription() {
        return new LocalizableMessage("common.default", url);
    }

    @JsonRemoteProperty
    private String url;
    @JsonRemoteProperty
    private int timeoutSeconds = 30;
    @JsonRemoteProperty
    private int retries = 2;
    private int scaleType;
    @JsonRemoteProperty
    private int scalePercent = 25;
    @JsonRemoteProperty
    private int scaleWidth = 100;
    @JsonRemoteProperty
    private int scaleHeight = 100;
    @JsonRemoteProperty
    private int readLimit = 10000;
    @JsonRemoteProperty
    private String webcamLiveFeedCode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getScaleType() {
        return scaleType;
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public int getScalePercent() {
        return scalePercent;
    }

    public void setScalePercent(int scalePercent) {
        this.scalePercent = scalePercent;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public void setScaleWidth(int scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public void setScaleHeight(int scaleHeight) {
        this.scaleHeight = scaleHeight;
    }

    public int getReadLimit() {
        return readLimit;
    }

    public void setReadLimit(int readLimit) {
        this.readLimit = readLimit;
    }

    public String getWebcamLiveFeedCode() {
        return webcamLiveFeedCode;
    }

    public void setWebcamLiveFeedCode(String webcamLiveFeedCode) {
        this.webcamLiveFeedCode = webcamLiveFeedCode;
    }

    public int getDataTypeId() {
        return DataTypes.IMAGE;
    }

    public void validate(DwrResponseI18n response) {
        if (StringUtils.isEmpty(url))
            response.addContextualMessage("url", "validate.required");
        if (timeoutSeconds <= 0)
            response.addContextualMessage("timeoutSeconds", "validate.greaterThanZero");
        if (retries < 0)
            response.addContextualMessage("retries", "validate.cannotBeNegative");
        if (!SCALE_TYPE_CODES.isValidId(scaleType))
            response.addContextualMessage("scaleType", "validate.invalidValue");
        if (scaleType == SCALE_TYPE_PERCENT) {
            if (scalePercent <= 0)
                response.addContextualMessage("scalePercent", "validate.greaterThanZero");
            else if (scalePercent > 100)
                response.addContextualMessage("scalePercent", "validate.lessThan100");
        }
        else if (scaleType == SCALE_TYPE_BOX) {
            if (scaleWidth <= 0)
                response.addContextualMessage("scaleWidth", "validate.greaterThanZero");
            if (scaleHeight <= 0)
                response.addContextualMessage("scaleHeight", "validate.greaterThanZero");
        }
        if (readLimit <= 0)
            response.addContextualMessage("readLimit", "validate.greaterThanZero");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.url", url);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.timeout", timeoutSeconds);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.retries", retries);
        AuditEventType.addExportCodeMessage(list, "dsEdit.httpImage.scalingType", SCALE_TYPE_CODES, scaleType);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.scalePercent", scalePercent);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.scaleWidth", scaleWidth);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.scaleHeight", scaleHeight);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.readLimit", readLimit);
        AuditEventType.addPropertyMessage(list, "dsEdit.httpImage.liveFeed", webcamLiveFeedCode);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        HttpImagePointLocatorVO from = (HttpImagePointLocatorVO) o;
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.url", from.url, url);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.timeout", from.timeoutSeconds,
                timeoutSeconds);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.retries", from.retries, retries);
        AuditEventType.maybeAddExportCodeChangeMessage(list, "dsEdit.httpImage.scalingType", SCALE_TYPE_CODES,
                from.scaleType, scaleType);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.scalePercent", from.scalePercent,
                scalePercent);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.scaleWidth", from.scaleWidth, scaleWidth);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.scaleHeight", from.scaleHeight,
                scaleHeight);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.readLimit", from.readLimit, readLimit);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.httpImage.liveFeed", from.webcamLiveFeedCode,
                webcamLiveFeedCode);
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
        SerializationHelper.writeSafeUTF(out, url);
        out.writeInt(timeoutSeconds);
        out.writeInt(retries);
        out.writeInt(scaleType);
        out.writeInt(scalePercent);
        out.writeInt(scaleWidth);
        out.writeInt(scaleHeight);
        out.writeInt(readLimit);
        SerializationHelper.writeSafeUTF(out, webcamLiveFeedCode);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            url = SerializationHelper.readSafeUTF(in);
            timeoutSeconds = in.readInt();
            retries = in.readInt();
            scaleType = in.readInt();
            scalePercent = in.readInt();
            scaleWidth = in.readInt();
            scaleHeight = in.readInt();
            readLimit = in.readInt();
            webcamLiveFeedCode = SerializationHelper.readSafeUTF(in);
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("scaleType");
        if (text != null) {
            scaleType = SCALE_TYPE_CODES.getId(text);
            if (scaleType == -1)
                throw new LocalizableJsonException("emport.error.invalid", "scaleType", text, SCALE_TYPE_CODES);
        }
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("scaleType", SCALE_TYPE_CODES.getCode(scaleType));
    }
}
