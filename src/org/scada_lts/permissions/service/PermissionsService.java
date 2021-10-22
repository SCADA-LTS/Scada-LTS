package org.scada_lts.permissions.service;

public interface PermissionsService<T, U> extends GetPermissions<T, U>, SetPermissions<T, U> {
    String CACHE_ENABLED_KEY = "permissions.cache.enabled";
}
