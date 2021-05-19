package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.permissions.service.*;

public class MigrationPermissionsService {

    private final PermissionsService<DataPointAccess, User> dataPointUserPermissionsService;
    private final PermissionsService<Integer, User> dataSourceUserPermissionsService;
    private final PermissionsService<WatchListAccess, User> watchListUserPermissionsService;
    private final PermissionsService<ViewAccess, User> viewUserPermissionsService;

    public MigrationPermissionsService(PermissionsService<DataPointAccess, User> dataPointUserPermissionsService,
                                       PermissionsService<Integer, User> dataSourceUserPermissionsService,
                                       PermissionsService<WatchListAccess, User> watchListUserPermissionsService,
                                       PermissionsService<ViewAccess, User> viewUserPermissionsService) {
        this.dataPointUserPermissionsService = dataPointUserPermissionsService;
        this.dataSourceUserPermissionsService = dataSourceUserPermissionsService;
        this.watchListUserPermissionsService = watchListUserPermissionsService;
        this.viewUserPermissionsService = viewUserPermissionsService;
    }

    public PermissionsService<DataPointAccess, User> getDataPointUserPermissionsService() {
        return dataPointUserPermissionsService;
    }

    public PermissionsService<Integer, User>  getDataSourceUserPermissionsService() {
        return dataSourceUserPermissionsService;
    }

    public PermissionsService<WatchListAccess, User> getWatchListUserPermissionsService() {
        return watchListUserPermissionsService;
    }

    public PermissionsService<ViewAccess, User> getViewUserPermissionsService() {
        return viewUserPermissionsService;
    }
}
