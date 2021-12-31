package org.scada_lts.web.mvc.api;

import org.scada_lts.utils.QueryUtils;

public class DbQuerySettings {

    private Boolean readEnabled;

    public DbQuerySettings() {
    }

    public DbQuerySettings(Boolean readEnabled) {
        this.readEnabled = readEnabled;
    }

    public static DbQuerySettings fromEnvProperties() {
        return new DbQuerySettings(QueryUtils.isReadEnabled());
    }

    public Boolean isReadEnabled() {
        return readEnabled;
    }

    public void setReadEnabled(Boolean readEnabled) {
        this.readEnabled = readEnabled;
    }
}
