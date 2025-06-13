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
package org.scada_lts.dao.migration.postgresql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;


public class V1_1__PostgresViewsHierarchy extends BaseJavaMigration {

	public void migrate(Context context) throws Exception {
		final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

		migrationScadaBr(jdbcTemplate);

		final String folderViewsHierarchySQL = ""
				+ "create table IF NOT EXISTS category_views_hierarchy ("
				+ "id  SERIAL NOT NULL,"
				+ "parentid INTEGER,"
				+ "name varchar(100) not null unique,"
				+ "primary key (id, parentid)"
				+ ")";

		final String viewsHierarchySQL = ""
				+ "create table IF NOT EXISTS views_category_views_hierarchy ("
				+ "view_id INTEGER,"
				+ "folder_views_hierarchy_id INTEGER not null,"
				+ "primary key (view_id)"
				+ ")";

		String fAdd =
				"CREATE OR REPLACE FUNCTION func_views_hierarchy_add("
						+ "a_parentid INTEGER, a_name VARCHAR(100)) "
						+ "RETURNS INTEGER AS $$ "
						+ "BEGIN "
						+ "IF (CHAR_LENGTH(a_name) > 2 AND CHAR_LENGTH(a_name) < 100) THEN "
						+ "INSERT INTO \"category_views_hierarchy\"(\"parentid\", \"name\") VALUES (a_parentid, a_name); "
						+ "RETURN currval('category_views_hierarchy_id_seq'); "
						+ "ELSE "
						+ "RAISE EXCEPTION '#error.view_hierarchy.add.error1#'; "
						+ "END IF; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";


		String fUpdate =
				"CREATE OR REPLACE FUNCTION func_views_hierarchy_update("
						+ "a_id INTEGER, a_parentid INTEGER, a_name VARCHAR(100)) "
						+ "RETURNS INTEGER AS $$ "
						+ "BEGIN "
						+ "UPDATE \"category_views_hierarchy\" "
						+ "SET \"parentid\" = a_parentid, \"name\" = a_name "
						+ "WHERE \"id\" = a_id; "
						+ "RETURN a_id; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		String fDeleteView =
				"CREATE OR REPLACE FUNCTION func_views_hierarchy_folder_delete(a_id INTEGER) "
						+ "RETURNS INTEGER AS $$ "
						+ "BEGIN "
						+ "DELETE FROM \"category_views_hierarchy\" WHERE \"id\" = a_id; "
						+ "UPDATE \"category_views_hierarchy\" SET \"parentid\" = -1 WHERE \"parentid\" = a_id; "
						+ "RETURN a_id; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		String fDeleteFolder =
				"CREATE OR REPLACE FUNCTION func_views_hierarchy_view_delete(a_id INTEGER) "
						+ "RETURNS INTEGER AS $$ "
						+ "BEGIN "
						+ "DELETE FROM \"views_category_views_hierarchy\" WHERE \"view_id\" = a_id; "
						+ "RETURN a_id; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";


		String fMoveFolder =
				"CREATE OR REPLACE FUNCTION func_views_hierarchy_move_folder(a_id INTEGER, a_new_parent_id INTEGER) "
						+ "RETURNS INTEGER AS $$ "
						+ "BEGIN "
						+ "UPDATE \"category_views_hierarchy\" SET \"parentid\" = a_new_parent_id WHERE \"id\" = a_id; "
						+ "RETURN a_id; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		String fMoveView =
				"CREATE OR REPLACE FUNCTION func_views_hierarchy_move_view(a_id INTEGER, a_new_parent_id INTEGER) "
						+ "RETURNS INTEGER AS $$ "
						+ "DECLARE "
						+ "varExistId INTEGER := 0; "
						+ "BEGIN "
						+ "SELECT \"view_id\" INTO varExistId FROM \"views_category_views_hierarchy\" WHERE \"view_id\" = a_id; "
						+ "IF NOT FOUND THEN "
						+ "INSERT INTO \"views_category_views_hierarchy\" (\"view_id\", \"folder_views_hierarchy_id\") "
						+ "VALUES (a_id, a_new_parent_id); "
						+ "ELSE "
						+ "UPDATE \"views_category_views_hierarchy\" SET \"folder_views_hierarchy_id\" = a_new_parent_id "
						+ "WHERE \"view_id\" = a_id; "
						+ "END IF; "
						+ "RETURN a_id; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		String pSelect =
				"CREATE OR REPLACE FUNCTION prc_views_hierarchy_select() "
						+ "RETURNS TABLE (id INTEGER, parentid INTEGER, name VARCHAR) AS $$ "
						+ "BEGIN "
						+ "RETURN QUERY SELECT * FROM \"category_views_hierarchy\" ORDER BY \"parentid\" ASC; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		String pSelectNode =
				"CREATE OR REPLACE FUNCTION prc_views_hierarchy_select_node(a_parent_id INTEGER) "
						+ "RETURNS TABLE (id INTEGER, parentid INTEGER, name VARCHAR) AS $$ "
						+ "BEGIN "
						+ "RETURN QUERY SELECT * FROM category_views_hierarchy AS c WHERE c.parentid = a_parent_id; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		String pSelectViewInFolders =
				"CREATE OR REPLACE FUNCTION prc_views_category_views_hierarchy_select() "
						+ "RETURNS TABLE (view_id INTEGER, folder_views_hierarchy_id INTEGER) AS $$ "
						+ "BEGIN "
						+ "RETURN QUERY SELECT * FROM \"views_category_views_hierarchy\" ORDER BY \"view_id\" ASC; "
						+ "END; "
						+ "$$ LANGUAGE plpgsql;";

		jdbcTemplate.execute(folderViewsHierarchySQL);
		jdbcTemplate.execute(viewsHierarchySQL);

		jdbcTemplate.execute(fAdd);
		jdbcTemplate.execute(fUpdate);
		jdbcTemplate.execute(fDeleteFolder);
		jdbcTemplate.execute(fDeleteView);
		jdbcTemplate.execute(fMoveFolder);
		jdbcTemplate.execute(fMoveView);
		jdbcTemplate.execute(pSelect);
		jdbcTemplate.execute(pSelectNode);
		jdbcTemplate.execute(pSelectViewInFolders);


	}

	private static void migrationScadaBr(JdbcTemplate jdbcTemplate) {
		final String eventDetectorTemplatesSQL = ""
				+ "CREATE TABLE IF NOT EXISTS eventDetectorTemplates ("
				+ "id SERIAL,"
				+ "name varchar(255) NOT NULL,"
				+ "primary key (id)"
				+ ")";

		jdbcTemplate.execute(eventDetectorTemplatesSQL);

		final String templatesDetectorsSQL = ""
				+ "CREATE TABLE IF NOT EXISTS templatesDetectors ("
				+ "id SERIAL,"
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
				+ ")";

		jdbcTemplate.execute(templatesDetectorsSQL);

		final String usersProfilesSQL = ""
				+ "CREATE TABLE IF NOT EXISTS usersProfiles ("
				+ "id SERIAL,"
				+ "xid varchar(50) not null,"
				+ "name varchar(255) NOT NULL,"
				+ "primary key (id),"
				+ "CONSTRAINT usersProfilesUn1 UNIQUE (xid)"
				+ ")";

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
				+ ")";

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
				+ ")";

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
				+ ")";

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
				+ ")";

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
				+ ")";

		jdbcTemplate.execute(viewUsersProfilesSQL);
	}
}
