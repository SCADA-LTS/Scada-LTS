package com.serotonin.mango.view;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Here we have data like username and viewId and time since view is locked
 *
 * @author Mateusz Hyski {@link "mailto:mateusz.hyski@softq.pl;hyski.mateusz@gmail.com","ScadaLTS"}
 */
public class LockedViews {

    private String userName;

    private String viewXid;

    private String sessionId;

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public LockedViews(String userName, String viewXid, String sessionId) {
        this.userName = userName;
        this.viewXid = viewXid;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getViewXid() {
        return viewXid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("username", this.userName);
            json.put("viewXid", this.viewXid);
            json.put("sessionId", this.sessionId);
            json.put("timestamp", this.timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
