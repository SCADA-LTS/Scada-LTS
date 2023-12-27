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
package com.serotonin.mango.rt.event.compound;

import java.util.List;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.SimpleEventDetector;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class EventDetectorWrapper extends LogicalOperator {
    private final String detectorKey;
    private SimpleEventDetector source;

    public EventDetectorWrapper(String detectorKey) {
        this.detectorKey = detectorKey;
    }

    @Override
    public boolean evaluate() {
        if (source == null)
            throw new ShouldNeverHappenException("No runtime object available");
        return source.isEventActive();
    }

    @Override
    public String toString() {
        return detectorKey;
    }

    @Override
    public void initialize() throws LocalizableException {
        source = Common.ctx.getRuntimeManager().getSimpleEventDetector(detectorKey);
        if (source == null)
            throw new LocalizableException(new LocalizableMessage("compoundDetectors.initError.wrapper", detectorKey));
    }

    @Override
    public void initSource(CompoundEventDetectorRT parent) {
        source.addListener(parent);
    }

    @Override
    public void terminate(CompoundEventDetectorRT parent) {
        if (source != null)
            source.removeListener(parent);
    }

    @Override
    protected void appendDetectorKeys(List<String> keys) {
        keys.add(detectorKey);
    }
}
