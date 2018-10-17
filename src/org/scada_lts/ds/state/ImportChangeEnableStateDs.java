package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @autor grzegorz.bylica@gmail.com on 10.10.18
 */
public class ImportChangeEnableStateDs implements IStateDs, Serializable {

    private static final String IMPORT = "ds.state.importChangeEnableStateDs";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(IMPORT, new Object[]{}, Locale.getDefault());
    }
}
