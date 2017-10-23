package org.scada_lts.permissions;

public interface IUser {
	
	/**
	 * Check if user is admin
	 * @param user
	 * @return boolean
	 */
	public boolean isAdminPermission();
}
