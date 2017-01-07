package org.scada_lts.dao.migration.mysql;

import org.springframework.jdbc.core.JdbcTemplate;

public class V1_1__ViewsHierarchy {
	
	public void migrate(JdbcTemplate jdbcTmp) {
		 final String pointHierarchySQL = ""
		    		+ "create table views_hierarchy ("
		    			+ "id int not null auto_increment,"
		    			+ "parentId int,"
		    			+ "name char(100) not null unique,"
		    			+ "primary key (id)"
		    		+ ") engine=innodb;";
		 
		 
		
		 jdbcTmp.execute(pointHierarchySQL);
		
		
	}

}
