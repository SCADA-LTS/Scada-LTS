package org.scada_lts.ds.state;


import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @autor grzegorz.bylica@gmail.com on 10.10.18
 */
public class MigrationOrErrorSerializeChangeEnableState implements IStateDs, Serializable {

    private static final String MIGRATION = "ds.state.migrationOrErrorSerializeChangeEnableState";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(MIGRATION, new Object[]{}, Locale.getDefault());
    }
}
