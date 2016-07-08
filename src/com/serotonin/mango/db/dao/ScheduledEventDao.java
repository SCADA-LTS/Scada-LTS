/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * @author Matthew Lohbihler
 * 
 */
public class ScheduledEventDao extends BaseDao {
    private static final String SCHEDULED_EVENT_SELECT = "select id, xid, alias, alarmLevel, scheduleType, "
            + "  returnToNormal, disabled, activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, "
            + "  activeCron, inactiveYear, inactiveMonth, inactiveDay, inactiveHour, inactiveMinute, inactiveSecond, "
            + "inactiveCron from scheduledEvents ";

    public String generateUniqueXid() {
        return generateUniqueXid(ScheduledEventVO.XID_PREFIX, "scheduledEvents");
    }

    public boolean isXidUnique(String xid, int excludeId) {
        return isXidUnique(xid, excludeId, "scheduledEvents");
    }

    public List<ScheduledEventVO> getScheduledEvents() {
        return query(SCHEDULED_EVENT_SELECT + " order by scheduleType", new ScheduledEventRowMapper());
    }

    public ScheduledEventVO getScheduledEvent(int id) {
        ScheduledEventVO se = queryForObject(SCHEDULED_EVENT_SELECT + "where id=?", new Object[] { id },
                new ScheduledEventRowMapper());
        return se;
    }

    public ScheduledEventVO getScheduledEvent(String xid) {
        return queryForObject(SCHEDULED_EVENT_SELECT + "where xid=?", new Object[] { xid },
                new ScheduledEventRowMapper(), null);
    }

