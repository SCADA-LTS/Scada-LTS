package org.scada_lts.dao.migration.postgresql;

import br.org.scadabr.view.component.LinkComponent;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.view.event.NoneEventRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.TimePeriodType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.http.HttpRetrieverDataSourceVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.util.StringUtils;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.*;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorVO;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.migration.MigrationDataService;
import org.scada_lts.permissions.migration.MigrationPermissions;
import org.scada_lts.permissions.migration.MigrationPermissionsService;
import org.scada_lts.permissions.migration.dao.*;
import org.scada_lts.permissions.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class V1__BasePostgresMigration extends BaseJavaMigration {

    private static final Logger LOG = LoggerFactory.getLogger(V1__BasePostgresMigration.class);

    private static final int Z_INDEX_DEFAULT = 2;
    private static final int Z_INDEX_MIN = 1;

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            jdbcTemplate.execute("SELECT 1");
            LOG.debug("===> JDBC is working");
        } catch (Exception e) {
            LOG.error("===> JDBC FAILURE: " + e.getMessage(), e);
            throw e;
        }

        //System settingsds

        final String settingSQL = ""
                + "create table IF NOT EXISTS systemSettings ("
                + "settingName varchar(32) not null,"
                + "settingValue TEXT,"
                + "primary key (settingName)"
                + ")";

        jdbcTemplate.execute(settingSQL);

        //Users
        final String usersSQL = ""
                + "create table IF NOT EXISTS users ("
                + "id SERIAL,"
                + "username varchar(40) not null,"
                + "password varchar(30) not null,"
                + "email varchar(255) not null,"
                + "phone varchar(40),"
                + "admin char(1) not null,"
                + "disabled char(1) not null,"
                + "lastLogin bigint,"
                + "selectedWatchList int,"
                + "homeUrl varchar(255),"
                + "receiveAlarmEmails int not null,"
                + "receiveOwnAuditEvents char(1) not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(usersSQL);

        //userCommets
        final String userCommentsSQL = ""
                + "create table IF NOT EXISTS userComments ("
                + "userId int,"
                + "commentType int not null,"
                + "typeKey int not null,"
                + "ts bigint not null,"
                + "commentText varchar(1024) not null"
                + ")";

        jdbcTemplate.execute(userCommentsSQL);

        final String constraintUserCommentsFk1 = ""
                + "alter table userComments "
                + "add constraint userCommentsFk1 foreign key (userId) "
                + "references users(id);";

        try {
            jdbcTemplate.execute(constraintUserCommentsFk1);
        } catch (Exception ex) {
            LOG.warn("Constraint userCommentsFk1 already exists, skipping.");
        }

        //Mailing lists
        final String mailingListsSQL = ""
                + "create table IF NOT EXISTS mailingLists (  "
                + "id SERIAL,  "
                + "xid varchar(50) not null,  "
                + "name varchar(40) not null,  "
                + "primary key (id))";

        jdbcTemplate.execute(mailingListsSQL);

        try{
            jdbcTemplate.execute("alter table mailingLists add constraint mailingListsUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint mailingListsUn1 already exists, skipping.");
        }

        final String mailingListInactiveSQL = ""
                + "create table IF NOT EXISTS mailingListInactive ("
                + "mailingListId int not null,"
                + "inactiveInterval int not null"
                + ")";

        jdbcTemplate.execute(mailingListInactiveSQL);

        try {
            jdbcTemplate.execute("alter table mailingListInactive add constraint mailingListInactiveFk1 foreign key (mailingListId) references mailingLists(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint mailingListInactiveFk1 already exists, skipping.");
        }

        final String mailingListMembersSQL=""
                + "create table IF NOT EXISTS mailingListMembers ("
                + "mailingListId int not null,"
                + "typeId int not null,"
                + "userId int,"
                + "address varchar(255)"
                + ")";

        jdbcTemplate.execute(mailingListMembersSQL);

        try{
        jdbcTemplate.execute("alter table mailingListMembers add constraint mailingListMembersFk1 foreign key (mailingListId) references mailingLists(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint mailingListMembersFk1 already exists, skipping.");
        }

        // Data Sources
        final String dataSourcesSQL = ""
                + "create table IF NOT EXISTS dataSources ("
                + "id SERIAL, "
                + "xid varchar(50) not null, "
                + "name varchar(40) not null, "
                + "dataSourceType int not null, "
                + "data BYTEA not null, "
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(dataSourcesSQL);

        try{
        jdbcTemplate.execute("alter table dataSources add constraint dataSourcesUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint dataSourcesUn1 already exists, skipping.");
        }
        // Data source permissions

        final String dataSourcePermissionsSQL = ""
                + "create table IF NOT EXISTS dataSourceUsers (dataSourceId int not null,userId int not null)";

        jdbcTemplate.execute(dataSourcePermissionsSQL);

        try{
        jdbcTemplate.execute("alter table dataSourceUsers add constraint dataSourceUsersFk1 foreign key (dataSourceId) references dataSources(id);");
        jdbcTemplate.execute("alter table dataSourceUsers add constraint dataSourceUsersFk2 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint dataSourceUsersFk1/dataSourceUsersFk2 already exists, skipping.");
        }

        // Scripts

        final String scriptsSQL = ""
                + "create table IF NOT EXISTS scripts ("
                + "id SERIAL,"
                + "userId int not null,"
                + "xid varchar(50) not null,"
                + "name varchar(40) not null,"
                + "script varchar(16384) not null,"
                + "data BYTEA not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(scriptsSQL);
        try {
            jdbcTemplate.execute("alter table scripts add constraint scriptsUn1 unique (xid);");
            jdbcTemplate.execute("alter table scripts add constraint scriptsFk1 foreign key (userId) references users(id);");
        } catch (Exception ex) {
            LOG.warn("Constraint scriptsUn1/scriptsFk1 already exists, skipping.");
        }

        // FlexProjects

        final String flexProjectsSQL = ""
                + "create table IF NOT EXISTS flexProjects ("
                + "id SERIAL,"
                + "name varchar(40) not null,"
                + "description varchar(1024),"
                + "xmlConfig varchar(16384) not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(flexProjectsSQL);

        // Data Points

        final String dataPointsSQL = ""
                + "create table IF NOT EXISTS dataPoints ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "dataSourceId int not null,"
                + "data BYTEA not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(dataPointsSQL);
        try {
            jdbcTemplate.execute("alter table dataPoints add constraint dataPointsUn1 unique (xid);");
            jdbcTemplate.execute("alter table dataPoints add constraint dataPointsFk1 foreign key (dataSourceId) references dataSources(id);");
        } catch (Exception ex) {
            LOG.warn("Constraint dataPointsUn1/dataPointsFk1 already exists, skipping.");
        }

        // Data point permissions

        final String dataPointUsersSql = ""
                + "create table IF NOT EXISTS dataPointUsers ("
                + "dataPointId int not null,"
                + "userId int not null,"
                + "permission int not null"
                + ")";

        jdbcTemplate.execute(dataPointUsersSql);

        try {
            jdbcTemplate.execute("alter table dataPointUsers add constraint dataPointUsersFk1 foreign key (dataPointId) references dataPoints(id);");
            jdbcTemplate.execute("alter table dataPointUsers add constraint dataPointUsersFk2 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint dataPointUsersFk1/dataPointUsersFk2 already exists, skipping.");
        }

        // Views

        final String viewsSql = ""
                + "create table IF NOT EXISTS mangoViews ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "name varchar(100) not null,"
                + "background varchar(255),"
                + "userId int not null,"
                + "anonymousAccess int not null,"
                + "data BYTEA not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(viewsSql);

        try {
        jdbcTemplate.execute("alter table mangoViews add constraint mangoViewsUn1 unique (xid);");
        jdbcTemplate.execute("alter table mangoViews add constraint mangoViewsFk1 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint mangoViewsUn1/mangoViewsFk1 already exists, skipping.");
        }

        final String mangoViewUsersSql = ""
                + "create table IF NOT EXISTS mangoViewUsers ("
                + "mangoViewId int not null,"
                + "userId int not null,"
                + "accessType int not null,"
                + "primary key (mangoViewId, userId)"
                + ")";

        jdbcTemplate.execute(mangoViewUsersSql);
        try {
        jdbcTemplate.execute("alter table mangoViewUsers add constraint mangoViewUsersFk1 foreign key (mangoViewId) references mangoViews(id) on delete cascade;");
        jdbcTemplate.execute("alter table mangoViewUsers add constraint mangoViewUsersFk2 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint mangoViewUsersFk1/mangoViewUsersFk2 already exists, skipping.");
        }

        // Point Values (historical dataf)

        final String pointValuesSQL = ""
                + "create table IF NOT EXISTS pointValues ("
                + "id SERIAL,"
                + "dataPointId int not null,"
                + "dataType int not null,"
                + "pointValue DOUBLE PRECISION,"
                + "ts bigint not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(pointValuesSQL);
        try{
            jdbcTemplate.execute("alter table pointValues add constraint pointValuesFk1 foreign key (dataPointId) references dataPoints(id) on delete cascade;");
            jdbcTemplate.execute("create index pointValuesIdx1 on pointValues (ts, dataPointId);");
            jdbcTemplate.execute("create index pointValuesIdx2 on pointValues (dataPointId, ts);");
        } catch (Exception ex) {
            LOG.warn("Constraint pointValuesFk1/pointValuesIdx1/pointValuesIdx2 already exists, skipping.");
        }

        final String pointValueAnnotations = ""
                + "create table IF NOT EXISTS pointValueAnnotations ("
                + "pointValueId bigint not null,"
                + "textPointValueShort varchar(128),"
                + "textPointValueLong TEXT,"
                + "sourceType smallint,"
                + "sourceId int)";

        jdbcTemplate.execute(pointValueAnnotations);
        try {
            jdbcTemplate.execute("alter table pointValueAnnotations add constraint pointValueAnnotationsFk1 foreign key (pointValueId) references pointValues(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint pointValueAnnotationsFk1 already exists, skipping.");
        }

        // Watch list

        final String watchListSQL = ""
                + "create table IF NOT EXISTS watchLists ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "userId int not null,"
                + "name varchar(50),"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(watchListSQL);
        try {
            jdbcTemplate.execute("alter table watchLists add constraint watchListsUn1 unique (xid);");
            jdbcTemplate.execute("alter table watchLists add constraint watchListsFk1 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint watchListsUn1/watchListsFk1 already exists, skipping.");
        }


        final String watchListPointsSQL = ""
                + "create table IF NOT EXISTS watchListPoints ("
                + "watchListId int not null,"
                + "dataPointId int not null,"
                + "sortOrder int not null"
                + ")";

        jdbcTemplate.execute(watchListPointsSQL);
        try {
            jdbcTemplate.execute("alter table watchListPoints add constraint watchListPointsFk1 foreign key (watchListId) references watchLists(id) on delete cascade;");
            jdbcTemplate.execute("alter table watchListPoints add constraint watchListPointsFk2 foreign key (dataPointId) references dataPoints(id);");
        }  catch (Exception ex) {
            LOG.warn("Constraint watchListPointsFk1/watchListPointsFk2 already exists, skipping.");
        }

        final String watchListUsersSQL = ""
                + "create table IF NOT EXISTS watchListUsers ("
                + "watchListId int not null,"
                + "userId int not null,"
                + "accessType int not null,"
                + "primary key (watchListId, userId)"
                + ")";

        jdbcTemplate.execute(watchListUsersSQL);
        try {
            jdbcTemplate.execute("alter table watchListUsers add constraint watchListUsersFk1 foreign key (watchListId) references watchLists(id) on delete cascade;");
            jdbcTemplate.execute("alter table watchListUsers add constraint watchListUsersFk2 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint watchListUsersFk1/watchListUsersFk2 already exists, skipping.");
        }

        //Point event detectors

        final String pointEventDetectorsSQL = ""
                + "create table IF NOT EXISTS pointEventDetectors ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "alias varchar(255),"
                + "dataPointId int not null,"
                + "detectorType int not null,"
                + "alarmLevel int not null,"
                + "stateLimit DOUBLE PRECISION,"
                + "duration int,"
                + "durationType int,"
                + "binaryState char(1),"
                + "multistateState int,"
                + "changeCount int,"
                + "alphanumericState varchar(128),"
                + "weight DOUBLE PRECISION,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(pointEventDetectorsSQL);
        try {
            jdbcTemplate.execute("alter table pointEventDetectors add constraint pointEventDetectorsUn1 unique (xid, dataPointId);");
            jdbcTemplate.execute("alter table pointEventDetectors add constraint pointEventDetectorsFk1 foreign key (dataPointId) references dataPoints(id);");
        } catch (Exception ex) {
            LOG.warn("Constraint pointEventDetectorsUn1/pointEventDetectorsFk1 already exists, skipping.");
        }

        // Events

        final String eventsSQL = ""
                + "create table IF NOT EXISTS events ("
                + "id SERIAL,"
                + "typeId int not null,"
                + "typeRef1 int not null,"
                + "typeRef2 int not null,"
                + "activeTs bigint not null,"
                + "rtnApplicable char(1) not null,"
                + "rtnTs bigint,rtnCause int,"
                + "alarmLevel int not null,"
                + "message TEXT,"
                + "ackTs bigint,"
                + "ackUserId int,"
                + "alternateAckSource int,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(eventsSQL);
        try {
            jdbcTemplate.execute("alter table events add constraint eventsFk1 foreign key (ackUserId) references users(id);");
        } catch (Exception ex) {
            LOG.warn("Constraint eventsFk1 already exists, skipping.");
        }

        final String userEventsSQL = ""
                + "create table IF NOT EXISTS userEvents ("
                + "eventId int not null,"
                + "userId int not null,"
                + "silenced char(1) not null,"
                + "primary key (eventId, userId)"
                + ")";

        jdbcTemplate.execute(userEventsSQL);
        try {
            jdbcTemplate.execute("alter table userEvents add constraint userEventsFk1 foreign key (eventId) references events(id) on delete cascade;");
            jdbcTemplate.execute("alter table userEvents add constraint userEventsFk2 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint userEventsFk1/userEventsFk2 already exists, skipping.");
        }

        // Event handlers

        final String eventHandlersSQL = ""
                + "create table IF NOT EXISTS eventHandlers ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "alias varchar(255),"
                //+ " -- Event type, see events  "
                + "eventTypeId int not null,"
                + "eventTypeRef1 int not null,"
                + "eventTypeRef2 int not null,"
                + "data BYTEA not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(eventHandlersSQL);
        try {
            jdbcTemplate.execute("alter table eventHandlers add constraint eventHandlersUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint eventHandlersUn1 already exists, skipping.");
        }


        final String scheduledEventsSQL = ""
                + "create table IF NOT EXISTS scheduledEvents ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "alias varchar(255),"
                + "alarmLevel int not null,"
                + "scheduleType int not null,"
                + "returnToNormal char(1) not null,"
                + "disabled char(1) not null,"
                + "activeYear int,"
                + "activeMonth int,"
                + "activeDay int,"
                + "activeHour int,"
                + "activeMinute int,"
                + "activeSecond int,"
                + "activeCron varchar(25),"
                + "inactiveYear int,"
                + "inactiveMonth int,"
                + "inactiveDay int,"
                + "inactiveHour int,"
                + "inactiveMinute int,"
                + "inactiveSecond int,"
                + "inactiveCron varchar(25),"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(scheduledEventsSQL);

        try {
            jdbcTemplate.execute("alter table scheduledEvents add constraint scheduledEventsUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint scheduledEventsUn1 already exists, skipping.");
        }

        // Point Hierarchy

        final String pointHierarchySQL = ""
                + "create table IF NOT EXISTS pointHierarchy ("
                + "id SERIAL,"
                + "parentId int,"
                + "name varchar(100),"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(pointHierarchySQL);

        // Compound events detectors

        final String compoundEventsDetectorsSQL = ""
                + "create table IF NOT EXISTS compoundEventDetectors ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "name varchar(100),"
                + "alarmLevel int not null,"
                + "returnToNormal char(1) not null,"
                + "disabled char(1) not null,"
                + "conditionText varchar(256) not null,"
                + "primary key (id))";

        jdbcTemplate.execute(compoundEventsDetectorsSQL);
        try {
            jdbcTemplate.execute("alter table compoundEventDetectors add constraint compoundEventDetectorsUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint compoundEventDetectorsUn1 already exists, skipping.");
        }

        // Reports

        final String reportsSQL = ""
                + "create table IF NOT EXISTS reports ("
                + "id SERIAL,"
                + "userId int not null,"
                + "name varchar(100) not null,"
                + "data BYTEA not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(reportsSQL);
        try {
            jdbcTemplate.execute("alter table reports add constraint reportsFk1 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportsFk1 already exists, skipping.");
        }


        final String reportInstancesSQL = ""
                + "create table IF NOT EXISTS reportInstances ("
                + "id SERIAL,"
                + "userId int not null,"
                + "name varchar(100) not null,"
                + "includeEvents int not null,"
                + "includeUserComments char(1) not null,"
                + "reportStartTime bigint not null,"
                + "reportEndTime bigint not null,"
                + "runStartTime bigint,"
                + "runEndTime bigint,"
                + "recordCount int,"
                + "preventPurge char(1),"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(reportInstancesSQL);
        try {
            jdbcTemplate.execute("alter table reportInstances add constraint reportInstancesFk1 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportInstancesFk1 already exists, skipping.");
        }


        final String reportInstancePointsSQL = ""
                + "create table IF NOT EXISTS reportInstancePoints ("
                + "id SERIAL,"
                + "reportInstanceId int not null,"
                + "dataSourceName varchar(40) not null,"
                + "pointName varchar(100) not null,"
                + "dataType int not null,"
                + "startValue varchar(4096),"
                + "textRenderer BYTEA,"
                + "colour varchar(6),"
                + "consolidatedChart char(1),"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(reportInstancePointsSQL);
        try {
            jdbcTemplate.execute("alter table reportInstancePoints add constraint reportInstancePointsFk1 foreign key (reportInstanceId) references reportInstances(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportInstancePointsFk1 already exists, skipping.");
        }

        final String reportInstanceDataSQL = ""
                + "create table IF NOT EXISTS reportInstanceData ("
                + "pointValueId bigint not null,"
                + "reportInstancePointId int not null,"
                + "pointValue DOUBLE PRECISION,"
                + "ts bigint not null,"
                + "primary key (pointValueId, reportInstancePointId)"
                + ")";

        jdbcTemplate.execute(reportInstanceDataSQL);
        try {
            jdbcTemplate.execute("alter table reportInstanceData add constraint reportInstanceDataFk1 foreign key (reportInstancePointId) references reportInstancePoints(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportInstanceDataFk1 already exists, skipping.");
        }
        final String reportInstanceDataAnnotationsSQL = ""
                + "create table IF NOT EXISTS reportInstanceDataAnnotations ("
                + "pointValueId bigint not null,"
                + "reportInstancePointId int not null,"
                + "textPointValueShort varchar(128),"
                + "textPointValueLong TEXT,"
                + "sourceValue varchar(128),"
                + "primary key (pointValueId,"
                + "reportInstancePointId)"
                + ")";

        jdbcTemplate.execute(reportInstanceDataAnnotationsSQL);
        try {
        jdbcTemplate.execute("alter table reportInstanceDataAnnotations add constraint reportInstanceDataAnnotationsFk1 foreign key (pointValueId, reportInstancePointId) references reportInstanceData(pointValueId, reportInstancePointId) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportInstanceDataAnnotationsFk1 already exists, skipping.");
        }


        final String reportInstanceEventsSQL = ""
                + "create table IF NOT EXISTS reportInstanceEvents ("
                + "eventId int not null,"
                + "reportInstanceId int not null,"
                + "typeId int not null,"
                + "typeRef1 int not null,"
                + "typeRef2 int not null,"
                + "activeTs bigint not null,"
                + "rtnApplicable char(1) not null,"
                + "rtnTs bigint,"
                + "rtnCause int,"
                + "alarmLevel int not null,"
                + "message TEXT,"
                + "ackTs bigint,"
                + "ackUsername varchar(40),"
                + "alternateAckSource int,"
                + "primary key (eventId, reportInstanceId)"
                + ")";

        jdbcTemplate.execute(reportInstanceEventsSQL);
        try {
            jdbcTemplate.execute("alter table reportInstanceEvents add constraint reportInstanceEventsFk1 foreign key (reportInstanceId) references reportInstances(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportInstanceEventsFk1 already exists, skipping.");
        }

        final String reportInstaceUserCommentsSQL = ""
                + "create table IF NOT EXISTS reportInstanceUserComments ("
                + "reportInstanceId int not null,"
                + "username varchar(40),"
                + "commentType int not null,"
                + "typeKey int not null,"
                + "ts bigint not null,"
                + "commentText varchar(1024) not null"
                + ")";

        jdbcTemplate.execute(reportInstaceUserCommentsSQL);
        try {
            jdbcTemplate.execute("alter table reportInstanceUserComments add constraint reportInstanceUserCommentsFk1 foreign key (reportInstanceId) references reportInstances(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint reportInstanceUserCommentsFk1 already exists, skipping.");
        }
        //Publishers

        final String publishersSQL = ""
                + "create table IF NOT EXISTS publishers ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "data BYTEA not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(publishersSQL);
        try {
            jdbcTemplate.execute("alter table publishers add constraint publishersUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint publishersUn1 already exists, skipping.");
        }


        // Point Links

        final String pointLinksSQL = ""
                + "create table IF NOT EXISTS pointLinks ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "sourcePointId int not null,"
                + "targetPointId int not null,"
                + "script TEXT,"
                + "eventType int not null,"
                + "disabled char(1) not null,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(pointLinksSQL);
        try {
            jdbcTemplate.execute("alter table pointLinks add constraint pointLinksUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint pointLinksUn1 already exists, skipping.");
        }

        // Maintenance events

        final String maintenanceEventsSQL = ""
                + "create table IF NOT EXISTS maintenanceEvents ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "dataSourceId int not null,"
                + "alias varchar(255),"
                + "alarmLevel int not null,"
                + "scheduleType int not null,"
                + "disabled char(1) not null,"
                + "activeYear int,"
                + "activeMonth int,"
                + "activeDay int,"
                + "activeHour int,"
                + "activeMinute int,"
                + "activeSecond int,"
                + "activeCron varchar(25),"
                + "inactiveYear int,"
                + "inactiveMonth int,"
                + "inactiveDay int,"
                + "inactiveHour int,"
                + "inactiveMinute int,"
                + "inactiveSecond int,"
                + "inactiveCron varchar(25),"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(maintenanceEventsSQL);
        try {
            jdbcTemplate.execute("alter table maintenanceEvents add constraint maintenanceEventsUn1 unique (xid);");
            jdbcTemplate.execute("alter table maintenanceEvents add constraint maintenanceEventsFk1 foreign key (dataSourceId) references dataSources(id);");
        } catch (Exception ex) {
            LOG.warn("Constraint maintenanceEventsUn1/maintenanceEventsFk1 already exists, skipping.");
        }

        // Event Detector Templates

        final String eventDetectorTemplatesSQL = ""
                + "CREATE TABLE IF NOT EXISTS eventDetectorTemplates ("
                + "id SERIAL,"
                + "name varchar(255) NOT NULL,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(eventDetectorTemplatesSQL);

        final String templatesDetectorsSQL = ""
                + "CREATE TABLE IF NOT EXISTS templatesDetectors ("
                + "id SERIAL,"
                + "xid varchar(50) NOT NULL,"
                + "alias varchar(255),"
                + "detectorType int NOT NULL,"
                + "alarmLevel int NOT NULL,"
                + "stateLimit REAL,"
                + "duration int,"
                + "durationType int,"
                + "binaryState char(1),"
                + "multistateState int,"
                + "changeCount int,"
                + "alphanumericState varchar(128),"
                + "weight REAL,"
                + "threshold DOUBLE PRECISION,"
                + "eventDetectorTemplateId int NOT NULL,"
                + "primary key (id)"
                + ");";

        jdbcTemplate.execute(templatesDetectorsSQL);
        try {
        jdbcTemplate.execute("ALTER TABLE templatesDetectors ADD CONSTRAINT templatesDetectorsFk1 FOREIGN KEY (eventDetectorTemplateId) REFERENCES eventDetectorTemplates (id);");
        } catch (Exception ex) {
            LOG.warn("Constraint templatesDetectorsFk1 already exists, skipping.");
        }


        final String usersProfilesSQL = ""
                + "CREATE TABLE IF NOT EXISTS usersProfiles ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "name varchar(255) NOT NULL,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(usersProfilesSQL);
        try {
            jdbcTemplate.execute("alter table usersProfiles add constraint usersProfilesUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint usersProfilesUn1 already exists, skipping.");
        }
        // Data source permissions

        final String dataSourceUsersProfilesSQL = ""
                + "create table IF NOT EXISTS dataSourceUsersProfiles ("
                + "dataSourceId int not null,"
                + "userProfileId int not null"
                + ")";

        jdbcTemplate.execute(dataSourceUsersProfilesSQL);
        try {
            jdbcTemplate.execute("alter table dataSourceUsersProfiles add constraint dataSourceUsersProfilesFk1 foreign key (dataSourceId) references dataSources(id) on delete cascade;");
            jdbcTemplate.execute("alter table dataSourceUsersProfiles add constraint dataSourceUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint dataSourceUsersProfilesFk1/dataSourceUsersProfilesFk2 already exists, skipping.");
        }
        // Data point permissions

        final String dataPointUsersProfilesSQL = ""
                + "create table IF NOT EXISTS dataPointUsersProfiles ("
                + "dataPointId int not null,"
                + "userProfileId int not null,"
                + "permission int not null"
                + ")";

        jdbcTemplate.execute(dataPointUsersProfilesSQL);
        try {
            jdbcTemplate.execute("alter table dataPointUsersProfiles add constraint dataPointUsersProfilesFk1 foreign key (dataPointId) references dataPoints(id) on delete cascade;");
            jdbcTemplate.execute("alter table dataPointUsersProfiles add constraint dataPointUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint dataPointUsersProfilesFk1/dataPointUsersProfilesFk2 already exists, skipping.");
        }
        //Data source permissions

        final String usersUsersProfilesSQL = ""
                + "create table IF NOT EXISTS usersUsersProfiles ("
                + "userProfileId int not null,"
                + "userId int not null"
                + ")";

        jdbcTemplate.execute(usersUsersProfilesSQL);
        try {
            jdbcTemplate.execute("alter table usersUsersProfiles add constraint usersUsersProfilesFk1 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
            jdbcTemplate.execute("alter table usersUsersProfiles add constraint usersUsersProfilesFk2 foreign key (userId) references users(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint usersUsersProfilesFk1/usersUsersProfilesFk2 already exists, skipping.");
        }

        // Watchlist permissions

        final String watchListUsersProfilesSQL = ""
                + "create table IF NOT EXISTS watchListUsersProfiles ("
                + "watchlistId int not null,"
                + "userProfileId int not null,"
                + "permission int not null"
                + ")";

        jdbcTemplate.execute(watchListUsersProfilesSQL);

        try {
            jdbcTemplate.execute("alter table watchListUsersProfiles add constraint watchlistUsersProfilesFk1 foreign key (watchlistId) references watchLists(id) on delete cascade;");
            jdbcTemplate.execute("alter table watchListUsersProfiles add constraint watchlistUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint watchlistUsersProfilesFk1/watchlistUsersProfilesFk2 already exists, skipping.");
        }

        // View Users Profiles

        final String viewUsersProfilesSQL = ""
                + "create table IF NOT EXISTS viewUsersProfiles ("
                + "viewId int not null,"
                + "userProfileId int not null,"
                + "permission int not null"
                + ")";

        jdbcTemplate.execute(viewUsersProfilesSQL);
        try {
            jdbcTemplate.execute("alter table viewUsersProfiles add constraint viewUsersProfilesFk1 foreign key (viewId) references mangoViews(id) on delete cascade;");
            jdbcTemplate.execute("alter table viewUsersProfiles add constraint viewUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
        } catch (Exception ex) {
            LOG.warn("Constraint viewUsersProfilesFk1/viewUsersProfilesFk2 already exists, skipping.");
        }

        if (!DAO.getInstance().isTest()) {
            // Create User
            String userInsert = "insert into users (username, password, email, homeUrl, phone, " +
                    "admin, disabled, receiveAlarmEmails, receiveOwnAuditEvents) " +
                    "values (?,?,?,?,?,?,?,?,?);";

            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(userInsert);
                new ArgumentPreparedStatementSetter(new Object[]{
                        "admin",
                        Common.encrypt("admin"),
                        "admin@yourMangoDomain.com",
                        "",
                        "",
                        "Y",
                        "N",
                        0,
                        "N"
                }).setValues(preparedStatement);
                return preparedStatement;
            });

            // Record the current version.
            jdbcTemplate.update(
                    "INSERT INTO systemSettings (settingname, settingvalue) " +
                            "VALUES (?, ?) " +
                            "ON CONFLICT (settingname) DO UPDATE SET settingvalue = EXCLUDED.settingvalue",
                    "databaseSchemaVersion", Common.getVersion()
            );        }

        //V1_1__ViewsHierarchy

        migrationScadaBr(jdbcTemplate);

        final String folderViewsHierarchySQL = ""
                + "create table IF NOT EXISTS category_views_hierarchy ("
                + "id  SERIAL NOT NULL,"
                + "parentId INTEGER,"
                + "name varchar(100) not null unique,"
                + "primary key (id, parentId)"
                + ")";

        final String viewsHierarchySQL = ""
                + "create table IF NOT EXISTS views_category_views_hierarchy ("
                + "view_id INTEGER,"
                + "folder_views_hierarchy_id INTEGER not null,"
                + "primary key (view_id)"
                + ")";

        String fAdd =
                "CREATE OR REPLACE FUNCTION func_views_hierarchy_add("
                        + "a_parentId INTEGER, a_name VARCHAR(100)) "
                        + "RETURNS INTEGER AS $$ "
                        + "BEGIN "
                        + "IF (CHAR_LENGTH(a_name) > 2 AND CHAR_LENGTH(a_name) < 100) THEN "
                        + "INSERT INTO \"category_views_hierarchy\"(\"parentId\", \"name\") VALUES (a_parentId, a_name); "
                        + "RETURN currval('category_views_hierarchy_id_seq'); "
                        + "ELSE "
                        + "RAISE EXCEPTION '#error.view_hierarchy.add.error1#'; "
                        + "END IF; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";


        String fUpdate =
                "CREATE OR REPLACE FUNCTION func_views_hierarchy_update("
                        + "a_id INTEGER, a_parentId INTEGER, a_name VARCHAR(100)) "
                        + "RETURNS INTEGER AS $$ "
                        + "BEGIN "
                        + "UPDATE \"category_views_hierarchy\" "
                        + "SET \"parentId\" = a_parentId, \"name\" = a_name "
                        + "WHERE \"id\" = a_id; "
                        + "RETURN a_id; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        String fDeleteView =
                "CREATE OR REPLACE FUNCTION func_views_hierarchy_folder_delete(a_id INTEGER) "
                        + "RETURNS INTEGER AS $$ "
                        + "BEGIN "
                        + "DELETE FROM \"category_views_hierarchy\" WHERE \"id\" = a_id; "
                        + "UPDATE \"category_views_hierarchy\" SET \"parentId\" = -1 WHERE \"parentId\" = a_id; "
                        + "RETURN a_id; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        String fDeleteFolder =
                "CREATE OR REPLACE FUNCTION func_views_hierarchy_view_delete(a_id INTEGER) "
                        + "RETURNS INTEGER AS $$ "
                        + "BEGIN "
                        + "DELETE FROM \"views_category_views_hierarchy\" WHERE \"view_id\" = a_id; "
                        + "RETURN a_id; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";


        String fMoveFolder =
                "CREATE OR REPLACE FUNCTION func_views_hierarchy_move_folder(a_id INTEGER, a_new_parent_id INTEGER) "
                        + "RETURNS INTEGER AS $$ "
                        + "BEGIN "
                        + "UPDATE \"category_views_hierarchy\" SET \"parentId\" = a_new_parent_id WHERE \"id\" = a_id; "
                        + "RETURN a_id; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        String fMoveView =
                "CREATE OR REPLACE FUNCTION func_views_hierarchy_move_view(a_id INTEGER, a_new_parent_id INTEGER) "
                        + "RETURNS INTEGER AS $$ "
                        + "DECLARE "
                        + "varExistId INTEGER := 0; "
                        + "BEGIN "
                        + "SELECT \"view_id\" INTO varExistId FROM \"views_category_views_hierarchy\" WHERE \"view_id\" = a_id; "
                        + "IF NOT FOUND THEN "
                        + "INSERT INTO \"views_category_views_hierarchy\" (\"view_id\", \"folder_views_hierarchy_id\") "
                        + "VALUES (a_id, a_new_parent_id); "
                        + "ELSE "
                        + "UPDATE \"views_category_views_hierarchy\" SET \"folder_views_hierarchy_id\" = a_new_parent_id "
                        + "WHERE \"view_id\" = a_id; "
                        + "END IF; "
                        + "RETURN a_id; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        String pSelect =
                "CREATE OR REPLACE FUNCTION prc_views_hierarchy_select() "
                        + "RETURNS TABLE (id INTEGER, parentId INTEGER, name VARCHAR) AS $$ "
                        + "BEGIN "
                        + "RETURN QUERY SELECT * FROM \"category_views_hierarchy\" ORDER BY \"parentId\" ASC; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        String pSelectNode =
                "CREATE OR REPLACE FUNCTION prc_views_hierarchy_select_node(a_parent_id INTEGER) "
                        + "RETURNS TABLE (id INTEGER, parentId INTEGER, name VARCHAR) AS $$ "
                        + "BEGIN "
                        + "RETURN QUERY SELECT * FROM \"category_views_hierarchy\" WHERE \"parentId\" = a_parent_id; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        String pSelectViewInFolders =
                "CREATE OR REPLACE FUNCTION prc_views_category_views_hierarchy_select() "
                        + "RETURNS TABLE (view_id INTEGER, folder_views_hierarchy_id INTEGER) AS $$ "
                        + "BEGIN "
                        + "RETURN QUERY SELECT * FROM \"views_category_views_hierarchy\" ORDER BY \"view_id\" ASC; "
                        + "END; "
                        + "$$ LANGUAGE plpgsql;";

        jdbcTemplate.execute(folderViewsHierarchySQL);
        jdbcTemplate.execute(viewsHierarchySQL);

        jdbcTemplate.execute(fAdd);
        jdbcTemplate.execute(fUpdate);
        jdbcTemplate.execute(fDeleteFolder);
        jdbcTemplate.execute(fDeleteView);
        jdbcTemplate.execute(fMoveFolder);
        jdbcTemplate.execute(fMoveView);
        jdbcTemplate.execute(pSelect);
        jdbcTemplate.execute(pSelectNode);
        jdbcTemplate.execute(pSelectViewInFolders);


        //V1_2__SetViewSizeProperties

        final String setViewSizeProperties = ""
                + "alter table mangoViews " +
                "ADD COLUMN width INTEGER,"+
                "ADD COLUMN height INTEGER;";

        try {
            jdbcTemplate.execute(setViewSizeProperties);
        } catch (Exception e) {
            LOG.warn("Column width/height already exists, skipping.");
        }


        //V1_3__SetXidPointHierarchy

        final String addXidInPointHierarchy = ""
                + "DO $$ BEGIN "
                + "IF NOT EXISTS (SELECT 1 FROM information_schema.columns "
                + "WHERE table_name='pointhierarchy' AND column_name='xid') THEN "
                + "ALTER TABLE \"pointHierarchy\" ADD COLUMN \"xid\" VARCHAR(100); "
                + "END IF; "
                + "END $$;";

        jdbcTemplate.execute(addXidInPointHierarchy);

        final String dropPrcIfExist =
                "DROP FUNCTION IF EXISTS func_gen_xid_point_hierarchy(INT);";

        jdbcTemplate.execute(dropPrcIfExist);

        final String addPrcGenerateXid =
                    "CREATE FUNCTION func_gen_xid_point_hierarchy(id INTEGER) "
                    + "RETURNS TEXT LANGUAGE sql AS $$ "
                    + "  SELECT 'DIR_' || FLOOR(EXTRACT(EPOCH FROM NOW())) || '_' || id; "
                    + "$$;";

        jdbcTemplate.execute(addPrcGenerateXid);

        final String runPrcToGenerateXid =
                "UPDATE pointHierarchy SET xid=func_gen_xid_point_hierarchy(id)";

        jdbcTemplate.execute(runPrcToGenerateXid);

        final String addConstraintUniqueXidPointHierarchy =
                "ALTER TABLE pointHierarchy ADD CONSTRAINT unique_xid_point_hierarchy UNIQUE (xid);";

        try {
            jdbcTemplate.execute(addConstraintUniqueXidPointHierarchy);
        } catch (Exception e) {
            LOG.warn("Constraint unique_xid_point_hierarchy already exists, skipping.");
        }

        final String addIndex = ""
                + "DO $$ BEGIN "
                + "IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_xid_point_hierarchy') THEN "
                + "CREATE INDEX idx_xid_point_hierarchy ON \"pointHierarchy\" (\"xid\"); "
                + "END IF; "
                + "END $$;";

        jdbcTemplate.execute(addIndex);


        //V2_0__CMP_history

        final String multiChangesHistory = ""
                + "create table IF NOT EXISTS multi_changes_history ("
                + "id SERIAL,"                                           // id
                + "userId INTEGER,"                                      // the user id of the user making the record in the database
                + "username varchar(50),"                                // the name of the user who writes the values
                + "viewAndComponentIdentification varchar(50) not null," // component and view identifier
                + "interpretedState varchar(50) not null,"               // the state we were trying to get into
                + "ts bigint,"                                       // recording time
                + "primary key (id)"
                + ")";

        final String valuesMultiChangesHistory = ""
                + "create table IF NOT EXISTS values_multi_changes_history ("
                + "id SERIAL,"                                          // id
                + "multiChangesHistoryId INTEGER,"                      // id to the multi_changes_history table ( without a foreign key)
                + "valueId bigint,"                                 // id of the value stored in pointValues (without a foreign key)
                + "value varchar(50) not null,"                         // a copy of the value recorded in pointValues
                + "dataPointId INTEGER,"                                // id to the dataPoins table (without a foreign key)
                + "ts bigint,"                                         // recording time
                + "primary key (id)"
                + ")";

        final String prcAddCMPHistory = ""
                + "CREATE OR REPLACE PROCEDURE \"prc_add_cmp_history\"(\n"
                + "    IN a_userId INTEGER,\n"
                + "    IN a_viewAndCmpId VARCHAR(50),\n"
                + "    IN a_interpretedState VARCHAR(50),\n"
                + "    IN a_ts BIGINT,\n"
                + "    IN a_list_of_values JSON\n"
                + ")\n"
                + "LANGUAGE plpgsql\n"
                + "AS $$\n"
                + "DECLARE\n"
                + "    v_usr_name VARCHAR(50);\n"
                + "    v_multiChangesHistoryId INTEGER;\n"
                + "    v_length INTEGER := json_array_length(a_list_of_values);\n"
                + "    v_index INTEGER := 0;\n"
                + "    v_data_point_id INTEGER;\n"
                + "    v_data_point_value VARCHAR(50);\n"
                + "BEGIN\n"
                + "    SELECT username INTO v_usr_name FROM users WHERE id = a_userId;\n"
                + "\n"
                + "    INSERT INTO multi_changes_history (userId, username, viewAndComponentIdentification, interpretedState, ts)\n"
                + "    VALUES (a_userId, v_usr_name, a_viewAndCmpId, a_interpretedState, a_ts);\n"
                + "\n"
                + "    SELECT currval(pg_get_serial_sequence('multi_changes_history', 'id')) INTO v_multiChangesHistoryId;\n"
                + "\n"
                + "    WHILE v_index < v_length LOOP\n"
                + "        SELECT json_extract_path_text(elem, 'value') INTO v_data_point_value\n"
                + "        FROM json_array_elements(a_list_of_values) WITH ORDINALITY AS arr(elem, ord)\n"
                + "        WHERE ord = v_index + 1;\n"
                + "\n"
                + "        SELECT id INTO v_data_point_id\n"
                + "        FROM dataPoints\n"
                + "        WHERE xid = (\n"
                + "            SELECT json_extract_path_text(elem, 'xid')\n"
                + "            FROM json_array_elements(a_list_of_values) WITH ORDINALITY AS arr(elem, ord)\n"
                + "            WHERE ord = v_index + 1\n"
                + "        );\n"
                + "\n"
                + "        INSERT INTO values_multi_changes_history (multiChangesHistoryId, value, dataPointId, ts)\n"
                + "        VALUES (v_multiChangesHistoryId, v_data_point_value, v_data_point_id, a_ts);\n"
                + "\n"
                + "        v_index := v_index + 1;\n"
                + "    END LOOP;\n"
                + "\n"
                + "    CREATE TEMP TABLE tmp_to_delete AS\n"
                + "        SELECT id FROM multi_changes_history\n"
                + "        WHERE viewAndComponentIdentification = a_viewAndCmpId\n"
                + "        ORDER BY ts DESC\n"
                + "        LIMIT 10;\n"
                + "\n"
                + "    DELETE FROM multi_changes_history\n"
                + "    WHERE id NOT IN (SELECT id FROM tmp_to_delete);\n"
                + "\n"
                + "    DROP TABLE tmp_to_delete;\n"
                + "END;\n"
                + "$$;";

        jdbcTemplate.execute(multiChangesHistory);
        jdbcTemplate.execute(valuesMultiChangesHistory);
        jdbcTemplate.execute(prcAddCMPHistory);

        //V2_0_CMP_history

        jdbcTemplate.execute("DROP VIEW IF EXISTS apiAlarmsAcknowledge;");
        //(1)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.3';");

        jdbcTemplate.execute("DROP VIEW IF EXISTS apiAlarmsHistory;");
        //(2)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.2';");

        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS prc_sort_alarms_and_storungs_depend_on_state;");
        jdbcTemplate.execute("DROP TABLE IF EXISTS \"tmp_sortedAlarmsStorungs\";");
        //(3)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.1.1';");

        jdbcTemplate.execute("DROP VIEW IF EXISTS apiAlarmsLive;");
        //(4)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1.1';");

        jdbcTemplate.execute("DROP VIEW IF EXISTS viewAllStorungs;");
        jdbcTemplate.execute("DROP VIEW IF EXISTS viewAllAlarms;");
        //(5)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.1';");

        try {
            jdbcTemplate.execute("ALTER TABLE dataPoints DROP COLUMN pointName;");
        } catch (Exception e) {
            LOG.warn(String.valueOf(e));
        }
        //(6)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0.2';");

        jdbcTemplate.execute("DROP TABLE IF EXISTS plcAlarms CASCADE;");
        //(7)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0.1';");

        //(8)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0.0.1';");

        try {
            jdbcTemplate.execute("ALTER TABLE dataPoints DROP COLUMN plcAlarmLevel;");
        } catch (Exception e) {
            LOG.warn(String.valueOf(e));
        }
        //(9)
        jdbcTemplate.execute( "DELETE FROM schema_version WHERE version = '2.2.0';");


        addColumnsToDataPointsTable(jdbcTemplate);
        updateDataPointsTable(jdbcTemplate);
        createPlcAlarmsTable(jdbcTemplate);
        createFunctions(jdbcTemplate);
        createViews(jdbcTemplate);
        createProcedure(jdbcTemplate);
        createTrigger(jdbcTemplate);


        //V2_4__

        jdbcTemplate.execute(
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (SELECT 1 FROM information_schema.columns " +
                        "WHERE table_name='mangoviews' AND column_name='modification_time') THEN " +
                        "ALTER TABLE mangoViews ADD COLUMN modification_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP; " +
                        "END IF; " +
                        "END $$;"
        );

        jdbcTemplate.execute(
                "CREATE OR REPLACE FUNCTION update_modification_time() " +
                        "RETURNS TRIGGER AS $$ " +
                        "BEGIN " +
                        "  NEW.modification_time = CURRENT_TIMESTAMP; " +
                        "  RETURN NEW; " +
                        "END; " +
                        "$$ LANGUAGE plpgsql;"
        );

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM pg_trigger " +
                        "  WHERE tgname = 'trg_mangoviews_modtime' " +
                        ") THEN " +
                        "  CREATE TRIGGER trg_mangoViews_modtime " +
                        "  BEFORE UPDATE ON mangoViews " +
                        "  FOR EACH ROW " +
                        "  EXECUTE FUNCTION update_modification_time(); " +
                        "END IF; " +
                        "END $$;"
        );


        //V2_5__ScheduledExecuteInactiveEvent

        // 1) cronPattern
        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='mailinglists' AND column_name='cronpattern'" +
                        ") THEN " +
                        "  ALTER TABLE mailingLists ADD COLUMN cronPattern VARCHAR(100); " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.cronPattern IS 'cron pattern';"
        );

