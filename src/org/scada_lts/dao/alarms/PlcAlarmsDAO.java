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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
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

    private static final Log LOG = LogFactory.getLog(PlcAlarmsDAO.class);

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_ACKNOWLEDGE_TIME = "acknowledgeTime";
    private static final String COLUMN_NAME_INACTIVE_TIME = "inactiveTime";
    private static final String COLUMN_NAME_ACTIVE_TIME = "activeTime";
    private static final String COLUMN_NAME_POINT_ID = "dataPointId";

    private static final String COLUMN_NAME_ACTIVATION_TIME_VIEW = "activation-time";
    private static final String COLUMN_NAME_INACTIVATION_TIME_VIEW = "inactivation-time";
    private static final String COLUMN_NAME_LEVEL_VIEW = "level";
    private static final String COLUMN_NAME_NAME_VIEW = "name";

    private static final String SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET = ""
            + "SELECT "
            + "la." + COLUMN_NAME_ID + ", "
            + "la.`" + COLUMN_NAME_ACTIVATION_TIME_VIEW + "`, "
            + "la.`" + COLUMN_NAME_INACTIVATION_TIME_VIEW + "`, "
            + "la." + COLUMN_NAME_LEVEL_VIEW + ", "
            + "la." + COLUMN_NAME_NAME_VIEW + ", "
            + "la." + COLUMN_NAME_POINT_ID + " "
            + "FROM liveAlarms la LIMIT ? OFFSET ?;";

    private static final String SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET = ""
            + "SELECT "
            + "ha." + COLUMN_NAME_NAME_VIEW + ", "
            + "ha." + COLUMN_NAME_ACTIVE_TIME + ", "
            + "ha." + COLUMN_NAME_INACTIVE_TIME + ", "
            + "ha." + COLUMN_NAME_ACKNOWLEDGE_TIME + ", "
            + "ha." + COLUMN_NAME_LEVEL_VIEW + " "
            + "FROM historyAlarms ha WHERE "
            //+ "DATE_FORMAT(ha." + COLUMN_NAME_INACTIVE_TIME + ", '%Y-%m-%d') = ? "
            + "DATE_FORMAT(ha." + COLUMN_NAME_ACTIVE_TIME + ", '%Y-%m-%d') = ? "
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

    @Override
    public List<LiveAlarm> getLiveAlarms(int offset, int limit) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET,
                    new Object[]{limit, offset},
                    new LiveAlarmRowMapper());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
    @Override
    public List<HistoryAlarm> getHistoryAlarms(String dayDate, String regex, int offset, int limit) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET,
                    new Object[]{dayDate, regex, limit, offset},
                    new HistoryAlarmRowMapper());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
    @Override
    public boolean setAcknowledgeTime(int id) {
        try {
            int numberUpdateRows = DAO.getInstance().getJdbcTemp().update(UPDATE_PLC_ALARMS_SET_ACKNOWLEDGE_TIME_WHERE_ID, id);
            if(numberUpdateRows > 1) {
                throw new IllegalStateException("Update more than 1 row plcAlarms for id: " + id);
            }
            return true;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public Optional<Long> getInactiveTimeMs(int id) {
        try {
            Long inactiveTime = DAO.getInstance().getJdbcTemp().queryForObject(SELECT_INACTIVE_TIME_FROM_PLC_ALARMS_WHERE_ID,
                    new Object[]{id}, Long.class);
            return Optional.ofNullable(inactiveTime);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
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
            liveAlarm.setDataPointId(rs.getInt(COLUMN_NAME_POINT_ID));

            return liveAlarm;
        }
    }

    private class HistoryAlarmRowMapper implements RowMapper<HistoryAlarm>{

        @Override
        public HistoryAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {

            HistoryAlarm historyAlarm = new HistoryAlarm();
            historyAlarm.setActiveTime(rs.getString(COLUMN_NAME_ACTIVE_TIME));
            historyAlarm.setInactiveTime(rs.getString(COLUMN_NAME_INACTIVE_TIME));
            historyAlarm.setAcknowledgeTime(rs.getString(COLUMN_NAME_ACKNOWLEDGE_TIME));
            historyAlarm.setName(rs.getString(COLUMN_NAME_NAME_VIEW));
            historyAlarm.setLevel(rs.getInt(COLUMN_NAME_LEVEL_VIEW));

            return historyAlarm;
        }
    }
}
