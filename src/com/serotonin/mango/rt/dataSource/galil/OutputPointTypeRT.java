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

import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.dataSource.galil.OutputPointTypeVO;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class OutputPointTypeRT extends PointTypeRT {
    private final OutputPointTypeVO vo;

    public OutputPointTypeRT(OutputPointTypeVO vo) {
        super(vo);
        this.vo = vo;
    }

    @Override
    public String getPollRequestImpl() {
        return "MG @OUT[" + vo.getOutputId() + "]";
    }

    @Override
    public MangoValue parsePollResponse(String data, String pointName) throws LocalizableException {
        return super.parseValue(data, vo.getDataTypeId(), pointName);
    }

    @Override
    protected String getSetRequestImpl(MangoValue value) {
        boolean b = ((BinaryValue) value).getBooleanValue();
        if (b)
            return "SB " + vo.getOutputId();
        return "CB " + vo.getOutputId();
    }

    @Override
    public MangoValue parseSetResponse(String data) throws LocalizableException {
        if (!"".equals(data))
            throw new LocalizableException(new LocalizableMessage("event.galil.unexpected", data));
        return null;
    }
}
