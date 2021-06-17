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

import com.serotonin.mango.vo.link.PointLinkVO;
import org.scada_lts.mango.adapter.MangoPointLink;
import org.scada_lts.mango.service.PointLinkService;

/**
 * @author Matthew Lohbihler
 */
public class PointLinkDao {

    MangoPointLink pointLinkService = new PointLinkService();

    public String generateUniqueXid() {
    	return pointLinkService.generateUniqueXid();
    }

    public boolean isXidUnique(String xid, int excludeId) {
		return pointLinkService.isXidUnique(xid, excludeId);
    }

    public List<PointLinkVO> getPointLinks() {
		return pointLinkService.getPointLinks();
    }

    public List<PointLinkVO> getPointLinksForPoint(int dataPointId) {
		return pointLinkService.getPointLinksForPoint(dataPointId);
    }

    public PointLinkVO getPointLink(int id) {
		return pointLinkService.getPointLink(id);
    }

    public PointLinkVO getPointLink(String xid) {
		return pointLinkService.getPointLink(xid);
    }

    public void savePointLink(final PointLinkVO pl) {
		pointLinkService.savePointLink(pl);
    }

    public void deletePointLink(final int pointLinkId) {
		pointLinkService.deletePointLink(pointLinkId);
    }
}
