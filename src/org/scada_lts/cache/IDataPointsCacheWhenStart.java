package org.scada_lts.cache;

import java.util.List;

import com.serotonin.mango.vo.DataPointVO;

public interface IDataPointsCacheWhenStart {
	
	List<DataPointVO> getDataPoints(int dataSourceId);
	
	void cacheInitialize();
	 
	void cacheFinalized();
	

}
