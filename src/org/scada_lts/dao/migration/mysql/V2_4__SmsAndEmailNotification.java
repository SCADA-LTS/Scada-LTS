package org.scada_lts.dao.migration.mysql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author rjajko@softq.pl
 */

public class V2_4__SmsAndEmailNotification extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_4__SmsAndEmailNotification.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        createCalendarTable(jdbcTmp);
        createRangesTable(jdbcTmp);
        createNotificationsTable(jdbcTmp);
        createSchedulerTable(jdbcTmp);

    }

    private void createCalendarTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(""
                + "CREATE TABLE calendar ("
                + "id INT(11) NOT NULL AUTO_INCREMENT,"
                + "hour INT(10),"
                + "day INT(10),"
                + "month INT(10),"
                + "year INT(10),"
                + "PRIMARY KEY (id)"
                + ");"
        );
    }

    private void createRangesTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(""
                + "CREATE TABLE ranges ("
                + "id INT(11) NOT NULL AUTO_INCREMENT,"
                + "days_of_weeks VARCHAR(45),"
                + "calendar_id_start INT(10),"
                + "calendar_id_stop INT(10),"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (calendar_id_start) REFERENCES calendar(id),"
                + "FOREIGN KEY (calendar_id_stop) REFERENCES calendar(id)"
                + ");"
        );
    }

    private void createNotificationsTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(""
                + "CREATE TABLE notifications ("
                + "id INT(11) NOT NULL AUTO_INCREMENT,"
                + "per_mail INT(10),"
                + "per_sms INT(10),"
                + "mtime TIMESTAMP(6),"
                + "PRIMARY KEY (id)"
                + ");"
        );
    }

    private void createSchedulerTable(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(""
                + "CREATE TABLE scheduler ("
                + "id INT(11) NOT NULL AUTO_INCREMENT,"
                + "ranges_id INT(10),"
                + "notifications_id INT(10),"
                + "PRIMARY KEY (id),"
                + "FOREIGN KEY (ranges_id) REFERENCES ranges(id),"
                + "FOREIGN KEY (notifications_id) REFERENCES notifications(id)"
                + ");"
        );
    }
}
