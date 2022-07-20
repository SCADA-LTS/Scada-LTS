package org.scada_lts.web.mvc.api.dto.view.components.compound;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.SimpleCompoundComponent;
import com.serotonin.mango.vo.User;

import java.util.Map;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class SimpleCompoundComponentDTO extends CompoundComponentDTO{

    private String backgroundColour;

    public SimpleCompoundComponentDTO() {
    }

    public SimpleCompoundComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String name, Map<String, String> compoundChildren, String backgroundColour) {
        super(index, idSuffix, x, y, z, typeName, name, compoundChildren);
        this.backgroundColour = backgroundColour;
    }

    public String getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    @Override
    public SimpleCompoundComponent createFromBody(User user) {
        SimpleCompoundComponent c = new SimpleCompoundComponent();
        c.setBackgroundColour(backgroundColour);

        c.setName(getName());
        c.setChildren(getCompoundChildren());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
