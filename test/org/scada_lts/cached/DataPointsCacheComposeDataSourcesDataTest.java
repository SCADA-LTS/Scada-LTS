package org.scada_lts.cached;


import com.serotonin.mango.vo.DataPointVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.cache.DataSourcePointsCache;
import org.scada_lts.dao.UserCommentDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class, DataPointService.class, DataSourcePointsCache.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
		"javax.activation.*", "javax.management.*"})
public class DataPointsCacheComposeDataSourcesDataTest {
	
	@Before
	public void init() {
		mockStatic(ApplicationBeans.class);
		UserCommentDAO userCommentDAOMock = mock(UserCommentDAO.class);
		when(ApplicationBeans.getUserCommentDaoBean()).thenReturn(userCommentDAOMock);
		DataSourcePointsCache.getInstance();
	}
	
	@After
	public void finalized() {
		//DataSourcePointsCache.getInstance().cacheFinalized();
	}
	
	@Test
	public void composeDsWithDp() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
		dpvo.setDataSourceId(1);
		
		DataPointVO dpvo1 = TestUtils.newDefaultEmptyDataPointVO();
		dpvo.setDataSourceId(2);
		
		DataPointVO dpvo2 = TestUtils.newDefaultEmptyDataPointVO();
		dpvo.setDataSourceId(2);
		
		lst.add(dpvo);
		lst.add(dpvo1);
		lst.add(dpvo2);
		
		Map<Integer, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==2);
		
	}
	
	@Test
	public void composeDsWithDpOne() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
		dpvo.setDataSourceId(1);
		
		lst.add(dpvo);
		
		Map<Integer, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==1);
		
	}
	
	@Test
	public void composeDsWithDpTwo() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		for (int i=0; i<1000; i++) {
			DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
			dpvo.setDataSourceId(1);
			
			lst.add(dpvo);
		}
		
		Map<Integer, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==1);
		
	}
	
	@Test
	public void composeDsWithDpThree() {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		int countOne = 0;
		for (int i=0; i<1000; i++) {
			DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
			dpvo.setDataSourceId(1);
			
			lst.add(dpvo);
			countOne++;
		}
		
		for (int i=0; i<100;i++) {
			DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
			if (i+1==1) {
				countOne++;
			}
			dpvo.setDataSourceId(i+1);
			
			lst.add(dpvo);
		}
		
		Map<Integer, List<DataPointVO>> map = DataSourcePointsCache.getInstance().composeCashData(lst);
		
		assertTrue(map.size()==100);
		assertTrue(map.get(1).size()==countOne);
		assertTrue(map.get(2).size()==1);
		assertTrue(map.get(100).size()==1);
	
	}

}
