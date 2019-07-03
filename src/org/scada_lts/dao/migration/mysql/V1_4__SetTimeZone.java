package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @autor grzegorz.bylica@gmail.com on 03.07.19 update task timezone
 */
public class V1_4__SetTimeZone implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final String setViewSizeProperties = ""
                + "alter table users " +
                "add column `timezone` varchar(100) after `receiveOwnAuditEvents`,"+
                "add column `zone` varchar(60) after `timezone`;";

        jdbcTemplate.execute(setViewSizeProperties);
    }
}
