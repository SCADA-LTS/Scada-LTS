package org.scada_lts.cache;

import java.util.List;

import com.serotonin.mango.vo.DataPointVO;

public interface IDataPointCacheWhenStart {
	
	final static Boolean START = new Boolean(false);
	
	List<DataPointVO> getDataPoint();
	
	void cacheInitialize();
	 
	void cacheFinalized();
	

}
