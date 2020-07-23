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
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class ApiAlarmsLiveRowMapper implements RowMapper<LiveAlarm>{

        @Override
        public LiveAlarm mapRow(ResultSet rs, int rowNum) throws SQLException {

                LiveAlarm liveAlarm = new LiveAlarm();
                liveAlarm.setId(rs.getInt("id"));
                liveAlarm.setActivationTime(String.valueOf(rs.getString("activation-time")));
                liveAlarm.setInactivationTime(
                        (rs.getString("inactivation-time") == null)
                        ?""
                        :rs.getString("inactivation-time")
                );
                liveAlarm.setLevel(String.valueOf(rs.getString("level")));
                liveAlarm.setName(String.valueOf(rs.getString("name")));

        return liveAlarm;
    }
}
