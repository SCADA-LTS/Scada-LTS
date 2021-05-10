package org.scada_lts.permissions.migration;

import org.scada_lts.permissions.service.DataPointUserPermissionsService;
import org.scada_lts.permissions.service.DataSourceUserPermissionsService;
import org.scada_lts.permissions.service.ViewUserPermissionsService;
import org.scada_lts.permissions.service.WatchListUserPermissionsService;

public class MigrationPermissionsService {

    private final DataPointUserPermissionsService dataPointUserPermissionsService;
    private final DataSourceUserPermissionsService dataSourceUserPermissionsService;
    private final WatchListUserPermissionsService watchListUserPermissionsService;
    private final ViewUserPermissionsService viewUserPermissionsService;

    public MigrationPermissionsService(DataPointUserPermissionsService dataPointUserPermissionsService,
                                       DataSourceUserPermissionsService dataSourceUserPermissionsService,
                                       WatchListUserPermissionsService watchListUserPermissionsService,
                                       ViewUserPermissionsService viewUserPermissionsService) {
        this.dataPointUserPermissionsService = dataPointUserPermissionsService;
        this.dataSourceUserPermissionsService = dataSourceUserPermissionsService;
        this.watchListUserPermissionsService = watchListUserPermissionsService;
        this.viewUserPermissionsService = viewUserPermissionsService;
    }

    public DataPointUserPermissionsService getDataPointUserPermissionsService() {
        return dataPointUserPermissionsService;
    }

    public DataSourceUserPermissionsService getDataSourceUserPermissionsService() {
        return dataSourceUserPermissionsService;
    }

    public WatchListUserPermissionsService getWatchListUserPermissionsService() {
        return watchListUserPermissionsService;
    }

    public ViewUserPermissionsService getViewUserPermissionsService() {
        return viewUserPermissionsService;
    }
}
