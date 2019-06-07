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
package org.scada_lts.mango.adapter;

import com.serotonin.mango.vo.link.PointLinkVO;

import java.util.List;

/**
 * PointLinkService adapter
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public interface MangoPointLink {

	String generateUniqueXid();

	boolean isXidUnique(String xid, int excludeId);

	List<PointLinkVO> getPointLinks();

	List<PointLinkVO> getPointLinksForPoint(int dataPointId);

	PointLinkVO getPointLink(int id);

	PointLinkVO getPointLink(String xid);

	void savePointLink(final PointLinkVO pl);

	void deletePointLink(final int pointLinkId);
}
