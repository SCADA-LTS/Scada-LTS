package org.scada_lts.permissions;

import java.util.Map;

import com.serotonin.mango.vo.User;

/** 
 * Permission interface 
 * 
 * @author Arkadiusz Parafiniuk    email: arkadiusz.parafiniuk@gmail.com
 * 
 */

public interface IPermission {
	
	/**
	 * Check if user has access to object<T>
	 * @param user
	 * @param <T> object is Data Source, View, Data Poin or Watch List
	 * @param access type
	 * @return boolean 
	 */
	public boolean hasObjectPermission(IUser user, IEntityPermision object, int accessType);
	
	/**
	 * @param user
	 * @param access type
	 * @return the map of objects type <T> that user has access 
	 */
	public Map<Long, IEntityPermision> getObjectsWithPermission(IUser user, IEntityPermision object);

	/**
	 * Check if user is admin
	 * @param user
	 * @return boolean
	 */
	public boolean hasAdminPermission(IUser user);
}
