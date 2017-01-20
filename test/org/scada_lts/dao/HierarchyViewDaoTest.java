/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
 
package org.scada_lts.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;

/**
 * DAO for Hierarchy View
 *
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
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
