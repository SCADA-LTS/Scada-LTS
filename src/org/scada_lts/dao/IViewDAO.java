package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public interface IViewDAO extends ScadaRepository<View, Integer> {

    default void init() {}

    View findByName(String name);

    @Deprecated
    void deleteViewForUser(int viewId);

    void deleteViewForUser(int viewId, int userId);

    List<ScadaObjectIdentifier> selectViewIdentifiersWithAccess(int userId, int profileId);

    List<View> selectViewWithAccess(int userId, int profileId);

    List<ViewAccess> selectViewPermissions(int userId);

    int[] insertPermissions(int userId, List<ViewAccess> toInsert);

    int[] deletePermissions(int userId, List<ViewAccess> toDelete);

    List<ShareUser> selectShareUsers(int viewId);

    List<ShareUser> selectShareUsersFromProfile(int viewId);
}
