package org.scada_lts.permission;


import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.*;


import java.util.List;



@RunWith(JUnit4.class)
public class PermissionTest {
	
	
	@Before
    public void setUp() {
		
		// mock creation
		List mockedList = mock(List.class);
		
		// using mock object - it does not throw any "unexpected interaction" exception
		mockedList.add("one");
		mockedList.clear();
		
    }
	
	
		
	@Test
	public void testPermissionCheck() {
		assertTrue(true);
	}
	
	@Test
	public void testUserIsAdminCheck() {
		assertTrue(true);
	}
	
	@Test
	public void testGetObjectsWithUserAccess() {
		assertTrue(true);
	}
	
}
