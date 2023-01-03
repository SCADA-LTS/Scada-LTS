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

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.ResourceBundle;

import org.scada_lts.dao.impl.DAO;
import org.scada_lts.mango.adapter.MangoDataSource;
import org.scada_lts.mango.service.DataSourceService;
import org.springframework.dao.DataAccessException;

import com.serotonin.db.spring.GenericResultSetExtractor;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.util.SerializationHelper;

public class DataSourceDao {

	private MangoDataSource dataSourceService = new DataSourceService();

	public List<DataSourceVO<?>> getDataSources() {
		return dataSourceService.getDataSources();
	}

	public DataSourceVO<?> getDataSource(int id) {
		return dataSourceService.getDataSource(id);
	}

	public DataSourceVO<?> getDataSource(String xid) {
		return dataSourceService.getDataSource(xid);
	}

	public String generateUniqueXid() {
		return dataSourceService.generateUniqueXid();
	}

	public boolean isXidUnique(String xid, int excludeId) {
		return dataSourceService.isXidUnique(xid, excludeId);
	}

	public void saveDataSource(final DataSourceVO<?> vo) {
		dataSourceService.saveDataSource(vo);
	}

	public void deleteDataSource(final int dataSourceId) {
		dataSourceService.deleteDataSource(dataSourceId);
	}

	public int copyDataSource(final int dataSourceId, final ResourceBundle bundle) {
		return dataSourceService.copyDataSource(dataSourceId, bundle);
	}

	public Object getPersistentData(int id) {
		return DAO.getInstance().getJdbcTemp().query("select rtdata from dataSources where id=?",
				new Object[] { id },
				new GenericResultSetExtractor<Serializable>() {
					@Override
					public Serializable extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (!rs.next())
							return null;

						InputStream is;

						if (Common.getEnvironmentProfile().getString("db.type")
								.equals("postgres")) {
							Blob blob = rs.getBlob(1);
							is = blob.getBinaryStream();
							if (blob == null)
								return null;
						} else {
							is = rs.getBinaryStream(1);
							if (is == null)
								return null;
						}

						return (Serializable) SerializationHelper
								.readObjectInContext(is);
					}
				});
	}

	public void savePersistentData(int id, Object data) {
		DAO.getInstance().getJdbcTemp().update(
				"update dataSources set rtdata=? where id=?",
				new Object[] { SerializationHelper.writeObject(data), id },
				new int[] {
						Common.getEnvironmentProfile().getString("db.type")
								.equals("postgres") ? Types.BINARY : Types.BLOB,
						Types.INTEGER });
	}
}
