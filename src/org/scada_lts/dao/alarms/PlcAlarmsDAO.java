package org.scada_lts.dao.alarms;
/*
 * (c) 2020 hyski.mateusz@gmail.com
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
    private static final String COLUMN_NAME_ACTIVATION_TIME = "activation-time";
    private static final String COLUMN_NAME_INACTIVATION_TIME = "inactivation-time";
    private static final String COLUMN_NAME_ACKNOWLEDGE_TIME = "acknowledgeTime";
    private static final String COLUMN_NAME_LEVEL = "level";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_NAME_TIME = "time";
    private static final String COLUMN_NAME_UNIQUENESS_TOKEN = "uniquenessToken";
    private static final String COLUMN_NAME_DESCRIPTION = "description";

    private static final String SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET = ""
            + "SELECT "
            + "la." + COLUMN_NAME_ID + ", "
            + "la.`" + COLUMN_NAME_ACTIVATION_TIME + "`, "
            + "la.`" + COLUMN_NAME_INACTIVATION_TIME + "`, "
            + "la." + COLUMN_NAME_LEVEL + ", "
            + "la." + COLUMN_NAME_NAME + " "
            + "FROM liveAlarms la LIMIT ? OFFSET ?;";

    private static final String SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET = ""
            + "SELECT "
            + "ha." + COLUMN_NAME_NAME + ", "
            + "ha." + COLUMN_NAME_TIME + ", "
            + "ha." + COLUMN_NAME_DESCRIPTION + " "
            + "FROM historyAlarms ha WHERE "
            + "DATE_FORMAT(ha." + COLUMN_NAME_TIME + ", '%Y-%m-%d') = ? "
            + "AND "
            + "ha." + COLUMN_NAME_NAME + " RLIKE ? LIMIT ? OFFSET ?;";

    private static final String SELECT_UNIQUENESS_TOKEN_FROM_PLC_ALARMS_WHERE_ID = ""
            + "SELECT "
            + "pa." + COLUMN_NAME_UNIQUENESS_TOKEN + " "
            + "FROM plcAlarms pa WHERE "
            + "pa." + COLUMN_NAME_ID + " = ?;";

    private static final String UPDATE_PLC_ALARMS_SET_ACKNOWLEDGE_TIME_WHERE_ID = ""
            + "UPDATE plcAlarms pa SET "
            + "pa." + COLUMN_NAME_ACKNOWLEDGE_TIME + " = from_unixtime(unix_timestamp()) "
            + "WHERE "
            + "pa." + COLUMN_NAME_ID + " = ?;";

    private final JdbcTemplate jdbcTemplate;

    PlcAlarmsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LiveAlarm> getLiveAlarms(int offset, int limit) throws DataAccessException{
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
    public Optional<Integer> getUniquenessToken(int id) throws DataAccessException {
        Integer uniquenessToken = jdbcTemplate.queryForObject(SELECT_UNIQUENESS_TOKEN_FROM_PLC_ALARMS_WHERE_ID,
                new Object[]{id}, Integer.class);
        return Optional.ofNullable(uniquenessToken);
    }

    private class LiveAlarmRowMapper implements RowMapper<LiveAlarm> {

        @Override
        public LiveAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {

            LiveAlarm liveAlarm = new LiveAlarm();
            String inactivationTime = rs.getString(COLUMN_NAME_INACTIVATION_TIME);

            liveAlarm.setId(rs.getInt(COLUMN_NAME_ID));
            liveAlarm.setActivationTime(rs.getString(COLUMN_NAME_ACTIVATION_TIME));
            liveAlarm.setInactivationTime(inactivationTime == null ? "" : inactivationTime);
            liveAlarm.setLevel(rs.getString(COLUMN_NAME_LEVEL));
            liveAlarm.setName(rs.getString(COLUMN_NAME_NAME));

            return liveAlarm;
        }
    }

    private class HistoryAlarmRowMapper implements RowMapper<HistoryAlarm>{

        @Override
        public HistoryAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {

            HistoryAlarm historyAlarm = new HistoryAlarm();
            historyAlarm.setTime(rs.getString(COLUMN_NAME_TIME));
            historyAlarm.setName(rs.getString(COLUMN_NAME_NAME));
            historyAlarm.setDescription(rs.getString(COLUMN_NAME_DESCRIPTION));

            return historyAlarm;
        }
    }
}
