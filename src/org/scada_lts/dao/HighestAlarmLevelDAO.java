package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.UserAlarmLevel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class HighestAlarmLevelDAO implements IHighestAlarmLevelDAO {
    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelDAO.class);

    private static final String COLUMN_NAME_MAX = "max";
    private static final String COLUMN_NAME_USERID = "userId";

    private static final String SQL = ""
            + "select "
            + "max(e.alarmLevel) as max "
            + "from "
            + "userEvents u "
            + "join events e on u.eventId=e.id "
            + "where "
            + "(e.ackTs is null or e.ackTs = 0) and "
            + "u.silenced='N' and userId=?";

    // @formatter:off
    private static final String ALL = ""
            + "select "
            + "max(e.alarmLevel) as max, "
            + "userId "
            + "from "
            + "userEvents u "
            + "join events e on u.eventId=e.id "
            + "where "
            + "(e.ackTs is null or e.ackTs = 0) and "
            + "u.silenced='N' "
            + "group by "
            + "userId";

    // @formatter:off

    @Override
    public Optional<UserAlarmLevel> selectAlarmLevel(User user) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SQL, new Object[]{user.getId()}, rs -> {
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
            return DAO.getInstance().getJdbcTemp().query(ALL, (rs, rownumber) -> {
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
