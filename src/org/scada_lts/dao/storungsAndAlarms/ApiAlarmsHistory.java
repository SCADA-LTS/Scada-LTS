package org.scada_lts.dao.storungsAndAlarms;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiAlarmsHistory {

    private String time;
    private String name;
    private String description;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
