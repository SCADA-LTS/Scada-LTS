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
package com.serotonin.mango.vo.dataSource.meta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.meta.MetaDataSourceRT;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class MetaDataSourceVO extends DataSourceVO<MetaDataSourceVO> {
    public static final Type TYPE = Type.META;

    @Override
    public DataSourceRT createDataSourceRT() {
        return new MetaDataSourceRT(this);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(MetaDataSourceRT.EVENT_TYPE_CONTEXT_POINT_DISABLED, new LocalizableMessage(
                "event.ds.contextPoint")));
        ets.add(createEventType(MetaDataSourceRT.EVENT_TYPE_SCRIPT_ERROR,
                new LocalizableMessage("event.ds.scriptError")));
        ets.add(createEventType(MetaDataSourceRT.EVENT_TYPE_RESULT_TYPE_ERROR, new LocalizableMessage(
                "event.ds.resultType")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(MetaDataSourceRT.EVENT_TYPE_CONTEXT_POINT_DISABLED, "CONTEXT_POINT_DISABLED");
        EVENT_CODES.addElement(MetaDataSourceRT.EVENT_TYPE_SCRIPT_ERROR, "SCRIPT_ERROR");
        EVENT_CODES.addElement(MetaDataSourceRT.EVENT_TYPE_RESULT_TYPE_ERROR, "RESULT_TYPE_ERROR");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.noMessage");
    }

    @Override
    public MetaPointLocatorVO createPointLocator() {
        return new MetaPointLocatorVO();
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        // no op
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, MetaDataSourceVO from) {
        // no op
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
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            // nothing to do here.
        }
    }
}
