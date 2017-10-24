package org.scada_lts.permission;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.scada_lts.permissions.IEntityPermision;
import org.scada_lts.permissions.IPermission;
import org.scada_lts.permissions.IUser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;



@RunWith(JUnit4.class)
public class PermissionMockTest {
	
	IPermission permisions;
	IUser adminUser;
	IUser user;
	IEntityPermision entity;
	
	@Before
    public void executedBeforeEach() {
		
		//mock creation
		permisions = mock(IPermission.class);
		adminUser = mock(IUser.class);
		user = mock(IUser.class);
		entity = mock(IEntityPermision.class);
				
    }
	
	
	@Test 
	public void validUserPermision() {
		when(user.isAdminPermission()).thenReturn(false);
		assertFalse(user.isAdminPermission());
	}
	
	@Test 
	public void validAdminUserPermision() {
		when(adminUser.isAdminPermission()).thenReturn(true);
		assertTrue(adminUser.isAdminPermission());
	}
	
	@Test 
	public void validPermision() {
	
		Map<Long, IEntityPermision> returnEntityDataSourcesForUser = new HashMap<Long, IEntityPermision>();
		//IEntityPermision is may DataSource, DataPoint, WatchList, Report, Event
		IEntityPermision entityPermision = mock(IEntityPermision.class);
		
		returnEntityDataSourcesForUser.put(1L, entityPermision );
		
		when(permisions.getObjectsWithPermission(user, entity)).thenReturn(returnEntityDataSourcesForUser);
		assertTrue(permisions.getObjectsWithPermission(user, entity).equals(returnEntityDataSourcesForUser));
		assertFalse(permisions.getObjectsWithPermission(user, null).equals(returnEntityDataSourcesForUser));

	}
	
	
	
}
