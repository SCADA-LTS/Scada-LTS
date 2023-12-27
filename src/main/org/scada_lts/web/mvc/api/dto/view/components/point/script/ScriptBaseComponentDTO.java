package org.scada_lts.web.mvc.api.dto.view.components.point.script;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.deserializer.ScriptBaseComponentDeserializer;
import org.scada_lts.web.mvc.api.dto.view.components.point.PointComponentDTO;

@JsonDeserialize(using = ScriptBaseComponentDeserializer.class)
public class ScriptBaseComponentDTO extends PointComponentDTO {

    private String script;

    public ScriptBaseComponentDTO() {
    }

    public ScriptBaseComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, String script) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls);
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

}

