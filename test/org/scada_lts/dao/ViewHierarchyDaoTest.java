package org.scada_lts.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;

public class ViewHierarchyDaoTest extends TestDAO {
	
	@Test
	public void getAll() throws Exception {
		List<ViewHierarchyNode> lst = new ViewHierarchyDAO().getAll();
		assertEquals(true,  lst.size()==0 );	
	}
	
	@Test
	public void getNode() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new ViewHierarchyDAO().add(vhn));
		List<ViewHierarchyNode> lst = new ViewHierarchyDAO().getAll();
		
		assertEquals(true,  lst.get(0).equals(vhn) );
		
		List<ViewHierarchyNode> nodes = new ViewHierarchyDAO().getNode(-1);
		
		assertEquals(true, nodes.get(0).equals(vhn));
		
	}
	
	@Test 
	public void add() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new ViewHierarchyDAO().add(vhn));
		List<ViewHierarchyNode> lst = new ViewHierarchyDAO().getAll();
		
		assertEquals(true,  lst.get(0).equals(vhn) );
	}
	
	@Test 
	public void update() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new ViewHierarchyDAO().add(vhn));
		List<ViewHierarchyNode> lst = new ViewHierarchyDAO().getAll();	
		assertEquals(true,  lst.get(0).equals(vhn) );
		vhn.setName("test1");
		new ViewHierarchyDAO().update(vhn);
		lst = new ViewHierarchyDAO().getAll();
		assertEquals(true, lst.get(0).getName().equals("test1"));
	}
	
	/*@Test 
	public void del() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new ViewHierarchyDAO().add(vhn));
		
		List<ViewHierarchyNode> lst = new ViewHierarchyDAO().getAll();	
		assertEquals(true,  (lst.size()==1) );
		
		new ViewHierarchyDAO().del(vhn.getId());
		lst = new ViewHierarchyDAO().getAll();
		
		assertEquals(true, lst.size()==0);
		
	}*/

}
