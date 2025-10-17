package org.scada_lts.web.mvc.api.dto.view.components.html;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.mango.vo.User;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class HtmlComponentDTO extends HtmlBaseComponentDTO {

    public HtmlComponentDTO() {
    }

    public HtmlComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String content) {
        super(index, idSuffix, x, y, z, typeName, content);
    }

    @Override
    public HtmlComponent createFromBody(User user) {
        HtmlComponent c = new HtmlComponent();
        c.setContent(getContent());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
