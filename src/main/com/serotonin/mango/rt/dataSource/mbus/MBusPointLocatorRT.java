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
package com.serotonin.mango.rt.dataSource.mbus;

import net.sf.mbus4j.dataframes.MBusMedium;
import net.sf.mbus4j.dataframes.datablocks.DataBlock;
import net.sf.mbus4j.dataframes.datablocks.dif.DataFieldCode;
import net.sf.mbus4j.dataframes.datablocks.dif.FunctionField;
import net.sf.mbus4j.dataframes.datablocks.vif.Vife;
import com.serotonin.mango.vo.dataSource.mbus.MBusPointLocatorVO;
import net.sf.mbus4j.master.ValueRequestPointLocator;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class MBusPointLocatorRT extends PointLocatorRT {
    private final MBusPointLocatorVO vo;

    public MBusPointLocatorRT(MBusPointLocatorVO vo) {
        this.vo = vo;
    }

    @Override
    public boolean isSettable() {
        return false;
    }

    /**
     * @return the vo
     */
    public MBusPointLocatorVO getVo() {
        return vo;
    }

    public ValueRequestPointLocator<DataPointRT> createValueRequestPointLocator(DataPointRT point) {
        ValueRequestPointLocator<DataPointRT> result = new ValueRequestPointLocator<DataPointRT>();
        result.setAddressing(vo.getAddressing());
        result.setAddress(vo.getAddress());
        result.setDeviceUnit(vo.getDeviceUnit());
        result.setDifCode(DataFieldCode.fromLabel(vo.getDifCode()));
        result.setFunctionField(FunctionField.fromLabel(vo.getFunctionField()));
        result.setIdentnumber(vo.getIdentNumber());
        result.setManufacturer(vo.getManufacturer());
        result.setMedium(MBusMedium.fromLabel(vo.getMedium()));
        result.setReference(point);
        result.setResponseFrameName(vo.getResponseFrame());
        result.setStorageNumber(vo.getStorageNumber());
        result.setTariff(vo.getTariff());
        result.setVersion(vo.getVersion());
        result.setVif(DataBlock.getVif(vo.getVifType(), vo.getVifLabel(), vo.getUnitOfMeasurement(), vo.getSiPrefix(),
                vo.getExponent()));
        if (vo.getVifeLabels().length == 0) {
            result.setVifes(DataBlock.EMPTY_VIFE);
        }
        else {
            Vife[] vifes = new Vife[vo.getVifeLabels().length];
            for (int i = 0; i < vo.getVifeLabels().length; i++) {
                vifes[i] = DataBlock.getVife(vo.getVifeTypes()[i], vo.getVifeLabels()[i]);
            }
            result.setVifes(vifes);
        }
        return result;
    }
}
