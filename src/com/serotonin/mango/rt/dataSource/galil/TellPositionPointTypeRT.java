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

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.dataSource.galil.TellPositionPointTypeVO;
import com.serotonin.web.i18n.LocalizableException;

/**
 * @author Matthew Lohbihler
 */
public class TellPositionPointTypeRT extends PointTypeRT {
    private final TellPositionPointTypeVO vo;

    public TellPositionPointTypeRT(TellPositionPointTypeVO vo) {
        super(vo);
        this.vo = vo;
    }

    @Override
    public String getPollRequestImpl() {
        return "TP" + vo.getAxis();
    }

    @Override
    public MangoValue parsePollResponse(String data, String pointName) throws LocalizableException {
        double value = parseValue(data, vo.getDataTypeId(), pointName).getDoubleValue();

        value = rawToEngineeringUnits(value, vo.getScaleRawLow(), vo.getScaleRawHigh(), vo.getScaleEngLow(), vo
                .getScaleEngHigh());

        if (vo.isRoundToInteger())
            value = Math.round(value);

        return new NumericValue(value);
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
