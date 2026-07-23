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

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.mailingList.AddressEntry;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.util.StringUtils;
import org.scada_lts.web.beans.validation.xss.XssProtect;

import java.util.Map;


public class AddressEntryJson extends EmailRecipientJson {

    @XssProtect
    private String address;

    public AddressEntryJson() {
    }

    public AddressEntryJson(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int getRecipientType() {
        return EmailRecipient.TYPE_ADDRESS;
    }

    @Override
    public int getReferenceId() {
        return 0;
    }

    @Override
    public String getReferenceAddress() {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        map.put("address", address);
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);

        address = json.getString("address");
        if (StringUtils.isEmpty(address))
            throw new LocalizableJsonException("emport.error.recipient.missing.reference", "address");
    }

    @Override
    public EmailRecipient to() {
        AddressEntry addressEntry = new AddressEntry();
        addressEntry.setAddress(address);
        return addressEntry;
    }
}
