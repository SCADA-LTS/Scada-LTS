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
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.GenericHierarchyDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.springframework.stereotype.Service;

/** 
 * Service for views hierarchy.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
@Service
public class ViewHierarchyService {
	
	private static final Log LOG = LogFactory.getLog(ViewHierarchyService.class);
	
	private String getPath(List<ViewHierarchyNode> lst, ViewHierarchyNode nextVhn, ViewHierarchyNode firstVhn) {
		String str = "|";
		
		if (nextVhn.getParentId()<=0) {
			str+= firstVhn.getName();
		} else {
			for (ViewHierarchyNode vhn: lst) {
				if (vhn.getId() == nextVhn.getParentId()) {
					ViewHierarchyNode newNextVhn = new ViewHierarchyNode(vhn);
					str += vhn.getName() +getPath(lst,newNextVhn,firstVhn);
				} 
			}
		}
		
		return str;
	}

	@Resource
	private ViewHierarchyDAO vhDAO;
	
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
	
	//TODO replace String to BEAN for represent tree data in JSON.
	public List<String> getAll(){
		List<ViewHierarchyNode> lst = vhDAO.getAll();
		List<String> lstStr = new ArrayList<String>();
		
		//travers tree and create path
		for (ViewHierarchyNode vhn: lst) {
			lstStr.add(getPath(lst, vhn, vhn));
		}
		//..
		//get view in tree add too tree.
		return lstStr;
	}
	
	public List<ViewHierarchyNode> getNode(int id) {
		return vhDAO.getNode(id);
	}
	
}
