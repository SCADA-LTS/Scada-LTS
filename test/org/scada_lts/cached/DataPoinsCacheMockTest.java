package org.scada_lts.cached;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.scada_lts.cache.IDataPointsCacheWhenStart;

import com.serotonin.mango.vo.DataPointVO;

public class DataPoinsCacheMockTest {
	
	IDataPointsCacheWhenStart cache;
	
	@Before
	public void init() {
		cache = mock(IDataPointsCacheWhenStart.class);
	}
	
	@Test
	public void getData() {
		List<DataPointVO> lst = new ArrayList<DataPointVO>();
		lst.add(new DataPointVO());
		when(cache.getDataPoints(1)).thenReturn(lst);
		
		assertTrue(cache.getDataPoints(1).size()==1);
	}
	
	
	
	

}
