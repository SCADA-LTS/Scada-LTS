package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IHighestAlarmLevelDAO;


public class HighestAlarmLevelCache implements HighestAlarmLevelCachable {

    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelCache.class);

    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;

    public HighestAlarmLevelCache(IHighestAlarmLevelDAO highestAlarmLevelDAO) {
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
    }

    @Override
    public int getAlarmLevel(User user) {
        LOG.info("getAlarmLevel user: " + getUserIdentifier(user));
        return highestAlarmLevelDAO.selectAlarmLevel(user).getAlarmLevel();
    }

    @Override
    public int putAlarmLevel(User user, int alarmLevel) {
        LOG.info("putAlarmLevel user: " + getUserIdentifier(user) + ", alarmLevel: " + alarmLevel);
        return alarmLevel;
    }

    @Override
    public void removeAlarmLevel(User user) {
        LOG.info("removeAlarmLevel user: " + getUserIdentifier(user));
    }

    @Override
    public void resetAlarmLevel() {
        LOG.info("removeAlarmLevel all");
    }

    private Object getUserIdentifier(User user) {
        return user.getUsername() == null ? user.getId() : user.getUsername();
    }
}
