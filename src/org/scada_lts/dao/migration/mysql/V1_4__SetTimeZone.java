package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_4__SetTimeZone implements SpringJdbcMigration {

	@Override
	public void migrate(JdbcTemplate jdbcTmp) throws Exception {
		
		final String setTimeZone = ""
				+ "alter table users " +
				"add column `timezone` varchar(60) after `receiveOwnAuditEvents`,"+
				"add column `zone` varchar(40) after `timezone`;";
				
		jdbcTmp.execute(setTimeZone);

		final String updateTimeZone = "update users set timezone=?, zone=?";

		jdbcTmp.update(updateTimeZone, new Object[]{"UTC+00:00","UCT"});
	}

}
