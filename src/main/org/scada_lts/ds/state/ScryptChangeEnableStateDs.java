package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @autor grzegorz.bylica@gmail.com on 10.10.18
 */
public class ScryptChangeEnableStateDs implements IStateDs, Serializable {

    private static final String SCRYPT_CHANGE_ENABLE = "ds.state.scryptChangeEnable";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(SCRYPT_CHANGE_ENABLE, new Object[]{}, Locale.getDefault());
    }
}
