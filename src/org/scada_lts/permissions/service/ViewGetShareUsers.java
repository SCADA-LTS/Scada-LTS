package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.utils.ApplicationContextProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.scada_lts.permissions.service.util.PermissionsUtils.merge;

@Service
@CacheConfig(cacheNames = "share_user_list_by_view")
public class ViewGetShareUsers implements GetShareUsers<View> {

    private final ViewDAO viewDAO;

    public ViewGetShareUsers() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        this.viewDAO = (ViewDAO) context.getBean("viewDAO");
    }

    public ViewGetShareUsers(ViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    @Cacheable(key = "'shareUsers' + #object.id", unless = "#object == null || #result.isEmpty()")
    public List<ShareUser> getShareUsers(View object) {
        return viewDAO.getShareUsers(object.getId());
    }

    @Override
    @Cacheable(key = "'shareUsersFromProfile' + #object.id", unless = "#object == null || #result.isEmpty()")
    public List<ShareUser> getShareUsersFromProfile(View object) {
        return viewDAO.selectViewShareUsers(object.getId());
    }

    @Override
    public List<ShareUser> getShareUsersWithProfile(View object) {
        List<ShareUser> shareUsers = getShareUsers(object);
        List<ShareUser> fromProfile = getShareUsersFromProfile(object);
        return merge(shareUsers, fromProfile);
    }
}
