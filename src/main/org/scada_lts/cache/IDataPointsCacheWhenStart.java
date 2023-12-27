package org.scada_lts.cache;

import java.util.List;

import com.serotonin.mango.vo.DataPointVO;

public interface IDataPointsCacheWhenStart {
	
	List<DataPointVO> getDataPoints(Long dataSourceId);
	
	void cacheInitialize();
	 
	void cacheFinalized();
	
	
	
}
