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

import java.util.List;
import java.util.Optional;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 * @update kamil.jarmusik@gmail.com
 *
 */

class PlcAlarmsDAOImpl implements PlcAlarmsDAO {


    private static final String SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET = "SELECT `id`, `activation-time`, `inactivation-time`, `level`, `name` FROM liveAlarms LIMIT ? OFFSET ?;";
    private static final String UPDATE_PLC_ALARMS_SET_ACKNOWLEDGE_TIME_WHERE_ID = "UPDATE plcAlarms SET acknowledgeTime=from_unixtime(unix_timestamp()) WHERE id=?;";
    private static final String SELECT_UNIQUENESS_TOKEN_FROM_PLC_ALARMS_WHERE_ID = "SELECT uniquenessToken FROM plcAlarms WHERE id = ?; ";
    private static final String SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET = "SELECT name, time, description FROM historyAlarms WHERE DATE_FORMAT(time, '%Y-%m-%d')=? AND name RLIKE ? LIMIT ? OFFSET ?";

    private final JdbcTemplate jdbcTemplate;

    PlcAlarmsDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LiveAlarm> getLiveAlarms(int offset, int limit) throws DataAccessException{
        return jdbcTemplate.query(SELECT_FROM_LIVE_ALARMS_VIEW_LIMIT_OFFSET, new Object[]{limit, offset},
                new LiveAlarmRowMapper());
    }
    @Override
    public List<HistoryAlarm> getHistoryAlarms(String dayDate, String regex, int offset, int limit) throws DataAccessException {
        return jdbcTemplate.query(SELECT_FROM_HISTORY_ALARMS_VIEW_WHERE_TIME_AND_RLIKE_LIMIT_OFFSET, new Object[]{dayDate, regex, limit, offset},
                new HistoryAlarmRowMapper());
    }
    @Override
    public int setAcknowledgeTime(int id) throws DataAccessException {
        return jdbcTemplate.update(UPDATE_PLC_ALARMS_SET_ACKNOWLEDGE_TIME_WHERE_ID, id);
    }

    @Override
    public Optional<Integer> getUniquenessToken(int id) throws DataAccessException {
        Integer uniquenessToken = jdbcTemplate.queryForObject(SELECT_UNIQUENESS_TOKEN_FROM_PLC_ALARMS_WHERE_ID, new Object[]{id}, Integer.class);
        return Optional.ofNullable(uniquenessToken);
    }
}
