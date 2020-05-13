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
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Create by at Mateusz Hyski
 *
 * @author hyski.mateusz@gmail.com
 */
class PointValuesStorungsAndAlarmsRowMapper implements RowMapper<PointValuesStorungsAndAlarms>{

        @Override
        public PointValuesStorungsAndAlarms mapRow(ResultSet rs, int rowNum) throws SQLException {

                PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms = new PointValuesStorungsAndAlarms();
                pointValuesStorungsAndAlarms.setId(rs.getInt("id"));
                pointValuesStorungsAndAlarms.setPointId(String.valueOf(rs.getString("pointId")));
                pointValuesStorungsAndAlarms.setPointXid((String.valueOf(rs.getString("pointXid"))));
                pointValuesStorungsAndAlarms.setPointType(String.valueOf(rs.getString("pointType")));
                pointValuesStorungsAndAlarms.setTriggerName(String.valueOf(rs.getString("triggerTime")));
                pointValuesStorungsAndAlarms.setInactiveTime(String.valueOf(rs.getString("inactiveTime")));
                pointValuesStorungsAndAlarms.setAcknowledgeTime(String.valueOf(rs.getString("acknowledgeTime")));
                pointValuesStorungsAndAlarms.setLastpointValue(String.valueOf(rs.getString("lastpointValue")));

        return pointValuesStorungsAndAlarms;
    }
}
