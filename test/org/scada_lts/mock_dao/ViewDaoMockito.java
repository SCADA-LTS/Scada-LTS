package org.scada_lts.mock_dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.scada_lts.dao.IViewDAO;

import com.serotonin.mango.view.View;

public class ViewDaoMockito {
	
	public static IViewDAO pupulateViewDAOToCheckViewsWithOutFolder() {
		IViewDAO vDAO = mock(IViewDAO.class);
		List<View> vl = new ArrayList<>();
		View v1 = new View();
		v1.setId(1);
		v1.setName("test1");
		
		View v2 = new View();
		v2.setId(2);
		v2.setName("test2");
		
		vl.add(v1);
		vl.add(v2);
		when(vDAO.findAll()).thenReturn(vl);
		
		return vDAO;
	}

}
