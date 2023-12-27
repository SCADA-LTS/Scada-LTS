package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.vo.event.EventHandlerVO;

public abstract class EventHandlerDTO {

    private int id;
    private String xid;
    private String alias;
    private int handlerType;
    private boolean disabled;

    public EventHandlerDTO() { }

    public EventHandlerDTO(int id, String xid, String alias, int handlerType, boolean disabled) {
        this.id = id;
        this.xid = xid;
        this.alias = alias;
        this.handlerType = handlerType;
        this.disabled = disabled;
    }

    public EventHandlerVO createEventHandlerVO() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(int handlerType) {
        this.handlerType = handlerType;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
