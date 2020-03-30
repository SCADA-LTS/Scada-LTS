package org.scada_lts.cache;


import org.scada_lts.config.ScadaConfig;
import java.io.IOException;
import static org.scada_lts.config.ScadaConfig.ENABLE_CACHE;

/**
 * Responsible for return only status of cache flag
 *
 * @author Mateusz Hyski Abil'I.T. development team, sdt@abilit.eu
 */
public final class CacheStatus {

    public static boolean isEnable;

    private static CacheStatus instance;

    private CacheStatus() {}

    public CacheStatus getInstance() {
        if ( instance == null ) {
            instance = new CacheStatus();
        }
        return instance;
    }



    static {
        try {
            isEnable = ScadaConfig.getInstance().getBoolean(ENABLE_CACHE, false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
