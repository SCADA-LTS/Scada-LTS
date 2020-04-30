package org.scada_lts.dao;

import org.json.JSONObject;

public interface PointValuesStorungsAndAlarms {

    JSONObject getStorungs(int id);

    JSONObject getAlarms(int id);

    JSONObject getHistoryAlarmsById(int id);

}
