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
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
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
		
		 jdbcTmp.execute(pointHierarchySQL);
		 		
	}

}
