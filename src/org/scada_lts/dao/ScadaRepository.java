package org.scada_lts.dao;


import org.scada_lts.dao.model.BaseObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public interface ScadaRepository<T, ID> extends QueryRepository<T, ID>, CommandRepository<T, ID> {

    void update(T entity);
    List<ScadaObjectIdentifier> findIdentifiers();
    List<BaseObjectIdentifier> findBaseIdentifiers();
}
