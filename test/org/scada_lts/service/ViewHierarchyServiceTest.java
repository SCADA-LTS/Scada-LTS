package org.scada_lts.service;

import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.mock_dao.ViewDaoMockito;
import org.scada_lts.mock_dao.ViewHierarchyDaoMockito;

import junit.framework.TestCase;

public class ViewHierarchyServiceTest extends TestCase {
	
	public void testGetAllWithOnlyView() {
		ViewHierarchyDAO vhd = ViewHierarchyDaoMockito.populateViewHierarchyDAOToCheckViewsWithOutFolder();
		IViewDAO vd = ViewDaoMockito.pupulateViewDAOToCheckViewsWithOutFolder();
		ViewHierarchyService vhs = new ViewHierarchyService(vhd, vd);

	    assertTrue(vhs.getAll().size()==2);
		
	}

}
