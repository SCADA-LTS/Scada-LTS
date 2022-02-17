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

package org.scada_lts.dao.model.pointhierarchy;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.serotonin.mango.vo.DataPointVO;

/**
 * Model for buffering.
 *  
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
@JsonPropertyOrder({ "key","title", "expanded", "folder", "lazy", "parentId", "childeren" })
@JsonInclude(Include.NON_NULL)
public class PointHierarchyNode {
	
	private int key;
	private String xid;
	private String title;
	private Boolean expanded;
	private Boolean folder;
	private boolean lazy;
	private Integer parentId;
	private List<PointHierarchyNode> children;
	private PointHierarchyDataSource pointHierarchyDataSource;
	
	//Blocking no arg constructor
	@SuppressWarnings("unused")
	private PointHierarchyNode() {
		//
	}
	
	/**
	 * 
	 * @param key
	 * @param xid
	 * @param parentId
	 * @param title
	 * @param isFolder
	 * @param phds
	 */
	public PointHierarchyNode(int key, String xid, int parentId, String title, boolean isFolder, PointHierarchyDataSource phds) {
		this.setKey(key);
		this.setParentId(parentId);
		this.setTitle(title);
		this.setFolder(isFolder);
		this.xid = xid;
		
		if (isFolder) {
		   //List<PointHierarchyNode> children = new ArrayList<PointHierarchyNode>();	
		   //this.setChildren(children);
		   this.setLazy(true);
		} else {
		   this.setChildren(null);
		   this.setPointHierarchyDataSource(phds);
		   this.setLazy(false);
		}
	}

	public static PointHierarchyNode pointNode(DataPointVO point) {
		return new PointHierarchyNode(point.getId(), point.getXid(),
				point.getPointFolderId(), point.getName(), false,
				PointHierarchyDataSource.newInstance(point));
	}

	public static PointHierarchyNode folderNode(int id, String xid, int parentId, String name) {
		PointHierarchyNode node = new PointHierarchyNode(id, xid, parentId, name, true, new PointHierarchyDataSource());
		node.setChildren(new ArrayList<>());
		return node;
	}

	public static PointHierarchyNode rootNode() {
		PointHierarchyNode node = new PointHierarchyNode(0, "", -1, "root", true, new PointHierarchyDataSource());
		node.setChildren(new ArrayList<>());
		return node;
	}


	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getXid() {
		return xid;
	}
	public void setXid(String xid) {
		this.xid = xid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public Boolean isFolder() {
		if (folder == null) {
			return false;
		}
		return folder;
	}
	public void setFolder(Boolean folder) {
		this.folder = folder;
	}
	public Boolean getLazy() {
		return lazy;
	}
	public void setLazy(Boolean lazy) {
		this.lazy = lazy;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public List<PointHierarchyNode> getChildren() {
		return children;
	}
	public void setChildren(List<PointHierarchyNode> children) {
		this.children = children;
	}
	public PointHierarchyDataSource getPointHierarchyDataSource() {
		return pointHierarchyDataSource;
	}
	public void setPointHierarchyDataSource(PointHierarchyDataSource pointHierarchyDataSource) {
		this.pointHierarchyDataSource = pointHierarchyDataSource;
	}
	
	@Override
	public String toString() {
		return "PointHierarchyNode [key=" + key + ", title=" + title + ", folder=" + folder + ", parentId=" + parentId
				+ "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((folder == null) ? 0 : folder.hashCode());
		result = prime * result + key;
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointHierarchyNode other = (PointHierarchyNode) obj;
		if (folder == null) {
			if (other.folder != null)
				return false;
		} else if (!folder.equals(other.folder))
			return false;
		if (key != other.key)
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
}
