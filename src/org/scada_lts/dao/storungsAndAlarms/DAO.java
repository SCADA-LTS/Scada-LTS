package org.scada_lts.dao.storungsAndAlarms;

public class DAO {
    private static PointValuesStorungsAndAlarmsDAO pointValuesStorungsAndAlarmsDAO = new PointValuesStorungsAndAlarmsDAO();

    public static PointValuesStorungsAndAlarmsDAO getInstance(){
        return pointValuesStorungsAndAlarmsDAO;
    }
}
