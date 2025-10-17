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

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.ViewHierarchyCache;
import org.scada_lts.dao.error.ParseError;
import org.scada_lts.dao.error.ParseErrorResult;
import org.scada_lts.dao.model.error.ViewError;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.service.ViewHierarchyService;
import org.scada_lts.service.model.ViewHierarchyJSON;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/**
 * Controller for API View Hierarchy
 * 
 * @author Grzesiek Bylica grzegorz.bylica@gmail.com
 */
@Controller 
public class ViewHierarchyAPI {
	
	private static final Log LOG = LogFactory.getLog(ViewHierarchyAPI.class);

	private final ViewHierarchyService viewHierarchyService;
	private final MessageSource msgSource;

	public ViewHierarchyAPI(ViewHierarchyService viewHierarchyService, MessageSource msgSource) {
		this.viewHierarchyService = viewHierarchyService;
		this.msgSource = msgSource;
	}

	@RequestMapping(value = "/api/view_hierarchy/getAll", method = RequestMethod.GET)
	public ResponseEntity<List<ViewHierarchyJSON>> getAll(HttpServletRequest request) {
		LOG.info("/api/view_hierarchy/getAll");
		try {
			User user = Common.getUser(request);
		
			if (user != null) {
				List<ViewHierarchyJSON> data = ViewHierarchyCache.getInstance().getAll();
				return new ResponseEntity<>(data,HttpStatus.OK);
			} 
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			throw new BadRequestException(e.toString(), request.getRequestURI());
		}
	}
	
		
	
	@RequestMapping(value = "/api/view_hierarchy/createFolder/{name}/{parentId}", method = RequestMethod.GET)
	public ResponseEntity<Object> createFolder(@PathVariable("name") String name, @PathVariable("parentId") int parentId, HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchy/createFolder/{name}:"+name+" {parentId}:"+parentId);
		
		try {
			User user = Common.getUser(request);
			if (user != null && user.isAdmin()) {
				try {
					ViewHierarchyNode node = new ViewHierarchyNode(parentId, name);
					viewHierarchyService.add(node);
					ViewHierarchyCache.getInstance().refresh();
					return new ResponseEntity<>(node,HttpStatus.OK);
				} catch ( Exception error) {
					LOG.error(error);
					ViewError er = new ViewError();
					
					ParseErrorResult per = ParseError.getError(error.getLocalizedMessage());
					String errStr="";
					if (per.getNotErr()) {
						errStr = msgSource.getMessage(per.getMessage(),null, Locale.getDefault());
					} else {
						errStr = per.getMessage();
					}
					er.setMessage(errStr);
					return new ResponseEntity<>(er,HttpStatus.BAD_REQUEST);
				} 
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/api/view_hierarchy/editFolder/{name}/{key}", method = RequestMethod.GET)
	public ResponseEntity<ViewHierarchyNode> editFolder(@PathVariable("name") String name, @PathVariable("key") int key, HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchy/editFolder/{name}:"+name+" {key}:"+key);
		
		try {
			User user = Common.getUser(request);
			if (user != null && user.isAdmin()) {
				
				ViewHierarchyNode node = new ViewHierarchyNode(ViewHierarchyService.ROOT_ID, name);
				node.setId(key);
				
				if (viewHierarchyService.edt(node)) {
					ViewHierarchyCache.getInstance().refresh();
					return new ResponseEntity<>(node,HttpStatus.OK);
				}
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/api/view_hierarchy/deleteFolder/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> deleteFolder(@PathVariable("id") int id, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/deleteFolder/{id}:"+id);
		
		try {
			User user = Common.getUser(request);
			if (user != null && user.isAdmin()) {
				
				if (viewHierarchyService.delFolder(id)) {
					ViewHierarchyCache.getInstance().refresh();
					return new ResponseEntity<>("success",HttpStatus.OK);
				}
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/api/view_hierarchy/deleteView/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> deleteView(@PathVariable("id") int id, HttpServletRequest request) {
		
		LOG.info("/api/watchlist/deleteView/{id}:"+id);
		
		try {
			User user = Common.getUser(request);
			if (user != null && user.isAdmin()) {
				
				if (viewHierarchyService.delView(id)) {
					ViewHierarchyCache.getInstance().refresh();
					return new ResponseEntity<>("success",HttpStatus.OK);
				}
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/api/view_hierarchy/moveFolder/{id}/{newParentId}", method = RequestMethod.GET)
	public ResponseEntity<String> moveFolder(@PathVariable("id") int id, @PathVariable("newParentId") int newParentId, HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchy/moveFolder/{id}/{newParentId} id:"+id+" newParentId:"+newParentId);
		
		try {
			User user = Common.getUser(request);
			if (user != null && user.isAdmin()) {
				
				if (viewHierarchyService.moveFolder(id, newParentId)) {
					ViewHierarchyCache.getInstance().refresh();
					return new ResponseEntity<>("moved",HttpStatus.OK);
				}
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/api/view_hierarchy/moveView/{id}/{newParentId}", method = RequestMethod.GET)
	public ResponseEntity<String> moveView(@PathVariable("id") int id, @PathVariable("newParentId") int newParentId, HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchyView/move/{id}/{newParentId} id:"+id+" newParentId:"+newParentId);
		
		try {
			User user = Common.getUser(request);
			if (user != null && user.isAdmin()) {
				
				if (viewHierarchyService.moveView(id, newParentId)) {
					ViewHierarchyCache.getInstance().refresh();
					return new ResponseEntity<>("moved",HttpStatus.OK);
				}
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(value = "/api/view_hierarchy/getFirstViewId", method = RequestMethod.GET)
	public ResponseEntity<ViewHierarchyJSON> getFirstViewId(HttpServletRequest request) {
		
		LOG.info("/api/view_hierarchy/getFirstViewId");
		
		try {
			User user = Common.getUser(request);
			if (user != null) {

				ViewHierarchyJSON response = viewHierarchyService.getFirstViewId();
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		} catch (Exception e) {
			LOG.error(e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
}
