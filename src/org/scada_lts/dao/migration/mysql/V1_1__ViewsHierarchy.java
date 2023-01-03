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

import org.flywaydb.core.api.migration.BaseJavaMigration;

import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author grzegorz bylica grzegorz.bylica@gmail.com
 */
public class V1_1__ViewsHierarchy extends BaseJavaMigration {

	public void migrate(Context context) throws Exception {

		final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

		final String folderViewsHierarchySQL = ""
		    		+ "create table category_views_hierarchy ("
		    			+ "id int(11) not null auto_increment,"
		    			+ "parentId int(11),"
		    			+ "name varchar(100) not null unique,"
		    			+ "primary key (id, parentId)"
		    		+ ") engine=innodb;";
		
		final String viewsHierarchySQL = ""
				+ "create table views_category_views_hierarchy ("
    			+ "view_id int(11),"
    			+ "folder_views_hierarchy_id int(11) not null,"
    			+ "primary key (view_id)"
    		+ ") engine=innodb;";
		 
		String fAdd = 
			    "CREATE FUNCTION func_views_hierarchy_add( "
				 	+ "a_parentId int(11),"
				 	+ "a_name CHAR(100)) "
				 + "RETURNS int(11) "
			  + "BEGIN "
				+ "DECLARE specialty CONDITION FOR SQLSTATE '45000'; "
			    + "IF ( (CHARACTER_LENGTH(a_name)>2) and (CHARACTER_LENGTH(a_name)<100) )  THEN "
				  +	"SIGNAL SQLSTATE '01000'; "
				  	+ "insert into category_views_hierarchy (parentId, name) values ( a_parentId, a_name); "
			  		+ "return last_insert_id(); "
			  	+ "ELSE " 
				  + "SIGNAL SQLSTATE '45000' "
			      + "SET MESSAGE_TEXT='#error.view_hierarchy.add.error1# '; "
			     + "END IF;"
			    + "END ";
			    
		
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
		
		String fDeleteView =
				"CREATE FUNCTION func_views_hierarchy_folder_delete( "+ 
				  "a_id int(11)) "+ 
				"RETURNS INT(11) "+ 
				"NOT DETERMINISTIC "+ 
				"BEGIN "+ 
				  "DELETE FROM category_views_hierarchy "+ 
				  "WHERE id=a_id; "+
				  "SET SQL_SAFE_UPDATES = 0;"+
				  "UPDATE category_views_hierarchy "+
				    "SET parentId=-1 "+
				  "WHERE "+
				    "parentId=a_id; "+
				  
				  "return a_id; "+ 
				"END; ";
		
		String fDeleteFolder =
				"CREATE FUNCTION func_views_hierarchy_view_delete( a_id int(11)) "+ 
				  "RETURNS INT(11) "+ 
				  "NOT DETERMINISTIC "+ 
				"BEGIN "+ 
				  "DELETE FROM views_category_views_hierarchy "+
				  "WHERE view_id=a_id; "+ 
				  "return a_id; "+ 
				"END; ";
		
		
		String fMoveFolder =
				"CREATE FUNCTION func_views_hierarchy_move_folder( "+ 
						"a_id INT, a_new_parent_id INT) "+ 
					"RETURNS INT(11) "+ 
					"NOT DETERMINISTIC "+ 
					"BEGIN "+
					 "UPDATE category_views_hierarchy "+ 
						"SET parentId=a_new_parent_id "+ 
					 "WHERE id=a_id; "+ 
					 "return a_id; "+ 
					"END;";
						
		String fMoveView =	
				"CREATE FUNCTION func_views_hierarchy_move_view(a_id INT, a_new_parent_id INT) " 
				 + "RETURNS INT(11) " 
				 + "NOT DETERMINISTIC " 
				+ "BEGIN "
				 + "DECLARE varExistId INT default 0; "
				 + "SELECT view_id into varExistId FROM views_category_views_hierarchy WHERE view_id=a_id; "
				 + "IF varExistId = 0 THEN "
				   + "INSERT INTO views_category_views_hierarchy (view_id, folder_views_hierarchy_id) " 
				   + "VALUES (a_id, a_new_parent_id); "
				 + "ELSE "
				   + "UPDATE views_category_views_hierarchy " 
				   + "SET folder_views_hierarchy_id=a_new_parent_id "
				   + "WHERE view_id=a_id; "
				 + "END IF; " 
				 + "return a_id; "  				  
				+ "END; ";
		
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
		jdbcTmp.execute(fDeleteFolder);
		jdbcTmp.execute(fDeleteView);
		jdbcTmp.execute(fMoveFolder);
		jdbcTmp.execute(fMoveView);
		jdbcTmp.execute(pSelect);
		jdbcTmp.execute(pSelectNode);
		jdbcTmp.execute(pSelectViewInFolders);
		
	}

}
