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
        LOG.debug("getAlarmLevel: " + user.getUsername());
        return highestAlarmLevelDAO.selectAlarmLevel(user);
    }

    @Override
    public int putAlarmLevel(User user, int alarmLevel) {
        LOG.debug("putAlarmLevel: " + user.getUsername() + ", alarmLevel: " + alarmLevel);
        return alarmLevel;
    }

    @Override
    public void removeAlarmLevel(User user) {
        LOG.debug("removeAlarmLevel: " + user.getUsername());
    }
}
