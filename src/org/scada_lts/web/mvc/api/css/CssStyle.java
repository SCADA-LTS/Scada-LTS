package org.scada_lts.web.mvc.api.css;

@CssValid
public class CssStyle {
    private String content;

    public CssStyle() {
    }

    public CssStyle(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
