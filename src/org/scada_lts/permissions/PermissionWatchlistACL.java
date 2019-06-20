package org.scada_lts.permissions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.permissions.model.EntryDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class created by Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class PermissionWatchlistACL {

    private static final Log LOG = LogFactory.getLog(PermissionWatchlistACL.class);

    private static int CLAZZ_ID = 4;

    private static PermissionWatchlistACL instance = null;

    private PermissionWatchlistACL() {
        //
    }

    public static PermissionWatchlistACL getInstance() {
        if (instance == null) {
            instance = new PermissionWatchlistACL();
        }

        return instance;
    }

    public  boolean hasPermissionToRead(int userId, int entityIdentityId) {
        return PermissionEvaluatorAclImp.getInstance().hasPermissionToRead(userId, CLAZZ_ID, entityIdentityId);
    }

    public  boolean hasPermissionToWrite(int userId, int entityIdentityId) {
        return PermissionEvaluatorAclImp.getInstance().hasPermissionToRead(userId, CLAZZ_ID, entityIdentityId);
    }

    public boolean hasPermissionToExecute(int userId, int entityIdentityId) {
        return PermissionEvaluatorAclImp.getInstance().hasPermissionToRead(userId, CLAZZ_ID, entityIdentityId);
    }


    public Map<Integer, EntryDto> filter(int userId) {

        List<EntryDto> lst = PermissionEvaluatorAclImp.getInstance().filter(userId, CLAZZ_ID);

        Map<Integer, EntryDto> map = lst.stream().collect(
                Collectors.toMap(EntryDto::getId, EntryDto::getSelf));

        return map;
    }
}
