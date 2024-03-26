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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;
import org.scada_lts.dao.HierarchyDAO;
import org.scada_lts.dao.pointhierarchy.PointHierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyComparator;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyDataSource;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.mango.adapter.MangoPointHierarchy;
import org.scada_lts.quartz.CronTriggerScheduler;
import org.scada_lts.web.beans.ApplicationBeans;

/** 
 * Class responsible for buffering data of PointHierarchy
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and correcting translation Jerzy Piejko
 */
public class PointHierarchyCache {
	
	private static final Log LOG = LogFactory.getLog(PointHierarchyCache.class);
	
	public static final int ROOT=0;
	public static final boolean IS_FOLDER=true;
	public static final boolean IS_NOT_FOLDER=false;
	public static final int ELEMENTS_ON_PAGE_HINT=10;
	
	private static PointHierarchyCache instance = null;
	private TreeMap<Integer, List<PointHierarchyNode>> cache = new TreeMap<Integer, List<PointHierarchyNode>>();
	
	private void checkAndCorrectOrphanedPointHierarchy(PointHierarchyNode phn) {
		if (cache.get(phn.getParentId())==null) {
			List<PointHierarchyNode> lst = new ArrayList<PointHierarchyNode>();
			cache.put(phn.getParentId(), lst);
		}
	}
	
	private void checkAndRootFolder() {
		if (cache.get(ROOT)==null) {
			List<PointHierarchyNode> lst = new ArrayList<PointHierarchyNode>();
			cache.put(ROOT, lst);
		}
	}
	
