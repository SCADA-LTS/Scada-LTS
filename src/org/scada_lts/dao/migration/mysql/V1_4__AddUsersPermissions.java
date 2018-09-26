package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Class created by Arkadiusz Parafiniuk
 *
 * @Author arkadiusz.parafiniuk@gmail.com
 */
public class V1_4__AddUsersPermissions implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTmp) throws Exception {
        final String usersPermissionsSQL = ""
                + "CREATE table usersPermissions (  "
                + "id BIGINT not null PRIMARY KEY auto_increment,  "
                + "entityXid varchar(50) not null,  "
                + "userId int not null,"
                + "permission int not null,"
                + "entityType smallint not null,"
                + ") ENGINE=InnoDB;";

        jdbcTmp.execute(usersPermissionsSQL);
    }
}
