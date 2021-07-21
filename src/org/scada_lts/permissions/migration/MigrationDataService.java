package org.scada_lts.permissions.migration;

import org.scada_lts.mango.adapter.MangoDataPoint;
import org.scada_lts.mango.adapter.MangoDataSource;
import org.scada_lts.mango.service.*;

public class MigrationDataService {

    private final MangoDataPoint dataPointService;
    private final MangoDataSource dataSourceService;
    private final ViewService viewService;
    private final WatchListService watchListService;
    private final UsersProfileService usersProfileService;

    public MigrationDataService(MangoDataPoint dataPointService, MangoDataSource dataSourceService,
                                ViewService viewService, WatchListService watchListService,
                                UsersProfileService usersProfileService) {
        this.dataPointService = dataPointService;
        this.dataSourceService = dataSourceService;
        this.viewService = viewService;
        this.watchListService = watchListService;
        this.usersProfileService = usersProfileService;
    }

    public MangoDataPoint getDataPointService() {
        return dataPointService;
    }

    public MangoDataSource getDataSourceService() {
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
