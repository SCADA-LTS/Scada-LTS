package org.scada_lts.permissions;

import java.util.Map;

import com.serotonin.mango.vo.User;

public class Permission implements IPermission {

	@Override
	public boolean hasObjectPermission(User user, Object object, int accessType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map getObjectsWithPermission(User user, int accessType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAdminPermission(User user) {
		
		//to modify with ACL
		return user.isAdmin();
	}


}
