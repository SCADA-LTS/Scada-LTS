package org.scada_lts.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;

public class HierarchyViewDaoTest  extends TestDAO {
	
	
	@Test
	public void getAll() throws Exception {
		List<ViewHierarchyNode> lst = new HierarchyViewDAO().getAll();
		assertEquals(true,  lst.size()==0 );	
	}
	
	@Test
	public void getNode() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new HierarchyViewDAO().add(vhn));
		List<ViewHierarchyNode> lst = new HierarchyViewDAO().getAll();
		
		assertEquals(true,  lst.get(0).equals(vhn) );
		
		List<ViewHierarchyNode> nodes = new HierarchyViewDAO().getNode(-1);
		
		assertEquals(true, nodes.get(0).equals(vhn));
		
	}
	
	@Test 
	public void add() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new HierarchyViewDAO().add(vhn));
		List<ViewHierarchyNode> lst = new HierarchyViewDAO().getAll();
		
		assertEquals(true,  lst.get(0).equals(vhn) );
	}
	
	@Test 
	public void update() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new HierarchyViewDAO().add(vhn));
		List<ViewHierarchyNode> lst = new HierarchyViewDAO().getAll();	
		assertEquals(true,  lst.get(0).equals(vhn) );
		vhn.setName("test1");
		new HierarchyViewDAO().update(vhn);
		lst = new HierarchyViewDAO().getAll();
		assertEquals(true, lst.get(0).getName().equals("test1"));
	}
	
	@Test 
	public void del() throws Exception {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(new HierarchyViewDAO().add(vhn));
		
		List<ViewHierarchyNode> lst = new HierarchyViewDAO().getAll();	
		assertEquals(true,  (lst.size()==1) );
		
		new HierarchyViewDAO().del(vhn.getId());
		lst = new HierarchyViewDAO().getAll();
		
		assertEquals(true, lst.size()==0);
		
	}
	
	
	
	
	

}
