package org.scada_lts.mock_dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.service.model.ViewHierarchyJSON;

public class ViewHierarchyDaoMockito {
	
	public static ViewHierarchyDAO populateViewHierarchyDAOToCheckViewsWithOutFolder() {
		ViewHierarchyDAO vHierarchyDAO = mock(ViewHierarchyDAO.class);
		List<ViewHierarchyNode> vhl = new ArrayList<ViewHierarchyNode>();
		
		when(vHierarchyDAO.getAll()).thenReturn(vhl);
		return vHierarchyDAO;
	}
	

}
