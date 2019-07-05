package com.serotonin.mango.dao_cache;

import br.org.scadabr.db.dao.FlexProjectDao;
import br.org.scadabr.db.dao.ScriptDao;
import br.org.scadabr.db.dao.UsersProfileDao;
import com.serotonin.mango.db.dao.*;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Responsibility :
 *
 * To have access to Dao, you have here it, by one place on Scada application
 *
 *
 * @autor hyski.mateusz@gmail.com (SoftQ) on 28.06.19
 *
 */

public class DaoInstances {

    /*private static final DaoInstances INSTANCE = new DaoInstances();
    private static final Map<Daos,Object> daoObjects = new HashMap<Daos,Object>();
    static {
        daoObjects.put(Daos.VIEW_DAO,new ViewDao());
        daoObjects.put(Daos.SCRIPT_DAO,new ScriptDao());
        daoObjects.put(Daos.DATA_POINT_DAO,new DataPointDao());
        daoObjects.put(Daos.USER_DAO,new UserDao());
        daoObjects.put(Daos.USERS_PROFILE_DAO,new UsersProfileDao());
        daoObjects.put(Daos.DATA_SOURCE_DAO,new DataSourceDao());
        daoObjects.put(Daos.WATCH_LIST_DAO,new WatchListDao());
        daoObjects.put(Daos.EVENT_DAO,new EventDao());
        daoObjects.put(Daos.SCHEDULED_EVENT_DAO,new ScheduledEventDao());
        daoObjects.put(Daos.REPORT_DAO,new ReportDao());
        daoObjects.put(Daos.POINT_LINK_DAO,new PointLinkDao());
        daoObjects.put(Daos.MAILING_LIST_DAO,new MailingListDao());
        daoObjects.put(Daos.MAINTENANCE_EVENT_DAO,new MaintenanceEventDao());
        daoObjects.put(Daos.COMPOUND_EVENT_DETECTOR_DAO,new CompoundEventDetectorDao());
        daoObjects.put(Daos.PUBLISHER_DAO,new PublisherDao());
        daoObjects.put(Daos.POINT_VALUE_DAO,new PointValueDao());
        daoObjects.put(Daos.FLEX_PROJECT_DAO,new FlexProjectDao());
    }*/
    private static final ViewDao ViewDao =new ViewDao();
    private static final ScriptDao ScriptDao=new ScriptDao();
    private static final DataPointDao DataPointDao=new DataPointDao();
    private static final UserDao UserDao=new UserDao();
    private static final UsersProfileDao UsersProfileDao=new UsersProfileDao();
    private static final DataSourceDao DataSourceDao=new DataSourceDao();
    private static final WatchListDao WatchListDao=new WatchListDao();
    private static final EventDao EventDao=new EventDao();
    private static final ScheduledEventDao ScheduledEventDao=new ScheduledEventDao();
    private static final ReportDao ReportDao=new ReportDao();
    private static final PointLinkDao PointLinkDao=new PointLinkDao();
    private static final MailingListDao MailingListDao=new MailingListDao();
    private static final MaintenanceEventDao MaintenanceEventDao=new MaintenanceEventDao();
    private static final CompoundEventDetectorDao CompoundEventDetectorDao=new CompoundEventDetectorDao();
    private static final PublisherDao PublisherDao=new PublisherDao();
    private static final PointValueDao PointValueDao=new PointValueDao();
    private static final FlexProjectDao FlexProjectDao=new FlexProjectDao();

