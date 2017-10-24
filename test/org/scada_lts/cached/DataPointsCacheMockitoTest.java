package org.scada_lts.cached;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.cache.IDataPointsCacheWhenStart;

import com.serotonin.mango.vo.DataPointVO;

@RunWith(JUnit4.class)
public class DataPointsCacheMockitoTest {
	
	IDataPointsCacheWhenStart cache;
	
	@Before
	public void init() {
		cache = mock(IDataPointsCacheWhenStart.class);
	}
	
	@Test
	public void dataPoints() {
		List<DataPointVO> dps = new ArrayList<DataPointVO>();
		dps.add(new DataPointVO());
		
		when(cache.getDataPoints(1)).thenReturn(dps);
		
		assertTrue(dps.size()==1);
		
	}

}
