package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class UserChangeEnableStateDs implements IStateDs, Serializable {

    private static final String USER = "ds.state.userChangeEnableStateDs";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(USER, new Object[]{}, Locale.getDefault());
    }

}
