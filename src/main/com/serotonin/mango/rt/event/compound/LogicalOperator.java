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

import java.util.LinkedList;
import java.util.List;

import com.serotonin.web.i18n.LocalizableException;

/**
 * @author Matthew Lohbihler
 */
abstract public class LogicalOperator {
    abstract public boolean evaluate();

    abstract public void initialize() throws LocalizableException;

    abstract public void initSource(CompoundEventDetectorRT parent);

    abstract public void terminate(CompoundEventDetectorRT parent);

    public List<String> getDetectorKeys() {
        List<String> keys = new LinkedList<String>();
        appendDetectorKeys(keys);
        return keys;
    }

    abstract protected void appendDetectorKeys(List<String> keys);
}
