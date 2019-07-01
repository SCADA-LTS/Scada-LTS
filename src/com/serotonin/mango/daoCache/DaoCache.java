package com.serotonin.mango.daoCache;

import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.db.dao.UsersProfileDao;
import com.serotonin.mango.db.dao.*;

import java.util.HashMap;
import java.util.Map;

public class DaoCache {

    private static final DaoCache instance = new DaoCache();
    private static Map<Daos ,Object> daoCache = new HashMap<Daos,Object>();
    static{
        daoCache.put(Daos.VIEW_DAO,new ViewDao());
        daoCache.put(Daos.SCRIPT_DAO,new ScriptDao());
        daoCache.put(Daos.DATA_POINT_DAO,new DataPointDao());
        daoCache.put(Daos.USER_DAO,new UserDao());
        daoCache.put(Daos.USERS_PROFILE_DAO,new UsersProfileDao());
        daoCache.put(Daos.DATA_SOURCE_DAO,new DataSourceDao());
        daoCache.put(Daos.WATCH_LIST_DAO,new WatchListDao());
        daoCache.put(Daos.EVENT_DAO,new EventDao());
        daoCache.put(Daos.SCHEDULED_EVENT_DAO,new ScheduledEventDao());
        daoCache.put(Daos.REPORT_DAO,new ReportDao());
        daoCache.put(Daos.POINT_LINK_DAO,new PointLinkDao());
        daoCache.put(Daos.MAILING_LIST_DAO,new MailingListDao());
        daoCache.put(Daos.MAINTENANCE_EVENT_DAO,new MaintenanceEventDao());
        daoCache.put(Daos.COMPOUND_EVENT_DETECTOR_DAO,new CompoundEventDetectorDao());
        daoCache.put(Daos.PUBLISHER_DAO,new PublisherDao());
        daoCache.put(Daos.POINT_VALUE_DAO,new PointValueDao());

    }

    private static DaoCache getInstance(){

        return instance;
    }
    public static synchronized ViewDao getViewDao(){

        return (ViewDao)getInstance().daoCache.get(Daos.VIEW_DAO);
    }
    public static synchronized ScriptDao getScriptDao(){
        return (ScriptDao)getInstance().daoCache.get(Daos.SCRIPT_DAO);
    }
    public static synchronized DataPointDao getDataPointDao(){
        return (DataPointDao)getInstance().daoCache.get(Daos.DATA_POINT_DAO);
    }
    public static synchronized UserDao getUserDao(){

        return (UserDao)getInstance().daoCache.get(Daos.USER_DAO);
    }
    public static synchronized UsersProfileDao getUsersProfileDao(){
        return (UsersProfileDao)getInstance().daoCache.get(Daos.USERS_PROFILE_DAO);
    }
    public static synchronized DataSourceDao getDataSourceDao(){
        return (DataSourceDao)getInstance().daoCache.get(Daos.DATA_SOURCE_DAO);
    }
    public static synchronized WatchListDao getWatchListDao(){
        return (WatchListDao)getInstance().daoCache.get(Daos.WATCH_LIST_DAO);
    }
    public static synchronized EventDao getEventDao(){

        return (EventDao)getInstance().daoCache.get(Daos.EVENT_DAO);
    }
    public static synchronized ScheduledEventDao getScheduledEventDao(){
        return (ScheduledEventDao)getInstance().daoCache.get(Daos.SCHEDULED_EVENT_DAO);
    }
    public static synchronized ReportDao getReportDao(){

        return (ReportDao)getInstance().daoCache.get(Daos.REPORT_DAO);
    }
    public static synchronized PointLinkDao getPointLinkDao(){
        return (PointLinkDao)getInstance().daoCache.get(Daos.POINT_LINK_DAO);
    }
    public static synchronized MailingListDao getMailingListDao(){
        return (MailingListDao)getInstance().daoCache.get(Daos.MAILING_LIST_DAO);
    }
    public static synchronized MaintenanceEventDao getMaintenanceEventDao(){
        return (MaintenanceEventDao)getInstance().daoCache.get(Daos.MAINTENANCE_EVENT_DAO);
    }
    public static synchronized CompoundEventDetectorDao getCompoundEventDetectorDao(){
        return (CompoundEventDetectorDao)getInstance().daoCache.get(Daos.COMPOUND_EVENT_DETECTOR_DAO);
    }
    public static synchronized PublisherDao getPublisherDao(){
        return (PublisherDao)getInstance().daoCache.get(Daos.PUBLISHER_DAO);
    }
    public static synchronized  PointValueDao getPointValueDao(){
        return (PointValueDao)getInstance().daoCache.get(Daos.POINT_VALUE_DAO);
    }
}
