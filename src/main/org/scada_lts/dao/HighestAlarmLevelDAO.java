package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.UserAlarmLevel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HighestAlarmLevelDAO implements IHighestAlarmLevelDAO {
    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelDAO.class);

    private static final String COLUMN_NAME_MAX = "max";
    private static final String COLUMN_NAME_USERID = "userId";

    private static final String SELECT_HIGHEST_ALARM_LEVEL_BY_USERID = ""
            + "select "
            + "max(e.alarmLevel) as " + COLUMN_NAME_MAX + " "
            + "from "
            + "userEvents u "
            + "join events e on u.eventId=e.id "
            + "where "
            + "(e.ackTs is null or e.ackTs = 0) "
            + "and u.silenced='N' and " + COLUMN_NAME_USERID + "=? "
            + "and (e.rtnCause is null or e.rtnCause = 0) "
            + "and e.rtnApplicable = 'Y'";

    // @formatter:off
    private static final String SELECT_HIGHEST_ALARM_LEVEL_FOR_ALL_USERS_GROUP_BY_USERID = ""
            + "select "
            + "max(e.alarmLevel) as " + COLUMN_NAME_MAX + ", "
            + COLUMN_NAME_USERID + " "
            + "from "
            + "userEvents u "
            + "join events e on u.eventId=e.id "
            + "where "
            + "(e.ackTs is null or e.ackTs = 0) "
            + "and u.silenced='N' "
            + "and (e.rtnCause is null or e.rtnCause = 0) "
            + "and e.rtnApplicable = 'Y' "
            + "group by "
            + COLUMN_NAME_USERID;

    // @formatter:off

    @Override
    public Optional<UserAlarmLevel> selectAlarmLevel(User user) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SELECT_HIGHEST_ALARM_LEVEL_BY_USERID, new Object[]{user.getId()}, rs -> {
                if(rs.next()) {
                    UserAlarmLevel userAlarmLevel = new UserAlarmLevel();
                    userAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
                    userAlarmLevel.setUserId(user.getId());
                    return Optional.of(userAlarmLevel);
                }
                return Optional.of(UserAlarmLevel.onlyUser(user));
            });
        } catch (Exception e) {
            LOG.warn(e);
            return Optional.empty();
        }
    }

    @Override
    public List<UserAlarmLevel> selectAlarmLevels() {
        try {
            return DAO.getInstance().getJdbcTemp().query(SELECT_HIGHEST_ALARM_LEVEL_FOR_ALL_USERS_GROUP_BY_USERID, (rs, rownumber) -> {
                UserAlarmLevel userAlarmLevel = new UserAlarmLevel();
                userAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
                userAlarmLevel.setUserId(rs.getInt(COLUMN_NAME_USERID));
                return userAlarmLevel;
            });
        } catch (Exception e) {
            LOG.error(e);
            return Collections.emptyList();
        }
    }
}
