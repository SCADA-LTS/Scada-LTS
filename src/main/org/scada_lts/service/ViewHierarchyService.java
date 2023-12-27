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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.GenericHierarchyDAO;
import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.dao.model.viewshierarchy.ViewInViewHierarchyNode;
import org.scada_lts.service.model.ViewHierarchyJSON;
import org.scada_lts.web.beans.ApplicationBeans;
import org.slf4j.profiler.Profiler;
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
	private Profiler profiler = new Profiler("ViewHierarchyService");
	
	private static final boolean FOLDER = true;
	
	public static final int ROOT_ID = -1;
	
	
	private ViewHierarchyDAO vhDAO = new ViewHierarchyDAO();
	
	private final IViewDAO viewDAO;
	
	public ViewHierarchyService(){
		this.viewDAO = ApplicationBeans.getViewDaoBean();
	}
	
	public ViewHierarchyService(ViewHierarchyDAO vhDAO, IViewDAO viewDAO){
		this.vhDAO = vhDAO;
		this.viewDAO = viewDAO;
	}
	
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
			if (vhNode.isFolder()) {
				if (vhNode.getKey()==vhNodeInFolder.getFolderViewsHierarchyId()){
					ViewHierarchyJSON vhJSON = new ViewHierarchyJSON();
					vhJSON.setKey(vhNodeInFolder.getViewId());
					vhJSON.setChildren(null);
					vhJSON.setFolder(false);
					profiler.start("Get info view");
					vhJSON.setTitle(viewDAO.findById((int)vhJSON.getKey()).getName());
					profiler.stop();
					LOG.info("Started "+getClass().getSimpleName()+" in "+ getClass().getSimpleName() +"ms "+ profiler.elapsedTime() / 1000 / 1000);
					tmpViewsInFolder.put(vhJSON.getKey(), new Boolean(true));
					if (vhNode.getChildren() == null) {
						vhNode.setChildren(new ArrayList<ViewHierarchyJSON>());
					}
					vhNode.getChildren().add(vhJSON);
				}
				if (vhNode.getChildren() != null && vhNode.getChildren().size()>0) {
					correctChildrenViewHierarchyFolderJSON( vhNode.getChildren(), vhNodeInFolder,  tmpViewsInFolder);
				}
			}
		}
		
	}
	
	public ViewHierarchyJSON createViewHierarchyFolderJSON(ViewHierarchyNode vhNode) {
		
		ViewHierarchyJSON vhJSON = new ViewHierarchyJSON();
		vhJSON.setTitle(vhNode.getName());
		vhJSON.setFolder(FOLDER);
		vhJSON.setKey(vhNode.getId());
		
		// child
		vhJSON.setChildren(getChildFolder( vhJSON.getKey()));
		
		return vhJSON;
	}
	
	public void add(ViewHierarchyNode node) {
		LOG.info("add:"+node.toString());
		node.setId(vhDAO.add(node));
	}
	
	public boolean edt(ViewHierarchyNode node) {
		LOG.info("edt:"+node.toString());
		return (vhDAO.update(node)> GenericHierarchyDAO.ERROR);
	}
	
	public boolean delFolder(int id) {
		LOG.info("delete folder from view hierarchy base on id:"+id);
		return (vhDAO.delFolder(id) > GenericHierarchyDAO.ERROR);
	}
	
	public boolean delView(int id) {
		LOG.info("delete view from view hierarchy base on id:"+id);
		return (vhDAO.delView(id) > GenericHierarchyDAO.ERROR);
	}
	
	public boolean moveFolder(int id, int newParentId) {
		LOG.info("move id:"+id+" newParentId:"+newParentId);
		return (vhDAO.moveFolder(id, newParentId)>GenericHierarchyDAO.ERROR);
	}
	
	public boolean moveView(int id, int newParentId) {
		if (newParentId == ViewHierarchyService.ROOT_ID) {
		   LOG.info("move id:"+id+" newParentId:"+newParentId);
		   return (vhDAO.delView(id)>GenericHierarchyDAO.ERROR);
		} else {
		   LOG.info("move id:"+id+" newParentId:"+newParentId);
		   return (vhDAO.moveView(id, newParentId)>GenericHierarchyDAO.ERROR);
		}
	}
	
	//TODO (userId profileId)
	public List<ViewHierarchyJSON> getAll(){
		profiler.start("correctChildrenViewHierarchyFolderJSON");
		
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
		profiler.stop();
		LOG.info("Started "+getClass().getSimpleName()+" in "+ getClass().getSimpleName() +"ms "+ profiler.elapsedTime() / 1000 / 1000);
			
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
	
}
