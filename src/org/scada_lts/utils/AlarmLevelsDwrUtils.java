package org.scada_lts.utils;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AlarmLevelsDwrUtils {

    private AlarmLevelsDwrUtils() {}

    public static void setAlarmLists(AmqpDataSourceVO dataSource) {
        getAlarmLevels("AlarmLevels_" + dataSource.getXid()).forEach(dataSource::setAlarmLevel);
    }

    public static void putAlarmLevels(String key, int eventId, int alarmLevel) {
        WebContext webContext = WebContextFactory.get();
        if(webContext!= null) {
            String realKey = createKey(key, webContext.getCurrentPage());
            Map<Integer, Integer> alarmLevels = (Map<Integer, Integer>)webContext.getSession().getAttribute(realKey);
            if(alarmLevels == null) {
                alarmLevels = new HashMap<>();
            }
            alarmLevels.put(eventId, alarmLevel);
            webContext.getSession().setAttribute(realKey, alarmLevels);
        }
    }

    public static Map<Integer, Integer> getAlarmLevels(String key) {
        WebContext webContext = WebContextFactory.get();
        if(webContext != null) {
            String realKey = createKey(key, webContext.getCurrentPage());
            Map<Integer, Integer> alarmLevels = (Map<Integer, Integer>)webContext.getSession().getAttribute(realKey);
            if(alarmLevels == null) {
                alarmLevels = new HashMap<>();
            }
            return  alarmLevels;
        }
        return Collections.emptyMap();
    }

    private static String createKey(String key, String currentPage) {
        return currentPage + "_" + key;
    }
}
