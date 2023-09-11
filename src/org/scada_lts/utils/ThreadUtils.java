package org.scada_lts.utils;

import org.scada_lts.config.ScadaConfig;

import java.io.IOException;

public final class ThreadUtils {

    private ThreadUtils() {}

    public static String reduceName(String name) {
        try {
            int limit = ScadaConfig.getInstance().getInt("thread.name.additional.length", 0);
            if(limit == 0)
                return "";
            return name.length() > limit ? name.substring(0, limit) + ".." : name;
        } catch (IOException e) {
            return "";
        }
    }
}
