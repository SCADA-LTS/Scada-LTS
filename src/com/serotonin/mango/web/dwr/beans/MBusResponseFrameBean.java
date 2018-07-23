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

import java.util.ArrayList;
import java.util.List;

import net.sf.mbus4j.dataframes.Frame;
import net.sf.mbus4j.dataframes.UserDataResponse;

/**
 * 
 * @author aploese
 */
public class MBusResponseFrameBean {
    private final String name;

    public MBusResponseFrameBean(Frame rsf, int devIndex, int frameIndex, String name) {
        this.rsf = rsf;
        this.name = name;
        if (rsf instanceof UserDataResponse) {
            UserDataResponse rf = (UserDataResponse) rsf;
            for (int i = 0; i < rf.getDataBlockCount(); i++) {
                dataBlocks.add(new MBusDataBlockBean(devIndex, frameIndex, i, rf.getDataBlock(i)));
            }
        }

    }

    private final Frame rsf;
    private final List<MBusDataBlockBean> dataBlocks = new ArrayList<MBusDataBlockBean>();

    public boolean addDataBlock(MBusDataBlockBean bean) {
        return dataBlocks.add(bean);
    }

    public MBusDataBlockBean[] getDataBlocks() {
        return dataBlocks.toArray(new MBusDataBlockBean[dataBlocks.size()]);
    }

    public String getName() {
        return name;
    }

    public Frame getRsf() {
        return rsf;
    }
}
