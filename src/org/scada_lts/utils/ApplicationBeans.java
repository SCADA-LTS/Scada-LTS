package org.scada_lts.utils;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.IUsersProfileDAO;
import org.scada_lts.dao.cache.HighestAlarmLevelCachable;
import org.scada_lts.dao.cache.UserCachable;
import org.scada_lts.dao.cache.UsersProfileCachable;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.*;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.web.ws.services.DataPointServiceWebSocket;
import org.scada_lts.web.ws.services.EventsServiceWebSocket;
import org.scada_lts.web.ws.services.UserEventServiceWebsocket;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;

public class ApplicationBeans implements ApplicationContextAware {
    private static final Log log = LogFactory.getLog(ApplicationBeans.class);

    private static ApplicationContext applicationContext;
    private static ApplicationContext servletContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        if(ApplicationBeans.applicationContext == null)
            applicationContext = context;
        else
            servletContext = context;
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        return getBeanContext(beanName, type);
    }

    public static IUserDAO getUserDaoBean() {
        boolean userCacheEnabled = Common.getEnvironmentProfile().getBoolean(UserCachable.CACHE_ENABLED_KEY, true);
        return userCacheEnabled ?
                getBeanContext("userDaoWithCache", IUserDAO.class) :
                getBeanContext("userDAO", IUserDAO.class);
    }

    public static IUsersProfileDAO getUsersProfileDaoBean() {
        boolean usersprofileCacheEnabled = Common.getEnvironmentProfile().getBoolean(UsersProfileCachable.CACHE_ENABLED_KEY, true);
        return usersprofileCacheEnabled ?
                getBeanContext("usersProfileDaoWithCache", IUsersProfileDAO.class) :
                getBeanContext("usersProfileDAO", IUsersProfileDAO.class);
    }

    public static PermissionsService<Integer, User> getDataSourceUserPermissionsServiceBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<Integer, User>) (permissionsCacheEnabled ?
                getBeanContext("dataSourceUserPermissionsServiceWithCache", PermissionsService.class) :
                getBeanContext("dataSourceUserPermissionsService", PermissionsService.class) );
    }

    public static PermissionsService<DataPointAccess, User> getDataPointUserPermissionsServiceBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<DataPointAccess, User>) (permissionsCacheEnabled ?
                getBeanContext("dataPointUserPermissionsServiceWithCache", PermissionsService.class) :
                getBeanContext("dataPointUserPermissionsService", PermissionsService.class));
    }

    public static GetShareUsers<View> getViewGetShareUsersBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<View>) (permissionsCacheEnabled ?
                getBeanContext("viewGetShareUsersWithCache", GetShareUsers.class) :
                getBeanContext("viewGetShareUsers", GetShareUsers.class));
    }

    public static GetShareUsers<WatchList> getWatchListGetShareUsersBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<WatchList>) (permissionsCacheEnabled ?
                getBeanContext("watchListGetShareUsersWithCache", GetShareUsers.class) :
                getBeanContext("watchListGetShareUsers", GetShareUsers.class));
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

    public static IHighestAlarmLevelService getHighestAlarmLevelServiceBean() {
        boolean highestAlarmLevelCacheEnabled = Common.getEnvironmentProfile().getBoolean(HighestAlarmLevelCachable.CACHE_ENABLED_KEY, true);
        return highestAlarmLevelCacheEnabled ? getBeanContext("highestAlarmLevelServiceWithCache", IHighestAlarmLevelService.class) :
                getBeanContext("highestAlarmLevelService", IHighestAlarmLevelService.class);
    }

    public static Optional<UserEventServiceWebsocket> getUserEventServiceWebsocketBean() {
        return Optional.ofNullable(getBeanContext("userEventServiceWebsocket", UserEventServiceWebsocket.class));
    }

    public static Optional<DataPointServiceWebSocket> getDataPointServiceWebSocketBean() {
        return Optional.ofNullable(getBeanContext("dataPointServiceWebSocket", DataPointServiceWebSocket.class));
    }

    public static Optional<EventsServiceWebSocket> getEventsServiceWebSocketBean() {
        return Optional.ofNullable(getBeanContext("eventsServiceWebSocket", EventsServiceWebSocket.class));
    }

    private static <T> T getBeanContext(String beanName, Class<T> clazz) {
        try {
            if (servletContext != null)
                return servletContext.getBean(beanName, clazz);
            return applicationContext.getBean(beanName, clazz);
        } catch (Exception e) {
            log.warn(e);
            return null;
        }
    }
}
