package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.dao.DAO;

public class MockDataPointDao extends BaseDao {

	public DataPointVO insertDataPoint(String dpname, int datasourceId) {
		DataPointVO vo = new DataPointVO();

		DAO.getInstance().getJdbcTemp().update("insert into dataPoints (xid, dataSourceId, data) values (?, ?, ?)", dpname, datasourceId, "");
		vo.setId(DAO.getInstance().getId());

		return vo;
	}

}
