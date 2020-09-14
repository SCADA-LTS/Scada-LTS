package org.scada_lts.dao.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @autor grzegorz.bylica@gmail.com on 14.09.2020
 */
public class CorrectMigration2_2 {

    private static final Log LOG = LogFactory.getLog(CorrectMigration2_2.class);

    public static void correct() {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        jdbcTmp.execute("DROP VIEW IF EXISTS apiAlarmsAcknowledge;");
        //(1)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.1.3';");

        jdbcTmp.execute("DROP VIEW IF EXISTS apiAlarmsHistory;");
        //(2)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.1.2';");

        jdbcTmp.execute("DROP PROCEDURE IF EXISTS prc_sort_alarms_and_storungs_depend_on_state;");
        jdbcTmp.execute("DROP TEMPORARY TABLE IF EXISTS tmp_sortedAlarmsStorungs;");
        //(3)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.1.1.1';");

        jdbcTmp.execute("DROP VIEW IF EXISTS apiAlarmsLive;");
        //(4)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.1.1';");

        jdbcTmp.execute("DROP VIEW IF EXISTS viewAllStorungs;");
        jdbcTmp.execute("DROP VIEW IF EXISTS viewAllAlarms;");
        //(5)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.1';");

        try {
            jdbcTmp.execute("ALTER TABLE dataPoints DROP COLUMN pointName;");
        } catch (Exception e) {
            LOG.warn(e);
        }
        //(6)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.0.2';");

        jdbcTmp.execute("DROP TABLE IF EXISTS plcAlarms;");
        //(7)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.0.1';");

        jdbcTmp.execute( "DROP TRIGGER IF EXISTS onlyForStorungsAndAlarmValues;");
        //(8)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.0.0.1';");

        try {
            jdbcTmp.execute("ALTER TABLE dataPoints DROP COLUMN plcAlarmLevel;");
        } catch (Exception e) {
            LOG.warn(e);
        }
        //(9)
        jdbcTmp.execute( "DELETE FROM schema_version WHERE version = '2.2.0';");

    }
}
