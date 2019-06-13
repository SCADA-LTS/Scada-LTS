package org.scada_lts.web.ws.beans;

import java.util.HashSet;
import java.util.Set;


public class UserAlarmLevel {
    int         level = 0;
    long        timestamp = 0;
    String      username;     
    Set<String> sessions = new HashSet<String>();
    

    public UserAlarmLevel(String username) {
        this.username = username;
        this.level = 0;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
        timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean addSession(String sessionId) {
    	return sessions.add(sessionId);
    }

    public int removeSession(String sessionId) {
    	sessions.remove(sessionId);
    	return sessions.size();
    }
}

