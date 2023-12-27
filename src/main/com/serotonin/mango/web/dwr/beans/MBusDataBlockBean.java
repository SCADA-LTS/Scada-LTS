/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.dwr.beans;

import net.sf.mbus4j.dataframes.datablocks.DataBlock;

public class MBusDataBlockBean {
    private final int dbIndex;
    private final int devIndex;
    private final int rsIndex;
    private final DataBlock db;

    MBusDataBlockBean(int devIndex, int rsIndex, int dbIndex, DataBlock db) {
        this.devIndex = devIndex;
        this.rsIndex = rsIndex;
        this.dbIndex = dbIndex;
        this.db = db;
    }

    public String getName() {
        return db.getParamDescr();
    }

    public int getDbIndex() {
        return dbIndex;
    }

    public int getRsIndex() {
        return rsIndex;
    }

    public int getDevIndex() {
        return devIndex;
    }

    public String getParams() {
        return db.toString();
    }

    public String getValue() {
        return db.getValueAsString();
    }
}
