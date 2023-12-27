package org.scada_lts.dao.model.viewshierarchy;

import java.io.Serializable;

public class ViewInViewHierarchyNode implements Serializable {
	
	private static final long serialVersionUID = 4643066441353024395L;
	
	private long viewId;
	private long folderViewsHierarchyId;
	
	public ViewInViewHierarchyNode() {
		
	}
	
	public ViewInViewHierarchyNode(long viewId, long folderViewsHierarchyId) {
		this.viewId = viewId;
		this.folderViewsHierarchyId = folderViewsHierarchyId;
	}

	/**
	 * @return the viewId
	 */
	public long getViewId() {
		return viewId;
	}
	
	/**
	 * @param viewId the viewId to set
	 */
	public void setViewId(long viewId) {
		this.viewId = viewId;
	}
	
	/**
	 * @return the folderViewsHierarchyId
	 */
	public long getFolderViewsHierarchyId() {
		return folderViewsHierarchyId;
	}
	
	/**
	 * @param folderViewsHierarchyId the folderViewsHierarchyId to set
	 */
	public void setFolderViewsHierarchyId(long folderViewsHierarchyId) {
		this.folderViewsHierarchyId = folderViewsHierarchyId;
	}
}
