package org.scada_lts.dao.event;

public interface IUserEventDAO extends org.scada_lts.dao.GenericDaoCR<org.scada_lts.dao.model.event.UserEvent> {
    public abstract java.util.List<org.scada_lts.dao.model.event.UserEvent> findAll();
    public abstract org.scada_lts.dao.model.event.UserEvent findById(java.lang.Object[] arg0);
    public abstract java.util.List<org.scada_lts.dao.model.event.UserEvent> filtered(java.lang.String arg0, java.lang.Object[] arg1, long arg2);
    public abstract java.lang.Object[] create(org.scada_lts.dao.model.event.UserEvent arg0);
    public abstract void batchUpdate(int arg0, java.util.List<java.lang.Integer> arg1, boolean arg2);
    public abstract void updateAck(long arg0, boolean arg1);
    public abstract void silenceEvent(long arg0, int arg1);
    public abstract void unsilenceEvent(long arg0, int arg1);
    public abstract void silenceEvents(java.util.List<java.lang.Integer> arg0, int arg1);
    public abstract void unsilenceEvents(java.util.List<java.lang.Integer> arg0, int arg1);
    public abstract void delete(int arg0);
}