// 2) collectInactiveEmails
        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'mailinglists' AND column_name = 'collectinactiveemails' " +
                        ") THEN " +
                        "  ALTER TABLE mailingLists ADD COLUMN collectInactiveEmails BOOLEAN NOT NULL DEFAULT FALSE; " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.collectInactiveEmails IS 'Collect inactive emails and send when activated';"
        );

// 3) dailyLimitSentEmails
        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'mailinglists' AND column_name = 'dailylimitsentemails' " +
                        ") THEN " +
                        "  ALTER TABLE mailingLists ADD COLUMN dailyLimitSentEmails BOOLEAN NOT NULL DEFAULT FALSE; " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.dailyLimitSentEmails IS 'Daily limit sent emails';"
        );

// 4) dailyLimitSentEmailsNumber
        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'mailinglists' AND column_name = 'dailylimitsentemailsnumber' " +
                        ") THEN " +
                        "  ALTER TABLE mailingLists ADD COLUMN dailyLimitSentEmailsNumber INTEGER DEFAULT 0; " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN mailingLists.dailyLimitSentEmailsNumber IS 'Daily limit sent emails number';"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS scheduledExecuteInactiveEvent ("
                        + "  mailingListId INTEGER NOT NULL,"
                        + "  sourceEventId  INTEGER NOT NULL,"
                        + "  eventHandlerId INTEGER NOT NULL,"
                        + "  UNIQUE (mailingListId, sourceEventId, eventHandlerId),"
                        + "  FOREIGN KEY (sourceEventId)  REFERENCES events(id)        ON DELETE CASCADE,"
                        + "  FOREIGN KEY (mailingListId)  REFERENCES mailingLists(id) ON DELETE CASCADE,"
                        + "  FOREIGN KEY (eventHandlerId) REFERENCES eventHandlers(id) ON DELETE CASCADE"
                        + ");"
        );


        //V2_6_5_0__ZIndexForViewComponent

        migrateViews(jdbcTemplate);


        //V2_6__

        updateDataPointsTable2(jdbcTemplate);

        String correctLiveAlarms =
                "CREATE OR REPLACE VIEW \"liveAlarms\" AS " +
                        "SELECT " +
                        "  id, " +
                        "  func_fromats_date(activeTime) AS \"activation-time\", " +
                        "  func_fromats_date(inactiveTime) AS \"inactivation-time\", " +
                        "  dataPointType AS \"level\", " +
                        "  dataPointName AS \"name\", " +
                        "  dataPointId " +
                        "FROM plcAlarms " +
                        "WHERE acknowledgeTime = 0 " +
                        "  AND (inactiveTime = 0 OR (inactiveTime > (EXTRACT(EPOCH FROM NOW() - INTERVAL '24 HOURS') * 1000))) " +
                        "ORDER BY (inactiveTime = 0) DESC, activeTime DESC, inactiveTime DESC, id DESC;";

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='events' AND column_name='shortmessage'" +
                        ") THEN " +
                        "  ALTER TABLE events ADD COLUMN shortMessage TEXT; " +
                        "END IF; " +
                        "END $$;"
        );
        jdbcTemplate.update("UPDATE events SET message = message || '||' WHERE typeId = 1;");
        jdbcTemplate.execute(correctLiveAlarms);


        //V2_7_0_1__UserParameters

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='hidemenu') THEN " +
                        "  ALTER TABLE users ADD COLUMN hideMenu BOOLEAN DEFAULT false; " +
                        "END IF; " +
                        "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='theme') THEN " +
                        "  ALTER TABLE users ADD COLUMN theme VARCHAR(255) DEFAULT 'DEFAULT'; " +
                        "END IF; " +
                        "END $$;"
        );


        //V2_7_0_2__FixViewPermissions

        migratePermissions();


        //V2_7_0_3__AnonymousUser

        String userInsert = "INSERT INTO users " +
                "(username, password, email, phone, admin, disabled, homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(userInsert);
            new ArgumentPreparedStatementSetter(new Object[]{
                    "anonymous-user",
                    Common.encrypt("anonymous"),
                    "anonymous@mail.com",
                    "",
                    "N",
                    "Y",
                    "",
                    0,
                    "N"
            }).setValues(ps);
            return ps;
        });


        //V2_7_0_4_1__CorrectProcedurePrcAlarmsNotify

        jdbcTemplate.execute(
                "DROP TRIGGER IF EXISTS tri_notify_faults_or_alarms ON pointValues;"
        );

        jdbcTemplate.execute(
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM pg_trigger " +
                        "  WHERE tgname = 'tri_notify_faults_or_alarms'" +
                        ") THEN " +
                        "  CREATE TRIGGER tri_notify_faults_or_alarms " +
                        "  AFTER INSERT ON pointValues " +
                        "  FOR EACH ROW " +
                        "  EXECUTE FUNCTION \"prc_alarms_notify\"(); " +
                        "END IF; " +
                        "END $$;"
        );

        dropPrcAlarmsNotify(jdbcTemplate);
        createPrcAlarmsNotify(jdbcTemplate);


        //V2_7_0_4_2__MultiChangeHistory

        try {
            String indexName = "index_multiChangesHistoryId";
            String check = "SELECT indexname FROM pg_indexes WHERE tablename = 'values_multi_changes_history'";
            List<String> indexes = jdbcTemplate.query(check, (rs, i) -> rs.getString("indexname"));

            boolean exists = indexes.stream().anyMatch(i -> i.equalsIgnoreCase(indexName));

            if (!exists) {
                final String indexHistory = "CREATE INDEX " + indexName + " ON values_multi_changes_history (multiChangesHistoryId);";
                jdbcTemplate.execute(indexHistory);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }


        //V2_7_0_4__ExtendedDelayForMetaDatapoints

        migrateExtendedDelayForMetaDatapoints(jdbcTemplate);


        //V2_7_0_5__SynopticPanel

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS synopticPanels (" +
                        "id SERIAL PRIMARY KEY, " +
                        "xid VARCHAR(50), " +
                        "name VARCHAR(50), " +
                        "vectorImage TEXT, " +
                        "componentData TEXT" +
                        ")"
        );


        //V2_7_1_0__UserNames

        String checkAndAddFirstName =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='users' AND column_name='firstName'" +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE users ADD COLUMN \"firstName\" VARCHAR(255) DEFAULT NULL;'; " +
                        "END IF; " +
                        "END $$;";

        String checkAndAddLastName =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='users' AND column_name='lastName'" +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE users ADD COLUMN \"lastName\" VARCHAR(255) DEFAULT NULL;'; " +
                        "END IF; " +
                        "END $$;";

        jdbcTemplate.execute(checkAndAddFirstName);
        jdbcTemplate.execute(checkAndAddLastName);


        //V2_7_1_1__HttpRetriever

        migrateHttpRetriever(jdbcTemplate);


        //V2_7_1_2__PurgeLimitStrategyDatapointProperty

        updatePurgeLimitStrategyDatapointProperty(jdbcTemplate);


        //V2_7_1_3__ExportImportReport

        try {
            List<ReportVO> reports = getReports(jdbcTemplate);
            try {
                createXidColumn(jdbcTemplate);
                setMissingFields(jdbcTemplate, reports);
                updateReports(jdbcTemplate, reports);
            } finally {
                reports.clear();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }


        //V2_7_2_1__MqttPointLocatorUpdateDataPointXid
        migrateMqttPointLocatorUpdateDataPointXid(jdbcTemplate);


        //V2_7_3_0__SqlDataSourceUpdate
        migrateSqlDataSourceUpdate(jdbcTemplate);


        //V2_7_4_1__AddSoapServicesUser
        addSoapServicesUser(jdbcTemplate);


        //V2_7_5_3_1__AddHttpdsUser
        addHttpdsBasicUser(jdbcTemplate);


        //V2_7_5_3_2__AddFullScreenToUser
        String addEnableFullScreen =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'users' AND column_name = 'enablefullscreen' " +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE users ADD COLUMN \"enableFullScreen\" BOOLEAN DEFAULT false'; " +
                        "END IF; " +
                        "END $$;";

        String addHideShortcutDisable =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'users' AND column_name = 'hideshortcutdisablefullscreen' " +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE users ADD COLUMN \"hideShortcutDisableFullScreen\" BOOLEAN DEFAULT false'; " +
                        "END IF; " +
                        "END $$;";

        jdbcTemplate.execute(addEnableFullScreen);
        jdbcTemplate.execute(addHideShortcutDisable);


        //V2_7_5_3__AddLangToUser
        migrateLangDefault(jdbcTemplate);


        //V2_7_5_4_1__UpdateAbsoluteToRelativeUrlInLinkComponent
        updateAbsoluteToRelativeUrlInLinkComponent(jdbcTemplate);


        //V2_7_6_1__ChangeLengthLimitForSettingName
        String alterSettingNameType =
                "DO $$ BEGIN " +
                        "IF EXISTS ( " +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name = 'systemsettings' AND column_name = 'settingname' " +
                        "    AND data_type != 'character varying' " +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE systemSettings ALTER COLUMN \"settingName\" TYPE VARCHAR(255)'; " +
                        "END IF; " +
                        "END $$;";

        jdbcTemplate.execute(alterSettingNameType);


        //V2_7_7_1__SqlDataSourceLimit
        migrateSqlDataSourceLimit(jdbcTemplate);


        //V2_8_0_1__AddTypeRef3ColumnToEvents
        boolean existsTypeRef3Column = jdbcTemplate.queryForObject(
                "SELECT EXISTS (" +
                        "SELECT 1 FROM information_schema.columns " +
                        "WHERE table_name = 'events' AND column_name = 'typeref3'" +
                        ")",
                Boolean.class
        );

        if (!existsTypeRef3Column) {
            jdbcTemplate.execute("ALTER TABLE events ADD COLUMN typeRef3 INTEGER NOT NULL DEFAULT 0");
        }


        //V2_8_0_2__FixedAggregationEnabled
        migrateFixedAggregationEnabled(jdbcTemplate);

        //V2_8_0__AddAssigneeColumnsToEvents
        addAssigneeColumn(jdbcTemplate);
        updateViewComponents(jdbcTemplate);
    }

    private static void migrationScadaBr(JdbcTemplate jdbcTemplate) {
        final String eventDetectorTemplatesSQL = ""
                + "CREATE TABLE IF NOT EXISTS eventDetectorTemplates ("
                + "id SERIAL,"
                + "name varchar(255) NOT NULL,"
                + "primary key (id)"
                + ")";

        jdbcTemplate.execute(eventDetectorTemplatesSQL);

        final String templatesDetectorsSQL = ""
                + "CREATE TABLE IF NOT EXISTS templatesDetectors ("
                + "id SERIAL,"
                + "xid varchar(50) NOT NULL,"
                + "alias varchar(255),"
                + "detectorType int NOT NULL,"
                + "alarmLevel int NOT NULL,"
                + "stateLimit FLOAT,"
                + "duration int,"
                + "durationType int,"
                + "binaryState char(1),"
                + "multistateState int,"
                + "changeCount int,"
                + "alphanumericState varchar(128),"
                + "weight float,"
                + "threshold double,"
                + "eventDetectorTemplateId int NOT NULL,"
                + "primary key (id),"
                + "KEY templatesDetectorsFk1 (eventDetectorTemplateId),"
                + "CONSTRAINT templatesDetectorsFk1 FOREIGN KEY (eventDetectorTemplateId) REFERENCES eventDetectorTemplates (id)"
                + ")";

        jdbcTemplate.execute(templatesDetectorsSQL);

        final String usersProfilesSQL = ""
                + "CREATE TABLE IF NOT EXISTS usersProfiles ("
                + "id SERIAL,"
                + "xid varchar(50) not null,"
                + "name varchar(255) NOT NULL,"
                + "primary key (id),"
                + "CONSTRAINT usersProfilesUn1 UNIQUE (xid)"
                + ")";

        jdbcTemplate.execute(usersProfilesSQL);

        // Data source permissions

        final String dataSourceUsersProfilesSQL = ""
                + "create table IF NOT EXISTS dataSourceUsersProfiles ("
                + "dataSourceId int not null,"
                + "userProfileId int not null, "
                + "KEY dataSourceUsersProfilesFk1 (dataSourceId),"
                + "KEY dataSourceUsersProfilesFk2 (userProfileId),"
                + "CONSTRAINT dataSourceUsersProfilesFk1 FOREIGN KEY (dataSourceId) REFERENCES dataSources (id) ON DELETE CASCADE,"
                + "CONSTRAINT dataSourceUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
                + ")";

        jdbcTemplate.execute(dataSourceUsersProfilesSQL);

        // Data point permissions

        final String dataPointUsersProfilesSQL = ""
                + "create table IF NOT EXISTS dataPointUsersProfiles ("
                + "dataPointId int not null,"
                + "userProfileId int not null,"
                + "permission int not null,"
                + "KEY dataPointUsersProfilesFk1 (dataPointId),"
                + "KEY dataPointUsersProfilesFk2 (userProfileId),"
                + "CONSTRAINT dataPointUsersProfilesFk1 FOREIGN KEY (dataPointId) REFERENCES dataPoints (id) ON DELETE CASCADE,"
                + "CONSTRAINT dataPointUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
                + ")";

        jdbcTemplate.execute(dataPointUsersProfilesSQL);

        //Data source permissions

        final String usersUsersProfilesSQL = ""
                + "create table IF NOT EXISTS usersUsersProfiles ("
                + "userProfileId int not null,"
                + "userId int not null,"
                + "KEY usersUsersProfilesFk1 (userProfileId),"
                + "KEY usersUsersProfilesFk2 (userId),"
                + "CONSTRAINT usersUsersProfilesFk1 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE,"
                + "CONSTRAINT usersUsersProfilesFk2 FOREIGN KEY (userId) REFERENCES users (id) ON DELETE CASCADE"
                + ")";

        jdbcTemplate.execute(usersUsersProfilesSQL);

        // Watchlist permissions

        final String watchListUsersProfilesSQL = ""
                + "create table IF NOT EXISTS watchListUsersProfiles ("
                + "watchlistId int not null,"
                + "userProfileId int not null,"
                + "permission int not null,"
                + "KEY watchlistUsersProfilesFk1 (watchlistId),"
                + "KEY watchlistUsersProfilesFk2 (userProfileId),"
                + "CONSTRAINT watchlistUsersProfilesFk1 FOREIGN KEY (watchlistId) REFERENCES watchLists (id) ON DELETE CASCADE,"
                + "CONSTRAINT watchlistUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
                + ")";

        jdbcTemplate.execute(watchListUsersProfilesSQL);

        // View Users Profiles

        final String viewUsersProfilesSQL = ""
                + "create table IF NOT EXISTS viewUsersProfiles ("
                + "viewId int not null,"
                + "userProfileId int not null,"
                + "permission int not null,"
                + "KEY viewUsersProfilesFk1 (viewId),"
                + "KEY viewUsersProfilesFk2 (userProfileId),"
                + "CONSTRAINT viewUsersProfilesFk1 FOREIGN KEY (viewId) REFERENCES mangoViews (id) ON DELETE CASCADE,"
                + "CONSTRAINT viewUsersProfilesFk2 FOREIGN KEY (userProfileId) REFERENCES usersProfiles (id) ON DELETE CASCADE"
                + ")";

        jdbcTemplate.execute(viewUsersProfilesSQL);
    }

    private void addColumnsToDataPointsTable(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute(
                "ALTER TABLE dataPoints ADD COLUMN pointName VARCHAR(250);"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN dataPoints.pointName IS 'copy point name from data';"
        );

        jdbcTemplate.execute(
                "ALTER TABLE dataPoints ADD COLUMN plcAlarmLevel SMALLINT;"
        );
        jdbcTemplate.execute(
                "COMMENT ON COLUMN dataPoints.plcAlarmLevel IS '1 - FAULT, 2 - ALARM';"
        );
    }

    private void updateDataPointsTable(JdbcTemplate jdbcTemplate) throws Exception {

        try {
            List<DataPointVO> dataPoints = jdbcTemplate.query("SELECT id, data FROM dataPoints", (resultSet, i) -> {
                try (InputStream inputStream = resultSet.getBinaryStream("data");
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    DataPointVO dataPointVO = (DataPointVO) objectInputStream.readObject();
                    dataPointVO.setId(resultSet.getInt("id"));
                    return dataPointVO;
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    return null;
                }
            });

            boolean isNull = dataPoints.stream().anyMatch(Objects::isNull);
            if (isNull) {
                throw new IllegalStateException("DataPointVO is null!");
            }

            for (DataPointVO dataPointPart : dataPoints) {
                String dataPointName = dataPointPart.getName();
                int plcAlarmLevel = 0;
                if (dataPointName.contains(" AL ")) {
                    plcAlarmLevel = 2;
                }
                if (dataPointName.contains(" ST ")) {
                    plcAlarmLevel = 1;
                }
                jdbcTemplate.update("UPDATE dataPoints SET plcAlarmLevel = ?, pointName = ? WHERE id = ?",
                        plcAlarmLevel, dataPointName, dataPointPart.getId());
            }
        } catch (EmptyResultDataAccessException empty) {
            LOG.warn(String.valueOf(empty));
        }

    }

    private void createPlcAlarmsTable(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS plcAlarms (\n" +
                "  id SERIAL PRIMARY KEY,\n" +
                "  dataPointId INTEGER NOT NULL,\n" +
                "  dataPointXid VARCHAR(50),\n" +
                "  dataPointType VARCHAR(45),\n" +
                "  dataPointName VARCHAR(45),\n" +
                "  activeTime BIGINT DEFAULT 0,\n" +
                "  inactiveTime BIGINT DEFAULT 0,\n" +
                "  acknowledgeTime BIGINT DEFAULT 0,\n" +
                "  level SMALLINT,\n" +
                "  UNIQUE (dataPointId, inactiveTime),\n" +
                "  FOREIGN KEY (dataPointId) REFERENCES dataPoints(id) ON DELETE CASCADE\n" +
                ");"
        );
    }

    private void createFunctions(JdbcTemplate jdbcTemplate) {

        jdbcTemplate.execute(
                "CREATE OR REPLACE FUNCTION func_formats_date(ts BIGINT)\n" +
                "  RETURNS VARCHAR(19)\n" +
                "  LANGUAGE plpgsql AS $$\n" +
                "BEGIN\n" +
                "  IF ts = 0 THEN\n" +
                "    RETURN ' ';\n" +
                "  END IF;\n" +
                "  RETURN to_char(to_timestamp(ts/1000.0), 'YYYY-MM-DD HH24:MI:SS');\n" +
                "END;\n" +
                "$$;"
        );
    }

    private void createViews(JdbcTemplate jdbcTemplate) throws Exception {


        jdbcTemplate.execute(
                "CREATE VIEW historyAlarms AS\n" +
                " SELECT\n" +
                "   func_formats_date(activeTime) AS activeTime,\n" +
                "   func_formats_date(inactiveTime) AS inactiveTime,\n" +
                "   func_formats_date(acknowledgeTime) AS acknowledgeTime,\n" +
                "   level,\n" +
                "   dataPointName AS name\n" +
                " FROM plcAlarms\n" +
                " ORDER BY (inactiveTime = 0) DESC, inactiveTime DESC, id DESC;"
        );

        jdbcTemplate.execute(
                "CREATE VIEW liveAlarms AS\n" +
                " SELECT\n" +
                "   id,\n" +
                "   func_formats_date(activeTime) AS activation_time,\n" +
                "   func_formats_date(inactiveTime) AS inactivation_time,\n" +
                "   level,\n" +
                "   dataPointName AS name\n" +
                " FROM plcAlarms\n" +
                " WHERE acknowledgeTime = 0\n" +
                "   AND (inactiveTime = 0\n" +
                "        OR inactiveTime > (EXTRACT(EPOCH FROM now() - INTERVAL '24 hours') * 1000))\n" +
                " ORDER BY (inactiveTime = 0) DESC, activeTime DESC, inactiveTime DESC, id DESC;"
        );
    }

    private void createProcedure(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.execute(
                "CREATE OR REPLACE FUNCTION prc_alarms_notify() RETURNS TRIGGER\n" +
                        "LANGUAGE plpgsql AS $$\n" +
                        "DECLARE\n" +
                        "  plc_level SMALLINT;\n" +
                        "  present_val INTEGER;\n" +
                        "  actual_row_id INTEGER;\n" +
                        "BEGIN\n" +
                        "  SELECT plcAlarmLevel INTO plc_level FROM dataPoints WHERE id = NEW.dataPointId;\n" +
                        "  present_val := NEW.pointValue::INTEGER;\n" +
                        "\n" +
                        "  IF plc_level IN (1,2) THEN\n" +
                        "    SELECT id INTO actual_row_id\n" +
                        "      FROM plcAlarms\n" +
                        "     WHERE dataPointId = NEW.dataPointId AND inactiveTime = 0\n" +
                        "     LIMIT 1;\n" +
                        "\n" +
                        "    IF (present_val = 1 AND actual_row_id IS NULL)\n" +
                        "       OR (present_val = 0 AND actual_row_id IS NOT NULL) THEN\n" +
                        "      INSERT INTO plcAlarms (\n" +
                        "        dataPointId,\n" +
                        "        dataPointXid,\n" +
                        "        dataPointType,\n" +
                        "        dataPointName,\n" +
                        "        activeTime,\n" +
                        "        inactiveTime,\n" +
                        "        acknowledgeTime,\n" +
                        "        level\n" +
                        "      )\n" +
                        "      VALUES (\n" +
                        "        NEW.dataPointId,\n" +
                        "        (SELECT xid FROM dataPoints WHERE id = NEW.dataPointId),\n" +
                        "        plc_level,\n" +
                        "        (SELECT pointName FROM dataPoints WHERE id = NEW.dataPointId),\n" +
                        "        NEW.ts,\n" +
                        "        0,\n" +
                        "        0,\n" +
                        "        plc_level\n" +
                        "      )\n" +
                        "      ON CONFLICT (dataPointId, inactiveTime)\n" +
                        "      DO UPDATE SET inactiveTime = EXCLUDED.inactiveTime;\n" +
                        "    END IF;\n" +
                        "  END IF;\n" +
                        "\n" +
                        "  RETURN NULL;\n" +
                        "END;\n" +
                        "$$;"
        );

    }

    private void createTrigger(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.execute(
                "DO $$ " +
                        "BEGIN " +
                        "IF NOT EXISTS ( " +
                        "    SELECT 1 FROM pg_trigger WHERE tgname = 'tri_notify_faults_or_alarms' " +
                        ") THEN " +
                        "    CREATE TRIGGER tri_notify_faults_or_alarms " +
                        "    AFTER INSERT ON pointValues " +
                        "    FOR EACH ROW " +
                        "    EXECUTE FUNCTION prc_alarms_notify(); " +
                        "END IF; " +
                        "END $$;"
        );

    }

    private void migrateViews(JdbcTemplate jdbcTemplate) {
        List<View> views = jdbcTemplate.query(
                "SELECT id, data FROM mangoViews",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        View v = (View) ois.readObject();
                        v.setId(rs.getInt("id"));
                        return v;
                    } catch (IOException | ClassNotFoundException ex) {
                        throw new RuntimeException("view deserialization error id=" + rs.getInt("id"), ex);
                    }
                }
        );

        if (views.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one View is null!");
        }

        for (View v : views) {
            for (ViewComponent c : v.getViewComponents()) {
                if (c.getZ() < Z_INDEX_MIN) {
                    c.setZ(Z_INDEX_DEFAULT);
                }
            }
        }

        for (View v : views) {
            ByteArrayInputStream bais = new SerializationData().writeObject(v);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE mangoViews SET data = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, v.getId());
                return ps;
            });
        }
    }

    private void updateDataPointsTable2(JdbcTemplate jdbcTemplate) throws Exception {
        try {
            List<DataPointVO> dataPoints = jdbcTemplate.query(
                    "SELECT id, data FROM dataPoints",
                    (rs, rowNum) -> {
                        try (InputStream is = rs.getBinaryStream("data");
                             ObjectInputStream ois = new ObjectInputStream(is)) {
                            DataPointVO dp = (DataPointVO) ois.readObject();
                            dp.setId(rs.getInt("id"));

                            if (dp.getEventTextRenderer() == null)
                                dp.setEventTextRenderer(new NoneEventRenderer());
                            if (dp.getDescription() == null)
                                dp.setDescription("");

                            return dp;
                        } catch (IOException | ClassNotFoundException ex) {
                            throw new RuntimeException("DataPointVO deserialization error id=" + rs.getInt("id"), ex);
                        }
                    }
            );

            if (dataPoints.stream().anyMatch(Objects::isNull)) {
                throw new IllegalStateException("At least one DataPointVO is null!");
            }

            for (DataPointVO dp : dataPoints) {
                ByteArrayInputStream bais = new SerializationData().writeObject(dp);

                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE dataPoints SET data = ? WHERE id = ?"
                    );
                    ps.setBinaryStream(1, bais, bais.available());
                    ps.setInt(2, dp.getId());
                    return ps;
                });
            }

        } catch (EmptyResultDataAccessException empty) {
            LOG.warn("No dataPoints found", empty);
        }
    }

    private void migratePermissions() {
        IUserDAO userDAO = new OnlyMigrationUserDAO();
        IUsersProfileDAO usersProfileDAO = new OnlyMigrationUsersProfileDAO();
        WatchListDAO watchListDAO = new OnlyMigrationWatchListDAO();
        DataPointDAO dataPointDAO = new OnlyMigrationDataPointDAO();
        IDataSourceDAO dataSourceDAO = new OnlyMigrationDataSourceDAO();
        IViewDAO viewDAO = new OnlyMigrationViewDAO();
        DataPointUserDAO dataPointUserDAO = new OnlyMigrationDataPointUserDAO();
        IUserCommentDAO userCommentDAO = new OnlyMigrationUserCommentDAO();

        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService =
                new WatchListProfilePermissionsService(usersProfileDAO);
        PermissionsService<DataPointAccess, UsersProfileVO> dataPointPermissionsService =
                new DataPointProfilePermissionsService(usersProfileDAO);
        PermissionsService<Integer, UsersProfileVO> dataSourcePermissionsService =
                new DataSourceProfilePermissionsService(usersProfileDAO);
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService =
                new ViewProfilePermissionsService(usersProfileDAO);

        UsersProfileService usersProfileService = new UsersProfileService(usersProfileDAO, userDAO,
                watchListPermissionsService, dataPointPermissionsService,
                dataSourcePermissionsService, viewPermissionsService);

        PermissionsService<DataPointAccess, User> dataPointUserPermissionsService =
                new DataPointUserPermissionsService(dataPointUserDAO);
        PermissionsService<Integer, User> dataSourceUserPermissionsService =
                new DataSourceUserPermissionsService(dataSourceDAO);
        PermissionsService<WatchListAccess, User> watchListUserPermissionsService =
                new WatchListUserPermissionsService(watchListDAO);
        PermissionsService<ViewAccess, User> viewUserPermissionsService =
                new ViewUserPermissionsService(viewDAO);

        UserService userService = new UserService(userDAO, userCommentDAO, null,
                null, null, usersProfileService,
                dataPointUserPermissionsService, dataSourceUserPermissionsService);

        List<User> users = userService.getUsersWithProfile().stream()
                .filter(a -> !a.isAdmin())
                .collect(Collectors.toList());

        if (!users.isEmpty()) {
            MigrationPermissionsService migrationPermissionsService = new MigrationPermissionsService(dataPointUserPermissionsService,
                    dataSourceUserPermissionsService, watchListUserPermissionsService, viewUserPermissionsService);

            Map<Integer, DataPointVO> dataPoints = dataPointDAO.getDataPoints().stream().collect(Collectors.toMap(DataPointVO::getId, a -> a));
            Map<Integer, DataSourceVO<?>> dataSources = dataSourceDAO.getDataSources().stream().collect(Collectors.toMap(DataSourceVO::getId, a -> a));
            Map<Integer, View> views = viewDAO.findAll().stream().collect(Collectors.toMap(View::getId, a -> a));
            Map<Integer, WatchList> watchLists = watchListDAO.findAll().stream().collect(Collectors.toMap(WatchList::getId, a -> a));

            MigrationDataService migrationDataService = new MigrationDataService(dataPoints, dataSources, views, watchLists, usersProfileService);

            MigrationPermissions migrationCommand = MigrationPermissions.newMigration(migrationPermissionsService, migrationDataService);
            migrationCommand.execute(users);

            views.clear();
            migrationDataService.clear();
        }

        users.clear();
    }

    private static void dropPrcAlarmsNotify(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP FUNCTION IF EXISTS \"prc_alarms_notify\"() CASCADE");
    }

    private static void createPrcAlarmsNotify(JdbcTemplate jdbcTemplate) {
        String sql =
                "CREATE OR REPLACE FUNCTION \"prc_alarms_notify\"() " +
                        "RETURNS TRIGGER " +
                        "LANGUAGE plpgsql " +
                        "AS $$ " +
                        "DECLARE " +
                        "    plc_level INTEGER; " +
                        "    present_val INTEGER; " +
                        "    actual_row_id INTEGER; " +
                        "BEGIN " +
                        "    SELECT plcAlarmLevel INTO plc_level FROM dataPoints WHERE id = NEW.dataPointId; " +
                        "    present_val := NEW.pointValue::INTEGER; " +
                        "    IF plc_level IN (1, 2) THEN " +
                        "        SELECT id INTO actual_row_id " +
                        "        FROM plcAlarms " +
                        "        WHERE dataPointId = NEW.dataPointId AND inactiveTime = 0 " +
                        "        LIMIT 1; " +
                        "        IF (present_val = 1 AND actual_row_id IS NULL) " +
                        "            OR (present_val = 0 AND actual_row_id IS NOT NULL) THEN " +
                        "            INSERT INTO plcAlarms ( " +
                        "                dataPointId, " +
                        "                dataPointXid, " +
                        "                dataPointType, " +
                        "                dataPointName, " +
                        "                activeTime, " +
                        "                inactiveTime, " +
                        "                acknowledgeTime, " +
                        "                level " +
                        "            ) VALUES ( " +
                        "                NEW.dataPointId, " +
                        "                (SELECT xid FROM dataPoints WHERE id = NEW.dataPointId), " +
                        "                plc_level, " +
                        "                (SELECT pointName FROM dataPoints WHERE id = NEW.dataPointId), " +
                        "                NEW.ts, " +
                        "                0, " +
                        "                0, " +
                        "                plc_level " +
                        "            ) ON CONFLICT (dataPointId, inactiveTime) " +
                        "            DO UPDATE SET inactiveTime = EXCLUDED.inactiveTime; " +
                        "        END IF; " +
                        "    END IF; " +
                        "    RETURN NULL; " +
                        "END; " +
                        "$$;";

        jdbcTemplate.execute(sql);
    }

    private void migrateExtendedDelayForMetaDatapoints(JdbcTemplate jdbcTemplate) {
        List<DataPointVO> dataPoints = jdbcTemplate.query("SELECT id, data FROM dataPoints", (rs, i) -> {
            try (InputStream is = rs.getBinaryStream("data");
                 ObjectInputStream ois = new ObjectInputStream(is)) {
                DataPointVO dp = (DataPointVO) ois.readObject();
                dp.setId(rs.getInt("id"));
                return dp;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error("Deserialization failed for dataPoint id=" + rs.getInt("id"), ex);
                return null;
            }
        });

        try {
            if (dataPoints.stream().anyMatch(Objects::isNull)) {
                throw new IllegalStateException("At least one DataPointVO is null!");
            }

            for (DataPointVO dp : dataPoints) {
                PointLocatorVO locator = dp.getPointLocator();
                if (locator instanceof MetaPointLocatorVO) {
                    MetaPointLocatorVO meta = (MetaPointLocatorVO) locator;
                    if (meta.getExecutionDelayPeriodTypeCode() == 0) {
                        meta.setExecutionDelayPeriodType(TimePeriodType.SECONDS);
                        dp.setPointLocator(meta);

                        ByteArrayInputStream bais = new SerializationData().writeObject(dp);

                        jdbcTemplate.update(conn -> {
                            PreparedStatement ps = conn.prepareStatement("UPDATE dataPoints SET data = ? WHERE id = ?");
                            ps.setBinaryStream(1, bais, bais.available());
                            ps.setInt(2, dp.getId());
                            return ps;
                        });
                    }
                }
            }
        } finally {
            dataPoints.clear();
        }
    }

    private void migrateHttpRetriever(JdbcTemplate jdbcTemplate) {
        List<HttpRetrieverDataSourceVO> dataSources = jdbcTemplate.query(
                "SELECT id, xid, name, data FROM dataSources WHERE dataSourceType = 11",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        HttpRetrieverDataSourceVO ds = (HttpRetrieverDataSourceVO) ois.readObject();
                        ds.setId(rs.getInt("id"));
                        ds.setXid(rs.getString("xid"));
                        ds.setName(rs.getString("name"));
                        if (ds.getStaticHeaders() == null) {
                            ds.setStaticHeaders(new ArrayList<>());
                        }
                        return ds;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Deserialization error for datasource ID " + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        if (dataSources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one HttpRetrieverDataSourceVO is null!");
        }

        for (HttpRetrieverDataSourceVO ds : dataSources) {
            ByteArrayInputStream bais = new SerializationData().writeObject(ds);

            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE dataSources SET data = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, ds.getId());
                return ps;
            });
        }
    }

    private void updatePurgeLimitStrategyDatapointProperty(JdbcTemplate jdbcTemplate) throws Exception {
        try {
            List<DataPointVO> dataPoints = jdbcTemplate.query(
                    "SELECT id, data FROM dataPoints",
                    (rs, rowNum) -> {
                        try (InputStream inputStream = rs.getBinaryStream("data");
                             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {

                            DataPointVO dp = (DataPointVO) objectInputStream.readObject();
                            dp.setId(rs.getInt("id"));
                            dp.setPurgeStrategy(DataPointVO.PurgeStrategy.PERIOD);
                            dp.setPurgeValuesLimit(SystemSettingsDAO
                                    .getIntValue(SystemSettingsDAO.VALUES_LIMIT_FOR_PURGE));
                            return dp;

                        } catch (IOException | ClassNotFoundException e) {
                            LOG.error("Error deserializing DataPointVO id=" + rs.getInt("id"), e);
                            return null;
                        }
                    }
            );

            boolean hasNull = dataPoints.stream().anyMatch(Objects::isNull);
            if (hasNull) {
                throw new IllegalStateException("At least one DataPointVO is null!");
            }

            for (DataPointVO dp : dataPoints) {
                ByteArrayInputStream bais = new SerializationData().writeObject(dp);

                jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE dataPoints SET data = ? WHERE id = ?"
                    );
                    ps.setBinaryStream(1, bais, bais.available());
                    ps.setInt(2, dp.getId());
                    return ps;
                });
            }

        } catch (EmptyResultDataAccessException e) {
            LOG.warn("No dataPoints found", e);
        }
    }

    private static void createXidColumn(JdbcTemplate jdbcTemplate) {
        String checkAndAddXidToReports =
                "DO $$ BEGIN " +
                        "IF NOT EXISTS (" +
                        "  SELECT 1 FROM information_schema.columns " +
                        "  WHERE table_name='reports' AND column_name='xid'" +
                        ") THEN " +
                        "  EXECUTE 'ALTER TABLE reports ADD COLUMN xid VARCHAR(50) DEFAULT NULL'; " +
                        "END IF; " +
                        "END $$;";

        jdbcTemplate.execute(checkAndAddXidToReports);
    }

    private static void updateReports(JdbcTemplate jdbcTemplate, List<ReportVO> reports) {
        for (ReportVO report : reports) {
            ByteArrayInputStream bais = new SerializationData().writeObject(report);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE reports SET data = ?, xid = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setString(2, report.getXid());
                ps.setInt(3, report.getId());
                return ps;
            });
        }
    }

    private static Map<Integer, String> toMapDataPointIdXid(JdbcTemplate jdbcTemplate) {
        List<DataPointVO> dataPoints = jdbcTemplate.query(
                "SELECT id, xid, data FROM dataPoints",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        DataPointVO dp = (DataPointVO) ois.readObject();
                        dp.setId(rs.getInt("id"));
                        dp.setXid(rs.getString("xid"));
                        return dp;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize datapoint id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        return dataPoints.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DataPointVO::getId, DataPointVO::getXid));
    }

    private static void setUsername(JdbcTemplate jdbcTemplate, ReportVO report) {
        String username = jdbcTemplate.queryForObject(
                "SELECT username FROM users WHERE id = ?",
                new Object[]{report.getUserId()},
                String.class
        );
        report.setUsername(username);
    }

    private static List<ReportVO> getReports(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(
                "SELECT id, userId, name, data FROM reports",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        ReportVO report = (ReportVO) ois.readObject();
                        report.setId(rs.getInt("id"));
                        report.setUserId(rs.getInt("userId"));
                        report.setName(rs.getString("name"));
                        return report;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize report id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );
    }

    private static void setMissingFields(JdbcTemplate jdbcTemplate, List<ReportVO> reports) {
        Map<Integer, String> dataPointIdXid = toMapDataPointIdXid(jdbcTemplate);
        try {
            for (ReportVO report : reports) {
                setUsername(jdbcTemplate, report);
                setDataPointXid(dataPointIdXid, report);
                if (StringUtils.isEmpty(report.getXid())) {
                    report.setXid(ReportVO.generateXid());
                }
            }
        } finally {
            dataPointIdXid.clear();
        }
    }

    private static void setDataPointXid(Map<Integer, String> dataPointIdXid, ReportVO report) {
        for (ReportPointVO point : report.getPoints()) {
            int dataPointId = point.getPointId();
            point.setPointXid(dataPointIdXid.get(dataPointId));
        }
    }

    private void migrateMqttPointLocatorUpdateDataPointXid(JdbcTemplate jdbcTemplate) {
        List<DataPointVO> dataPoints = jdbcTemplate.query(
                "SELECT id, xid, data FROM dataPoints",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        DataPointVO dp = (DataPointVO) ois.readObject();
                        dp.setId(rs.getInt("id"));
                        dp.setXid(rs.getString("xid"));
                        return dp;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize DataPointVO id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        try {
            if (dataPoints.stream().anyMatch(Objects::isNull)) {
                throw new IllegalStateException("DataPointVO is null!");
            }

            for (DataPointVO dataPoint : dataPoints) {
                PointLocatorVO locator = dataPoint.getPointLocator();
                if (locator instanceof MqttPointLocatorVO) {
                    MqttPointLocatorVO mqttLocator = (MqttPointLocatorVO) locator;
                    if (mqttLocator.getDataPointXid() == null) {
                        mqttLocator.setDataPointXid(dataPoint.getXid());
                        dataPoint.setPointLocator(mqttLocator);

                        ByteArrayInputStream bais = new SerializationData().writeObject(dataPoint);
                        jdbcTemplate.update(connection -> {
                            PreparedStatement ps = connection.prepareStatement(
                                    "UPDATE dataPoints SET data = ? WHERE id = ?"
                            );
                            ps.setBinaryStream(1, bais, bais.available());
                            ps.setInt(2, dataPoint.getId());
                            return ps;
                        });
                    }
                }
            }
        } finally {
            dataPoints.clear();
        }
    }

    private void migrateSqlDataSourceUpdate(JdbcTemplate jdbcTemplate) {
        List<SqlDataSourceVO> dataSources = jdbcTemplate.query(
                "SELECT id, data FROM dataSources WHERE dataSourceType = 6",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        SqlDataSourceVO ds = (SqlDataSourceVO) ois.readObject();
                        ds.setId(rs.getInt("id"));
                        if (StringUtils.isEmpty(ds.getJndiResourceName())) {
                            ds.setJndiResource(false);
                        }
                        return ds;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize SqlDataSourceVO id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        if (dataSources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("SqlDataSourceVO is null!");
        }

        for (SqlDataSourceVO dataSource : dataSources) {
            ByteArrayInputStream bais = new SerializationData().writeObject(dataSource);
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE dataSources SET data = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, dataSource.getId());
                return ps;
            });
        }
    }

    public static void addSoapServicesUser(JdbcTemplate jdbcTemplate) {
        String username = "soap-services";

        List<Integer> ids = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE username = ?",
                new Object[]{username},
                Integer.class
        );

        if (ids.isEmpty()) {
            String sql = "INSERT INTO users (username, password, email, phone, admin, disabled, " +
                    "homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) VALUES (?,?,?,?,?,?,?,?,?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                new ArgumentPreparedStatementSetter(new Object[]{
                        username,
                        Common.encrypt(username),
                        username + "@mail.com",
                        "",
                        "N",
                        "Y",
                        "",
                        0,
                        "N"
                }).setValues(ps);
                return ps;
            });

        }
    }

    public static void addHttpdsBasicUser(JdbcTemplate jdbcTemplate) {
        String username = "httpds-basic";

        List<Integer> ids = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE username = ?",
                new Object[]{username},
                Integer.class
        );

        if (ids.isEmpty()) {
            String sql = "INSERT INTO users (username, password, email, phone, admin, disabled, " +
                    "homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) VALUES (?,?,?,?,?,?,?,?,?)";

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                new ArgumentPreparedStatementSetter(new Object[]{
                        username,
                        Common.encrypt(username),
                        "null@null.com",
                        "",
                        "N",
                        "Y",
                        "",
                        0,
                        "N"
                }).setValues(ps);
                return ps;
            });

        }
    }

    private void migrateLangDefault(JdbcTemplate jdbcTemplate) {
        try {
            String defaultLangSql = "SELECT settingValue FROM systemSettings WHERE settingName='" + SystemSettingsDAO.LANGUAGE + "'";
            String defaultLang;
            try {
                defaultLang = jdbcTemplate.queryForObject(defaultLangSql, String.class);
            } catch (Exception ex) {
                LOG.debug("Could not fetch default language, fallback to 'en': " + ex.getMessage(), ex);
                defaultLang = "en";
            }

            List<Integer> results = jdbcTemplate.query(
                    "SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'lang'",
                    (rs, rowNum) -> rs.getInt(1)
            );

            if (results.isEmpty()) {
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN lang VARCHAR(10) DEFAULT '" + defaultLang + "'");
            }
        } catch (Exception ex) {
            LOG.error("Error during migrateLangDefault: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    private void updateAbsoluteToRelativeUrlInLinkComponent(JdbcTemplate jdbcTemplate) {
        List<View> views = jdbcTemplate.query("SELECT id, data FROM mangoViews", (rs, rowNum) -> {
            try (InputStream is = rs.getBinaryStream("data");
                 ObjectInputStream ois = new ObjectInputStream(is)) {
                View view = (View) ois.readObject();
                view.setId(rs.getInt("id"));
                return view;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error("Deserialization error for view ID: " + rs.getInt("id"), ex);
                return null;
            }
        });

        if (views.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one View is null!");
        }

        for (View view : views) {
            for (ViewComponent component : view.getViewComponents()) {
                if (component instanceof LinkComponent) {
                    LinkComponent linkComponent = (LinkComponent) component;
                    String link = linkComponent.getLink();
                    if (link != null) {
                        int index = link.lastIndexOf("/");
                        if (index != -1) {
                            linkComponent.setLink(link.substring(index + 1));
                        }
                    }
                }
            }
        }

        for (View view : views) {
            ByteArrayInputStream bais = new SerializationData().writeObject(view);
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement("UPDATE mangoViews SET data = ? WHERE id = ?");
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, view.getId());
                return ps;
            });
        }
    }

    private void migrateSqlDataSourceLimit(JdbcTemplate jdbcTemplate) {
        List<SqlDataSourceVO> dataSources = jdbcTemplate.query(
                "SELECT id, data FROM dataSources WHERE dataSourceType = 6",
                (resultSet, i) -> {
                    try (InputStream inputStream = resultSet.getBinaryStream("data");
                         ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                        SqlDataSourceVO dataSourceVO = (SqlDataSourceVO) objectInputStream.readObject();
                        dataSourceVO.setId(resultSet.getInt("id"));
                        dataSourceVO.setStatementLimit(0);
                        return dataSourceVO;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Deserialization error for SqlDataSourceVO id=" + resultSet.getInt("id"), ex);
                        return null;
                    }
                }
        );

        if (dataSources.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one SqlDataSourceVO is null!");
        }

        for (SqlDataSourceVO dataSource : dataSources) {
            jdbcTemplate.update(
                    "UPDATE dataSources SET data = ? WHERE id = ?",
                    new SerializationData().writeObject(dataSource),
                    dataSource.getId()
            );
        }
    }

    private void migrateFixedAggregationEnabled(JdbcTemplate jdbcTmp) {
        String settingName = "aggregationEnabled";
        try {
            String aggregationEnabled = jdbcTmp.queryForObject(
                    "SELECT settingValue FROM systemSettings WHERE settingName = ?",
                    new Object[]{settingName},
                    String.class
            );

            if (aggregationEnabled != null) {
                boolean value = "true".equalsIgnoreCase(aggregationEnabled) || "Y".equalsIgnoreCase(aggregationEnabled);
                String settingValue = value ? "Y" : "N";
                jdbcTmp.update(
                        "UPDATE systemSettings SET settingValue = ? WHERE settingName = ?",
                        settingValue,
                        settingName
                );
            }
        } catch (EmptyResultDataAccessException ex) {
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void addAssigneeColumn(JdbcTemplate jdbcTemplate) {
        String checkColumnSql = "SELECT 1 FROM information_schema.columns WHERE table_name = ? AND column_name = ?";

        boolean hasAssigneeTs = !jdbcTemplate.query(
                checkColumnSql,
                new Object[]{"events", "assigneets"},
                (rs, rowNum) -> rs.getInt(1)
        ).isEmpty();

        boolean hasAssigneeUsername = !jdbcTemplate.query(
                checkColumnSql,
                new Object[]{"events", "assigneeusername"},
                (rs, rowNum) -> rs.getInt(1)
        ).isEmpty();

        if (!hasAssigneeTs) {
            jdbcTemplate.execute("ALTER TABLE events ADD COLUMN assigneeTs BIGINT DEFAULT NULL");
        }

        if (!hasAssigneeUsername) {
            jdbcTemplate.execute("ALTER TABLE events ADD COLUMN assigneeUsername VARCHAR(40) DEFAULT NULL");
        }
    }

    private void updateViewComponents(JdbcTemplate jdbcTemplate) {
        List<View> views = jdbcTemplate.query("SELECT id, data FROM mangoViews", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                View view = (View) objectInputStream.readObject();
                view.setId(resultSet.getInt("id"));
                return view;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error("Deserialization error for mangoView id=" + resultSet.getInt("id"), ex);
                return null;
            }
        });

        if (views.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one View is null!");
        }

        for (View view : views) {
            jdbcTemplate.update("UPDATE mangoViews SET data = ? WHERE id = ?",
                    new SerializationData().writeObject(view), view.getId());
        }
    }


}
