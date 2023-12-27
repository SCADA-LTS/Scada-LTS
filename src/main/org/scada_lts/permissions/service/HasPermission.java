package org.scada_lts.permissions.service;


public interface HasPermission<U, T> {

    boolean hasReadPermission(U user, T object);
    boolean hasSetPermission(U user, T object);
    boolean hasOwnerPermission(U user, T object);
}
