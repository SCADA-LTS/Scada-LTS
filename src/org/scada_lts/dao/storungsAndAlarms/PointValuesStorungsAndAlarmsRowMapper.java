package org.scada_lts.dao.storungsAndAlarms;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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
