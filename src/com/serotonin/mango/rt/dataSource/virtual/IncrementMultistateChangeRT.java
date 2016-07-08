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
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.vo.dataSource.virtual.IncrementMultistateChangeVO;

public class IncrementMultistateChangeRT extends ChangeTypeRT {
    private final IncrementMultistateChangeVO vo;
    private boolean decrement;

    public IncrementMultistateChangeRT(IncrementMultistateChangeVO vo) {
        this.vo = vo;
    }

    @Override
    public MangoValue change(MangoValue currentValue) {
        // Get the current index.
        int currentInt = currentValue.getIntegerValue();
        int index = -1;
        for (int i = 0; i < vo.getValues().length; i++) {
            if (vo.getValues()[i] == currentInt) {
                index = i;
                break;
            }
        }

        if (index == -1)
            return new MultistateValue(vo.getValues()[0]);

        if (vo.isRoll()) {
            index++;
            if (index >= vo.getValues().length)
                index = 0;
        }
        else {
            if (decrement) {
                index--;
                if (index == -1) {
                    index = 1;
                    decrement = false;
                }
            }
            else {
                index++;
                if (index == vo.getValues().length) {
                    index = vo.getValues().length - 2;
                    decrement = true;
                }
            }
        }

        return new MultistateValue(vo.getValues()[index]);
    }
}
