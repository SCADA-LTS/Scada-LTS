package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;

import java.util.List;

public interface GetPermissions<T> {
    List<T> getPermissions(User user);
    List<T> getPermissionsByProfile(UsersProfileVO profile);
}
