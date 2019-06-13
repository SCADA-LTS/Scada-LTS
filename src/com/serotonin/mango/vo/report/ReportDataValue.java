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

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.stats.IValueTime;

/**
 * @author Matthew Lohbihler
 */
public class ReportDataValue implements IValueTime {
    private int reportPointId;
    private MangoValue value;
    private long time;
    private String annotation;

    public ReportDataValue() {
        // no op
    }

    public ReportDataValue(MangoValue value, long time) {
        this.value = value;
        this.time = time;
    }

    public int getReportPointId() {
        return reportPointId;
    }

    public void setReportPointId(int reportPointId) {
        this.reportPointId = reportPointId;
    }

    public MangoValue getValue() {
        return value;
    }

    public void setValue(MangoValue value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return value + "@" + time;
    }
}
