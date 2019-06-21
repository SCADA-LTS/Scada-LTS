package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.DataPointVO;

import static org.scada_lts.dao.SharedSQLs.INSERT_INTO_DATAPOINTS;

public class MockDataPointDao extends BaseDao {

	public DataPointVO insertDataPoint(String dpname, int datasourceId) {
		DataPointVO vo = new DataPointVO();

		vo.setId(doInsert(
				INSERT_INTO_DATAPOINTS+" values (?, ?, ?)",
				new Object[] { dpname, datasourceId, "" }));

		return vo;
	}

}
