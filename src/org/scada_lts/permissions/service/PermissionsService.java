package org.scada_lts.permissions.service;

public interface PermissionsService<T, O> extends GetObjectsAccess<O>,
        GetPermissions<T>, SetPermissions<T>, GetShareUsers<O> {
}
