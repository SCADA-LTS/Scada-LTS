package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @autor grzegorz.bylica@gmail.com on 10.10.18
 */
public class StopChangeEnableStateDs implements IStateDs, Serializable {

    private static final String STOP = "ds.state.stopChangeEnableStateDs";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(STOP, new Object[]{}, Locale.getDefault());
    }
}
