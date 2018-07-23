package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.util.SerializationHelper;

public class MockDataSourceDao extends BaseDao {

	public DataSourceVO insertDataSource(String dsname) {
		VirtualDataSourceVO vo = new VirtualDataSourceVO();

		vo.setId(doInsert(
				"insert into dataSources (xid, name, dataSourceType, data) values (?,?,?,?)",
				new Object[] { dsname, dsname, vo.getType().getId(),
						SerializationHelper.writeObjectToArray(vo) }));

		return vo;
	}

}
