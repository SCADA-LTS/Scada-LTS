package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;

import java.util.List;

public class EventHandlerSmsDTO extends EventHandlerDTO {

    private List<RecipientListEntryBean> activeRecipients;

    public EventHandlerSmsDTO() {}

    public EventHandlerSmsDTO(int id, String xid, String alias, int handlerType, boolean disabled, List<RecipientListEntryBean> activeRecipients) {
        super(id, xid, alias, handlerType, disabled);
        this.activeRecipients = activeRecipients;
    }

    @Override
    public EventHandlerVO createEventHandlerVO() {
        EventHandlerVO eh = new EventHandlerVO();
        eh.setHandlerType(EventHandlerVO.TYPE_SMS);
        eh.setId(getId());
        eh.setXid(getXid());
        eh.setAlias(getAlias());
        eh.setDisabled(isDisabled());

        eh.setActiveRecipients(activeRecipients);

        return eh;
    }

    public List<RecipientListEntryBean> getActiveRecipients() {
        return activeRecipients;
    }

    public void setActiveRecipients(List<RecipientListEntryBean> activeRecipients) {
        this.activeRecipients = activeRecipients;
    }
}
