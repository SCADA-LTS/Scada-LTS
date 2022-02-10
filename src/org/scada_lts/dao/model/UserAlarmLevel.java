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
package org.scada_lts.dao.model;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.User;

/**
 * Model for buffering UnsilencedAlarmLevel
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class UserAlarmLevel {

	private int userId = -1;
	private int alarmLevel = AlarmLevels.NONE;

	public UserAlarmLevel() {
	}

	public UserAlarmLevel(User user, int alarmLevel) {
		this.userId = user.getId();
		this.alarmLevel = alarmLevel;
	}

	public static UserAlarmLevel onlyUser(User user) {
		return new UserAlarmLevel(user, AlarmLevels.NONE);
	}

	public int getUserId() {
		return userId;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	@Override
	public String toString() {
		return "UserAlarmLevel{" +
				"userId=" + userId +
				", alarmLevel=" + alarmLevel +
				'}';
	}
}
