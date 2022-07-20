package org.scada_lts.web.mvc.api.json;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.List;

public class JsonIdSelection {
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
