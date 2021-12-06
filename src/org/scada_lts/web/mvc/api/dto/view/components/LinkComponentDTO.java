package org.scada_lts.web.mvc.api.dto.view.components;


import br.org.scadabr.view.component.LinkComponent;
import com.serotonin.mango.view.component.HtmlComponent;
import com.serotonin.mango.vo.User;

public class LinkComponentDTO extends HtmlComponentDTO {

    private String link;
    private String text;

    public LinkComponentDTO() {
    }

    public LinkComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String content, String link, String text) {
        super(index, idSuffix, x, y, z, typeName, content);
        this.link = link;
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public LinkComponent createFromBody(User user) {
        LinkComponent c = new LinkComponent();
        c.setLink(link);
        c.setText(text);

        c.setContent(getContent());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
