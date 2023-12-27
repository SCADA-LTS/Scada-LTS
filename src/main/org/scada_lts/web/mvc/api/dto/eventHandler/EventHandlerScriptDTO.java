package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.vo.event.EventHandlerVO;

public class EventHandlerScriptDTO extends EventHandlerDTO {

    private int activeScriptCommand;
    private int inactiveScriptCommand;

    public EventHandlerScriptDTO() { }

    public EventHandlerScriptDTO(int id, String xid, String alias, boolean disabled, int activeScriptCommand, int inactiveScriptCommand) {
        super(id, xid, alias, EventHandlerVO.TYPE_SCRIPT, disabled);
        this.activeScriptCommand = activeScriptCommand;
        this.inactiveScriptCommand = inactiveScriptCommand;
    }

    @Override
    public EventHandlerVO createEventHandlerVO() {
        EventHandlerVO eh = new EventHandlerVO();
        eh.setHandlerType(EventHandlerVO.TYPE_SCRIPT);
        eh.setId(getId());
        eh.setXid(getXid());
        eh.setAlias(getAlias());
        eh.setDisabled(isDisabled());

        eh.setActiveScriptCommand(activeScriptCommand);
        eh.setInactiveScriptCommand(inactiveScriptCommand);

        return eh;
    }

    public int getActiveScriptCommand() {
        return activeScriptCommand;
    }

    public void setActiveScriptCommand(int activeScriptCommand) {
        this.activeScriptCommand = activeScriptCommand;
    }

    public int getInactiveScriptCommand() {
        return inactiveScriptCommand;
    }

    public void setInactiveScriptCommand(int inactiveScriptCommand) {
        this.inactiveScriptCommand = inactiveScriptCommand;
    }
}
