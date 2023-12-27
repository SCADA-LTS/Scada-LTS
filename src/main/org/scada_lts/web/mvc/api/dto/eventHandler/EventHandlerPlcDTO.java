package org.scada_lts.web.mvc.api.dto.eventHandler;

import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;

import java.util.List;

public class EventHandlerPlcDTO {


    private int id;
    private String xid;
    private String alias;
    private int eventTypeId;
    private int eventTypeRef1;
    private int eventTypeRef2;
    private int handlerType;
    private List<RecipientListEntryBean> recipients;

    public EventHandlerPlcDTO() {
    }

    public EventHandlerPlcDTO(int id, String xid, String alias, int eventTypeId, int eventTypeRef1, int eventTypeRef2, int handlerType, List<RecipientListEntryBean> recipients) {
        this.id = id;
        this.xid = xid;
        this.alias = alias;
        this.eventTypeId = eventTypeId;
        this.eventTypeRef1 = eventTypeRef1;
        this.eventTypeRef2 = eventTypeRef2;
        this.handlerType = handlerType;
        this.recipients = recipients;
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

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public int getEventTypeRef1() {
        return eventTypeRef1;
    }

    public void setEventTypeRef1(int eventTypeRef1) {
        this.eventTypeRef1 = eventTypeRef1;
    }

    public int getEventTypeRef2() {
        return eventTypeRef2;
    }

    public void setEventTypeRef2(int eventTypeRef2) {
        this.eventTypeRef2 = eventTypeRef2;
    }

    public List<RecipientListEntryBean> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<RecipientListEntryBean> recipients) {
        this.recipients = recipients;
    }

    public int getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(int handlerType) {
        this.handlerType = handlerType;
    }
}
