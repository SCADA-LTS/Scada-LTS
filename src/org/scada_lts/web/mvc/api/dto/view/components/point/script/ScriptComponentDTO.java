package org.scada_lts.web.mvc.api.dto.view.components.point.script;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.ScriptComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ScriptComponentDTO extends ScriptBaseComponentDTO {

    public ScriptComponentDTO() {
    }

    public ScriptComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, String script) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, script);
    }

    @Override
    public ScriptComponent createFromBody(User user) {
        ScriptComponent c = new ScriptComponent();
        c.setScript(getScript());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        DataPointVO dataPoint = getDataPoint();
        c.tsetDataPoint(dataPoint);
        c.setNameOverride(getNameOverride());
        c.setSettableOverride(getSettableOverride() && Permissions.hasDataPointSetPermission(user, dataPoint));
        c.setBkgdColorOverride(getBkgdColorOverride());
        c.setDisplayControls(getDisplayControls());
        c.validateDataPoint(user, false);
        resetPointComponent(c);
        return c;
    }
}
