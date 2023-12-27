package org.scada_lts.web.mvc.api.json;

public class JsonSettingsSmsDomain {

    String domainName;

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
