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
package org.scada_lts.cache;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.scada_lts.service.ViewHierarchyService;
import org.scada_lts.service.model.ViewHierarchyJSON;

/** 
 * Class responsible for buffering data of ViewHierarchy
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class ViewHierarchyCache {
	
	private static final Log LOG = LogFactory.getLog(ViewHierarchyCache.class);
	
	private static ViewHierarchyCache viewHierarchyCache = null; 
	
	private ViewHierarchyCache() {
		initial();
	}
	
	private List<ViewHierarchyJSON> cache;
	
	private void initial() {
		LOG.info("Initial ViewHierarchyCache");
		cache =  new ViewHierarchyService().getAll();
	}
	
	public static ViewHierarchyCache getInstance() {
		if (viewHierarchyCache==null) {
			LOG.info("Creating ViewHierarchyCache");
			viewHierarchyCache = new ViewHierarchyCache();
		} 
		
		return viewHierarchyCache;
	}
	
	public List<ViewHierarchyJSON> getAll() {
		return cache;
	}
	
	public void refresh() {
		cache =  new ViewHierarchyService().getAll();
	}
	
}
