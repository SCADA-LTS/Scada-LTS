/*
 * (c) 2020 hyski.mateusz@gmail.com, kamil.jarmusik@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.scada_lts.dao.alarms;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

class PlcAlarmsDAO implements AlarmsDAO {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_ACKNOWLEDGE_TIME = "acknowledgeTime";
    private static final String COLUMN_NAME_INACTIVE_TIME = "inactiveTime";

    private static final String COLUMN_NAME_ACTIVATION_TIME_VIEW = "activation-time";
    private static final String COLUMN_NAME_INACTIVATION_TIME_VIEW = "inactivation-time";
    private static final String COLUMN_NAME_LEVEL_VIEW = "level";
    private static final String COLUMN_NAME_NAME_VIEW = "name";
    private static final String COLUMN_NAME_TIME_VIEW = "time";

    private static final String SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET = ""
            + "SELECT "
            + "la." + COLUMN_NAME_ID + ", "
            + "la.`" + COLUMN_NAME_ACTIVATION_TIME_VIEW + "`, "
            + "la.`" + COLUMN_NAME_INACTIVATION_TIME_VIEW + "`, "
            + "la." + COLUMN_NAME_LEVEL_VIEW + ", "
            + "la." + COLUMN_NAME_NAME_VIEW + " "
            + "FROM liveAlarms la LIMIT ? OFFSET ?;";

    private static final String SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET = ""
            + "SELECT "
            + "ha." + COLUMN_NAME_NAME_VIEW + ", "
            + "ha." + COLUMN_NAME_TIME_VIEW + ", "
            + "ha." + COLUMN_NAME_LEVEL_VIEW + " "
            + "FROM historyAlarms ha WHERE "
            + "DATE_FORMAT(ha." + COLUMN_NAME_TIME_VIEW + ", '%Y-%m-%d') = ? "
            + "AND "
            + "ha." + COLUMN_NAME_NAME_VIEW + " RLIKE ? LIMIT ? OFFSET ?;";

    private static final String SELECT_INACTIVE_TIME_FROM_PLC_ALARMS_WHERE_ID = ""
            + "SELECT "
            + "pa." + COLUMN_NAME_INACTIVE_TIME + " "
            + "FROM plcAlarms pa WHERE "
            + "pa." + COLUMN_NAME_ID + " = ?;";

    private static final String UPDATE_PLC_ALARMS_SET_ACKNOWLEDGE_TIME_WHERE_ID = ""
            + "UPDATE plcAlarms pa SET "
            + "pa." + COLUMN_NAME_ACKNOWLEDGE_TIME + " = 1000*UNIX_TIMESTAMP(current_timestamp(3)) "
            + "WHERE "
            + "pa." + COLUMN_NAME_ID + " = ?;";

    private final JdbcTemplate jdbcTemplate;

    PlcAlarmsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LiveAlarm> getLiveAlarms(int offset, int limit) throws DataAccessException {
        return jdbcTemplate.query(SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET,
                new Object[]{limit, offset},
                new LiveAlarmRowMapper());
    }
    @Override
    public List<HistoryAlarm> getHistoryAlarms(String dayDate, String regex, int offset, int limit) throws DataAccessException {
        return jdbcTemplate.query(SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET,
                new Object[]{dayDate, regex, limit, offset},
                new HistoryAlarmRowMapper());
    }
    @Override
    public int setAcknowledgeTime(int id) throws DataAccessException {
        return jdbcTemplate.update(UPDATE_PLC_ALARMS_SET_ACKNOWLEDGE_TIME_WHERE_ID, id);
    }

    @Override
    public Optional<Long> getInactiveTimeMs(int id) throws DataAccessException {
        Long inactiveTime = jdbcTemplate.queryForObject(SELECT_INACTIVE_TIME_FROM_PLC_ALARMS_WHERE_ID,
                new Object[]{id}, Long.class);
        return Optional.ofNullable(inactiveTime);
    }

    private class LiveAlarmRowMapper implements RowMapper<LiveAlarm> {

        @Override
        public LiveAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {

            LiveAlarm liveAlarm = new LiveAlarm();
            String inactivationTime = rs.getString(COLUMN_NAME_INACTIVATION_TIME_VIEW);

            liveAlarm.setId(rs.getInt(COLUMN_NAME_ID));
            liveAlarm.setActivationTime(rs.getString(COLUMN_NAME_ACTIVATION_TIME_VIEW));
            liveAlarm.setInactivationTime(inactivationTime == null ? "" : inactivationTime);
            liveAlarm.setLevel(rs.getString(COLUMN_NAME_LEVEL_VIEW));
            liveAlarm.setName(rs.getString(COLUMN_NAME_NAME_VIEW));

            return liveAlarm;
        }
    }

    private class HistoryAlarmRowMapper implements RowMapper<HistoryAlarm>{

        @Override
        public HistoryAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {

            HistoryAlarm historyAlarm = new HistoryAlarm();
            historyAlarm.setTime(rs.getString(COLUMN_NAME_TIME_VIEW));
            historyAlarm.setName(rs.getString(COLUMN_NAME_NAME_VIEW));
            String level = String.valueOf(rs.getLong(COLUMN_NAME_LEVEL_VIEW));
            historyAlarm.setDescription(level);

            return historyAlarm;
        }
    }
}
