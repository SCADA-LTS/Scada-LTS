package org.scada_lts.dao.storungsAndAlarms;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class PointValuesStorungsAndAlarmsRowMapper implements RowMapper<PointValuesStorungsAndAlarms>{

        @Override
        public PointValuesStorungsAndAlarms mapRow(ResultSet rs, int rowNum) throws SQLException {

                PointValuesStorungsAndAlarms pointValuesStorungsAndAlarms = new PointValuesStorungsAndAlarms();
                pointValuesStorungsAndAlarms.setPointId("A");
                pointValuesStorungsAndAlarms.setPointXid("A");
                pointValuesStorungsAndAlarms.setAcknowledgeTime("A");
                pointValuesStorungsAndAlarms.setInactiveTime("A");
                pointValuesStorungsAndAlarms.setTriggerName("A");
                pointValuesStorungsAndAlarms.setLastpointValue("A");

        return pointValuesStorungsAndAlarms;
    }
}
