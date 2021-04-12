package org.scada_lts.permissions.service;


import java.util.List;

public interface GetPermissions<T, U> {
    List<T> getPermissions(U object);
}