	public static PointHierarchyCache getInstance() throws Exception {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Get PointHierarchyCache instance ");
		}
		if (instance == null) {
			instance = new PointHierarchyCache(true);
		}
		return instance;
	}
	
	public void printTreeInCash(String str, int key) throws Exception {
		if (getOnBaseParentId(key)!=null) {
			for (PointHierarchyNode p: getOnBaseParentId(key)){
				LOG.trace(str+"-"+p.getTitle()+" key:"+p.getKey()+" isFolder:"+p.isFolder());
				if (p.isFolder()){
				  printTreeInCash(str+"-", p.getKey());
				}
			}
		}
	}
	
	/**
	 * Refreshing data in buffer based on data in database
	 * @throws Exception 
	 */
	public void updateData() throws Exception {
		
		if (LOG.isTraceEnabled()) {
		  LOG.trace("update data");	
		  printTreeInCash("-", 0);
		}
		
		cache.clear();
		List<PointHierarchyNode> lstHierarchy = new HierarchyDAO().getHierarchy();
		
		for (PointHierarchyNode ph : lstHierarchy) {
			addFolder(ph);
		}
		List<PointHierarchyNode> lstPointHierarchy = new PointHierarchyDAO().getPointsHierarchy();
		Collections.sort(lstPointHierarchy, PointHierarchyComparator.getInst());
		
		for (PointHierarchyNode ph : lstPointHierarchy) {
			addPoint(ph);
		}		
		
		if (LOG.isTraceEnabled()) {
		  LOG.trace("after - ");	
		  printTreeInCash("-", 0);
		}
	}
	
	/**
	 * Check is added
	 * @param parentId
	 * @param key
	 * @param isFolder
	 * @return
	 */
	public PointHierarchyNode checkIsAdded(int parentId, int key, boolean isFolder) {
		for ( PointHierarchyNode p : cache.get(parentId)){
			if ( (p.getKey()==key) && (p.isFolder()==isFolder)){
				return p;
			}
		}
		return null; 
	}
	
	public int checkIsAdded(PointHierarchyNode phn) {
		return cache.get(phn.getParentId()).indexOf(phn);
	}
	
	/**
	 * Add data to cache
	 * @param phn
	 * @throws Exception 
	 */
	public void addFolder(PointHierarchyNode phn) throws Exception {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("Add data hierarchy:"+phn.toString());
		  printTreeInCash("_",0);
		}
		
		checkAndRootFolder();
		
		if (cache.get(phn.getParentId())!=null ) {
			if ( checkIsAdded(phn)<0) {
				cache.get(phn.getParentId()).add(phn);
				if (cache.get(phn.getKey())==null){
					List<PointHierarchyNode> lst = new ArrayList<PointHierarchyNode>();
					cache.put(phn.getKey(),lst);
				}
			} 
		} else {
			checkAndCorrectOrphanedPointHierarchy(phn);
			addFolder(phn);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("after - ");
			printTreeInCash("_",0);
		}
	}
	
	/**
	 * Add point do cache
	 */
	public void addPoint(PointHierarchyNode phn) throws Exception {
		
		if (LOG.isTraceEnabled()) {
			  LOG.trace("Add point hierarchy:"+phn.toString());
			  printTreeInCash("_",0);
		}
		
		checkAndRootFolder();
			
		if (cache.get(phn.getParentId())!=null) {
			cache.get(phn.getParentId()).add(phn);
		} else {
				checkAndCorrectOrphanedPointHierarchy(phn);	
				addPoint(phn);
		}
		if (LOG.isTraceEnabled()) {
		  LOG.trace("after - ");
		  printTreeInCash("_",0);
		}
	}
	
	
	/**
	 * Get data of selected (parendId) folder
	 * 
	 * @param parentId
	 * @return
	 */
	public List<PointHierarchyNode> getOnBaseParentId(int parentId) throws Exception {
		return cache.get(parentId);
	}
	
	/**
	 * Get hit for search hierarchi name or point name
	 * @param name
	 * @return
	 */
	public List<PointHierarchyNode> getOnBaseName(String name, int page) {
		int count=0;
		int offset=ELEMENTS_ON_PAGE_HINT;
		int pageStart=(page-1)*ELEMENTS_ON_PAGE_HINT;
		int pageEnd=pageStart+offset;
		List<PointHierarchyNode> result = new ArrayList<PointHierarchyNode>();
		for (Map.Entry<Integer, List<PointHierarchyNode>> entry : cache.entrySet()) {
			List<PointHierarchyNode> list = entry.getValue();
			for (PointHierarchyNode node : list) {
				count++;
				if (count>pageEnd) {
					break;
				}
				if (node.getTitle().toUpperCase().contains(name.toUpperCase()) ) {
					if ( (pageStart<=count) && (count<=pageEnd)) {
						result.add(node);
					}
				}
			}
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("result:"+result.toString());
		}
		return result;
	}
	
	private PointHierarchyNode search(int key, boolean isFolder ){	
		for (Map.Entry<Integer, List<PointHierarchyNode>> entry : cache.entrySet()) {
			List<PointHierarchyNode> list = entry.getValue();
			for (PointHierarchyNode node : list) {
				if (node.getKey() == key && (node.isFolder()==isFolder)) {
					return node;
				}
			}
		}
		return null;
	}
	
	public List<PointHierarchyNode> getPaths(int key, boolean isFolder) {
		PointHierarchyNode phn = null;
		Integer keyA = key;
		boolean isFolderA = isFolder;
		Boolean searchNext = true;
		List<PointHierarchyNode> result = new ArrayList<PointHierarchyNode>();
		do {
		  phn = search(keyA, isFolderA);
		  result.add(phn);
		  searchNext = (phn != null) &&	(phn.getParentId() != 0);
		  if (searchNext) {
		     keyA = phn.getParentId();
		     isFolderA=true;
		  } else {
			  searchNext = false;
		  }
		} while ( searchNext ); 
		return result;
	}
	
	public void deleteFolder(int parentId, int key) throws Exception {
		if (LOG.isTraceEnabled()) {
			  LOG.trace("before delete folder parentId:"+parentId+" key:"+key);
			  printTreeInCash("-", 0);
		}
		if (LOG.isTraceEnabled()) {
			  LOG.trace("parentId:"+parentId+" size list:"+cache.get(parentId).size());
		}
		PointHierarchyNode toDelete = null;
		for (PointHierarchyNode f : cache.get(parentId)) {
			if ( (f.getKey()==key) && (f.isFolder()==true)){
				toDelete = f;
				break;
			}
		}
		boolean test = cache.get(parentId).remove(toDelete);
		if (!test) {
			throw new Exception("dddd");
		}
		if (LOG.isTraceEnabled()) {
			  LOG.trace("parentId:"+parentId+" size list:"+cache.get(parentId).size());
			  printTreeInCash("-", 0);
		}
		
		if (cache.get(key)!=null) {
		  for(PointHierarchyNode p: cache.get(key)) {
			p.setParentId(0);
			cache.get(0).add(p);
		  }
		  cache.remove(key);
	    }
		if (LOG.isTraceEnabled()) {
			  LOG.trace("after delete folder");
			  printTreeInCash("-", 0);
		}
	}
	
	public void deletePoint(int parentId, int key) throws Exception {
		if (LOG.isTraceEnabled()) {
			  LOG.trace("before delete point parentId:"+parentId+" key:"+key);
			  printTreeInCash("-", 0);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("parentId:"+parentId+" size list:"+cache.get(parentId).size());
		}
		PointHierarchyNode toDelete = null;
		for (PointHierarchyNode f : cache.get(parentId)) {
			if ( (f.getKey()==key) && (f.isFolder()==false)){
				toDelete = f;
				break;
			}
		}
		cache.get(parentId).remove(toDelete);
		cache.get(ROOT).add(toDelete);
		if (LOG.isTraceEnabled()) {
			LOG.trace("parentId:"+parentId+" size list:"+cache.get(parentId).size());
		}
		if (LOG.isTraceEnabled()) {
			  LOG.trace("after delete point");
			  printTreeInCash("-", 0);
		}
	}
	
	
	/**
	 * Deleted element in cache based on ids
	 * @see MangoPointHierarchy
	 * @param ids
	 */
	public void delete(int[] ids) throws Exception {
		if (LOG.isTraceEnabled()) {
		  LOG.trace("delete ids:"+ids);
		  printTreeInCash("_",0);
		}
		
		for(Map.Entry<Integer, List<PointHierarchyNode>> entry : cache.entrySet()) {
			  List<PointHierarchyNode> values = entry.getValue();
			  for(int i=0; i<values.size(); i++) {
				  for (int j=0; j<ids.length;j++) {
				    if ((values.get(i).getKey()==ids[j]) && (values.get(i).isFolder()==false)) {
				    	values.remove(i);
				    };
				  }
			  }
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("after - ");
			printTreeInCash("_",0);
		}

	}
	
	/**
	 * Update information on datasource related data points.
	 * @see MangoPointHierarchy
	 * @param phds
	 */
	public void update(PointHierarchyDataSource phds) throws Exception {
		if (LOG.isTraceEnabled()){
			LOG.trace("update phds:"+phds);
			printTreeInCash("_",0);
		}
		
		for(Map.Entry<Integer, List<PointHierarchyNode>> entry : cache.entrySet()) {
			  List<PointHierarchyNode> values = entry.getValue();
			  for(int i=0; i<values.size(); i++) {
				  if ( !values.get(i).isFolder() && (values.get(i).getPointHierarchyDataSource().getId()==phds.getId())) {
					  values.get(i).setPointHierarchyDataSource(phds);
				  };
			  }
		}
		
		if (LOG.isTraceEnabled()){
			LOG.trace("after - ");
			printTreeInCash("_",0);
		}
	}
	
	/**
	 * Move element in cache
	 * @param oldParendId 
	 * @param newParentId
	 * @param key
	 * @see PointHierarchyCache
	 */
	public void move(int oldParendId, int newParentId, int key, boolean isFolder ) throws Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Move oldParentId:"+oldParendId+" newParentId:"+newParentId+" key:"+key);
			printTreeInCash("_",0);
		}
		PointHierarchyNode toMove=null;
		for (PointHierarchyNode p: cache.get(oldParendId)){
		  if (  (p.getKey()==key) && (isFolder == p.isFolder()) ) {
		     toMove=p;
			 break;
		  }
		}
		
		if (toMove != null) {
		   toMove.setParentId(newParentId);
		   if (cache.get(newParentId)==null) {
			   List<PointHierarchyNode> children = new ArrayList<PointHierarchyNode>();
			   cache.put(newParentId, children);
		   }
		   cache.get(newParentId).add(toMove);
		   cache.get(oldParendId).remove(toMove);
		 }
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("after - ");
			printTreeInCash("_",0);
		}
	}

	/**
	 * Edit title folder in cache
	 * @param parentId
	 * @param key
	 * @param newTitle
	 */
	public void edit(int parentId, int key, String newTitle, boolean isFolder) throws Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Edit parentId:"+parentId+" key:"+key+" newTtile:"+newTitle);
			printTreeInCash("_",0);
		}
		List<PointHierarchyNode> lstPhn = cache.get(parentId);
		if (lstPhn != null) {
		  //TODO check PointHierarchyNode change = lstPhn.stream().filter(e -> e.getKey()== phn.getKey()).findFirst().orElse(null);
		  for (PointHierarchyNode p : lstPhn) {
			if ( (p.getKey()==key) && (p.isFolder()==isFolder)) {
				p.setTitle(newTitle);
				break;
			}
		  }
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("after - ");
			printTreeInCash("_",0);
		}
	}
	
	/**
	 * Constructor - public because use in test
	 * @param init
	 * @throws Exception 
	 */
	public PointHierarchyCache(boolean init) throws Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Point hierarchy cache creating");
		}
		if (init) {
			updateData();
			//cacheInitialize();
		}
	}
	
	public void log(String str) {
		LOG.trace(str);
	}
	
	private void cacheInitialize() throws IOException {
		if (LOG.isTraceEnabled()) {
			LOG.trace("cacheInitialize");
		}
    	String cronExpression = ScadaConfig.getInstance().getProperty(ScadaConfig.CRONE_UPDATE_CACHE_POINT_HIERARCHY);
		ApplicationBeans.getBean("updatePointHierarchyScheduler", CronTriggerScheduler.class).schedule(cronExpression);
	}

}
