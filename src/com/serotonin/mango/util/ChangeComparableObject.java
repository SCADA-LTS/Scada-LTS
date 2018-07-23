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
package com.serotonin.mango.util;

import java.util.List;

import com.serotonin.web.i18n.LocalizableMessage;

/**
 * This interface is meant for comparable objects (for audit purposes) that are members of other comparable objects. It
 * does not use generics so to avoid having to spread generic definitions throughout the entire code base.
 * 
 * @author Matthew Lohbihler
 */
public interface ChangeComparableObject {
    void addProperties(List<LocalizableMessage> list);

    void addPropertyChanges(List<LocalizableMessage> list, Object o);
}
