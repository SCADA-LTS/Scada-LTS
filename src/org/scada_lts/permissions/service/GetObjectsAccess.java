package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;

import java.util.List;

public interface GetObjectsAccess<T> {
    List<T> getObjectsWithAccess(User user);
}
