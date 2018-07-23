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

import java.util.ArrayList;
import java.util.List;

import com.serotonin.db.IntValuePair;
import com.serotonin.util.ArrayUtils;

/**
 * @author Matthew Lohbihler
 */
public class ExportCodes {
    private final List<Element> elements = new ArrayList<Element>();

    public void addElement(int id, String code) {
        elements.add(new Element(id, code, null));
    }

    public void addElement(int id, String code, String key) {
        elements.add(new Element(id, code, key));
    }

    public String getCode(int id) {
        Element e = getElement(id);
        if (e == null)
            return null;
        return e.code;
    }

    public String getKey(int id) {
        Element e = getElement(id);
        if (e == null)
            return null;
        return e.key;
    }

    public int getId(String code, int... excludeIds) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).code.equalsIgnoreCase(code) && !ArrayUtils.contains(excludeIds, elements.get(i).id))
                return elements.get(i).id;
        }
        return -1;
    }

    public boolean isValidId(int id, int... excludeIds) {
        for (int i = 0; i < elements.size(); i++) {
            int eid = elements.get(i).id;
            if (!ArrayUtils.contains(excludeIds, eid) && eid == id)
                return true;
        }
        return false;
    }

    public List<String> getCodeList(int... excludeIds) {
        List<String> result = new ArrayList<String>(elements.size());
        for (Element e : elements) {
            if (!ArrayUtils.contains(excludeIds, e.id))
                result.add(e.code);
        }
        return result;
    }

    private Element getElement(int id) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).id == id)
                return elements.get(i);
        }
        return null;
    }

    public int size() {
        return elements.size();
    }

    public int getId(int index) {
        return elements.get(index).id;
    }

    public List<IntValuePair> getIdKeys(int... excludeIds) {
        List<IntValuePair> result = new ArrayList<IntValuePair>(elements.size());
        for (Element e : elements) {
            if (!ArrayUtils.contains(excludeIds, e.id))
                result.add(new IntValuePair(e.id, e.key));
        }
        return result;
    }

    class Element {
        final int id;
        final String code;
        final String key;

        Element(int id, String code, String key) {
            this.id = id;
            this.code = code;
            this.key = key;
        }
    }
}
