package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class StartSleepStateDs implements IStateDs, Serializable {

    private static final String START_SLEEP = "ds.state.startSleep";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(START_SLEEP, new Object[]{}, Locale.getDefault());
    }

}
