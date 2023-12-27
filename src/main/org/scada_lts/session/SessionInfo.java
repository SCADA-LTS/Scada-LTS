/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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

package org.scada_lts.session;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;

/** 
 * @author Abil'I.T. development team, sdt@abilit.eu
 */
public final class SessionInfo {

	private final Log LOGGER = LogFactory.getLog(SessionInfo.class);
	private HttpSession session;
	
	/**
	 * key storing ip address of logged user
	 */
	private static final String SESSION_LOG_ON_IP_ADDR = "com.abilit.logOnIpAddr";
	
	/**
	 * Constructor SessionInfo
	 * @param session
	 */
	public SessionInfo(HttpSession session) {
		this.session = session; 
	}
	
	/**
	 * Protection against creating object without session
	 */
	@SuppressWarnings("unused")
	private SessionInfo() { }
	
	/**
	 * get Username in session
	 * 
	 * @return
	 */
	public String getUsername() {
		if (session != null) {
			return session.getAttribute(Common.SESSION_USER) != null ? ((User) session.getAttribute(Common.SESSION_USER)).getUsername() : "";
		} 
		return "session doesn't exist";
	}
	
	/**
	 * get max inactive interval for session in format HH:mm:ss
	 * @return
	 */
	public String getMaxInactiveInterval() {
		return session != null ? new SimpleDateFormat(" HH:mm:ss").format(new Time(session.getMaxInactiveInterval())): "";
	}
	
	/**
	 * get last access time for session
	 * @return
	 */
	public String getLastAccessedTime() {
		return session != null ? DateFormat.getDateTimeInstance().format(new Date(session.getLastAccessedTime())): "";
	}
	
	/**
	 * get session id
	 * @return
	 */
	public String getSessionId() {
		return session != null ? session.getId(): "";
	}
	
	/**
	 * get ip address when user sign in 
	 * @return
	 */
	public String getLogOnIpAddr() {
		return session != null ? (String) session.getAttribute(SESSION_LOG_ON_IP_ADDR): "";
	}
	
	/**
	 * set address ip when user sign in
	 * @param logOnIpAddr
	 */
	public void setLogOnIpAddr(String logOnIpAddr) {
		session.setAttribute(SESSION_LOG_ON_IP_ADDR, logOnIpAddr);
	}
	
	/**
	 * check existing ip address
	 * @return
	 */
	public boolean haveLogOnIPAddr() {
		return session != null ? (getLogOnIpAddr() != null) : false;
	}
	
	/**
	 * create object JSON with data for presentation
	 * @return
	 */
	public JSONObject toJSON() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("id", " "+getSessionId());
			obj.put("user", " " + getUsername());
			obj.put("lastAccessTime", " " + getLastAccessedTime());
			obj.put("maxInactiveInterval", " " + getMaxInactiveInterval());
			obj.put("logOnIpAddr", " " + getLogOnIpAddr());
		} catch (JSONException e) {
			LOGGER.info(e);
		}

		return obj;
	}

}
