package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @autor grzegorz.bylica@gmail.com on 02.11.18
 */
public class SleepStateDs implements  IStateDs, Serializable {

    private static final String STOP_SLEEP = "ds.state.sleep";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(STOP_SLEEP, new Object[]{}, Locale.getDefault());
    }
}
