package org.scada_lts.service;

public interface UserHighestAlarmLevelListener {
	
	public void onAlarmTimestampChange(long alarmTimestamp);
	
	public void onEventRaise(int eventId, int userId, int alarmLevel);
	public void onEventAck(int eventId, int userId);
	public void onEventToggle(int eventId, int userId, boolean isSilenced);
}