    public static /*synchronized */ViewDao getViewDao(){
        return ViewDao;//(ViewDao)getInstance().daoObjects.get(Daos.VIEW_DAO);
    }
    public static /*synchronized*/  ScriptDao getScriptDao(){
        return ScriptDao;//(ScriptDao)getInstance().daoObjects.get(Daos.VIEW_DAO);
    }
    public static /*synchronized  */DataPointDao getDataPointDao(){
        return DataPointDao;//(DataPointDao)getInstance().daoObjects.get(Daos.DATA_POINT_DAO);
    }
    public static /*synchronized  */UserDao getUserDao(){
        return UserDao;//(UserDao)getInstance().daoObjects.get(Daos.USER_DAO);
    }
    public static /*synchronized*/  UsersProfileDao getUsersProfileDao(){
        return UsersProfileDao;//(UsersProfileDao)getInstance().daoObjects.get(Daos.USERS_PROFILE_DAO);
    }
    public static /*synchronized  */DataSourceDao getDataSourceDao(){
        return DataSourceDao;//(DataSourceDao)getInstance().daoObjects.get(Daos.DATA_SOURCE_DAO);
    }
    public static /*synchronized*/  WatchListDao getWatchListDao(){
        return WatchListDao;//(WatchListDao)getInstance().daoObjects.get(Daos.WATCH_LIST_DAO);
    }
    public static /*synchronized  */EventDao getEventDao(){
        return EventDao;//(EventDao)getInstance().daoObjects.get(Daos.EVENT_DAO);
    }
    public static /*synchronized  */ScheduledEventDao getScheduledEventDao(){
        return ScheduledEventDao;//(ScheduledEventDao)getInstance().daoObjects.get(Daos.SCHEDULED_EVENT_DAO);
    }
    public static /*synchronized*/  ReportDao getReportDao(){
        return ReportDao;//(ReportDao)getInstance().daoObjects.get(Daos.REPORT_DAO);
    }
    public static /*synchronized  */PointLinkDao getPointLinkDao(){
        return PointLinkDao;//(PointLinkDao)getInstance().daoObjects.get(Daos.POINT_LINK_DAO);
    }
    public static /*synchronized*/  MailingListDao getMailingListDao(){
        return MailingListDao;//(MailingListDao)getInstance().daoObjects.get(Daos.MAILING_LIST_DAO);
    }
    public static /*synchronized  */MaintenanceEventDao getMaintenanceEventDao(){
        return MaintenanceEventDao;//(MaintenanceEventDao)getInstance().daoObjects.get(Daos.MAINTENANCE_EVENT_DAO);
    }
    public static /*synchronized*/  CompoundEventDetectorDao getCompoundEventDetectorDao(){
        return CompoundEventDetectorDao;//(CompoundEventDetectorDao)getInstance().daoObjects.get(Daos.COMPOUND_EVENT_DETECTOR_DAO);
    }
    public static /*synchronized  */PublisherDao getPublisherDao(){
        return PublisherDao;//(PublisherDao)getInstance().daoObjects.get(Daos.PUBLISHER_DAO);
    }
    public static /*synchronized*/  PointValueDao getPointValueDao(){
        return PointValueDao;//(PointValueDao)getInstance().daoObjects.get(Daos.POINT_VALUE_DAO);
    }
    public static /*synchronized  */FlexProjectDao getFlexProjectDao(){
        return FlexProjectDao;//(FlexProjectDao)getInstance().daoObjects.get(Daos.FLEX_PROJECT_DAO);
    }
    /*public static final ViewDao getViewDao =new ViewDao();
    public static final ScriptDao getScriptDao =new ScriptDao();
    public static final DataPointDao getDataPointDao =new DataPointDao();
    public static final UserDao getUserDao =new UserDao();
    public static final UsersProfileDao getUsersProfileDao =new UsersProfileDao();
    public static final DataSourceDao getDataSourceDao =new DataSourceDao();
    public static final WatchListDao getWatchListDao =new WatchListDao();
    public static final EventDao getEventDao =new EventDao();
    public static final ScheduledEventDao getScheduledEventDao =new ScheduledEventDao();
    public static final ReportDao getReportDao =new ReportDao();
    public static final PointLinkDao getPointLinkDao =new PointLinkDao();
    public static final MailingListDao getMailingListDao =new MailingListDao();
    public static final MaintenanceEventDao getMaintenanceEventDao =new MaintenanceEventDao();
    public static final CompoundEventDetectorDao getCompoundEventDetectorDao =new CompoundEventDetectorDao();
    public static final PublisherDao getPublisherDao =new PublisherDao();
    public static final PointValueDao getPointValueDao =new PointValueDao();
    public static final FlexProjectDao getFlexProjectDao =new FlexProjectDao();*/

    /*public static DaoInstances getInstance(){
        return INSTANCE;
    }*/


}
