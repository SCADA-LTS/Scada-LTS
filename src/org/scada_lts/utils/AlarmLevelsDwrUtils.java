package org.scada_lts.utils;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.scada_lts.mango.service.DataSourceService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AlarmLevelsDwrUtils {

    private AlarmLevelsDwrUtils() {}

    public static void setAlarmLists(DataSourceVO<?> form, DataSourceService dataSourceService) {
        if(form.getId() != Common.NEW_ID) {
            DataSourceVO<?> fromDatabase = dataSourceService.getDataSource(form.getXid());
            if(fromDatabase != null) {
                ExportCodes exportCodes = fromDatabase.getEventCodes();
                if (exportCodes != null) {
                    for (IntValuePair id : exportCodes.getIdKeys()) {
                        if(fromDatabase.getAlarmLevel(id.getKey()) != null)
                            form.setAlarmLevel(id.getKey(), fromDatabase.getAlarmLevel(id.getKey()));
                    }
                }
            }
            Map<Integer, Integer> alarmLevels = getAlarmLevels("AlarmLevels_" + form.getXid());
            if(!alarmLevels.isEmpty()) {
                alarmLevels.forEach(form::setAlarmLevel);
            }
        }
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

    private static Map<Integer, Integer> getAlarmLevels(String key) {
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

    private static void setAlarmLists(DataSourceVO<?> dataSource) {
        getAlarmLevels("AlarmLevels_" + dataSource.getXid()).forEach(dataSource::setAlarmLevel);
    }

    private static String createKey(String key, String currentPage) {
        return currentPage + "_" + key;
    }
}
