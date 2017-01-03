package org.scada_lts.dao.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_BaseVersion implements SpringJdbcMigration {
	
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("INSERT INTO test_user (name) VALUES ('Obelix')");
    }

}
