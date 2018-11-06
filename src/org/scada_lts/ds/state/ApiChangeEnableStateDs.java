package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @autor grzegorz.bylica@gmail.com on 10.10.18
 */
public class ApiChangeEnableStateDs implements IStateDs, Serializable {

    private static final String API_CHANGE_ENABLE_STATE_DS = "ds.state.apiChangeEnableStateDs";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(API_CHANGE_ENABLE_STATE_DS, new Object[]{}, Locale.getDefault());
    }
}
