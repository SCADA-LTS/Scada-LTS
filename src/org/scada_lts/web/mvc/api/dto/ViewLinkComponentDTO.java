package org.scada_lts.web.mvc.api.dto;

import com.serotonin.mango.view.component.ViewComponent;
import org.scada_lts.web.beans.validation.xss.XssProtect;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewLinkComponentDTO extends ViewComponentDTO {

    @XssProtect
    private String text;
    @XssProtect
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
