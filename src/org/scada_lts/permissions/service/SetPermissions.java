package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;

import java.util.List;

public interface SetPermissions<T> {
    void addOrUpdatePermissions(User user, List<T> toAddOrUpdate);
    void removePermissions(User user, List<T> toRemove);
}
