package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
public class V1_4__UserLocks implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTmp) throws Exception {
        final String createUserLocksTable =
                "CREATE TABLE `scadalts`.`userLocks` (\n" +
                        "  `userId` INT NOT NULL,\n" +
                        "  `lockType` SMALLINT NOT NULL,\n" +
                        "  `typeKey` BIGINT NOT NULL,\n" +
                        "  `ts` BIGINT NOT NULL);";

        jdbcTmp.execute(createUserLocksTable);
    }
}
