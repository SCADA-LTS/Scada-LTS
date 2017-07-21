package org.scada_lts.dao.model.viewshierarchy;

import java.io.Serializable;

public class ViewHierarchyNode implements Serializable {
	
	private static final long serialVersionUID = 4079855826946331275L;
	private int id;
	private int parentId;
	private String name;
	
	public ViewHierarchyNode(int id, int parentId, String name) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.name = name;
	}
	
	public ViewHierarchyNode(int parentId, String name) {
		super();
		this.parentId = parentId;
		this.name = name;
	}
	
	public ViewHierarchyNode(ViewHierarchyNode vhn) {
		super();
		this.id = vhn.getId();
		this.parentId = vhn.getParentId();
		this.name = vhn.getName();
	}
	
	public ViewHierarchyNode() {
		super();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}
	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + parentId;
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
		ViewHierarchyNode other = (ViewHierarchyNode) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentId != other.parentId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ViewHierarchyNode [id=" + id + ", parentId=" + parentId + ", name=" + name + "]";
	}

	
	

}
