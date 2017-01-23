package org.scada_lts.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
	
	@Before
	public void setUp() {
		
		//this.mockMvc = webAppContextSetup(wac).build();
	}
	
	private void populateData() {
		ViewHierarchyNode vhn = new ViewHierarchyNode(-1, -1, "test");
		vhn.setId(vhd.add(vhn));
		List<ViewHierarchyNode> lst = vhd.getAll();	
		assertEquals(true,  lst.get(0).equals(vhn) );
		vhn.setName("test1");
		vhd.update(vhn);
		lst = vhd.getAll();
		assertEquals(true, lst.get(0).getName().equals("test1"));
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
	//@ComponentScan(basePackageClasses = ViewHierarchyService.class)
	static class Config {
		
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
