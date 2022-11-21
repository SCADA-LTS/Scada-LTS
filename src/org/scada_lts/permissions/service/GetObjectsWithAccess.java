package org.scada_lts.permissions.service;

import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public interface GetObjectsWithAccess<T, U> {
    List<T> getObjectsWithAccess(U object);
    List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(U object);
}
