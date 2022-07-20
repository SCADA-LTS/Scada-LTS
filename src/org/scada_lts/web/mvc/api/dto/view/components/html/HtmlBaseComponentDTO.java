package org.scada_lts.web.mvc.api.dto.view.components.html;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.deserializer.HtmlComponentDeserializer;
import org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO;

@JsonDeserialize(using = HtmlComponentDeserializer.class)
public class HtmlBaseComponentDTO extends GraphicalViewComponentDTO {

    private String content;

    public HtmlBaseComponentDTO() {
    }

    public HtmlBaseComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String content) {
        super(index, idSuffix, x, y, z, typeName);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
