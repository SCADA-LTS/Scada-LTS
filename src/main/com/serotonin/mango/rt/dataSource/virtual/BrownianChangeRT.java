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
import com.serotonin.mango.vo.dataSource.virtual.BrownianChangeVO;

public class BrownianChangeRT extends ChangeTypeRT {
    private final BrownianChangeVO vo;

    public BrownianChangeRT(BrownianChangeVO vo) {
        this.vo = vo;
    }

    @Override
    public MangoValue change(MangoValue currentValue) {
        double change = RANDOM.nextDouble() * vo.getMaxChange() * 2 - vo.getMaxChange();
        double newValue = currentValue.getDoubleValue() + change;
        if (newValue > vo.getMax())
            newValue = vo.getMax();
        if (newValue < vo.getMin())
            newValue = vo.getMin();
        return new NumericValue(newValue);
    }
}
