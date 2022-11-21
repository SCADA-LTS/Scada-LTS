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

import java.util.Comparator;
import java.util.List;

import org.scada_lts.mango.adapter.MangoPublisher;
import org.scada_lts.mango.service.PublisherService;

import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;

/**
 * @author Matthew Lohbihler
 */
public class PublisherDao {

    MangoPublisher publisherService = new PublisherService();

    public String generateUniqueXid() {
		return publisherService.generateUniqueXid();
    }

    public boolean isXidUnique(String xid, int excludeId) {
		return publisherService.isXidUnique(xid, excludeId);
    }

    public List<PublisherVO<? extends PublishedPointVO>> getPublishers() {
		return publisherService.getPublishers();
    }

    public List<PublisherVO<? extends PublishedPointVO>> getPublishers(Comparator<PublisherVO<?>> comparator) {
		return publisherService.getPublishers(comparator);
    }

    public PublisherVO<? extends PublishedPointVO> getPublisher(int id) {
    	return publisherService.getPublisher(id);
    }

    public PublisherVO<? extends PublishedPointVO> getPublisher(String xid) {
    	return publisherService.getPublisher(xid);
    }

    public void savePublisher(final PublisherVO<? extends PublishedPointVO> vo) {
		publisherService.savePublisher(vo);
    }

    public void deletePublisher(final int publisherId) {
		publisherService.deletePublisher(publisherId);
    }

    //TODO rtdata doesn't
    public Object getPersistentData(int id) {
//        return query("select rtdata from publishers where id=?", new Object[] { id },
//                new GenericResultSetExtractor<Serializable>() {
//                    @Override
//                    public Serializable extractData(ResultSet rs) throws SQLException, DataAccessException {
//                        if (!rs.next())
//                            return null;
//
//                        InputStream is;
//
//                        if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){
//                            Blob blob = rs.getBlob(1);
//                            is = blob.getBinaryStream();
//                            if (blob == null)
//                                return null;
//                        }
//                        else{
//                            is = rs.getBinaryStream(1);
//                            if (is == null)
//                                return null;
//                        }
//
//                        return (Serializable) SerializationHelper.readObjectInContext(is);
//                    }
//                });
		return null;
    }

    public void savePersistentData(int id, Object data) {
//        ejt.update("update publishers set rtdata=? where id=?", new Object[] { SerializationHelper.writeObject(data),
//                id }, new int[] { Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB, Types.INTEGER });
    }
}
