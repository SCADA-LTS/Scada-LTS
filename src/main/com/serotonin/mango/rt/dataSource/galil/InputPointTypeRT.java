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
package com.serotonin.mango.rt.dataSource.galil;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.dataSource.galil.InputPointTypeVO;
import com.serotonin.web.i18n.LocalizableException;

/**
 * @author Matthew Lohbihler
 */
public class InputPointTypeRT extends PointTypeRT {
    private final InputPointTypeVO vo;

    public InputPointTypeRT(InputPointTypeVO vo) {
        super(vo);
        this.vo = vo;
    }

    @Override
    public String getPollRequestImpl() {
        if (vo.getDataTypeId() == DataTypes.BINARY)
            return "MG @IN[" + vo.getInputId() + "]";
        return "MG @AN[" + vo.getInputId() + "]";
    }

    @Override
    public MangoValue parsePollResponse(String data, String pointName) throws LocalizableException {
        int dataTypeId = vo.getDataTypeId();
        MangoValue value = parseValue(data, dataTypeId, pointName);

        if (dataTypeId == DataTypes.NUMERIC)
            value = new NumericValue(rawToEngineeringUnits(value.getDoubleValue(), vo.getScaleRawLow(), vo
                    .getScaleRawHigh(), vo.getScaleEngLow(), vo.getScaleEngHigh()));

        return value;
    }

    @Override
    protected String getSetRequestImpl(MangoValue value) {
        return null;
    }

    @Override
    public MangoValue parseSetResponse(String data) {
        return null;
    }
}
