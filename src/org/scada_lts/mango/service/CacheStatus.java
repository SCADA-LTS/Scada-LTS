package org.scada_lts.mango.service;

import org.scada_lts.config.ScadaConfig;
import java.io.IOException;

public final class CacheStatus {

    public static boolean isEnable;

    static {
        try {
            isEnable = ScadaConfig.getInstance().getBoolean(ScadaConfig.ENABLE_CACHE, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
