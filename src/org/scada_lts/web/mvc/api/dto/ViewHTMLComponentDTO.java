package org.scada_lts.web.mvc.api.dto;

/**
 * @author Arkadiusz Parafiniuk on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
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
