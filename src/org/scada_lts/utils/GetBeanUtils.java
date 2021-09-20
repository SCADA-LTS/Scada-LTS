package org.scada_lts.utils;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.IUsersProfileDAO;
import org.scada_lts.dao.cache.UserCachable;
import org.scada_lts.dao.cache.UsersProfileCachable;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.GetShareUsers;
import org.scada_lts.permissions.service.PermissionsService;
import org.springframework.context.ApplicationContext;

public class GetBeanUtils {

    public static IUserDAO getUserDaoBean(ApplicationContext context) {
        boolean userCacheEnabled = Common.getEnvironmentProfile().getBoolean(UserCachable.CACHE_ENABLED_KEY, true);
        return (IUserDAO) (userCacheEnabled ? context.getBean("userDaoWithCache") : context.getBean("userDAO"));
    }

    public static IUsersProfileDAO getUsersProfileDaoBean(ApplicationContext context) {
        boolean usersprofileCacheEnabled = Common.getEnvironmentProfile().getBoolean(UsersProfileCachable.CACHE_ENABLED_KEY, true);
        return (IUsersProfileDAO) (usersprofileCacheEnabled ? context.getBean("usersProfileDaoWithCache") : context.getBean("usersProfileDAO"));
    }

    public static UsersProfileService getUsersProfileServiceBean(ApplicationContext context) {
        boolean usersprofileCacheEnabled = Common.getEnvironmentProfile().getBoolean(UsersProfileCachable.CACHE_ENABLED_KEY, true);
        return (UsersProfileService) (usersprofileCacheEnabled ? context.getBean("usersProfileServiceWithCache") : context.getBean("usersProfileService"));
    }

    public static PermissionsService<Integer, UsersProfileVO> getDataSourceProfilePermissionsServiceBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<Integer, UsersProfileVO>) (permissionsCacheEnabled ? context.getBean("dataSourceProfilePermissionsServiceWithCache") : context.getBean("dataSourceProfilePermissionsService"));
    }

    public static PermissionsService<DataPointAccess, UsersProfileVO> getDataPointProfilePermissionsServiceBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<DataPointAccess, UsersProfileVO>) (permissionsCacheEnabled ? context.getBean("dataPointProfilePermissionsServiceWithCache") : context.getBean("dataPointProfilePermissionsService"));
    }

    public static PermissionsService<ViewAccess, UsersProfileVO> getViewProfilePermissionsServiceBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<ViewAccess, UsersProfileVO>) (permissionsCacheEnabled ? context.getBean("viewProfilePermissionsServiceWithCache") : context.getBean("viewProfilePermissionsService"));
    }

    public static PermissionsService<WatchListAccess, UsersProfileVO> getWatchListProfilePermissionsServiceBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<WatchListAccess, UsersProfileVO>) (permissionsCacheEnabled ? context.getBean("watchListProfilePermissionsServiceWithCache") : context.getBean("watchListProfilePermissionsService"));
    }

    public static PermissionsService<Integer, User> getDataSourceUserPermissionsServiceBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<Integer, User>) (permissionsCacheEnabled ? context.getBean("dataSourceUserPermissionsServiceWithCache") : context.getBean("dataSourceUserPermissionsService"));
    }

    public static PermissionsService<DataPointAccess, User> getDataPointUserPermissionsServiceBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<DataPointAccess, User>) (permissionsCacheEnabled ? context.getBean("dataPointUserPermissionsServiceWithCache") : context.getBean("dataPointUserPermissionsService"));
    }

    public static GetShareUsers<View> getViewGetShareUsersBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<View>) (permissionsCacheEnabled ? context.getBean("viewGetShareUsersWithCache") : context.getBean("viewGetShareUsers"));
    }

    public static GetShareUsers<WatchList> getWatchListGetShareUsersBean(ApplicationContext context) {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<WatchList>) (permissionsCacheEnabled ? context.getBean("watchListGetShareUsersWithCache") : context.getBean("watchListGetShareUsers"));
    }

    public static GetShareUsers<View> getViewGetShareUsersBean() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        return getViewGetShareUsersBean(context);
    }

    public static GetShareUsers<WatchList> getWatchListGetShareUsersBean() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        return getWatchListGetShareUsersBean(context);
    }
}
