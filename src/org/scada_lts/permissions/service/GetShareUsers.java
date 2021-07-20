package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;

import java.util.List;

public interface GetShareUsers<T> {
    List<ShareUser> getShareUsers(T object);
    List<ShareUser> getShareUsersFromProfile(T object);
    List<ShareUser> getShareUsersWithProfile(T object);
}
