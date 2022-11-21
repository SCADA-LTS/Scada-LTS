package org.scada_lts.permissions.service;

import java.util.List;

public interface SetPermissions<T, U> {
    void addOrUpdatePermissions(U user, List<T> toAddOrUpdate);
    void removePermissions(U user, List<T> toRemove);
}
