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
package org.scada_lts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.GenericHierarchyDAO;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.dao.model.viewshierarchy.ViewInViewHierarchyNode;
import org.scada_lts.service.model.ViewHierarchyJSON;
import org.springframework.stereotype.Service;

import com.serotonin.mango.view.View;

/** 
 * Service for views hierarchy.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
@Service
public class ViewHierarchyService {
	
	private static final Log LOG = LogFactory.getLog(ViewHierarchyService.class);
	
	private static final boolean FOLDER = true;
	
	public static final int ROOT_ID = -1;
	
	@Resource
	private ViewHierarchyDAO vhDAO;
	
	@Resource
	private ViewDAO viewDAO;
	
	private List<ViewHierarchyJSON> getChildFolder(long l) {
		ArrayList<ViewHierarchyJSON> lst = new ArrayList<ViewHierarchyJSON>();
		
		List<ViewHierarchyNode> lstNodeDb = vhDAO.getNode(l);
		
		for (ViewHierarchyNode vhNode: lstNodeDb) {
			ViewHierarchyJSON vhJSON = createViewHierarchyFolderJSON(vhNode);
			lst.add(vhJSON);
		}
		
		return lst;
	}
	
	private void addViewInNotInViewHierarchy(List<ViewHierarchyJSON> lst, View view,  HashMap<Long, Boolean> tmpViewsInFolder) {
		if (tmpViewsInFolder.containsKey(new Long(view.getId())) == false ) {
			ViewHierarchyJSON vhJSON = new ViewHierarchyJSON();
			vhJSON.setKey(view.getId());
			vhJSON.setChildren(null);
			vhJSON.setFolder(false);
			//TODO cash name views?
			vhJSON.setTitle(view.getName());
			lst.add(vhJSON);
		}
	}
	
	private void correctChildrenViewHierarchyFolderJSON(List<ViewHierarchyJSON> lst, ViewInViewHierarchyNode vhNodeInFolder,  HashMap<Long, Boolean> tmpViewsInFolder) {
		for (ViewHierarchyJSON vhNode:lst){
			if (vhNode.getKey()==vhNodeInFolder.getFolderViewsHierarchyId()){
				ViewHierarchyJSON vhJSON = new ViewHierarchyJSON();
				vhJSON.setKey(vhNodeInFolder.getViewId());
				vhJSON.setChildren(null);
				vhJSON.setFolder(false);
				//TODO cash name views?
				vhJSON.setTitle(viewDAO.findById(new Object[] { vhJSON.getKey() }).getName());
				tmpViewsInFolder.put(vhJSON.getKey(), new Boolean(true));
				vhNode.getChildren().add(vhJSON);
			}	
		}	
	}
	
	private ViewHierarchyJSON createViewHierarchyFolderJSON(ViewHierarchyNode vhNode) {
		
		ViewHierarchyJSON vhJSON = new ViewHierarchyJSON();
		vhJSON.setTitle(vhNode.getName());
		vhJSON.setFolder(FOLDER);
		vhJSON.setKey(vhNode.getId());
		
		// child
		vhJSON.setChildren(getChildFolder( vhJSON.getKey()));
		
		return vhJSON;
	}
	
	public boolean add(ViewHierarchyNode node) {
		LOG.info("add:"+node.toString());
		node.setId(vhDAO.add(node));
		return (node.getId() > GenericHierarchyDAO.ERROR);
	}
	
	public boolean edt(ViewHierarchyNode node) {
		LOG.info("edt:"+node.toString());
		return (vhDAO.update(node)> GenericHierarchyDAO.ERROR);
	}
	
	public boolean del(int id) {
		LOG.info("delete id:"+id);
		return (vhDAO.del(id) > GenericHierarchyDAO.ERROR);
	}
	
	public boolean move(int id, int newParentId) {
		LOG.info("move id:"+id+" newParentId:"+newParentId);
		return (vhDAO.move(id, newParentId)>GenericHierarchyDAO.ERROR);
	}
	
	//TODO (userId profileId)
	public List<ViewHierarchyJSON> getAll(){
		
		List<ViewHierarchyNode> lstNodeDb = vhDAO.getNode(ViewHierarchyDAO.ROOT_ID);
		List<ViewHierarchyJSON> lst = new ArrayList<ViewHierarchyJSON>();
		
		for (ViewHierarchyNode vhNode: lstNodeDb) {
			ViewHierarchyJSON vhJSON = createViewHierarchyFolderJSON(vhNode);
			lst.add(vhJSON);
		}
		
		HashMap<Long,Boolean> tmpViewInFolders = new HashMap<Long,Boolean>();
		
		// add views to folder
		List<ViewInViewHierarchyNode> lstViewsInFolders = vhDAO.getViewInHierarchyNode();
		for (ViewInViewHierarchyNode vhNodeInFolder: lstViewsInFolders) {
			correctChildrenViewHierarchyFolderJSON(lst, vhNodeInFolder,tmpViewInFolders);
		}
		
		// add views with not have folder
		List<View> lstView= viewDAO.findAll();
		for (View view: lstView) {
			addViewInNotInViewHierarchy(lst, view, tmpViewInFolders);
		}
				
		return lst;	
	}

	public List<ViewHierarchyNode> getNode(int id) {
		return vhDAO.getNode(id);
	}
	
	public ViewHierarchyJSON getFirstViewId(){
		
		List<View> lstView= viewDAO.findAll();
		if (lstView != null) {
			if (lstView.get(0) != null ) {
				ViewHierarchyJSON vhJSON = new ViewHierarchyJSON();
				vhJSON.setKey(lstView.get(0).getId());
				vhJSON.setTitle(lstView.get(0).getName());
				vhJSON.setFolder(false);
				return vhJSON;
			}
		}
		return null;
	}
	
	public boolean isUsedName(String name){
		return vhDAO.isNameUsed(name);
	}
	
}
