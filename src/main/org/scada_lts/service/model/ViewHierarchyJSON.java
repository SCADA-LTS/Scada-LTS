package org.scada_lts.service.model;

import java.util.List;

public class ViewHierarchyJSON {
	
	private long key;
	private String title;
	private boolean folder;
	private List<ViewHierarchyJSON> children;
	
	public ViewHierarchyJSON() {
		
	}
	
	public ViewHierarchyJSON(int key, String title, boolean folder, List<ViewHierarchyJSON> children) {
		this.setKey(key);
		this.title = title;
		this.setFolder(folder);
		this.children = children;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ViewHierarchyJSON> getChildren() {
		return children;
	}

	public void setChildren(List<ViewHierarchyJSON> children) {
		this.children = children;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

}
