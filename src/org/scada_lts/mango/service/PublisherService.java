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
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.PublisherDAO;
import org.scada_lts.mango.adapter.MangoPublisher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * PublisherDAO service
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PublisherService implements MangoPublisher {

	private PublisherDAO publisherDAO = new PublisherDAO();

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(PublisherVO.XID_PREFIX, "publishers");
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "publishers");
	}

	@Override
	public List<PublisherVO<? extends PublishedPointVO>> getPublishers() {
		return publisherDAO.getPublishers();
	}

	@Override
	public List<PublisherVO<? extends PublishedPointVO>> getPublishers(Comparator<PublisherVO<?>> comparator) {
		List<PublisherVO<? extends  PublishedPointVO>> publisherList = getPublishers();
		Collections.sort(publisherList, comparator);
		return publisherList;
	}

	@Override
	public PublisherVO<? extends PublishedPointVO> getPublisher(int id) {
		return publisherDAO.getPublisher(id);
	}

	@Override
	public PublisherVO<? extends PublishedPointVO> getPublisher(String xid) {
		return publisherDAO.getPublisher(xid);
	}

	@Override
	public void savePublisher(PublisherVO<? extends PublishedPointVO> publisher) {
		if (publisher.getId() == Common.NEW_ID) {
			publisher.setId(publisherDAO.insert(publisher));
		} else {
			publisherDAO.update(publisher);
		}
	}

	@Override
	public void deletePublisher(int publisherId) {
		publisherDAO.delete(publisherId);
	}

	public static class PublisherNameComparator implements Comparator<PublisherVO<?>> {
       	 public int compare(PublisherVO<?> p1, PublisherVO<?> p2) {
			if (p1.getName() == null || p1.getName().trim().length() == 0) {
				return -1;
			}
         	return p1.getName().compareTo(p2.getName());
        }
    }
}
