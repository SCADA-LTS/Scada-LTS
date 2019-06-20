package org.scada_lts.permissions;

import org.scada_lts.permissions.model.EntryDto;

import java.util.List;

/**
 * @author Grzegorz Bylica grzegorz.bylica@gmail.com
 **/
public interface PermissionEvaluatorAcl {

    boolean hasPermissionToWrite(int userId, int classId, int entityIdentityId);

    boolean hasPermissionToRead(int userId, int classId, int entityIdentityId);

    boolean hasPermissionToExecute(int userId, int classId, int entityIdentityId);

    //filter is always check isRead
    List<EntryDto> filter(long clazzId, long userId );
}
