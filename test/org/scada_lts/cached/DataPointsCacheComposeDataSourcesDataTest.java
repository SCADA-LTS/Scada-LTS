package org.scada_lts.cached;


import com.serotonin.mango.vo.DataPointVO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.cache.DataPointsCacheWhenStart;
import org.scada_lts.cache.DataSourcePointsCache;
import org.scada_lts.dao.UserCommentDAO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationBeans.class, DataPointService.class, DataPointsCacheWhenStart.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
		"javax.activation.*", "javax.management.*"})
public class DataPointsCacheComposeDataSourcesDataTest {

	private DataPointService dataPointServiceMock;
	
	@Before
	public void init() throws Exception {
		mockStatic(ApplicationBeans.class);
		UserCommentDAO userCommentDAOMock = mock(UserCommentDAO.class);
		when(ApplicationBeans.getUserCommentDaoBean()).thenReturn(userCommentDAOMock);
		DataSourcePointsCache.getInstance();


		dataPointServiceMock = mock(DataPointService.class);
		whenNew(DataPointService.class).withNoArguments().thenReturn(dataPointServiceMock);

		mockStatic(ApplicationBeans.class);
		DataSourcePointsCache cache = new DataPointsCacheWhenStart();
		when(ApplicationBeans.getDataSourcePointsCacheBean()).thenReturn(cache);
	}
	
	@After
	public void finalized() {
		//DataSourcePointsCache.getInstance().cacheFinalized();
	}

	
	@Test
	public void composeDsWithDp() throws Exception {
		
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
		when(dataPointServiceMock.getDataPoints(any(), anyBoolean())).thenReturn(lst);

		DataSourcePointsCache dataSourcePointsCache = DataSourcePointsCache.getInstance();
		dataSourcePointsCache.cacheInitialize();

		Assert.assertEquals(2, dataSourcePointsCache.size());
		
	}
	
	@Test
	public void composeDsWithDpOne() throws Exception {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
		dpvo.setDataSourceId(1);
		
		lst.add(dpvo);
		when(dataPointServiceMock.getDataPoints(any(), anyBoolean())).thenReturn(lst);

		DataSourcePointsCache dataSourcePointsCache = DataSourcePointsCache.getInstance();
		dataSourcePointsCache.cacheInitialize();

		Assert.assertEquals(1, dataSourcePointsCache.size());
	}
	
	@Test
	public void composeDsWithDpTwo() throws Exception {
		
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		
		for (int i=0; i<1000; i++) {
			DataPointVO dpvo = TestUtils.newDefaultEmptyDataPointVO();
			dpvo.setDataSourceId(1);
			
			lst.add(dpvo);
		}
		when(dataPointServiceMock.getDataPoints(any(), anyBoolean())).thenReturn(lst);

		DataSourcePointsCache dataSourcePointsCache = DataSourcePointsCache.getInstance();
		dataSourcePointsCache.cacheInitialize();

		Assert.assertEquals(1, dataSourcePointsCache.size());
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

		when(dataPointServiceMock.getDataPoints(any(), anyBoolean())).thenReturn(lst);

		DataSourcePointsCache dataSourcePointsCache = DataSourcePointsCache.getInstance();
		dataSourcePointsCache.cacheInitialize();

		Assert.assertEquals(100, dataSourcePointsCache.size());
		Assert.assertEquals(countOne, dataSourcePointsCache.getDataPoints(1).size());
		Assert.assertEquals(1, dataSourcePointsCache.getDataPoints(2).size());
		Assert.assertEquals(1, dataSourcePointsCache.getDataPoints(100).size());
	}

}
