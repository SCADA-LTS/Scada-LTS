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
package org.scada_lts.web.mvc.api.dto;

import com.serotonin.json.*;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.mailingList.EmailRecipient;

import java.util.Map;

abstract public class EmailRecipientJson implements JsonSerializable {
    public static final int TYPE_MAILING_LIST = 1;
    public static final int TYPE_USER = 2;
    public static final int TYPE_ADDRESS = 3;

    public static final ExportCodes TYPE_CODES = new ExportCodes();
    static {
        TYPE_CODES.addElement(TYPE_MAILING_LIST, "MAILING_LIST", "mailingLists.mailingList");
        TYPE_CODES.addElement(TYPE_USER, "USER", "mailingLists.emailAddress");
        TYPE_CODES.addElement(TYPE_ADDRESS, "ADDRESS", "common.user");
    }

    abstract public int getRecipientType();
    abstract public int getReferenceId();
    abstract public String getReferenceAddress();
    abstract public EmailRecipient to();

    /**
     * @throws JsonException
     */
    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        // no op. The type value is used by the factory.
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        map.put("recipientType", TYPE_CODES.getCode(getRecipientType()));
    }
}
