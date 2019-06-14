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
package com.serotonin.mango.rt.dataSource.virtual;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.vo.dataSource.virtual.IncrementAnalogChangeVO;

public class IncrementAnalogChangeRT extends ChangeTypeRT {
    private final IncrementAnalogChangeVO vo;
    private boolean decrement = false;

    public IncrementAnalogChangeRT(IncrementAnalogChangeVO vo) {
        this.vo = vo;
    }

    @Override
    public MangoValue change(MangoValue currentValue) {
        double newValue = currentValue.getDoubleValue();

        if (vo.isRoll()) {
            newValue += vo.getChange();
            if (newValue > vo.getMax())
                newValue = vo.getMin();
            if (newValue < vo.getMin())
                newValue = vo.getMax();
        }
        else {
            if (decrement) {
                newValue -= vo.getChange();
                if (newValue <= vo.getMin()) {
                    newValue = vo.getMin();
                    decrement = false;
                }
            }
            else {
                newValue += vo.getChange();
                if (newValue >= vo.getMax()) {
                    newValue = vo.getMax();
                    decrement = true;
                }
            }
        }

        return new NumericValue(newValue);
    }
}
