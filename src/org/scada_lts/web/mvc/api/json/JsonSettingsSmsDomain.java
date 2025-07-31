package org.scada_lts.web.mvc.api.json;

import org.scada_lts.web.beans.validation.xss.XssProtect;

public class JsonSettingsSmsDomain {
    @XssProtect
    private String domainName;

    public JsonSettingsSmsDomain() {
    }

    public JsonSettingsSmsDomain(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}
