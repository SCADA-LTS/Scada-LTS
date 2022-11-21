package br.org.scadabr.db.dao.mocks;

import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.DataPointVO;

public class MockDataPointDao extends BaseDao {

	public DataPointVO insertDataPoint(String dpname, int datasourceId) {
		DataPointVO vo = new DataPointVO(LoggingTypes.ON_CHANGE);

		vo.setId(doInsert(
				"insert into dataPoints (xid, dataSourceId, data) values (?, ?, ?)",
				new Object[] { dpname, datasourceId, "" }));

		return vo;
	}

}
