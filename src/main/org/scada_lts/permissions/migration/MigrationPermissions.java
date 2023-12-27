package org.scada_lts.permissions.migration;

import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;

import java.util.List;

public interface MigrationPermissions {
    void execute(List<User> users);
    String getName();

    static MigrationPermissions newMigration(MigrationPermissionsService migrationPermissionsService,
                                             MigrationDataService migrationDataService) {
        return new MigrationPermissionsCommand(migrationPermissionsService, migrationDataService);
    }
}
