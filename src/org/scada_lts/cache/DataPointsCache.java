package org.scada_lts.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scada_lts.dao.DataPointDAO;

import com.serotonin.mango.vo.DataPointVO;

public class DataPointsCache implements IDataPointsCacheWhenStart {
	
	private boolean start = false;
	
	private static DataPointsCache instance = null;
	
	private Map<Integer, List<DataPointVO>> dss = new HashMap<Integer, List<DataPointVO>>();
	
	private DataPointsCache() {
		
		 List<DataPointVO> dps = new DataPointDAO().getDataPoints();
		
		for (DataPointVO dp : dps) {
			List<DataPointVO> cacheDs = dss.get(dp.getDataSourceId()); 
			if (cacheDs==null) {
				cacheDs = new ArrayList<DataPointVO>();
				cacheDs.add(dp);
				dss.put(dp.getDataSourceId(), cacheDs);			
			} else {
				cacheDs.add(dp);
			}
		}
	}
	
	public static DataPointsCache getInstance() {
		if (instance==null) {
			instance = new DataPointsCache();
		}
		return instance;
	}

	@Override
	public List<DataPointVO> getDataPoints(int dataSourceId) {
		if (start) {
			return dss.get(dataSourceId);
		} else {
			throw new RuntimeException("Cache may work only when scada start");
		}
	}

	@Override
	public void cacheInitialize() {
		start = true;
	}

	@Override
	public void cacheFinalized() {
		start = false;
		instance = null;
	}

	public boolean isStart() {
		return start;
	}

}
