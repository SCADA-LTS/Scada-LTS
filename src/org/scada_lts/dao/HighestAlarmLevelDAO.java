package org.scada_lts.dao;

import com.serotonin.mango.rt.event.AlarmLevels;
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

@Repository
public class HighestAlarmLevelDAO implements IHighestAlarmLevelDAO {
    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelDAO.class);

    private static final String COLUMN_NAME_MAX = "max";

    private static final String SQL = ""
            + "select "
            + "max(e.alarmLevel) as max "
            + "from "
            + "userEvents u "
            + "join events e on u.eventId=e.id "
            + "where "
            + "u.silenced='N' and userId=?";

    private static final String COLUMN_NAME_USERID = "userId";
    // @formatter:off
    private static final String ALL = ""
            + "select "
            + "max(e.alarmLevel) as max,"
            + "userId "
            + "from "
            + "userEvents u "
            + "join events e on u.eventId=e.id "
            + "where "
            + "u.silenced='N' "
            + "group by "
            + "userId";

    // @formatter:off

    @Override
    public UserAlarmLevel selectAlarmLevel(User user) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SQL, new Object[]{user.getId()}, rs -> {
                if(rs.next()) {
                    UserAlarmLevel userAlarmLevel = new UserAlarmLevel();
                    userAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
                    userAlarmLevel.setUserId(user.getId());
                    return userAlarmLevel;
                }
                return new UserAlarmLevel(user.getId(), AlarmLevels.NONE);
            });
        } catch (Exception e) {
            LOG.warn(e);
            return new UserAlarmLevel(user.getId(), -1);
        }
    }

    @Override
    public List<UserAlarmLevel> selectAlarmLevels() {
        try {
            @SuppressWarnings("unchecked")
            List<UserAlarmLevel> userAlarmLevels = DAO.getInstance().getJdbcTemp().query(ALL, new RowMapper() {
                @Override
                public UserAlarmLevel mapRow(ResultSet rs, int rownumber) throws SQLException {
                    UserAlarmLevel userAlarmLevel = new UserAlarmLevel();
                    userAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
                    userAlarmLevel.setUserId(rs.getInt(COLUMN_NAME_USERID));
                    return userAlarmLevel;
                }
            });

            return userAlarmLevels;
        } catch (Exception e) {
            LOG.error(e);
            return Collections.emptyList();
        }
    }
}
