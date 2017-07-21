package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_2__SetViewSizeProperties implements SpringJdbcMigration {

	@Override
	public void migrate(JdbcTemplate jdbcTmp) throws Exception {
		
		final String setViewSizeProperties = ""
	    		+ "alter table mangoViews " +
					"add column `width` int after `data`,"+
					"add column `height` int after `width`;";
		
		//TODO migrate on existing views and set not null for width and height.
		
		jdbcTmp.execute(setViewSizeProperties);
	}

}
