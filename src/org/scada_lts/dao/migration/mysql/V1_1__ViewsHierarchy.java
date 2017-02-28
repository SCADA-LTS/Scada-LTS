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
		
		final String folderViewsHierarchySQL = ""
		    		+ "create table category_views_hierarchy ("
		    			+ "id int(11) not null auto_increment,"
		    			+ "parentId int(11) default null,"
		    			+ "name varchar(100) not null unique,"
		    			+ "primary key (id)"
		    		+ ") engine=innodb;";
		
		final String viewsHierarchySQL = ""
				+ "create table views_category_views_hierarchy ("
    			+ "view_id int(11) not null,"
    			+ "folder_views_hierarchy_id int(11) not null"
    		+ ") engine=innodb;";
		 
		String fAdd = 
			    "CREATE FUNCTION func_views_hierarchy_add( "
				 	+ "a_parentId int(11),"
				 	+ "a_name CHAR(100)) "
				 + "RETURNS INT(11) "
				 + "NOT DETERMINISTIC "
			  + "BEGIN "
				+ "DECLARE specialty CONDITION FOR SQLSTATE '45000'; "
			    + "IF ( (CHARACTER_LENGTH(a_name)>2) and (CHARACTER_LENGTH(a_name)<100) )  THEN "
				  +	"SIGNAL SQLSTATE '01000';"
				  	+ "insert into category_views_hierarchy ("
			  			+ "parentId, "
			  			+ "name) "
			  			+ "values ( a_parentId, a_name); "
			  		+ "return last_insert_id(); "
			  	+ "ELSE " 
				  + "SIGNAL SQLSTATE '45000' "
			      + "SET MESSAGE_TEXT = 'SET MESSATE_TEXT = '#error.view_hierarchy.add.error1# An error occurred add view hierarchy name is not validat' "
			    + "END IF;"
              + "END";
		
		String fUpdate = 
				"CREATE FUNCTION func_views_hierarchy_update( "
					+ "a_id int(11),"
				 	+ "a_parentId int(11),"
				 	+ "a_name CHAR(100)) "
				 + "RETURNS INT(11) "
				 + "NOT DETERMINISTIC "
			  + "BEGIN "
			  	+ "update category_views_hierarchy "
			  	  + "set parentId=a_parentId, "
			  	      + "name=a_name "
			  	+ "where id=a_id; "
			  	+ "return a_id; "
			  + "END";
		
		String fDelete =
				"CREATE FUNCTION func_views_hierarchy_delete( "
					+ "a_id int(11)) "
				 + "RETURNS INT(11) "
				 + "NOT DETERMINISTIC "
			  + "BEGIN "
			  	+ "delete from category_views_hierarchy where id=a_id; "
			  	+ "return a_id; "
			  + "END";
		
		String fMove = 
				"CREATE FUNCTION func_views_hierarchy_move( "
						+ "a_id INT, a_new_parent_id INT) "
					 + "RETURNS INT(11) "
					 + "NOT DETERMINISTIC "
				  + "BEGIN "
				  	+ "update category_views_hierarchy "
				  		+ "set parentId=a_parentId "
				  	+ "where id=a_id; "
				  	+ "return a_id; "
				  + "END";
		
		String pSelect = 
				"CREATE PROCEDURE prc_views_hierarchy_select() "
				  +"BEGIN "					 
				  	  + "SELECT * "
					  + "FROM category_views_hierarchy Order by parentId ASC; "
				  +"END;";
		
		String pSelectNode = 
				"CREATE PROCEDURE prc_views_hierarchy_select_node("
				+ "a_parent_id INT(11)) "
				  +"BEGIN "					 
				  	  + "SELECT * "
					  + "FROM category_views_hierarchy "
					  + "WHERE parentid=a_parent_id; "
				  +"END;";
		
		String pSelectViewInFolders =
				"CREATE PROCEDURE prc_views_category_views_hierarchy_select() "
						  +"BEGIN "					 
						  	  + "SELECT * "
							  + "FROM views_category_views_hierarchy Order by view_id ASC;"
						  +"END;";

		
		jdbcTmp.execute(folderViewsHierarchySQL);
		jdbcTmp.execute(viewsHierarchySQL);
		
		jdbcTmp.execute(fAdd);
		jdbcTmp.execute(fUpdate);
		jdbcTmp.execute(fDelete);
		jdbcTmp.execute(fMove);
		jdbcTmp.execute(pSelect);
		jdbcTmp.execute(pSelectNode);
		jdbcTmp.execute(pSelectViewInFolders);
		
	}

}
