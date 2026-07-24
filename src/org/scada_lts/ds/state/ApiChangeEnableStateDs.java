package org.scada_lts.ds.state;

import org.scada_lts.localization.ConfigLocalization;

import java.io.Serializable;
import java.util.Locale;

/**
 * @project Scada-LTS-master
 * @author Grzegorz Bylica on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class ApiChangeEnableStateDs implements IStateDs, Serializable {

    private static final String API_CHANGE_ENABLE_STATE_DS = "ds.state.apiChangeEnableStateDs";

    @Override
    public String getDescribe() {
        return ConfigLocalization.getInstance().messageSource().getMessage(API_CHANGE_ENABLE_STATE_DS, new Object[]{}, Locale.getDefault());
    }
}
