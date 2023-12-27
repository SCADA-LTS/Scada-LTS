package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.vo.event.EventHandlerVO;

public class EventHandlerPointDTO extends EventHandlerDTO {

    private int targetPointId;
    private int activeAction;
    private String activeValueToSet;
    private int activePointId;
    private int inactiveAction;
    private String inactiveValueToSet;
    private int inactivePointId;

    public EventHandlerPointDTO() { }

    public EventHandlerPointDTO(int id, String xid, String alias, boolean disabled, int targetPointId, int activeAction, String activeValueToSet, int activePointId, int inactiveAction, String inactiveValueToSet, int inactivePointId) {
        super(id, xid, alias, EventHandlerVO.TYPE_SET_POINT, disabled);
        this.targetPointId = targetPointId;
        this.activeAction = activeAction;
        this.activeValueToSet = activeValueToSet;
        this.activePointId = activePointId;
        this.inactiveAction = inactiveAction;
        this.inactiveValueToSet = inactiveValueToSet;
        this.inactivePointId = inactivePointId;
    }

    @Override
    public EventHandlerVO createEventHandlerVO() {
        EventHandlerVO eh = new EventHandlerVO();
        eh.setHandlerType(EventHandlerVO.TYPE_SET_POINT);
        eh.setId(getId());
        eh.setXid(getXid());
        eh.setAlias(getAlias());
        eh.setDisabled(isDisabled());

        eh.setTargetPointId(targetPointId);
        eh.setActiveAction(activeAction);
        eh.setActiveValueToSet(activeValueToSet);
        eh.setActivePointId(activePointId);
        eh.setInactiveAction(inactiveAction);
        eh.setInactiveValueToSet(inactiveValueToSet);
        eh.setInactivePointId(inactivePointId);

        return eh;
    }

    public int getTargetPointId() {
        return targetPointId;
    }

    public void setTargetPointId(int targetPointId) {
        this.targetPointId = targetPointId;
    }

    public int getActiveAction() {
        return activeAction;
    }

    public void setActiveAction(int activeAction) {
        this.activeAction = activeAction;
    }

    public String getActiveValueToSet() {
        return activeValueToSet;
    }

    public void setActiveValueToSet(String activeValueToSet) {
        this.activeValueToSet = activeValueToSet;
    }

    public int getActivePointId() {
        return activePointId;
    }

    public void setActivePointId(int activePointId) {
        this.activePointId = activePointId;
    }

    public int getInactiveAction() {
        return inactiveAction;
    }

    public void setInactiveAction(int inactiveAction) {
        this.inactiveAction = inactiveAction;
    }

    public String getInactiveValueToSet() {
        return inactiveValueToSet;
    }

    public void setInactiveValueToSet(String inactiveValueToSet) {
        this.inactiveValueToSet = inactiveValueToSet;
    }

    public int getInactivePointId() {
        return inactivePointId;
    }

    public void setInactivePointId(int inactivePointId) {
        this.inactivePointId = inactivePointId;
    }
}
