package org.scada_lts.cached;


import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.cache.DataSourcePointsCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class DataPointsCacheComposeDataSourcesDataTest {
	
	@Before
	public void init() {
		DataSourcePointsCache.getInstance();
	}
	
	@After
	public void finalized() {
		//DataSourcePointsCache.getInstance().cacheFinalized();
	}
	
	@Test
	public void composeDsWithDp() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		DataPointVO dpvo = new DataPointVO(LoggingTypes.ON_CHANGE);
		dpvo.setDataSourceId(1);
		
		DataPointVO dpvo1 = new DataPointVO(LoggingTypes.ON_CHANGE);
		dpvo.setDataSourceId(2);
		
		DataPointVO dpvo2 = new DataPointVO(LoggingTypes.ON_CHANGE);
		dpvo.setDataSourceId(2);
		
		lst.add(dpvo);
		lst.add(dpvo1);
		lst.add(dpvo2);
		
		Map<Long, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==2);
		
	}
	
	@Test
	public void composeDsWithDpOne() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		DataPointVO dpvo = new DataPointVO(LoggingTypes.ON_CHANGE);
		dpvo.setDataSourceId(1);
		
		lst.add(dpvo);
		
		Map<Long, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==1);
		
	}
	
	@Test
	public void composeDsWithDpTwo() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		for (int i=0; i<1000; i++) {
			DataPointVO dpvo = new DataPointVO(LoggingTypes.ON_CHANGE);
			dpvo.setDataSourceId(1);
			
			lst.add(dpvo);
		}
		
		Map<Long, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==1);
		
	}
	
	@Test
	public void composeDsWithDpThree() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		int countOne = 0;
		for (int i=0; i<1000; i++) {
			DataPointVO dpvo = new DataPointVO(LoggingTypes.ON_CHANGE);
			dpvo.setDataSourceId(1);
			
			lst.add(dpvo);
			countOne++;
		}
		
		for (int i=0; i<100;i++) {
			DataPointVO dpvo = new DataPointVO(LoggingTypes.ON_CHANGE);
			if (i+1==1) {
				countOne++;
			}
			dpvo.setDataSourceId(i+1);
			
			lst.add(dpvo);
		}
		
		Map<Long, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==100);
		assertTrue(map.get(1L).size()==countOne);
		assertTrue(map.get(2L).size()==1);
		assertTrue(map.get(100L).size()==1);
	
	}

}
