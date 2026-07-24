package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.view.component.ViewComponent;

/**
 * @author Arkadiusz Parafiniuk on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class ViewLinkComponentDTO extends ViewComponentDTO {
    private String text;
    private String link;

    public ViewLinkComponentDTO() {
    }

    public ViewLinkComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String text, String link) {
        super(id, index, defName, idSuffix, style, x, y);
        this.text = text;
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
