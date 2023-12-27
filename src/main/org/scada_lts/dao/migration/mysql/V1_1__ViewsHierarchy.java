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
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author grzegorz bylica grzegorz.bylica@gmail.com
 */
public class V1_1__ViewsHierarchy extends BaseJavaMigration {

	public void migrate(Context context) throws Exception {

		final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

		// Event Detector Templates/Users Profiles for migration from ScadaBR 1.2
		migrationScadaBr(jdbcTmp);

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

	private static void migrationScadaBr(JdbcTemplate jdbcTemplate) {
		final String eventDetectorTemplatesSQL = ""
				+ "CREATE TABLE IF NOT EXISTS eventDetectorTemplates ("
				+ "id int NOT NULL auto_increment,"
				+ "name varchar(255) NOT NULL,"
				+ "primary key (id)"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(eventDetectorTemplatesSQL);

		final String templatesDetectorsSQL = ""
				+ "CREATE TABLE IF NOT EXISTS templatesDetectors ("
				+ "id int NOT NULL auto_increment,"
				+ "xid varchar(50) NOT NULL,"
				+ "alias varchar(255),"
				+ "detectorType int NOT NULL,"
				+ "alarmLevel int NOT NULL,"
				+ "stateLimit FLOAT,"
				+ "duration int,"
				+ "durationType int,"
				+ "binaryState char(1),"
				+ "multistateState int,"
				+ "changeCount int,"
				+ "alphanumericState varchar(128),"
				+ "weight float,"
				+ "threshold double,"
				+ "eventDetectorTemplateId int NOT NULL,"
				+ "primary key (id),"
				+ "KEY templatesDetectorsFk1 (eventDetectorTemplateId),"
				+ "CONSTRAINT templatesDetectorsFk1 FOREIGN KEY (eventDetectorTemplateId) REFERENCES eventDetectorTemplates (id)"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(templatesDetectorsSQL);

		final String usersProfilesSQL = ""
				+ "CREATE TABLE IF NOT EXISTS usersProfiles ("
				+ "id int NOT NULL auto_increment,"
				+ "xid varchar(50) not null,"
				+ "name varchar(255) NOT NULL,"
				+ "primary key (id),"
				+ "UNIQUE KEY usersProfilesUn1 (xid)"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(usersProfilesSQL);

		// Data source permissions

		final String dataSourceUsersProfilesSQL = ""
				+ "create table IF NOT EXISTS dataSourceUsersProfiles ("
				+ "dataSourceId int not null,"
				+ "userProfileId int not null, "
				+ "KEY dataSourceUsersProfilesFk1 (dataSourceId),"
				+ "KEY dataSourceUsersProfilesFk2 (userProfileId),"
				+ "CONSTRAINT dataSourceUsersProfilesFk1 FOREIGN KEY (dataSourceId) REFERENCES dataSources (id) ON DELETE CASCADE,"
				+ "CONSTRAINT dataSourceUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(dataSourceUsersProfilesSQL);

		// Data point permissions

		final String dataPointUsersProfilesSQL = ""
				+ "create table IF NOT EXISTS dataPointUsersProfiles ("
				+ "dataPointId int not null,"
				+ "userProfileId int not null,"
				+ "permission int not null,"
				+ "KEY dataPointUsersProfilesFk1 (dataPointId),"
				+ "KEY dataPointUsersProfilesFk2 (userProfileId),"
				+ "CONSTRAINT dataPointUsersProfilesFk1 FOREIGN KEY (dataPointId) REFERENCES dataPoints (id) ON DELETE CASCADE,"
				+ "CONSTRAINT dataPointUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(dataPointUsersProfilesSQL);

		//Data source permissions

		final String usersUsersProfilesSQL = ""
				+ "create table IF NOT EXISTS usersUsersProfiles ("
				+ "userProfileId int not null,"
				+ "userId int not null,"
				+ "KEY usersUsersProfilesFk1 (userProfileId),"
				+ "KEY usersUsersProfilesFk2 (userId),"
				+ "CONSTRAINT usersUsersProfilesFk1 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE,"
				+ "CONSTRAINT usersUsersProfilesFk2 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(usersUsersProfilesSQL);

		// Watchlist permissions

		final String watchListUsersProfilesSQL = ""
				+ "create table IF NOT EXISTS watchListUsersProfiles ("
				+ "watchlistId int not null,"
				+ "userProfileId int not null,"
				+ "permission int not null,"
				+ "KEY watchlistUsersProfilesFk1 (watchlistId),"
				+ "KEY watchlistUsersProfilesFk2 (userProfileId),"
				+ "CONSTRAINT watchlistUsersProfilesFk1 FOREIGN KEY (watchlistId) REFERENCES watchLists (id) ON DELETE CASCADE,"
				+ "CONSTRAINT watchlistUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(watchListUsersProfilesSQL);

		// View Users Profiles

		final String viewUsersProfilesSQL = ""
				+ "create table IF NOT EXISTS viewUsersProfiles ("
				+ "viewId int not null,"
				+ "userProfileId int not null,"
				+ "permission int not null,"
				+ "KEY viewUsersProfilesFk1 (viewId)," 
				+ "KEY viewUsersProfilesFk2 (userProfileId)," 
				+ "CONSTRAINT viewUsersProfilesFk1 FOREIGN KEY (viewId) REFERENCES mangoViews (id) ON DELETE CASCADE,"
				+ "CONSTRAINT viewUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
				+ ") ENGINE=InnoDB;";

		jdbcTemplate.execute(viewUsersProfilesSQL);
	}
}
