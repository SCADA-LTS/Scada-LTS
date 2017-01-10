package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_1__ViewsHierarchy implements SpringJdbcMigration {
	
	public void migrate(JdbcTemplate jdbcTmp) throws Exception {
		 final String pointHierarchySQL = ""
		    		+ "create table views_hierarchy ("
		    			+ "id int not null auto_increment,"
		    			+ "parentId int default null,"
		    			+ "name char(100) not null unique,"
		    			+ "primary key (id)"
		    		+ ") engine=innodb;";
		
		 jdbcTmp.execute(pointHierarchySQL);
		 
		 /*final String funcViewsHierarchyAdd = ""
			+ "create function `func_views_hierarchy_add`(`a_parentId` int,`a_name` char(100)) "
				+ "return int "
				+ "language sql "
				+ "not deterministic "
				+ "modifies sql data "
				+ "sql security definer "
				+ "comment '' "
				+ "begin "
					+ "insert into views_hierarchy (parentId, name) values (a_parentId, a_name); "
					+ "return last_insert_id(); "
				+ "end ";
			 
		  jdbcTmp.execute(funcViewsHierarchyAdd);*/
		
	}

}
