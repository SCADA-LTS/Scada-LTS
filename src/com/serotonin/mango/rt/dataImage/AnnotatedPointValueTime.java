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
package com.serotonin.mango.rt.dataImage;

import java.util.Objects;
import java.util.ResourceBundle;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.util.AnnotatedPointValueUtils;
import com.serotonin.web.taglib.DateFunctions;

/**
 * This class provides a way of arbitrarily annotating a PointValue. Point value annotations should not be confused with
 * Java annotations. A point value annotation will typically explain the source of the value when it did not simply come
 * from data source.
 * 
 * @see SetPointSource
 * @author Matthew Lohbihler
 */
public class AnnotatedPointValueTime extends PointValueTime {
    private static final long serialVersionUID = -1;

    /**
     * The type of source that created the annotation.
     * 
     * @see SetPointSource
     */
    private final int sourceType;

    /**
     * The id of the source that created the annotation.
     * 
     * @see SetPointSource
     */
    private final int sourceId;

    /**
     * An arbitrary description of the source, human readable. This depends on the source type, but will typically be
     * the source's name. For example, for a user source it would be the username.
     * 
     * @see SetPointSource
     */
    private String sourceDescriptionArgument;

    public AnnotatedPointValueTime(MangoValue value, long time, int sourceType, int sourceId) {
        super(value, time);
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    @Override
    public boolean isAnnotated() {
        return true;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getSourceType() {
        return sourceType;
    }

    public String getSourceDescriptionKey() {
        return AnnotatedPointValueUtils.getSourceDescriptionKey(sourceType);
    }

    public String getSourceDescriptionArgument() {
        return sourceDescriptionArgument;
    }

    public void setSourceDescriptionArgument(String sourceDescriptionArgument) {
        this.sourceDescriptionArgument = sourceDescriptionArgument;
    }

    public String getAnnotation(ResourceBundle bundle) {
        return AnnotatedPointValueUtils.getAnnotation(bundle, sourceType, sourceDescriptionArgument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotatedPointValueTime)) return false;
        if (!super.equals(o)) return false;
        AnnotatedPointValueTime that = (AnnotatedPointValueTime) o;
        return getSourceType() == that.getSourceType() && getSourceId() == that.getSourceId() && Objects.equals(getSourceDescriptionArgument(), that.getSourceDescriptionArgument());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSourceType(), getSourceId(), getSourceDescriptionArgument());
    }

    @Override
    public String toString() {
        return "AnnotatedPointValueTime(" + (sourceDescriptionArgument == null ? getAnnotation(Common.getBundle()) : sourceDescriptionArgument) + " -- " + getValue() + "@" + DateFunctions.getTime(getTime()) + ")";
    }
}
