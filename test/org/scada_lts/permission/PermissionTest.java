package org.scada_lts.permission;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.scada_lts.permissions.IPermission;
import org.scada_lts.permissions.Permission;

import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;

@RunWith(MockitoJUnitRunner.class)
public class PermissionTest {
	
	IPermission permission;
	
	@Mock
	User user;
	
	@Mock
	User userWithoutPerm;
	
	@Mock
	User admin;
	
	@Mock
	Object object;
	
	@Mock
	View view;
	
	@Before
    public void setUp() {
        permission = new Permission();
        
        user = new User();
        user.setUsername("user");
        user.setAdmin(false);
        
    	userWithoutPerm = new User();
    	userWithoutPerm.setUsername("userWithoutPerm");
    	userWithoutPerm.setAdmin(false);
    	
    	admin = new User();
    	admin.setUsername("admin");
    	admin.setAdmin(true);
    	
    	view.setName("view");
    	view.setId(123);
    	view.setXid("view_123");
    }
	
	@After
    public void tearDown() {
        user = null;
        permission = null;    
        view = null;
    }
		
	@Test
	public void testPermissionCheck() {
		assertTrue(permission.hasObjectPermission(user, view, User.ACCESS_TYPE_READ));
		assertTrue(permission.hasObjectPermission(admin, view, User.ACCESS_TYPE_SET));
		assertFalse(permission.hasObjectPermission(userWithoutPerm, view, User.ACCESS_TYPE_READ));
	}
	
	@Test
	public void testUserIsAdminCheck() {
		assertTrue(permission.hasAdminPermission(admin));
		assertFalse(permission.hasAdminPermission(user));
		assertFalse(permission.hasAdminPermission(userWithoutPerm));
	}
	
	@Test
	public void testGetObjectsWithUserAccess() {
		assertTrue(permission.getObjectsWithPermission(user, User.ACCESS_TYPE_READ) != null);
		assertTrue(permission.getObjectsWithPermission(admin, User.ACCESS_TYPE_SET) != null);
	}
	
}
