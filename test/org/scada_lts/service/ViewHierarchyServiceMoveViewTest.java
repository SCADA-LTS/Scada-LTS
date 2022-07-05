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

public class ViewHierarchyServiceMoveViewTest extends TestDAO {
	
	@Test
	public void checkViewHierarchyDAOToMoveView() {
		
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
		vhn3.setParentId(vhn2.getId());
		vhn3.setId(vhDAO.add(vhn3));
				
		ViewHierarchyService vhs = new ViewHierarchyService(vhDAO, vDAO);
		
		assertTrue(vhs.getAll().size()==1);
	    assertTrue(vhs.getNode(1).size()==1);
	    assertTrue(vhs.getNode(2).size()==1);
	    assertTrue(vhs.getNode(3).size()==0);
	    assertTrue(vhs.getNode(ViewHierarchyDAO.ROOT_ID).size()==1);
	    
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
		vDAO.insertView(v1);
		
		assertTrue(v1.getId()>0);
		
		View v2 = new View();
		v2.setName("view_2");
		v2.setXid("XID_view2");
		v2.setUserId(user.getId());
		vDAO.insertView(v2);
		
		assertTrue(v2.getId()>0);
		
		View v3 = new View();
		v3.setName("view_3");
		v3.setXid("XID_view3");
		v3.setUserId(user.getId());
		vDAO.insertView(v3);
		
		assertTrue(v3.getId()>0);
		
		assertTrue(vhs.getAll().size()==4);
	    assertTrue(vhs.getNode(1).size()==1);
	    assertTrue(vhs.getNode(2).size()==1);
	    assertTrue(vhs.getNode(3).size()==0);
	    //ToDo check why not 4
	    assertTrue(vhs.getNode(ViewHierarchyDAO.ROOT_ID).size()==1);
	    
	    vhs.moveView(v1.getId(), vhn1.getId());
	    vhs.moveView(v2.getId(), vhn1.getId());
	    vhs.moveView(v3.getId(), vhn1.getId());
		
	    //vhs.getNode() result only folder
		assertTrue(vhs.getAll().size()==1);
	    assertTrue(vhs.getNode(vhn1.getId()).size()==1);
	    assertTrue(vhs.getNode(vhn2.getId()).size()==1);
	    assertTrue(vhs.getNode(vhn3.getId()).size()==0);
	    assertTrue(vhs.getNode(ViewHierarchyDAO.ROOT_ID).size()==1);	
	    
	    assertTrue(vhs.getAll().get(0).getChildren().size()==4);	  	    
		
	}
	
	@Test
	public void checkViewHierarchyDAOToMoveViewSecondLevel() {
	
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
		vhn3.setParentId(vhn2.getId());
		vhn3.setId(vhDAO.add(vhn3));
			
		ViewHierarchyService vhs = new ViewHierarchyService(vhDAO, vDAO);
	
		UserDAO uDAO = new UserDAO();
			
		User user = new User();
		user.setAdmin(true);
		user.setUsername("admin");
		user.setPassword("admin");
		user.setEmail("test@t.com");
		
		user.setId(uDAO.insert(user));
		
		View v1 = new View();
		v1.setName("view_1");
		v1.setXid("XID_view1");
		v1.setUserId(user.getId());
		vDAO.insertView(v1);
		
		View v2 = new View();
		v2.setName("view_2");
		v2.setXid("XID_view2");
		v2.setUserId(user.getId());
		vDAO.insertView(v2);
		
		View v3 = new View();
		v3.setName("view_3");
		v3.setXid("XID_view3");
		v3.setUserId(user.getId());
		vDAO.insertView(v3);
	
		vhs.moveView(v1.getId(), vhn2.getId());
		vhs.moveView(v2.getId(), vhn2.getId());
		vhs.moveView(v3.getId(), vhn2.getId());

	  	assertTrue(vhs.getAll().size()==1);
	  	assertTrue(vhs.getNode(vhn1.getId()).size()==1);
	  	assertTrue(vhs.getNode(vhn2.getId()).size()==1);
	  	assertTrue(vhs.getNode(vhn3.getId()).size()==0);
	  	assertTrue(vhs.getNode(ViewHierarchyDAO.ROOT_ID).size()==1);	
	  	assertTrue(vhs.getAll().get(0).getChildren().get(0).getTitle().equals(vhn2.getName()));
	  	//assertTrue(vhs.getAll().get(0).getChildren().get(0).getChildren().size()==3);
	}


}
