package org.scada_lts.cached;

import com.serotonin.mango.vo.DataPointVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.cache.DataPointsCacheWhenStart;
import org.scada_lts.cache.DataSourcePointsCache;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

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
public class DataSourcePointsCacheMockitoTest {

	private DataPointService dataPointServiceMock;
	
	@Before
	public void init() throws Exception {

		dataPointServiceMock = mock(DataPointService.class);
		whenNew(DataPointService.class).withNoArguments().thenReturn(dataPointServiceMock);

		mockStatic(ApplicationBeans.class);
		DataSourcePointsCache cache = new DataPointsCacheWhenStart();
		when(ApplicationBeans.getDataSourcePointsCacheBean()).thenReturn(cache);
	}
	
	@Test
	public void dataPoints() {
		List<DataPointVO> dps = new ArrayList<DataPointVO>();
		dps.add(TestUtils.newDefaultEmptyDataPointVO());

		when(dataPointServiceMock.getDataPoints(any(), anyBoolean())).thenReturn(dps);

		DataSourcePointsCache dataSourcePointsCache = DataSourcePointsCache.getInstance();
		dataSourcePointsCache.cacheInitialize();

		Assert.assertEquals(1, dataSourcePointsCache.size());
		
	}

}
