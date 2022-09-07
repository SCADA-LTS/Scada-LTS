package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.IViewDAO;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

@Service
public class ViewGetShareUsers implements GetShareUsers<View> {

    private final IViewDAO viewDAO;

    public ViewGetShareUsers(IViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<ShareUser> getShareUsers(View object) {
        return viewDAO.selectShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersFromProfile(View object) {
        return viewDAO.selectShareUsersFromProfile(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(View object) {
        List<ShareUser> shareUsers = getShareUsers(object);
        List<ShareUser> fromProfile = getShareUsersFromProfile(object);
        return merge(shareUsers, fromProfile);
    }
}
