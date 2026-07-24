package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class SleepStateDs implements  IStateDs, Serializable {

    private static final String STOP_SLEEP = "ds.state.sleep";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(STOP_SLEEP, new Object[]{}, Locale.getDefault());
    }
}
