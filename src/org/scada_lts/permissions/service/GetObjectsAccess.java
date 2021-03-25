package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public interface GetObjectsAccess<T> {
    List<T> getObjectsWithAccess(User user);
    List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user);
}
