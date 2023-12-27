package org.scada_lts.web.mvc.api.dto.view.components.compound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.CompoundChild;
import com.serotonin.mango.view.component.deserializer.CompoundComponentDeserializer;
import org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = CompoundComponentDeserializer.class)
public class CompoundComponentDTO extends GraphicalViewComponentDTO {
    private String name;
    private Map<String, String> children;

    @JsonIgnore
    private List<CompoundChild> compoundChildren = new ArrayList<CompoundChild>();

    public CompoundComponentDTO() {
    }

    public CompoundComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String name, Map<String, String> children) {
        super(index, idSuffix, x, y, z, typeName);
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getChildren() {
        return children;
    }

    public void setChildren(Map<String, String> children) {
        this.children = children;
    }

    public List<CompoundChild> getCompoundChildren() {
        return compoundChildren;
    }

    public void setCompoundChildren(List<CompoundChild> compoundChildren) {
        this.compoundChildren = compoundChildren;
    }
}
