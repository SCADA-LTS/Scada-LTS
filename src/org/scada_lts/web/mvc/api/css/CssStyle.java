package org.scada_lts.web.mvc.api.css;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.scada_lts.web.beans.validation.css.CssValid;

public class CssStyle {

    @CssValid
    private final String content;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CssStyle(@JsonProperty("content") String content) {
        this.content = CustomCssUtils.replaceToTab(content);
    }

    public String getContent() {
        return content;
    }

    public CssStyle clearedOfTabs() {
        return new CssStyle(getContent().replace("\t", " "));
    }
}
