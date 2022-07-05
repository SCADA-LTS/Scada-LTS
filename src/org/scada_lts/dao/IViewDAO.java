package org.scada_lts.dao;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public interface IViewDAO {

    void deleteViewForUser(int viewId);

    void deleteViewForUser(int viewId, int userId);

    int insertView(View entity);

    void updateView(View entity);

    void deleteView(View entity);

    List<View> selectViews();

    View selectView(int id);

    View selectViewByName(String name);

    View selectViewByXid(String xid);

    List<ScadaObjectIdentifier> selectViewIdentifiersWithAccess(int userId, int profileId);

    List<ScadaObjectIdentifier> selectViewIdentifiers();


    List<View> selectViewWithAccess(int userId, int profileId);

    List<ViewAccess> selectViewPermissions(int userId);

    int[] insertPermissions(int userId, List<ViewAccess> toInsert);

    int[] deletePermissions(int userId, List<ViewAccess> toDelete);

    List<ShareUser> selectShareUsers(int viewId);

    List<ShareUser> selectShareUsersFromProfile(int viewId);
}
