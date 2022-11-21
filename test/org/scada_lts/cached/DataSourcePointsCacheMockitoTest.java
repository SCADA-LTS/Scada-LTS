package org.scada_lts.cached;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.cache.IDataPointsCacheWhenStart;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataSourcePointsCacheMockitoTest {
	
	IDataPointsCacheWhenStart cache;
	
	@Before
	public void init() {
		cache = mock(IDataPointsCacheWhenStart.class);
	}
	
	@Test
	public void dataPoints() {
		List<DataPointVO> dps = new ArrayList<DataPointVO>();
		dps.add(new DataPointVO(LoggingTypes.ON_CHANGE));
		
		when(cache.getDataPoints(1L)).thenReturn(dps);
		
		assertTrue(dps.size()==1);
		
	}

}
