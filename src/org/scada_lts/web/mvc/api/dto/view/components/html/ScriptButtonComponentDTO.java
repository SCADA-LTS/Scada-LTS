package org.scada_lts.web.mvc.api.dto.view.components.html;

import br.org.scadabr.view.component.ScriptButtonComponent;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.vo.User;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ScriptButtonComponentDTO extends HtmlComponentDTO {
    private String scriptXid;
    private String text;

    public ScriptButtonComponentDTO() {
    }

    public ScriptButtonComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String content, String scriptXid, String text) {
        super(index, idSuffix, x, y, z, typeName, content);
        this.scriptXid = scriptXid;
        this.text = text;
    }

    public String getScriptXid() {
        return scriptXid;
    }

    public void setScriptXid(String scriptXid) {
        this.scriptXid = scriptXid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public ScriptButtonComponent createFromBody(User user) {
        ScriptButtonComponent c = new ScriptButtonComponent();
        c.setScriptXid(scriptXid);
        c.setText(text);

        c.setContent(c.createScriptButtonContent());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }

}
