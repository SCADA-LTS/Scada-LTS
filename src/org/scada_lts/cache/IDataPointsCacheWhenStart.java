package org.scada_lts.cache;

import java.util.List;

import com.serotonin.mango.vo.DataPointVO;

public interface IDataPointsCacheWhenStart {

	@Deprecated(since = "2.8.1")
	List<DataPointVO> getDataPoints(Long dataSourceId);

	List<DataPointVO> getDataPoints(int dataSourceId);
	
	void cacheInitialize();
	 
	void cacheFinalized();

	DataPointVO getDataPoint(int dataPointId);

	DataPointVO getDataPoint(String dataPointXid);
	
}
