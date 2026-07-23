package org.scada_lts.cache;

import java.util.List;

import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.beans.ApplicationBeans;

public interface DataSourcePointsCache {

	List<DataPointVO> getDataPoints(int dataSourceId);
	void cacheInitialize();
	void cacheDestroy();
	void cacheFinalized();
	DataPointVO getDataPoint(int dataPointId);
	DataPointVO getDataPoint(String dataPointXid);
	int size();
	boolean isCacheEnabled();

	static DataSourcePointsCache getInstance() {
		return ApplicationBeans.getDataSourcePointsCacheBean();
	}
}
