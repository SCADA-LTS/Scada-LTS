package org.scada_lts.dao;

import java.util.List;

public interface QueryRepository<T, ID> {

    List<T> findAll();
    T findById(ID id);
    T findByXid(String xid);
}
