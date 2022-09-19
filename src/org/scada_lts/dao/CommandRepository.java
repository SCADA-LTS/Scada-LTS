package org.scada_lts.dao;


public interface CommandRepository<T, ID> {

    T save(T entity);
    void delete(ID id);
}
