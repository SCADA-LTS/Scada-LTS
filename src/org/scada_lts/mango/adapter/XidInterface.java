package org.scada_lts.mango.adapter;

public interface XidInterface {

    String generateUniqueXid();

    boolean isXidUnique(String xid, int excludeId);

}
