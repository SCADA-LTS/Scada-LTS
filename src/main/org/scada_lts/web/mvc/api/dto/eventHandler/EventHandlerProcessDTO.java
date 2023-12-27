package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.vo.event.EventHandlerVO;

public class EventHandlerProcessDTO extends EventHandlerDTO {

    private String activeProcessCommand;
    private String inactiveProcessCommand;

    public EventHandlerProcessDTO() { }

    public EventHandlerProcessDTO(int id, String xid, String alias, boolean disabled, String activeProcessCommand, String inactiveProcessCommand) {
        super(id, xid, alias, EventHandlerVO.TYPE_PROCESS, disabled);
        this.activeProcessCommand = activeProcessCommand;
        this.inactiveProcessCommand = inactiveProcessCommand;
    }

    @Override
    public EventHandlerVO createEventHandlerVO() {
        EventHandlerVO eh = new EventHandlerVO();
        eh.setHandlerType(EventHandlerVO.TYPE_PROCESS);
        eh.setId(getId());
        eh.setXid(getXid());
        eh.setAlias(getAlias());
        eh.setDisabled(isDisabled());

        eh.setActiveProcessCommand(activeProcessCommand);
        eh.setInactiveProcessCommand(inactiveProcessCommand);

        return eh;
    }

    public String getActiveProcessCommand() {
        return activeProcessCommand;
    }

    public void setActiveProcessCommand(String activeProcessCommand) {
        this.activeProcessCommand = activeProcessCommand;
    }

    public String getInactiveProcessCommand() {
        return inactiveProcessCommand;
    }

    public void setInactiveProcessCommand(String inactiveProcessCommand) {
        this.inactiveProcessCommand = inactiveProcessCommand;
    }
}
