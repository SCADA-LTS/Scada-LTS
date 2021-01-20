package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;

import java.util.List;

public class EventHandlerEmailDTO extends EventHandlerDTO {

    private List<RecipientListEntryBean> activeRecipients;
    private boolean sendEscalation;
    private int escalationDelayType;
    private int escalationDelay;
    private List<RecipientListEntryBean> escalationRecipients;
    private boolean sendInactive;
    private boolean inactiveOverride;
    private List<RecipientListEntryBean> inactiveRecipients;

    public EventHandlerEmailDTO() { }

    public EventHandlerEmailDTO(int id, String xid, String alias, boolean disabled, List<RecipientListEntryBean> activeRecipients, boolean sendEscalation, int escalationDelayType, int escalationDelay, List<RecipientListEntryBean> escalationRecipients, boolean sendInactive, boolean inactiveOverride, List<RecipientListEntryBean> inactiveRecipients) {
        super(id, xid, alias, EventHandlerVO.TYPE_EMAIL, disabled);
        this.activeRecipients = activeRecipients;
        this.sendEscalation = sendEscalation;
        this.escalationDelayType = escalationDelayType;
        this.escalationDelay = escalationDelay;
        this.escalationRecipients = escalationRecipients;
        this.sendInactive = sendInactive;
        this.inactiveOverride = inactiveOverride;
        this.inactiveRecipients = inactiveRecipients;
    }

    @Override
    public EventHandlerVO createEventHandlerVO() {
        EventHandlerVO eh = new EventHandlerVO();
        eh.setHandlerType(EventHandlerVO.TYPE_EMAIL);
        eh.setId(getId());
        eh.setXid(getXid());
        eh.setAlias(getAlias());
        eh.setDisabled(isDisabled());

        eh.setActiveRecipients(activeRecipients);
        eh.setSendEscalation(sendEscalation);
        eh.setEscalationDelayType(escalationDelayType);
        eh.setEscalationDelay(escalationDelay);
        eh.setEscalationRecipients(escalationRecipients);
        eh.setSendInactive(sendInactive);
        eh.setInactiveOverride(inactiveOverride);
        eh.setInactiveRecipients(inactiveRecipients);

        return eh;
    }

    public List<RecipientListEntryBean> getActiveRecipients() {
        return activeRecipients;
    }

    public void setActiveRecipients(List<RecipientListEntryBean> activeRecipients) {
        this.activeRecipients = activeRecipients;
    }

    public boolean isSendEscalation() {
        return sendEscalation;
    }

    public void setSendEscalation(boolean sendEscalation) {
        this.sendEscalation = sendEscalation;
    }

    public int getEscalationDelayType() {
        return escalationDelayType;
    }

    public void setEscalationDelayType(int escalationDelayType) {
        this.escalationDelayType = escalationDelayType;
    }

    public int getEscalationDelay() {
        return escalationDelay;
    }

    public void setEscalationDelay(int escalationDelay) {
        this.escalationDelay = escalationDelay;
    }

    public List<RecipientListEntryBean> getEscalationRecipients() {
        return escalationRecipients;
    }

    public void setEscalationRecipients(List<RecipientListEntryBean> escalationRecipients) {
        this.escalationRecipients = escalationRecipients;
    }

    public boolean isSendInactive() {
        return sendInactive;
    }

    public void setSendInactive(boolean sendInactive) {
        this.sendInactive = sendInactive;
    }

    public boolean isInactiveOverride() {
        return inactiveOverride;
    }

    public void setInactiveOverride(boolean inactiveOverride) {
        this.inactiveOverride = inactiveOverride;
    }

    public List<RecipientListEntryBean> getInactiveRecipients() {
        return inactiveRecipients;
    }

    public void setInactiveRecipients(List<RecipientListEntryBean> inactiveRecipients) {
        this.inactiveRecipients = inactiveRecipients;
    }
}
