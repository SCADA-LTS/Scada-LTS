package org.scada_lts.dao.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_5__ScheduledExecuteInactiveEvent extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        jdbcTmp.execute("ALTER TABLE mailingLists ADD cronPattern VARCHAR(100) COMMENT 'cron pattern';");
        jdbcTmp.execute("ALTER TABLE mailingLists ADD collectInactiveEmails BINARY DEFAULT false COMMENT 'Collect inactive emails and send when activated';");
        jdbcTmp.execute("ALTER TABLE mailingLists ADD dailyLimitSentEmails BINARY DEFAULT false COMMENT 'Daily limit sent emails';");
        jdbcTmp.execute("ALTER TABLE mailingLists ADD dailyLimitSentEmailsNumber INT DEFAULT 0 COMMENT 'Daily limit sent emails number';");
        jdbcTmp.execute("CREATE TABLE scheduledExecuteInactiveEvent (\n" +
                "  mailingListId INT NOT NULL,\n" +
                "  sourceEventId INT NOT NULL,\n" +
                "  eventHandlerId INT NOT NULL,\n" +
                "  UNIQUE (mailingListId, sourceEventId, eventHandlerId), \n" +
                "  FOREIGN KEY (sourceEventId) REFERENCES events(id) ON DELETE CASCADE,\n" +
                "  FOREIGN KEY (mailingListId) REFERENCES mailingLists(id) ON DELETE CASCADE,\n" +
                "  FOREIGN KEY (eventHandlerId) REFERENCES eventHandlers(id) ON DELETE CASCADE\n" +
                ") ENGINE=InnoDB;");

    }


}