package org.scada_lts.permissions;

import java.util.Map;

public class Permission implements IPermission {

	@Override
	public boolean hasObjectPermission(IUser user, Object object, IAccessType accessType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map getObjectsWithPermission(IUser user, IAccessType accessType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAdminPermission(IUser user) {
		// TODO Auto-generated method stub
		return false;
	}

}
