/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author grzegorz bylica grzegorz.bylica@gmail.com
 */
public class V1_1__ViewsHierarchy implements SpringJdbcMigration {
	
	public void migrate(JdbcTemplate jdbcTmp) throws Exception {
		
		final String pointHierarchySQL = ""
		    		+ "create table views_hierarchy ("
		    			+ "id int not null auto_increment,"
		    			+ "parentId int default null,"
		    			+ "name char(100) not null unique,"
		    			+ "primary key (id)"
		    		+ ") engine=innodb;";
		 
		String fAdd = 
			    "CREATE FUNCTION func_views_hierarchy_add( "
				 	+ "a_parentId INT,"
				 	+ "a_name CHAR(100)) "
				 + "RETURNS INT "
				 + "NOT DETERMINISTIC "
			  + "BEGIN "
			  	+ "insert into views_hierarchy ("
			  		+ "parentId, "
			  		+ "name) "
			  		+ "values ( a_parentId, a_name); "
			  	+ "return last_insert_id(); "
			  + "END";
		
		String fUpdate = 
				"CREATE FUNCTION func_views_hierarchy_update( "
					+ "a_id INT,"
				 	+ "a_parentId INT,"
				 	+ "a_name CHAR(100)) "
				 + "RETURNS INT "
				 + "NOT DETERMINISTIC "
			  + "BEGIN "
			  	+ "update views_hierarchy "
			  	  + "set parentId=a_parentId, "
			  	      + "name=a_name "
			  	+ "where id=a_id; "
			  	+ "return a_id; "
			  + "END";
		
		String fDelete =
				"CREATE FUNCTION func_views_hierarchy_delete( "
					+ "a_id INT) "
				 + "RETURNS INT "
				 + "NOT DETERMINISTIC "
			  + "BEGIN "
			  	+ "delete from views_hierarchy where id=a_id; "
			  	+ "return a_id; "
			  + "END";
		
		String fMove = 
				"CREATE FUNCTION func_views_hierarchy_move( "
						+ "a_id INT, a_new_parent_id INT) "
					 + "RETURNS INT "
					 + "NOT DETERMINISTIC "
				  + "BEGIN "
				  	+ "update views_hierarchy "
				  		+ "set parentId=a_parentId "
				  	+ "where id=a_id; "
				  	+ "return a_id; "
				  + "END";
		
		String pSelect = 
				"CREATE PROCEDURE prc_views_hierarchy_select() "
				  +"BEGIN "					 
				  	  + "SELECT * "
					  + "FROM views_hierarchy; "
				  +"END;";
		
		String pSelectNode = 
				"CREATE PROCEDURE prc_views_hierarchy_select_node("
				+ "a_parent_id INT) "
				  +"BEGIN "					 
				  	  + "SELECT * "
					  + "FROM views_hierarchy "
					  + "WHERE parent_id=a_parent_id; "
				  +"END;";
		
		jdbcTmp.execute(pointHierarchySQL);
		
		jdbcTmp.execute(fAdd);
		jdbcTmp.execute(fUpdate);
		jdbcTmp.execute(fDelete);
		jdbcTmp.execute(fMove);
		jdbcTmp.execute(pSelect);
		jdbcTmp.execute(pSelectNode);
		
	}

}
