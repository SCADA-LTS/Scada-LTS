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
	
	private void dell (List<ViewHierarchyJSON> data, long id) {
		for (ViewHierarchyJSON vhJSON :data) {
			if (vhJSON.isFolder() && (vhJSON.getKey()==id)){
				cache.remove(vhJSON);
				break;
			}			
			dell(vhJSON.getChildren(), id);
		}
	}
	
	private ViewHierarchyJSON getAndDelete(List<ViewHierarchyJSON> data, long id) {
		for (ViewHierarchyJSON vhJSON :data) {
			if (vhJSON.getKey()==id){
				cache.remove(vhJSON);
				return vhJSON;
			}
			ViewHierarchyJSON child = getAndDelete(vhJSON.getChildren(), id);
			if (child !=null) {
				return child;
			}
			
		}
		return null;
	}
	
	private void addTo(List<ViewHierarchyJSON> data, long newParentId, ViewHierarchyJSON toAdd) {
		for (ViewHierarchyJSON vhJSON :data) {
			if (vhJSON.isFolder() && (vhJSON.getKey()==newParentId)){
				vhJSON.getChildren().add(toAdd);
				break;
			}			
			addTo(vhJSON.getChildren(), newParentId, toAdd);
		}
	}
	
	private List<ViewHierarchyJSON> cache;
	
	public static ViewHierarchyCache getInstance() {
		if (viewHierarchyCache==null) {
			LOG.info("Creating ViewHierarchyCache");
			viewHierarchyCache = new ViewHierarchyCache();
		} 
		
		return viewHierarchyCache;
	}
	
	
    public void add(ViewHierarchyNode vhn) {
      
    	ViewHierarchyService vhs = new ViewHierarchyService();
    	new ViewHierarchyService().add(vhn);
    	ViewHierarchyJSON vhf = vhs.createViewHierarchyFolderJSON(vhn);
        cache.add(vhf);
  
	}
    
    public boolean delete(ViewHierarchyNode vhn) {
    	dell(cache, vhn.getId());
    	return new ViewHierarchyService().delFolder(vhn.getId());
  	}
  	
    
    public boolean move(int from, int to) {
    	ViewHierarchyJSON vhj = getAndDelete(cache, from);
    	addTo(cache, from, vhj);
    	if (vhj.isFolder()){
    		return new ViewHierarchyService().moveFolder(from, to);
    	} else {
    		return new ViewHierarchyService().moveView(from, to);
    	}
    }

	
	private void initial() {
		LOG.info("Initial ViewHierarchyCache");
		cache =  new ViewHierarchyService().getAll();
	}
	
	public List<ViewHierarchyJSON> getAll() {
		return cache;
	}
	
}
