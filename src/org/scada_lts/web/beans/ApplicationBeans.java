package org.scada_lts.web.beans;

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
import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.cache.HighestAlarmLevelCachable;
import org.scada_lts.dao.cache.UserCachable;
import org.scada_lts.dao.cache.UsersProfileCachable;
import org.scada_lts.dao.cache.ViewCachable;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.*;
import org.scada_lts.service.IHighestAlarmLevelService;
import org.scada_lts.web.ws.services.DataPointServiceWebSocket;
import org.scada_lts.web.ws.services.EventsServiceWebSocket;
import org.scada_lts.web.ws.services.UserEventServiceWebSocket;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class ApplicationBeans {

    private ApplicationBeans() {}

    private static final Log LOG = LogFactory.getLog(ApplicationBeans.class);

    public static <T> T getBean(String beanName, Class<T> type) {
        return getBeanFromContext(beanName, type);
    }

    public static IUserDAO getUserDaoBean() {
        boolean userCacheEnabled = Common.getEnvironmentProfile().getBoolean(UserCachable.CACHE_ENABLED_KEY, true);
        return userCacheEnabled ?
                getBeanFromContext("userDaoWithCache", IUserDAO.class) :
                getBeanFromContext("userDAO", IUserDAO.class);
    }

    public static IUsersProfileDAO getUsersProfileDaoBean() {
        boolean usersprofileCacheEnabled = Common.getEnvironmentProfile().getBoolean(UsersProfileCachable.CACHE_ENABLED_KEY, true);
        return usersprofileCacheEnabled ?
                getBeanFromContext("usersProfileDaoWithCache", IUsersProfileDAO.class) :
                getBeanFromContext("usersProfileDAO", IUsersProfileDAO.class);
    }

    public static PermissionsService<Integer, User> getDataSourceUserPermissionsServiceBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<Integer, User>) (permissionsCacheEnabled ?
                getBeanFromContext("dataSourceUserPermissionsServiceWithCache", PermissionsService.class) :
                getBeanFromContext("dataSourceUserPermissionsService", PermissionsService.class) );
    }

    public static PermissionsService<DataPointAccess, User> getDataPointUserPermissionsServiceBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(PermissionsService.CACHE_ENABLED_KEY, true);
        return (PermissionsService<DataPointAccess, User>) (permissionsCacheEnabled ?
                getBeanFromContext("dataPointUserPermissionsServiceWithCache", PermissionsService.class) :
                getBeanFromContext("dataPointUserPermissionsService", PermissionsService.class));
    }

    @Deprecated
    public static GetShareUsers<View> getViewGetShareUsersBean() {
        return new ViewGetShareUsers(getViewDaoBean());
    }

    public static GetShareUsers<WatchList> getWatchListGetShareUsersBean() {
        boolean permissionsCacheEnabled = Common.getEnvironmentProfile().getBoolean(GetShareUsers.CACHE_ENABLED_KEY, true);
        return (GetShareUsers<WatchList>) (permissionsCacheEnabled ?
                getBeanFromContext("watchListGetShareUsersWithCache", GetShareUsers.class) :
                getBeanFromContext("watchListGetShareUsers", GetShareUsers.class));
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
        return highestAlarmLevelCacheEnabled ? getBeanFromContext("highestAlarmLevelServiceWithCache", IHighestAlarmLevelService.class) :
                getBeanFromContext("highestAlarmLevelService", IHighestAlarmLevelService.class);
    }

    public static IViewDAO getViewDaoBean() {
        boolean viewCacheEnabled = Common.getEnvironmentProfile().getBoolean(ViewCachable.CACHE_ENABLED_KEY, true);
        return viewCacheEnabled ?
                getBeanFromContext("viewDaoWithCache", IViewDAO.class) :
                getBeanFromContext("viewDAO", IViewDAO.class);
    }

    public static class Lazy {

        private Lazy() {}

        public static Optional<UserEventServiceWebSocket> getUserEventServiceWebsocketBean() {
            return getBeanFromContext("userEventServiceWebSocket", UserEventServiceWebSocket.class);
        }

        public static Optional<DataPointServiceWebSocket> getDataPointServiceWebSocketBean() {
            return getBeanFromContext("dataPointServiceWebSocket", DataPointServiceWebSocket.class);
        }

        public static Optional<EventsServiceWebSocket> getEventsServiceWebSocketBean() {
            return getBeanFromContext("eventsServiceWebSocket", EventsServiceWebSocket.class);
        }

        private static <T> Optional<T> getBeanFromContext(String beanName, Class<T> clazz) {
            try {
                return Optional.ofNullable(get(beanName, clazz));
            } catch (NoSuchBeanDefinitionException ex) {
                LOG.debug(ex);
                return Optional.empty();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                return Optional.empty();
            }
        }
    }

    private static <T> T getBeanFromContext(String beanName, Class<T> clazz) {
        try {
            return get(beanName, clazz);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    private static <T> T get(String beanName, Class<T> clazz) {
        ApplicationContext context = getBeansContext();
        if (context != null)
            return context.getBean(beanName, clazz);
        return null;
    }

    private static ApplicationContext getBeansContext() {
        return GetServletBeans.context() == null ? getBeansApplication() : GetServletBeans.context();
    }

    private static ApplicationContext getBeansApplication() {
        return GetApplicationBeans.context() == null ? null : GetApplicationBeans.context();
    }
}
