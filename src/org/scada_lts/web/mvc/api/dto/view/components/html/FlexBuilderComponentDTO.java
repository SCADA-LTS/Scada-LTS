package org.scada_lts.web.mvc.api.dto.view.components.html;

import br.org.scadabr.view.component.FlexBuilderComponent;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.vo.User;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class FlexBuilderComponentDTO extends HtmlComponentDTO {
    private Boolean projectDefined;
    private String projectSource;
    private Integer projectId;
    private Boolean runtimeMode;
    private Integer width;
    private Integer height;

    public FlexBuilderComponentDTO() {
    }

    public FlexBuilderComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String content, Boolean projectDefined, String projectSource, Integer projectId, Boolean runtimeMode, Integer width, Integer height) {
        super(index, idSuffix, x, y, z, typeName, content);
        this.projectDefined = projectDefined;
        this.projectSource = projectSource;
        this.projectId = projectId;
        this.runtimeMode = runtimeMode;
        this.width = width;
        this.height = height;
    }

    public Boolean getProjectDefined() {
        return projectDefined;
    }

    public void setProjectDefined(Boolean projectDefined) {
        this.projectDefined = projectDefined;
    }

    public String getProjectSource() {
        return projectSource;
    }

    public void setProjectSource(String projectSource) {
        this.projectSource = projectSource;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Boolean getRuntimeMode() {
        return runtimeMode;
    }

    public void setRuntimeMode(Boolean runtimeMode) {
        this.runtimeMode = runtimeMode;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public FlexBuilderComponent createFromBody(User user) {
        FlexBuilderComponent c = new FlexBuilderComponent();
        c.setHeight(height);
        c.setWidth(width);
        c.setProjectDefined(projectDefined);
        c.setProjectId(projectId);
        c.setRuntimeMode(runtimeMode);
        c.setProjectSource(projectSource);

        c.setContent(c.createFlexBuilderContent());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
