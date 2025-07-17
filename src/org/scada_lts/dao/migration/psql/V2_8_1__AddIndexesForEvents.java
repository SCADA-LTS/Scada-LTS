package org.scada_lts.dao.migration.psql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class V2_8_1__AddIndexesForEvents extends BaseJavaMigration {

    private static final Logger LOG = LoggerFactory.getLogger(V2_8_1__AddIndexesForEvents.class);

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        // Index for filtering/sorting by alarmLevel and activeTs (for frequent WHERE/ORDER BY)
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_events_alarmlevel_activets ON events (alarmLevel, activeTs DESC)");

        // Index for fast filtering by ackTs (acknowledged / unacknowledged events)
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_events_ackts ON events (ackTs)");

        // Index for filtering by typeId (event type)
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_events_typeid ON events (typeId)");

        // Index for fast sorting by activeTs DESC (for dashboard/event list)
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_events_activets_desc ON events (activeTs DESC)");

        // Index for frequent JOINs between userEvents and events on (userId, eventId)
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_userevents_userid_eventid ON userevents (userId, eventId)");
 
        // Index for filtering userEvents by userId (single-user event queries)
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_userevents_userid ON userevents (userId)");
    }
}
