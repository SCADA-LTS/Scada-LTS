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
	
	private Map<Long, List<DataPointVO>> dss = new HashMap<Long, List<DataPointVO>>();
	
	private DataPointsCache() {
		
	}
	
	public static DataPointsCache getInstance() {
		if (instance==null) {
			instance = new DataPointsCache();
		}
		return instance;
	}

	@Override
	public List<DataPointVO> getDataPoints(Long dataSourceId) {
		if (start) {
			return dss.get(dataSourceId);
		} else {
			throw new RuntimeException("Cache may work only when scada start");
		}
	}

	@Override
	public void cacheFinalized() {
		start = false;
		instance = null;
	}

	public boolean isStart() {
		return start;
	}

	@Override
	public void cacheInitialize() {
		
		List<DataPointVO> dps = new DataPointDAO().getDataPoints();
		
		dss = composeCashData(dps);
		
		start = true;
	}
	
	public Map<Long, List<DataPointVO>> composeCashData(List<DataPointVO> dps) {
		
		Map<Long, List<DataPointVO>> dss = new HashMap<Long, List<DataPointVO>>();
		if (dps != null && dps.size()>0) {
			for (DataPointVO dp : dps) {
				List<DataPointVO> cacheDs = dss.get((long)dp.getDataSourceId()); 
				if (cacheDs==null) {
					cacheDs = new ArrayList<DataPointVO>();
					cacheDs.add(dp);
					dss.put((long) dp.getDataSourceId(), cacheDs);			
				} else {
					cacheDs.add(dp);
				}
			}
		}
		
		return dss;
		
		
	}

}
