package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_5__ScheduledExecuteInactiveEvent extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        jdbcTmp.execute("ALTER TABLE mailinglists ADD cronPattern VARCHAR(100) COMMENT 'cron pattern';");
        jdbcTmp.execute("ALTER TABLE mailinglists ADD collectInactiveEmails BINARY DEFAULT false COMMENT 'Collect inactive emails and send when activated';");
        jdbcTmp.execute("ALTER TABLE mailinglists ADD dailyLimitSentEmails BINARY DEFAULT false COMMENT 'Daily limit sent emails';");
        jdbcTmp.execute("ALTER TABLE mailinglists ADD dailyLimitSentEmailsNumber INT DEFAULT 0 COMMENT 'Daily limit sent emails number';");
        jdbcTmp.execute("CREATE TABLE scheduledExecuteInactiveEvent (\n" +
                "  id INT NOT NULL AUTO_INCREMENT,\n" +
                "  mailingListId INT NOT NULL,\n" +
                "  sourceEventId INT NOT NULL,\n" +
                "  eventHandlerType INT NOT NULL,\n" +
                "  PRIMARY KEY (id), \n" +
                "  UNIQUE (mailingListId, sourceEventId), \n" +
                "  FOREIGN KEY (sourceEventId) REFERENCES events(id) ON DELETE CASCADE,\n" +
                "  FOREIGN KEY (mailingListId) REFERENCES mailinglists(id) ON DELETE CASCADE\n" +
                ") ENGINE=InnoDB;");

    }


}