/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
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

package org.scada_lts.dao.model.event;

import java.util.List;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance.RtnCauses;
import com.serotonin.mango.vo.UserComment;

/**
 * Event bean
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class Event {

	private long id;

	/**
	 * Configuration field. Provided by the event producer. Identifies where the
	 * event came from and what it means. (EventType)
	 */
	private int eventType;

	
	private int typeRef1;
	
	private int typeRef2;
	
	/**
	 * State field. The time that the event became active.
	 */
	private long activeTimestamp;

	/**
	 * Configuration field. Is this type of event capable of returning to normal
	 * (true), or is it stateless (false).
	 */
	private boolean rtnApplicable;

	/**
	 * State field. The time that the event returned to normal.
	 */
	private long rtnTimestamp;

	/**
	 * State field. The action that caused the event to RTN. One of
	 * {@link RtnCauses}
	 */
	private int rtnCause;

	/**
	 * Configuration field. The alarm level assigned to the event.
	 * 
	 * @see AlarmLevels
	 */
	private int alarmLevel;

	/**
	 * Configuration field. The message associated with the event.
	 */
	private String message;
	
	/**
	 * ?
	 */
	private int ackTS;

	/**
	 * User comments on the event. Added in the events interface after the event
	 * has been raised.
	 */
	private List<UserComment> eventComments;
	
	private long actUserId;
	
	private String userName;
	
	private long alternateAckSource;

	public Event() {
		//
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRtnTimestamp() {
		return rtnTimestamp;
	}

	public void setRtnTimestamp(long rtnTimestamp) {
		this.rtnTimestamp = rtnTimestamp;
	}

	public int getRtnCause() {
		return rtnCause;
	}

	public void setRtnCause(int rtnCause) {
		this.rtnCause = rtnCause;
	}

	public List<UserComment> getEventComments() {
		return eventComments;
	}

	public void setEventComments(List<UserComment> eventComments) {
		this.eventComments = eventComments;
	}

	public int getEventType() {
		return eventType;
	}

	public long getActiveTimestamp() {
		return activeTimestamp;
	}

	public boolean isRtnApplicable() {
		return rtnApplicable;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public String getMessage() {
		return message;
	}

	public int getTypeRef1() {
		return typeRef1;
	}

	public void setTypeRef1(int typeRef1) {
		this.typeRef1 = typeRef1;
	}

	public int getTypeRef2() {
		return typeRef2;
	}

	public void setTypeRef2(int typeRef2) {
		this.typeRef2 = typeRef2;
	}

	public long getActUserId() {
		return actUserId;
	}

	public void setActUserId(long actUserId) {
		this.actUserId = actUserId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getAlternateAckSource() {
		return alternateAckSource;
	}

	public void setAlternateAckSource(long alternateAckSource) {
		this.alternateAckSource = alternateAckSource;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public void setActiveTimestamp(long activeTimestamp) {
		this.activeTimestamp = activeTimestamp;
	}

	public void setRtnApplicable(boolean rtnApplicable) {
		this.rtnApplicable = rtnApplicable;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getAckTS() {
		return ackTS;
	}

	public void setAckTS(int ackTS) {
		this.ackTS = ackTS;
	}

}
