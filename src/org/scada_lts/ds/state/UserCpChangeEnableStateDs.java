package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class UserCpChangeEnableStateDs implements IStateDs, Serializable {

    private static final String USER_CP_DS =  "ds.state.userCpChangeEnableStateDs";

    @Override
    public String getDescribe() {
       return ConfigLocalization.getInstance().messageSource().getMessage(USER_CP_DS, new Object[]{}, Locale.getDefault());
    }
}
