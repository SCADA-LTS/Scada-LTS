package org.scada_lts.dao.migration.postgres;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_5__ScheduledExecuteInactiveEvent extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        // 1) cronPattern
        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='mailinglists' AND column_name='cronpattern'" +
                        ") THEN " +
                        "  ALTER TABLE mailingLists ADD COLUMN cronPattern VARCHAR(100); " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.cronPattern IS 'cron pattern';"
        );

// 2) collectInactiveEmails
        jdbcTemplate.execute(
                "ALTER TABLE mailingLists ADD COLUMN IF NOT EXISTS collectInactiveEmails BOOLEAN NOT NULL DEFAULT FALSE"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.collectInactiveEmails IS 'Collect inactive emails and send when activated';"
        );

// 3) dailyLimitSentEmails
        jdbcTemplate.execute(
                "ALTER TABLE mailinglists ADD COLUMN IF NOT EXISTS dailylimitsentemails BOOLEAN NOT NULL DEFAULT FALSE"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.dailylimitSentEmails IS 'Daily limit sent emails';"
        );

// 4) dailyLimitSentEmailsNumber
        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'mailinglists' AND column_name = 'dailylimitsentemailsnumber' " +
                        ") THEN " +
                        "  ALTER TABLE mailingLists ADD COLUMN dailyLimitSentEmailsNumber INTEGER DEFAULT 0; " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.dailyLimitSentEmailsNumber IS 'Daily limit sent emails number';"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS scheduledExecuteInactiveEvent ("
                        + "  mailingListId INTEGER NOT NULL,"
                        + "  sourceEventId  INTEGER NOT NULL,"
                        + "  eventHandlerId INTEGER NOT NULL,"
                        + "  UNIQUE (mailingListId, sourceEventId, eventHandlerId),"
                        + "  FOREIGN KEY (sourceEventId)  REFERENCES events(id)        ON DELETE CASCADE,"
                        + "  FOREIGN KEY (mailingListId)  REFERENCES mailingLists(id) ON DELETE CASCADE,"
                        + "  FOREIGN KEY (eventHandlerId) REFERENCES eventHandlers(id) ON DELETE CASCADE"
                        + ");"
        );
    }
}