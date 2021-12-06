package org.scada_lts.web.mvc.api.dto.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.view.component.deserializer.ViewComponentDeserializer;
import com.serotonin.mango.vo.User;
import org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO;

import java.util.List;

public class GraphicalViewDTO {
    private Integer id;
    private String xid;
    private String name;
    private String backgroundFilename;
    private Integer width;
    private Integer height;
    private Integer resolution;
    private Long modificationTime;
    @JsonDeserialize(using = ViewComponentDeserializer.class)
    private List<GraphicalViewComponentDTO> viewComponents;
    private Integer userId;
    private Integer anonymousAccess;

    public GraphicalViewDTO() {
    }

    public GraphicalViewDTO(Integer id, String xid, String name, String backgroundFilename, Integer width, Integer height, Integer resolution, Long modificationTime, List<GraphicalViewComponentDTO> viewComponents, Integer userId, Integer anonymousAccess) {
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.backgroundFilename = backgroundFilename;
        this.width = width;
        this.height = height;
        this.resolution = resolution;
        this.modificationTime = modificationTime;
        this.viewComponents = viewComponents;
        this.userId = userId;
        this.anonymousAccess = anonymousAccess;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundFilename() {
        return backgroundFilename;
    }

    public void setBackgroundFilename(String backgroundFilename) {
        this.backgroundFilename = backgroundFilename;
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

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public List<GraphicalViewComponentDTO> getViewComponents() {
        return viewComponents;
    }

    public void setViewComponents(List<GraphicalViewComponentDTO> viewComponents) {
        this.viewComponents = viewComponents;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAnonymousAccess() {
        return anonymousAccess;
    }

    public void setAnonymousAccess(Integer anonymousAccess) {
        this.anonymousAccess = anonymousAccess;
    }

    public View createViewFromBody(User user) {
        View view = new View();
        view.setId(id);
        view.setXid(xid);
        view.setName(name);
        view.setBackgroundFilename(backgroundFilename);
        view.setWidth(width);
        view.setHeight(height);
        view.setResolution(resolution);
        view.setModificationTime(modificationTime);
        setViewComponentsForView(view, user);
        view.setUserId(userId);
        view.setAnonymousAccess(anonymousAccess);
        return view;
    }

    private void setViewComponentsForView(View view, User user) {
        for (GraphicalViewComponentDTO component : viewComponents) {
            view.addViewComponent((ViewComponent) component.createFromBody(user));
        }
    }

}
