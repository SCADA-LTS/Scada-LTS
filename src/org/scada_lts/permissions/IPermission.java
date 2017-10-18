package org.scada_lts.permissions;

import java.util.List;
import java.util.Map;

/** 
 * Permission interface 
 * 
 * @author Arkadiusz Parafiniuk    email: arkadiusz.parafiniuk@gmail.com
 * 
 */

public interface IPermission<T> {
	
	/**
	 * Check if user has access to object<T>
	 * @param user
	 * @param <T> object is Data Source, View, Data Poin or Watch List
	 * @param access type
	 * @return boolean 
	 */
	public boolean hasObjectPermission(IUser user, T object, IAccessType accessType);
	
	/**
	 * @param user
	 * @param access type
	 * @return the map of objects type <T> that user has access 
	 */
	public Map<String, T> getObjectsWithPermission(IUser user, IAccessType accessType);

	/**
	 * Check if user is admin
	 * @param user
	 * @return boolean
	 */
	public boolean hasAdminPermission(IUser user);
}
