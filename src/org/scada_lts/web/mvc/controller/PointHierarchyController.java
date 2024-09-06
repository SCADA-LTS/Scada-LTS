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
package org.scada_lts.web.mvc.controller;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.util.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.PointHierarchyCache;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.service.pointhierarchy.PointHierarchyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/** 
 * Controller for points hierarchy.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@Controller
public class PointHierarchyController {

	private static final Log LOG = LogFactory.getLog(PointHierarchyController.class);

	private final PointHierarchyService pointHierarchyService;

	public PointHierarchyController(PointHierarchyService pointHierarchyService) {
		this.pointHierarchyService = pointHierarchyService;
	}

	@RequestMapping(value = "/pointHierarchySLTS", method = RequestMethod.GET)
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
		User user = Common.getUser(request);
		if (user == null ) {
			return new ModelAndView("redirect:/login.htm");
		} else {
			if (user.isAdmin()) {
				LOG.trace("/pointHierarchySLTS");
				Map<String, Object> model = new HashMap<String, Object>();
				
				model.put("appName", request.getContextPath());
				model.put("appPort", request.getLocalPort());
				model.put("toYear", DateUtils.getCurrentYearInt());
				return new ModelAndView("pointHierarchySLTS", model);
			} 
		}
		return new ModelAndView("redirect:/login.htm");
	}

	@RequestMapping(value = "/pointHierarchy/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<PointHierarchyNode>> getPointHierarchy(@PathVariable("key") int key, HttpServletRequest request) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("/pointHierarchy/{key} key:" + key);
		}
		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			List<PointHierarchyNode> lst = null;
			try {
				lst = PointHierarchyCache.getInstance().getOnBaseParentId(key);
			} catch (Exception e1) {
				LOG.error(e1);
			}
			return ResponseEntity.ok(lst);
		} else {
			return ResponseEntity.ok(new ArrayList<>());
		}
	}

	@RequestMapping(value = "/pointHierarchy/del/{parentId}/{key}/{isFolder}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> deletePointHierarchy(@PathVariable("parentId") int parentId,
			@PathVariable("key") int key,@PathVariable("isFolder") boolean isFolder, HttpServletRequest request) throws InterruptedException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("/pointHierarchy/del/{parentId}/{key} parentId:" + parentId + " key:" + key + " isFolder:"+isFolder);
		}
		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			boolean ok = false;
			if (isFolder) {
				try {

					List<PointHierarchyNode> elements = PointHierarchyCache.getInstance().getOnBaseParentId(key);
					for (PointHierarchyNode element : elements) {
						if (!element.isFolder()) {
							pointHierarchyService.move(parentId, 0, element.getKey(), false);
						}
					}
				} catch (Exception e) {
					LOG.error(e.getMessage());
					ok = false;
				}

			  	ok = pointHierarchyService.delete(parentId, key, isFolder);
			} else {
				// is point
			  ok = pointHierarchyService.move(parentId, 0, key, isFolder);
			}

			return ResponseEntity.ok(String.valueOf(ok));
		} else {
			return ResponseEntity.ok("");
		}
	}

	@RequestMapping(value = "/pointHierarchy/new/{newParentId}/{newTitle}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> addPointHierarchy(@PathVariable("newParentId") int newParentId,
			@PathVariable("newTitle") String newTitle, ModelMap model, HttpServletRequest request)
			throws IOException, NullPointerException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("/pointHierarchy/new/{newParentId}/{newTitle} newParentId:" + newParentId + " newTitle:" + newTitle);
		}

		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			int id = pointHierarchyService.add(PointHierarchyCache.ROOT, newTitle);
			return ResponseEntity.ok(String.valueOf(id));
		} else {
			return ResponseEntity.ok("");
		}
	}

	@RequestMapping(value = "/pointHierarchy/edit/{parentId}/{key}/{newTitle}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> editPointHierarchy(@PathVariable("parentId") int parentId, @PathVariable("key") int key,
			@PathVariable("newTitle") String newTitle, HttpServletRequest request) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("/pointHierarchy/edit/{key}/{newTitle} key:" + key + " newTitle:" + newTitle);
		}
		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			boolean ok = pointHierarchyService.edt(parentId, key, newTitle, true);
			return ResponseEntity.ok(String.valueOf(ok));
		} else {
			return ResponseEntity.ok("");
		}
	}

	@RequestMapping(value = "/pointHierarchy/move/{key}/{oldParentId}/{newParentId}/{isFolder}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> movePointHierarchy(@PathVariable("key") int key,
			@PathVariable("oldParentId") int oldParentId, @PathVariable("newParentId") int newParentId,
			@PathVariable("isFolder") boolean isFolder, HttpServletRequest request) {
		LOG.info("/pointHierarchy/move/{key}/{oldParentId}/{newParentId}" + " key:" + key + "" + " oldParentId:"
				+ oldParentId + "" + " newParentId:" + newParentId + "" + " isFolder:" + isFolder);
		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			pointHierarchyService.move(oldParentId, newParentId, key, isFolder);
			return ResponseEntity.ok(String.valueOf(true));
		} else {
			return ResponseEntity.ok("");
		}
	}
	
	@RequestMapping(value = "/pointHierarchy/find/{search}/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<PointHierarchyNode>> find(@PathVariable("search") String search, @PathVariable("page") int page, HttpServletRequest request) throws Exception {
		LOG.info("/pointHierarchy/find/{search} search:"+search+" page:"+page);
		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			List<PointHierarchyNode> lst = pointHierarchyService.search(search, page);
			return ResponseEntity.ok(lst);
		} else {
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
	
	@RequestMapping(value = "/pointHierarchy/paths/{key}/{isFolder}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<PointHierarchyNode>> find(@PathVariable("key") int key, @PathVariable("isFolder") boolean isFolder, @PathVariable("interval") int interval, HttpServletRequest request) throws Exception {
		LOG.info("/pointHierarchy/paths/{key}/{isFolder} key:"+key+" isFolder:"+isFolder+" interval:"+interval);
		User user = Common.getUser(request);
		if (user != null && user.isAdmin()) {
			List<PointHierarchyNode> lst = pointHierarchyService.getPaths(key, isFolder);
			return ResponseEntity.ok(lst);
		} else {
			return ResponseEntity.ok(new ArrayList<>());
		}
	}
}
