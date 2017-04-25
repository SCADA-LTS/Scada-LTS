package org.scada_lts.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.scada_lts.dao.TestDAO;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;

public class ViewHierarchyServiceTestOnDB extends TestDAO {
	
	@Test
	public void populateViewHierarchyDAOToCheckGetChildFolder() {
		ViewHierarchyDAO vhDAO = new ViewHierarchyDAO();
		ViewDAO vDAO = new ViewDAO();
		
		ViewHierarchyNode vhn1 = new ViewHierarchyNode();
		vhn1.setName("vhn1");
		vhn1.setParentId(ViewHierarchyDAO.ROOT_ID);
		vhn1.setId(vhDAO.add(vhn1));
		
		ViewHierarchyNode vhn2 = new ViewHierarchyNode();
		vhn2.setName("vhn2");
		vhn2.setParentId(vhn1.getId());
		vhn2.setId(vhDAO.add(vhn2));
		
		ViewHierarchyNode vhn3 = new ViewHierarchyNode();
		vhn3.setName("vhn3");
		vhn3.setParentId(vhn1.getId());
		vhn3.setId(vhDAO.add(vhn3));
		
		ViewHierarchyService vhs = new ViewHierarchyService(vhDAO, vDAO);
		
		assertTrue(vhs.getAll().size()==1);
	    assertTrue(vhs.getNode(1).size()==2);
	    assertTrue(vhs.getNode(2).size()==0);
	    assertTrue(vhs.getNode(3).size()==0);
	    assertTrue(vhs.getNode(ViewHierarchyDAO.ROOT_ID).size()==1);
		
	}

}
