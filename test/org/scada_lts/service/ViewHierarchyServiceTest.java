package org.scada_lts.service;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.scada_lts.dao.TestDAO;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.model.viewshierarchy.ViewHierarchyNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringRunner.class)
@WebAppConfiguration
public class ViewHierarchyServiceTest {

	@Resource
	ViewHierarchyDAO vhd;
	
	@Resource
	ViewHierarchyService vhs;
	
	private void populateData() {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test-service1");
		vhn.setId(vhd.add(vhn));
		
	}
	
	@Test
	public void getAll() {
		
		populateData();
		
		List<String> lst = vhs.getAll();
		
		for (String str: lst) {
			System.out.println(str);
		}
		assertEquals(true, true);
	}
	
	@Configuration
	@EnableWebMvc
	static class Config {
		
		Config() {
			try {
				new TestDAO().setUp();
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
			return Mockito.mock(ViewHierarchyService.class);
		}
	}
	
	
}
