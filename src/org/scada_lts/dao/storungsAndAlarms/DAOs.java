package org.scada_lts.dao.storungsAndAlarms;

import java.util.HashMap;
import java.util.Map;

class DAOs {
    private static PointValuesStorungsAndAlarmsDAO pointValuesStorungsAndAlarmsDAO = new PointValuesStorungsAndAlarmsDAO();
    enum DAOInstances{
        POINTVALUESSTORUNGSANDALARMS
    }
    private static Map<DAOInstances,Object> daos = new HashMap<>();
    static {
        daos.put(DAOInstances.POINTVALUESSTORUNGSANDALARMS,pointValuesStorungsAndAlarmsDAO);
    }
    public static PointValuesStorungsAndAlarmsDAO getPointValuesStorungsAndAlarms(){
        return (PointValuesStorungsAndAlarmsDAO) daos.get(DAOInstances.POINTVALUESSTORUNGSANDALARMS);
    }
}
