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
import org.scada_lts.permissions.service.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationBeans implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        return context.getBean(beanName, type);
    }

    public static IUserDAO getUserDaoBean() {
        boolean userCacheEnabled = Common.getEnvironmentProfile().getBoolean(UserCachable.CACHE_ENABLED_KEY, true);
        return (IUserDAO) (userCacheEnabled ? context.getBean("userDaoWithCache") : context.getBean("userDAO"));
    }

    public static IUsersProfileDAO getUsersProfileDaoBean() {
        boolean usersprofileCacheEnabled = Common.getEnvironmentProfile().getBoolean(UsersProfileCachable.CACHE_ENABLED_KEY, true);
        return (IUsersProfileDAO) (usersprofileCacheEnabled ? context.getBean("usersProfileDaoWithCache") : context.getBean("usersProfileDAO"));
    }

    public static PermissionsService<Integer, User> getDataSourceUserPermissionsServiceBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<Integer, User>) (permissionsCacheEnabled ? context.getBean("dataSourceUserPermissionsServiceWithCache") : context.getBean("dataSourceUserPermissionsService"));
    }

    public static PermissionsService<DataPointAccess, User> getDataPointUserPermissionsServiceBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<DataPointAccess, User>) (permissionsCacheEnabled ? context.getBean("dataPointUserPermissionsServiceWithCache") : context.getBean("dataPointUserPermissionsService"));
    }

    public static GetShareUsers<View> getViewGetShareUsersBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<View>) (permissionsCacheEnabled ? context.getBean("viewGetShareUsersWithCache") : context.getBean("viewGetShareUsers"));
    }

    public static GetShareUsers<WatchList> getWatchListGetShareUsersBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<WatchList>) (permissionsCacheEnabled ? context.getBean("watchListGetShareUsersWithCache") : context.getBean("watchListGetShareUsers"));
    }

    public static UsersProfileService getUsersProfileService() {
        return new UsersProfileService(getUsersProfileDaoBean(), getUserDaoBean(),
                getWatchListProfilePermissionsService(), getDataPointProfilePermissionsService(),
                getDataSourceProfilePermissionsService(), getViewProfilePermissionsService());
    }

    public static PermissionsService<Integer, UsersProfileVO> getDataSourceProfilePermissionsService() {
        return new DataSourceProfilePermissionsService(getUsersProfileDaoBean());
    }

    public static PermissionsService<DataPointAccess, UsersProfileVO> getDataPointProfilePermissionsService() {
        return new DataPointProfilePermissionsService(getUsersProfileDaoBean());
    }

    public static PermissionsService<ViewAccess, UsersProfileVO> getViewProfilePermissionsService() {
        return new ViewProfilePermissionsService(getUsersProfileDaoBean());
    }

    public static PermissionsService<WatchListAccess, UsersProfileVO> getWatchListProfilePermissionsService() {
        return new WatchListProfilePermissionsService(getUsersProfileDaoBean());
    }
}
