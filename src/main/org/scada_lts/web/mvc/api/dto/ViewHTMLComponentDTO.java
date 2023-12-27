package org.scada_lts.web.mvc.api.dto;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewHTMLComponentDTO extends ViewComponentDTO {
    private String content;

    public ViewHTMLComponentDTO() {
    }

    public ViewHTMLComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String content) {
        super(id, index, defName, idSuffix, style, x, y);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
