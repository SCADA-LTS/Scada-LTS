package org.scada_lts.permissions.model;

public class AccessType {
	
	private String name;
	private Byte mask;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Byte getMask() {
		return mask;
	}
	public void setMask(Byte mask) {
		this.mask = mask;
	}

}
