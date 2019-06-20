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
package com.serotonin.mango.web.dwr;

import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;

/**
 * @author Matthew Lohbihler
 * 
 */
public class PointHierarchyDwr {
    public PointFolder getPointHierarchy() {
        DataPointDao dataPointDao = new DataPointDao();
        PointHierarchy ph = dataPointDao.getPointHierarchy();
        return ph.getRoot();
    }

    public PointFolder savePointHierarchy(PointFolder rootFolder) {
        new DataPointDao().savePointHierarchy(rootFolder);
        return rootFolder;
    }
}
