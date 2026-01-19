package org.scada_lts.web.mvc.api.json;

import org.scada_lts.web.beans.validation.xss.XssProtect;

public class JsonIdSelection {

    @XssProtect
    private String ids;

    public JsonIdSelection() { }
    public JsonIdSelection(String ids) {
        this.ids = ids;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
