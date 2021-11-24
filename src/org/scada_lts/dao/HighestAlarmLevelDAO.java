package org.scada_lts.dao;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.UserAlarmLevelEvent;
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
    private static final String COLUMN_NAME_EVENT_ID = "eventId";

    private static final String SQL = ""
            + "select "
            + "max(e.alarmLevel) as max, "
            + "max(e.id) as eventId "
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
            + "max(e.id) as eventId,"
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
    public Optional<UserAlarmLevelEvent> selectAlarmLevel(User user) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SQL, new Object[]{user.getId()}, rs -> {
                if(rs.next()) {
                    UserAlarmLevelEvent userAlarmLevel = new UserAlarmLevelEvent();
                    userAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
                    userAlarmLevel.setUserId(user.getId());
                    userAlarmLevel.setEventId(rs.getInt(COLUMN_NAME_EVENT_ID));
                    return Optional.of(userAlarmLevel);
                }
                return Optional.of(new UserAlarmLevelEvent(user.getId(), AlarmLevels.NONE, -1));
            });
        } catch (Exception e) {
            LOG.warn(e);
            return Optional.empty();
        }
    }

    @Override
    public List<UserAlarmLevelEvent> selectAlarmLevels() {
        try {
            @SuppressWarnings("unchecked")
            List<UserAlarmLevelEvent> userAlarmLevels = DAO.getInstance().getJdbcTemp().query(ALL, new RowMapper() {
                @Override
                public UserAlarmLevelEvent mapRow(ResultSet rs, int rownumber) throws SQLException {
                    UserAlarmLevelEvent userAlarmLevel = new UserAlarmLevelEvent();
                    userAlarmLevel.setAlarmLevel(rs.getInt(COLUMN_NAME_MAX));
                    userAlarmLevel.setUserId(rs.getInt(COLUMN_NAME_USERID));
                    userAlarmLevel.setEventId(rs.getInt(COLUMN_NAME_EVENT_ID));
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
