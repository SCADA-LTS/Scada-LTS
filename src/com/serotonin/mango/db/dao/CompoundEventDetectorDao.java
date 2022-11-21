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
package com.serotonin.mango.db.dao;

import java.util.List;

import org.scada_lts.mango.adapter.MangoCompoundEventDetector;
import org.scada_lts.mango.service.CompoundEventDetectorService;

import com.serotonin.mango.vo.event.CompoundEventDetectorVO;

/**
 * @author Matthew Lohbihler
 * rewrite grzegorz bylica 
 */
public class CompoundEventDetectorDao {
    
	private MangoCompoundEventDetector mced;
	
	public CompoundEventDetectorDao() {
		mced = new CompoundEventDetectorService();
	}
	
    public String generateUniqueXid() {
        return mced.generateUniqueXid();
    }

    public boolean isXidUnique(String xid, int excludeId) {
        return mced.isXidUnique(xid, excludeId);
    }

    public List<CompoundEventDetectorVO> getCompoundEventDetectors() {
    	return mced.getCompoundEventDetectors();
    }

    public CompoundEventDetectorVO getCompoundEventDetector(int id) {
        return mced.getCompoundEventDetector(id);
    }

    public CompoundEventDetectorVO getCompoundEventDetector(String xid) {
        return mced.getCompoundEventDetector(xid);
    }
    
    public void saveCompoundEventDetector(final CompoundEventDetectorVO ced) {
        mced.saveCompoundEventDetector(ced);
    }

    public void deleteCompoundEventDetector(final int compoundEventDetectorId) {
        mced.deleteCompoundEventDetector(compoundEventDetectorId);
    }
}
