/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.mango.service;

import java.util.List;

import com.serotonin.mango.rt.event.type.AuditEventUtils;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.event.CompoundEventDetectorDAO;
import org.scada_lts.mango.adapter.MangoCompoundEventDetector;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;

/**
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class CompoundEventDetectorService implements MangoCompoundEventDetector {
	
	private CompoundEventDetectorDAO cedDao;
	
	public CompoundEventDetectorService() {
		cedDao = new CompoundEventDetectorDAO();
	}
	
	@Override
	public String generateUniqueXid() {
        return DAO.getInstance().generateUniqueXid(CompoundEventDetectorVO.XID_PREFIX, "compoundEventDetectors");
    }

    @Override
	public boolean isXidUnique(String xid, int excludeId) {
        return DAO.getInstance().isXidUnique(xid, excludeId, "compoundEventDetectors");
    }

    @Override
	public List<CompoundEventDetectorVO> getCompoundEventDetectors() {
        return cedDao.findAll();
    }

    @Override
	public CompoundEventDetectorVO getCompoundEventDetector(int id) {
        return cedDao.findById(new Object[]{id});
    }

    @Override
	public CompoundEventDetectorVO getCompoundEventDetector(String xid) {
        return cedDao.findByXId(new Object[]{xid});
    }
    
    @Override
	public void saveCompoundEventDetector(final CompoundEventDetectorVO ced) {
    	 if (ced.getId() == Common.NEW_ID) {
             cedDao.create(ced);
             AuditEventUtils.raiseAddedEvent(AuditEventType.TYPE_COMPOUND_EVENT_DETECTOR, ced);
    	 } else {
             CompoundEventDetectorVO oldCed = cedDao.findById(new Object[]{ced.getId()});
             cedDao.update(ced);
             AuditEventUtils.raiseChangedEvent(AuditEventType.TYPE_COMPOUND_EVENT_DETECTOR, oldCed, ced);
    	 }
    }
    
    @Override
	public void deleteCompoundEventDetector(final int compoundEventDetectorId) {
    	CompoundEventDetectorVO compoundEventDetectorVO = cedDao.findById(new Object[]{compoundEventDetectorId});
    	cedDao.delete(compoundEventDetectorVO);
    }
    
}
