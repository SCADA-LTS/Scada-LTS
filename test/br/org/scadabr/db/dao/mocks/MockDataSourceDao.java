package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import com.serotonin.util.SerializationHelper;
import org.scada_lts.dao.DAO;

public class MockDataSourceDao extends BaseDao {

	public DataSourceVO insertDataSource(String dsname) {
		VirtualDataSourceVO vo = new VirtualDataSourceVO();


		DAO.getInstance().getJdbcTemp().update("insert into dataSources (xid, name, dataSourceType, data) values (?,?,?,?)", dsname, dsname, vo.getType().getId(),
				SerializationHelper.writeObjectToArray(vo));

		vo.setId(DAO.getInstance().getId());


		return vo;
	}

}
