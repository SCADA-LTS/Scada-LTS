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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.cache.PointHierarchyCache;
import org.scada_lts.dao.PointHierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.exception.CacheHierarchyException;
import org.scada_lts.mango.service.DataPointService;
import org.springframework.stereotype.Service;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;
import com.serotonin.mango.vo.hierarchy.PointHierarchyEventDispatcher;

/** 
 * Service for points hierarchy.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@Service
public class PointHierarchyService {
	// cache
	private static final Log LOG = LogFactory.getLog(PointHierarchyService.class);
	
	
	@Resource
	private PointHierarchyDAO phDAO;
	
	public PointHierarchyService() {
		//
	}
	
	/**
	 * Delete 
	 * @param parentId
	 * @param key
	 * @param isFolder
	 * @return
	 */
	public boolean delete(int parentId, int key, boolean isFolder) {
		LOG.info("delete key:"+key+" parentId:"+parentId);
		boolean res = false;
		try {
			if (isFolder) {
			  res = phDAO.deleteFolder(key, parentId);
			  if (res) {
				  PointHierarchyCache.getInstance().deleteFolder(parentId, key);
			  }
			} else {
				res = phDAO.updateParentIdDataPoint(key, 0);
				if (res) {
					PointHierarchyCache.getInstance().deletePoint(parentId, key);
				}
			}
		} catch (Exception e) {
			  LOG.error(new CacheHierarchyException(e));
		}
		return res;
	}
	
	/**
	 * Edit folder
	 * @param parentId
	 * @param id
	 * @param newTitle
	 * @return
	 */
	public boolean edt(int parentId, int id, String newTitle, boolean isFolder) {
		LOG.info("edt id:"+id+" newTitle:"+newTitle+ " isFolder:"+isFolder);
		boolean res = phDAO.updateTitle(id, newTitle);
		if (res) {
			try {
				PointHierarchyCache.getInstance().edit(parentId, id, newTitle, isFolder);
			} catch (Exception e) {
				LOG.error(new CacheHierarchyException(e));
			}
		}
		return res;
	}
	
	/**
	 * Move folder or data point
	 * @param oldParentId
	 * @param newParentId
	 * @param key
	 * @param isFolder
	 * @return
	 * @throws PointHierarchyException
	 */
	public boolean move(int oldParentId,int newParentId, int key, boolean isFolder) {
		boolean res = false;
		if (isFolder) {
	      res = phDAO.updateParentId(key, newParentId);
		} else {
		  res = phDAO.updateParentIdDataPoint(key, newParentId);	
		}
		if (res) {
		  try {
			PointHierarchyCache.getInstance().move( oldParentId, newParentId, key, isFolder);
	      } catch (Exception e) {
			LOG.error(new CacheHierarchyException(e));
		  }
		}
	   return res;
	}
	
	/**
	 * Search
	 * @throws Exception 
	 */
	public List<PointHierarchyNode> search(String search, int page) throws Exception {
		return PointHierarchyCache.getInstance().getOnBaseName(search, page);	
	}
	
	/**
	 * Get paths 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public List<PointHierarchyNode> getPaths(int key, boolean isFolder) throws Exception {
		return PointHierarchyCache.getInstance().getPaths(key, isFolder);
	}
	
	/**
	 * Add folder	
	 * @param parentId
	 * @param title
	 * @return
	 */
	public int add(int parentId, String title) {
		//TODO check title null
		LOG.info("add parentId:"+parentId+" title:"+title);
		int id = phDAO.insert(parentId, title);
		//actualize cache
		if (id>0) {
			PointHierarchyNode phn = new PointHierarchyNode(
					id, 
					parentId, 
					title, 
					true, 
					null);
			try {
			  if (phn.isFolder()) {
				  PointHierarchyCache.getInstance().addFolder(phn);
			  } else {
				  PointHierarchyCache.getInstance().addPoint(phn);
			  }
			} catch (Exception e) {
				LOG.error(new CacheHierarchyException(e));
				return -1;
			}
		}
		return id;
	}

	public void addFoldersToHierarchy(PointHierarchy ph, int parentId, Map<Integer, List<PointFolder>> folders) {
		List<PointFolder> folderList = folders.remove(parentId);
		if (folderList == null)
			return;

		for (PointFolder f : folderList) {
			ph.addPointFolder(f, parentId);
			addFoldersToHierarchy(ph, f.getId(), folders);
		}
	}

	public void savePointFolder(PointFolder folder, int parentId) {
		if (folder.getId() == Common.NEW_ID) {
			folder.setId(phDAO.insert(parentId, folder.getName()));
		} else if (folder.getId() != 0) {
			phDAO.insert(folder.getId(), parentId, folder.getName());
		}

		//Save subfolders
		for (PointFolder pF: folder.getSubfolders()) {
			savePointFolder(pF, folder.getId());
		}
	}

	public void savePointHierarchy(final PointFolder pointFolder) {

		phDAO.delete();
		savePointFolder(pointFolder, 0);

		DataPointService dpService = new DataPointService();
		dpService.savePointsInFolder(pointFolder);

		PointHierarchyDAO.cachedPointHierarchy = null;
		PointHierarchyDAO.cachedPointHierarchy = dpService.getPointHierarchy();
		PointHierarchyEventDispatcher.firePointHierarchySaved(pointFolder);
	}

}
