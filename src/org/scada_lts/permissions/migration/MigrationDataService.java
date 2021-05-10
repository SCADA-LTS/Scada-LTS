package org.scada_lts.permissions.migration;

import org.scada_lts.mango.service.*;

public class MigrationDataService {

    private final DataPointService dataPointService;
    private final DataSourceService dataSourceService;
    private final ViewService viewService;
    private final WatchListService watchListService;
    private final UsersProfileService usersProfileService;

    public MigrationDataService(DataPointService dataPointService, DataSourceService dataSourceService,
                                ViewService viewService, WatchListService watchListService,
                                UsersProfileService usersProfileService) {
        this.dataPointService = dataPointService;
        this.dataSourceService = dataSourceService;
        this.viewService = viewService;
        this.watchListService = watchListService;
        this.usersProfileService = usersProfileService;
    }

    public DataPointService getDataPointService() {
        return dataPointService;
    }

    public DataSourceService getDataSourceService() {
        return dataSourceService;
    }

    public ViewService getViewService() {
        return viewService;
    }

    public WatchListService getWatchListService() {
        return watchListService;
    }

    public UsersProfileService getUsersProfileService() {
        return usersProfileService;
    }
}
