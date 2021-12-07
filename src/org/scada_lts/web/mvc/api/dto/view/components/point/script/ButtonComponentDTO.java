package org.scada_lts.web.mvc.api.dto.view.components.point.script;

import br.org.scadabr.view.component.ButtonComponent;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ButtonComponentDTO extends ScriptBaseComponentDTO {

    private String whenOffLabel;
    private String whenOnLabel;
    private Integer width;
    private Integer height;

    public ButtonComponentDTO() {
    }

    public ButtonComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, String script, String whenOffLabel, String whenOnLabel, Integer width, Integer height) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, script);
        this.whenOffLabel = whenOffLabel;
        this.whenOnLabel = whenOnLabel;
        this.width = width;
        this.height = height;
    }

    public String getWhenOffLabel() {
        return whenOffLabel;
    }

    public void setWhenOffLabel(String whenOffLabel) {
        this.whenOffLabel = whenOffLabel;
    }

    public String getWhenOnLabel() {
        return whenOnLabel;
    }

    public void setWhenOnLabel(String whenOnLabel) {
        this.whenOnLabel = whenOnLabel;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public ButtonComponent createFromBody(User user) {
        ButtonComponent c = new ButtonComponent();
        c.setHeight(height);
        c.setWidth(width);
        c.setWhenOffLabel(whenOffLabel);
        c.setWhenOnLabel(whenOnLabel);

        c.setScript(c.createButtonScriptContent());

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
