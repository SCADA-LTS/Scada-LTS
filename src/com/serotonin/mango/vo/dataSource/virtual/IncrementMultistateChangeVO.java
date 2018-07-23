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
import java.util.Arrays;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.rt.dataSource.virtual.ChangeTypeRT;
import com.serotonin.mango.rt.dataSource.virtual.IncrementMultistateChangeRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class IncrementMultistateChangeVO extends ChangeTypeVO {
    public static final LocalizableMessage KEY = new LocalizableMessage("dsEdit.virtual.changeType.increment");

    private int[] values = new int[0];
    private boolean roll;

    @Override
    public int typeId() {
        return Types.INCREMENT_MULTISTATE;
    }

    @Override
    public LocalizableMessage getDescription() {
        return KEY;
    }

    @Override
    public ChangeTypeRT createRuntime() {
        return new IncrementMultistateChangeRT(this);
    }

    public boolean isRoll() {
        return roll;
    }

    public void setRoll(boolean roll) {
        this.roll = roll;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        super.addProperties(list);
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.values", Arrays.toString(values));
        AuditEventType.addPropertyMessage(list, "dsEdit.virtual.roll", roll);
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, Object o) {
        super.addPropertyChanges(list, o);
        IncrementMultistateChangeVO from = (IncrementMultistateChangeVO) o;
        if (Arrays.equals(from.values, values))
            AuditEventType.addPropertyChangeMessage(list, "dsEdit.virtual.values", Arrays.toString(from.values), Arrays
                    .toString(values));
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.virtual.roll", from.roll, roll);
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
        out.writeObject(values);
        out.writeBoolean(roll);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            values = (int[]) in.readObject();
            roll = in.readBoolean();
        }
    }
}
