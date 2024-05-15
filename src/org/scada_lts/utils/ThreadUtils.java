package org.scada_lts.utils;

import org.scada_lts.mango.service.SystemSettingsService;

public final class ThreadUtils {

    private ThreadUtils() {}

    public static String reduceName(String name, SystemSettingsService systemSettingsService) {
        try {
            int length = systemSettingsService.getThreadsNameAdditionalLength();
            if(length == 0)
                return "";
            return name.length() > length ? name.substring(0, length) + ".." : name;
        } catch (Exception e) {
            return "";
        }
    }
}