    class ScheduledEventRowMapper implements GenericRowMapper<ScheduledEventVO> {
        public ScheduledEventVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduledEventVO se = new ScheduledEventVO();
            int i = 0;
            se.setId(rs.getInt(++i));
            se.setXid(rs.getString(++i));
            se.setAlias(rs.getString(++i));
            se.setAlarmLevel(rs.getInt(++i));
            se.setScheduleType(rs.getInt(++i));
            se.setReturnToNormal(charToBool(rs.getString(++i)));
            se.setDisabled(charToBool(rs.getString(++i)));
            se.setActiveYear(rs.getInt(++i));
            se.setActiveMonth(rs.getInt(++i));
            se.setActiveDay(rs.getInt(++i));
            se.setActiveHour(rs.getInt(++i));
            se.setActiveMinute(rs.getInt(++i));
            se.setActiveSecond(rs.getInt(++i));
            se.setActiveCron(rs.getString(++i));
            se.setInactiveYear(rs.getInt(++i));
            se.setInactiveMonth(rs.getInt(++i));
            se.setInactiveDay(rs.getInt(++i));
            se.setInactiveHour(rs.getInt(++i));
            se.setInactiveMinute(rs.getInt(++i));
            se.setInactiveSecond(rs.getInt(++i));
            se.setInactiveCron(rs.getString(++i));
            return se;
        }
    }

    public void saveScheduledEvent(final ScheduledEventVO se) {
        if (se.getId() == Common.NEW_ID)
            insertScheduledEvent(se);
        else
            updateScheduledEvent(se);
    }

    private void insertScheduledEvent(ScheduledEventVO se) {
        if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
            try {
                //id = doInsert(EVENT_INSERT, args, EVENT_INSERT_TYPES);
                Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                            Common.getEnvironmentProfile().getString("db.username"),
                                            Common.getEnvironmentProfile().getString("db.password"));
                PreparedStatement preStmt = conn.prepareStatement("insert into scheduledEvents ("
                                    + "  xid, alarmLevel, alias, scheduleType, returnToNormal, disabled, "
                                    + "  activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, activeCron, "
                                    + "  inactiveYear, inactiveMonth, inactiveDay, inactiveHour, inactiveMinute, inactiveSecond, inactiveCron "
                                    + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
               
                preStmt.setString(1, se.getXid());
                preStmt.setInt(2, se.getAlarmLevel());
                preStmt.setString(3, se.getAlias());
                preStmt.setInt(4, se.getScheduleType());
                preStmt.setString(5, boolToChar(se.isReturnToNormal()));
                preStmt.setString(6, boolToChar(se.isDisabled()));
                preStmt.setInt(7, se.getActiveYear());
                preStmt.setInt(8, se.getActiveMonth());
                preStmt.setInt(9, se.getActiveDay());
                preStmt.setInt(10, se.getActiveHour());
                preStmt.setInt(11, se.getActiveMinute());
                preStmt.setInt(12, se.getActiveSecond());
                preStmt.setString(13, se.getActiveCron());
                preStmt.setInt(14, se.getInactiveYear());
                preStmt.setInt(15, se.getInactiveMonth());
                preStmt.setInt(16, se.getInactiveDay());
                preStmt.setInt(17, se.getInactiveHour());
                preStmt.setInt(18, se.getInactiveMinute());
                preStmt.setInt(19, se.getInactiveSecond());
                preStmt.setString(20, se.getInactiveCron());
                preStmt.executeUpdate();

                ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('scheduledevents_id_seq')");
                resSEQ.next();
                int id = resSEQ.getInt(1);

                conn.close(); 

                se.setId(id);

            } catch (SQLException ex) {
                ex.printStackTrace();
                se.setId(0);
            }
        } 
        else{
            se.setId(doInsert(
                            "insert into scheduledEvents ("
                                    + "  xid, alarmLevel, alias, scheduleType, returnToNormal, disabled, "
                                    + "  activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, activeCron, "
                                    + "  inactiveYear, inactiveMonth, inactiveDay, inactiveHour, inactiveMinute, inactiveSecond, inactiveCron "
                                    + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[] { se.getXid(),
                                    se.getAlarmLevel(), se.getAlias(), se.getScheduleType(),
                                    boolToChar(se.isReturnToNormal()), boolToChar(se.isDisabled()), se.getActiveYear(),
                                    se.getActiveMonth(), se.getActiveDay(), se.getActiveHour(), se.getActiveMinute(),
                                    se.getActiveSecond(), se.getActiveCron(), se.getInactiveYear(), se.getInactiveMonth(),
                                    se.getInactiveDay(), se.getInactiveHour(), se.getInactiveMinute(),
                                    se.getInactiveSecond(), se.getInactiveCron() }));
        }
        AuditEventType.raiseAddedEvent(AuditEventType.TYPE_SCHEDULED_EVENT, se);
    }

    private void updateScheduledEvent(ScheduledEventVO se) {
        ScheduledEventVO old = getScheduledEvent(se.getId());
        ejt.update(
                        "update scheduledEvents set "
                                + "  xid=?, alarmLevel=?, alias=?, scheduleType=?, returnToNormal=?, disabled=?, "
                                + "  activeYear=?, activeMonth=?, activeDay=?, activeHour=?, activeMinute=?, activeSecond=?, activeCron=?, "
                                + "  inactiveYear=?, inactiveMonth=?, inactiveDay=?, inactiveHour=?, inactiveMinute=?, inactiveSecond=?, "
                                + "  inactiveCron=? " + "where id=?", new Object[] { se.getXid(), se.getAlarmLevel(),
                                se.getAlias(), se.getScheduleType(), boolToChar(se.isReturnToNormal()),
                                boolToChar(se.isDisabled()), se.getActiveYear(), se.getActiveMonth(),
                                se.getActiveDay(), se.getActiveHour(), se.getActiveMinute(), se.getActiveSecond(),
                                se.getActiveCron(), se.getInactiveYear(), se.getInactiveMonth(), se.getInactiveDay(),
                                se.getInactiveHour(), se.getInactiveMinute(), se.getInactiveSecond(),
                                se.getInactiveCron(), se.getId() });
        AuditEventType.raiseChangedEvent(AuditEventType.TYPE_SCHEDULED_EVENT, old, se);
    }

    public void deleteScheduledEvent(final int scheduledEventId) {
        ScheduledEventVO se = getScheduledEvent(scheduledEventId);
        final ExtendedJdbcTemplate ejt2 = ejt;
        if (se != null) {
            getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    ejt2.update("delete from eventHandlers where eventTypeId=" + EventType.EventSources.SCHEDULED
                            + " and eventTypeRef1=?", new Object[] { scheduledEventId });
                    ejt2.update("delete from scheduledEvents where id=?", new Object[] { scheduledEventId });
                }
            });

            AuditEventType.raiseDeletedEvent(AuditEventType.TYPE_SCHEDULED_EVENT, se);
        }
    }
}
