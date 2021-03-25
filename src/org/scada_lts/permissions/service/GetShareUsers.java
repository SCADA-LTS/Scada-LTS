package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;

import java.util.Collections;
import java.util.List;

public interface GetShareUsers<T> {
    default List<ShareUser> getShareUsers(T object) {
        return Collections.emptyList();
    }
}
