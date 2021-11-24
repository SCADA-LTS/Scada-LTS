package org.scada_lts.dao.cache;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IHighestAlarmLevelDAO;
import org.scada_lts.dao.model.UserAlarmLevelEvent;

public class HighestAlarmLevelCache implements HighestAlarmLevelCachable {

    private static final Log LOG = LogFactory.getLog(HighestAlarmLevelCache.class);

    private final IHighestAlarmLevelDAO highestAlarmLevelDAO;

    public HighestAlarmLevelCache(IHighestAlarmLevelDAO highestAlarmLevelDAO) {
        this.highestAlarmLevelDAO = highestAlarmLevelDAO;
    }

    @Override
    public UserAlarmLevelEvent getAlarmLevel(User user) {
        LOG.debug("getAlarmLevel user: " + getUserIdentifier(user));
        return highestAlarmLevelDAO.selectAlarmLevel(user).orElse(UserAlarmLevelEvent.onlyUser(user));
    }

    @Override
    public UserAlarmLevelEvent putAlarmLevel(User user, UserAlarmLevelEvent alarmLevel) {
        LOG.debug("putAlarmLevel user: " + getUserIdentifier(user) + ", alarmLevel: " + alarmLevel);
        return alarmLevel;
    }

    @Override
    public void removeAlarmLevel(User user) {
        LOG.debug("removeAlarmLevel user: " + getUserIdentifier(user));
    }

    @Override
    public void resetAlarmLevels() {
        LOG.debug("resetAlarmLevels");
    }

    private Object getUserIdentifier(User user) {
        return user.getUsername() == null ? user.getId() : user.getUsername();
    }
}
