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

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.PointLinkDAO;
import org.scada_lts.mango.adapter.MangoPointLink;

import java.util.List;

/**
 * PointLinkService
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PointLinkService implements MangoPointLink {

	private static PointLinkDAO pointLinkDAO = new PointLinkDAO();

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(PointLinkVO.XID_PREFIX, "pointLinks");
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "pointLinks");
	}

	@Override
	public List<PointLinkVO> getPointLinks() {
		return pointLinkDAO.getPointLinks();
	}

	@Override
	public List<PointLinkVO> getPointLinksForPoint(int dataPointId) {
		return pointLinkDAO.getPointLinksForPoint(dataPointId);
	}

	@Override
	public PointLinkVO getPointLink(int id) {
		return pointLinkDAO.getPointLink(id);
	}

	@Override
	public PointLinkVO getPointLink(String xid) {
		return pointLinkDAO.getPointLink(xid);
	}

	@Override
	public void savePointLink(PointLinkVO pl) {
		if (pl.getId() == Common.NEW_ID) {
			pl.setId(pointLinkDAO.insert(pl));
			AuditEventType.raiseAddedEvent(AuditEventType.TYPE_POINT_LINK, pl);
		} else {
			PointLinkVO oldPl = pointLinkDAO.getPointLink(pl.getId());
			pointLinkDAO.update(pl);
			AuditEventType.raiseChangedEvent(AuditEventType.TYPE_POINT_LINK, oldPl, pl);
		}
	}

	@Override
	public void deletePointLink(final int pointLinkId) {
		PointLinkVO pointLinkVO = pointLinkDAO.getPointLink(pointLinkId);
		if (pointLinkVO != null) {
			pointLinkDAO.delete(pointLinkId);
		}
	}
}
