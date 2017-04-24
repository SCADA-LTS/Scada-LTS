package org.scada_lts.service;

import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.moc_dao.ViewHierarchyDaoMockito;

import junit.framework.TestCase;

public class ViewHierarchyServiceTest extends TestCase {
	
	public void test() {
		ViewHierarchyDAO vhd = ViewHierarchyDaoMockito.populateViewHierarchyDAOToCheckViewsWithOutFolder();
		assertTrue(vhd.getAll().size()==0);
		assertTrue(true);
	}

}
