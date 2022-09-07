package org.scada_lts.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.scada_lts.dao.TestDAO;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;

import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;

public class ViewHierarchyServiceTestOnDB extends TestDAO {
	
	@Test
	public void checkViewHierarchyDAOToGetChildFolder() {
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
	
	@Test
	public void checkViewHierarchyDAOToGetChildFolderAndViewInRoot() {
		ViewHierarchyDAO vhDAO = new ViewHierarchyDAO();
		ViewDAO vDAO = new ViewDAO();
		
		UserDAO uDAO = new UserDAO();
		
		User user = new User();
		user.setAdmin(true);
		user.setUsername("admin");
		user.setPassword("admin");
		user.setEmail("test@t.com");
		
		user.setId(uDAO.insert(user));
		
		assertTrue(user.getId()>0);
		
		View v1 = new View();
		v1.setName("view_1");
		v1.setXid("XID_view1");
		v1.setUserId(user.getId());
		vDAO.save(v1);
		
		assertTrue(v1.getId()>0);
		
		View v2 = new View();
		v2.setName("view_2");
		v2.setXid("XID_view2");
		v2.setUserId(user.getId());
		vDAO.save(v2);
		
		assertTrue(v2.getId()>0);
		
		View v3 = new View();
		v3.setName("view_3");
		v3.setXid("XID_view3");
		v3.setUserId(user.getId());
		vDAO.save(v3);
		
		assertTrue(v3.getId()>0);
		
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
		
		assertTrue(vhs.getAll().size()==4);
	    assertTrue(vhs.getNode(1).size()==2);
	    assertTrue(vhs.getNode(2).size()==0);
	    assertTrue(vhs.getNode(3).size()==0);
	    assertTrue(vhs.getNode(ViewHierarchyDAO.ROOT_ID).size()==1);	
	    
	}


}
