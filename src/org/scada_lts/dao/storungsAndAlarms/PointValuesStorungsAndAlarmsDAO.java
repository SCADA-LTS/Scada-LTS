package org.scada_lts.dao.storungsAndAlarms;
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

import org.json.JSONArray;
import org.scada_lts.dao.DAO;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class PointValuesStorungsAndAlarmsDAO {

    private static final String GET_LIVES_SQL = "SELECT `id`, `activation-time`, `inactivation-time`, `level`, `name` FROM apiAlarmsLive LIMIT ? OFFSET ?;";
    private static final String ACKNOWLEDGE_SQL = "UPDATE plcAlarms SET acknowledgeTime=from_unixtime(unix_timestamp()) WHERE id=?;";
    private static final String GET_UNIQUENESS_TOKEN_SQL = "SELECT uniquenessToken FROM plcAlarms WHERE id = ?; ";
    private static final String GET_HISTORY_SQL = "SELECT name, time, description FROM apiAlarmsHistory WHERE DATE_FORMAT(time, '%Y-%m-%d')=? AND name RLIKE ? LIMIT ? OFFSET ?";

    public List<ApiAlarmsLive> getLiveAlarms(int offset,int limit) throws DataAccessException{
        return DAO.getInstance().getJdbcTemp().query(
                GET_LIVES_SQL, new Object[]{limit, offset},
                new ApiAlarmsLiveRowMapper());
    }
    public List<ApiAlarmsHistory> getHistoryAlarmsByDateDayAndFilter(
            String dayDate,
            String dataPointNameFilter,
            int offset,
            int limit) throws DataAccessException {

        String regex = dataPointNameFilter == null || "EMPTY".equals(dataPointNameFilter) ? "(.*)" : dataPointNameFilter;
        return DAO.getInstance().getJdbcTemp().query(
                GET_HISTORY_SQL, new Object[]{dayDate, regex, limit, offset},
                new ApiAlarmsHistoryRowMapper());
    }
    public int setAcknowledge(int id) throws DataAccessException {

        int uniquenessToken = _getUniquenessToken(id).orElse(-1);
        if(uniquenessToken == -1)
            return -1;
        if(uniquenessToken == 0)
            return 0;
        return DAO.getInstance().getJdbcTemp().update(ACKNOWLEDGE_SQL, id);

    }

    private static Optional<Integer> _getUniquenessToken(int id) throws DataAccessException {
        Integer uniquenessToken = DAO.getInstance().getJdbcTemp().queryForObject(GET_UNIQUENESS_TOKEN_SQL, new Object[]{id}, Integer.class);
        return Optional.ofNullable(uniquenessToken);
    }
}
