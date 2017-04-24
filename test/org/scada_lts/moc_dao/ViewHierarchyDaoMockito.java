package org.scada_lts.moc_dao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;

public class ViewHierarchyDaoMockito {
	
	public static ViewHierarchyDAO populateViewHierarchyDAOToCheckViewsWithOutFolder() {
		ViewHierarchyDAO vHierarchyDAO = mock(ViewHierarchyDAO.class);
		List<ViewHierarchyNode> vhl = new ArrayList<ViewHierarchyNode>();
		
		when(vHierarchyDAO.getAll()).thenReturn(vhl);
		return vHierarchyDAO;
	}
	
	public ViewDAO pupulateViewDAOToCheckViewsWithOutFolder() {
		/*ViewDAO vDAO = mock(ViewDAO.class);
		List<> vDAO = new ArrayList<>();
		
		when(vHierarchyDAO.getAll()).thenReturn(vhn);*/
		return null;
	}

}
