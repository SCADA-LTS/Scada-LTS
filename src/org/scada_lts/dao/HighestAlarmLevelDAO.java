package org.scada_lts.dao;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

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

    // @formatter:off

    @Override
    public int selectAlarmLevel(User user) {
        try {
            return DAO.getInstance().getJdbcTemp().query(SQL, new Object[]{user.getId()}, resultSet -> {
                if(resultSet.next()) {
                    return resultSet.getInt(COLUMN_NAME_MAX);
                }
                return 0;
            });
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return -1;
        }
    }
}
