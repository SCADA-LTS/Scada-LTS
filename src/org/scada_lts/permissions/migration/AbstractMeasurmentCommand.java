package org.scada_lts.permissions.migration;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.printTime;

abstract class AbstractMeasurmentCommand implements MigrationPermissions {

    private static final Log LOG = LogFactory.getLog(AbstractMeasurmentCommand.class);

    @Override
    public final void execute(List<User> users) {
        long start = System.nanoTime();
        String msg = getName() + " - users size: " + users.size();
        LOG.info(msg);
        work(users);
        printTime(start, getName() + " executed: ");
    }

    public abstract void work(List<User> users);
}
