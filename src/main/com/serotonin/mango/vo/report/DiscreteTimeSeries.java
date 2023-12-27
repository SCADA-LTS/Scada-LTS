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
package com.serotonin.mango.vo.report;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.text.TextRenderer;

/**
 * @author Matthew Lohbihler
 */
public class DiscreteTimeSeries {
    private final String name;
    private final TextRenderer textRenderer;
    private final Paint paint;
    private final List<PointValueTime> valueTimes = new ArrayList<PointValueTime>();
    private final List<ValueDescription> valueDescriptions = new ArrayList<ValueDescription>();

    public DiscreteTimeSeries(String name, TextRenderer textRenderer) {
        this(name, textRenderer, null);
    }

    public DiscreteTimeSeries(String name, TextRenderer textRenderer, Paint paint) {
        this.name = name;
        this.textRenderer = textRenderer;
        this.paint = paint;
    }

    @SuppressWarnings("unchecked")
    public void addValueTime(PointValueTime pvt) {
        MangoValue value = pvt.getValue();
        if (value == null)
            return;

        valueTimes.add(pvt);

        if (getValueIndex(value) == -1) {
            String text;
            if (textRenderer == null)
                text = value.toString();
            else
                text = textRenderer.getText(value, TextRenderer.HINT_FULL);

            ValueDescription vd = new ValueDescription((Comparable<Object>) value, text);

            int index = Collections.binarySearch(valueDescriptions, vd);
            valueDescriptions.add(-index - 1, vd);
        }
    }

    public String getName() {
        return name;
    }

    public Paint getPaint() {
        return paint;
    }

    public List<PointValueTime> getValueTimes() {
        return valueTimes;
    }

    public int getDiscreteValueCount() {
        return valueDescriptions.size();
    }

    public int getValueIndex(MangoValue value) {
        for (int i = 0; i < valueDescriptions.size(); i++) {
            if (valueDescriptions.get(i).getValue().equals(value))
                return i;
        }
        return -1;
    }

    public String getValueText(int index) {
        return valueDescriptions.get(index).getDescription();
    }

    class ValueDescription implements Comparable<ValueDescription> {
        private final Comparable<Object> value;
        private final String description;

        public ValueDescription(Comparable<Object> value, String description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public int compareTo(ValueDescription that) {
            return value.compareTo(that.getValue());
        }

        public Comparable<Object> getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }
}
