package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @autor grzegorz.bylica@gmail.com on 02.11.18
 */
public class StartSleepStateDs implements IStateDs, Serializable {

    private static final String START_SLEEP = "ds.state.startSleep";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(START_SLEEP, new Object[]{}, Locale.getDefault());
    }

}
