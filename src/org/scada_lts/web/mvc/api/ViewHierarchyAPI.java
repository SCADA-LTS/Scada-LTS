/*
 * (c) 2017 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.web.mvc.api;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.service.ViewHierarchyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/**
 * Controller for API View Hierarchy
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
public class ViewHierarchyAPI {
	
	private static final Log LOG = LogFactory.getLog(ViewHierarchyAPI.class);
	
	@Resource
	private ViewHierarchyService viewHierarchyService;
	
	@RequestMapping(value = "/api/view_hierarchy/getAll", method = RequestMethod.GET)
	public ResponseEntity<String> getAll(HttpServletRequest request) {
		LOG.info("/api/view_hierarchy/getAll");
		try {
			User user = Common.getUser(request);
		
			if (user != null) {
				
				class ViewHierarchyJSON implements Serializable{
					private String xid;
					private String name;
					private long key;
					private boolean folder;
					private boolean view;
					
					ViewHierarchyJSON(String xid,String name, long key, boolean folder, boolean view) {
						
					}
					public String getXid() {
						return xid;
					}
					public void setXid(String xid) {
						this.xid = xid;
					}
					public String getName() {
						return name;
					}
					public void setName(String name) {
						this.name = name;
					}
					public Long getKey() {
						return key;
					}
					public void setKey(Long key) {
						this.key = key;
					}
					public boolean isFolder() {
						return folder;
					}
					public void setFolder(boolean folder) {
						this.folder = folder;
					}
					
					public boolean isView() {
						return view;
					}
					public void setView(boolean view) {
						this.view = view;
					}
				}
				
				
				String json = null;
				ObjectMapper mapper = new ObjectMapper();
				json = mapper.writeValueAsString("");
				
				return new ResponseEntity<String>(json,HttpStatus.OK);				
			} 
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(value = "/api/view_hierarchy/createFolder/{name}/{parentId}", method = RequestMethod.GET)
	public ResponseEntity<String> createFolder(@PathVariable("name") String name, @PathVariable("parentId") int parentId, HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchy/createFolder/{name}:"+name);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
				ViewHierarchyNode node = new ViewHierarchyNode(parentId, name);
				if (viewHierarchyService.add(node)) {
					String json = null;
					ObjectMapper mapper = new ObjectMapper();
					json = mapper.writeValueAsString(node);
					return new ResponseEntity<String>(json,HttpStatus.OK);
				}
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/api/view_hierarchy/deleteFolder/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> getChartData(@PathVariable("id") int id, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/deleteFolder/{id}:"+id);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
				
				if (viewHierarchyService.del(id)) {
					String json = null;
					ObjectMapper mapper = new ObjectMapper();
					json = mapper.writeValueAsString("dell");
					return new ResponseEntity<String>(json,HttpStatus.OK);
				}
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/api/view_hierarchy/move/{id}/{newParentId}", method = RequestMethod.GET)
	public ResponseEntity<String> move(@PathVariable("newParentId") int id, @PathVariable("newParentId") int newParentId, HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchy/move/{id}/{newParentId} id:"+id+" newParentId:"+newParentId);
		
		try {
			User user = Common.getUser(request);
			if (user != null) {
				
				if (viewHierarchyService.move(id, newParentId)) {
					String json = null;
					ObjectMapper mapper = new ObjectMapper();
					json = mapper.writeValueAsString("move");
					return new ResponseEntity<String>(json,HttpStatus.OK);
				}
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
	}

}
