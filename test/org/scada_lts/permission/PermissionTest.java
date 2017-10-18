package org.scada_lts.permission;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.scada_lts.permissions.IAccessType;
import org.scada_lts.permissions.IPermission;
import org.scada_lts.permissions.Permission;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;

@RunWith(MockitoJUnitRunner.class)
public class PermissionTest {
	
	IPermission permission;
	
	@Mock
	IAccessType accessType;
	
	@Mock
	User user;

	DataSourceVO dataSource = DataSourceVO.createDataSourceVO(0);
	
	@Before
    public void setUp() {
        permission = new Permission();
    }
	
	@After
    public void tearDown() {
        user = null;
        accessType = null;
        permission = null;       
    }
		
	@Test
	public void testPermissionCheck() {
		assertTrue(permission.hasObjectPermission(user, dataSource, accessType));
	}
	
	@Test
	public void testUserIsAdminCheck() {
		assertTrue(permission.hasAdminPermission(user));
	}
	
	@Test
	public void testGetObjectsWithUserAccess() {
		assertTrue(permission.getObjectsWithPermission(user, accessType) != null);
	}
	
}
