package org.scada_lts.web.mvc.api.dto.view.components;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.mango.vo.User;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class HtmlComponentDTO extends GraphicalViewComponentDTO{

    private String content;

    public HtmlComponentDTO() {
    }

    public HtmlComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String content) {
        super(index, idSuffix, x, y, z, typeName);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public HtmlComponent createFromBody(User user) {
        HtmlComponent c = new HtmlComponent();
        c.setContent(content);

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
