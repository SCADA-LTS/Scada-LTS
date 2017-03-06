package org.scada_lts.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@WebAppConfiguration
public class ViewHierarchyServiceTest {

	@Resource
	ViewHierarchyDAO vhd;
	
	@Resource
	ViewHierarchyService vhs;
	
	private void populateData() {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test-service1");
		vhn.setId(vhd.add(vhn));
		ViewHierarchyNode vhn1 = new ViewHierarchyNode(-1, 1, "test-service2");
		vhn.setId(vhd.add(vhn1));
	}
	
	/*@Test
	public void getAll() {
		
		populateData();
		
		List<String> lst = vhs.getAll();
		
		for (String str: lst) {
			System.out.println(str);
		}
		assertEquals(true, true);
	}*/
	
	/*@Configuration
	@EnableWebMvc
	static class Config {
		
		Config() {
			try {
				if (DAO.getInstance().isTest()) {
					new TestDAO().setUp();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Bean
		ViewHierarchyDAO viewHierarchyDAO() {
			return new ViewHierarchyDAO();
		}
		
		@Bean
		ViewHierarchyService viewHierarchyService() {
			//return Mockito.mock(ViewHierarchyService.class);
			return new ViewHierarchyService();
		}
	}*/
	
	
}
