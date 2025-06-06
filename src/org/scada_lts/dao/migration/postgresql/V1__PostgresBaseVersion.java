package org.scada_lts.dao.migration.postgresql;


import com.serotonin.mango.Common;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;

public class V1__PostgresBaseVersion extends BaseJavaMigration {

    private static final Logger LOG = LoggerFactory.getLogger(V1__PostgresBaseVersion.class);

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

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS schema_version (" +
                        "    installed_rank INT NOT NULL," +
                        "    version VARCHAR(50)," +
                        "    description VARCHAR(200) NOT NULL," +
                        "    type VARCHAR(20) NOT NULL," +
                        "    script VARCHAR(1000) NOT NULL," +
                        "    checksum INT," +
                        "    installed_by VARCHAR(100) NOT NULL," +
                        "    installed_on TIMESTAMP NOT NULL DEFAULT now()," +
                        "    execution_time INT NOT NULL," +
                        "    success BOOLEAN NOT NULL," +
                        "    PRIMARY KEY (installed_rank)" +
                        ")"
        );

        //System settingsds

        final String settingSQL = ""
                + "create table IF NOT EXISTS systemsettings ("
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

        try {
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

        final String mailingListMembersSQL = ""
                + "create table IF NOT EXISTS mailingListMembers ("
                + "mailingListId int not null,"
                + "typeId int not null,"
                + "userId int,"
                + "address varchar(255)"
                + ")";

        jdbcTemplate.execute(mailingListMembersSQL);

        try {
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

        try {
            jdbcTemplate.execute("alter table dataSources add constraint dataSourcesUn1 unique (xid);");
        } catch (Exception ex) {
            LOG.warn("Constraint dataSourcesUn1 already exists, skipping.");
        }
        // Data source permissions

        final String dataSourcePermissionsSQL = ""
                + "create table IF NOT EXISTS dataSourceUsers (dataSourceId int not null,userId int not null)";

        jdbcTemplate.execute(dataSourcePermissionsSQL);

        try {
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
        try {
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
        } catch (Exception ex) {
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
                + "CREATE TABLE IF NOT EXISTS events ("
                + "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
                + "typeId INTEGER NOT NULL,"
                + "typeRef1 INTEGER NOT NULL,"
                + "typeRef2 INTEGER NOT NULL,"
                + "activeTs BIGINT NOT NULL,"
                + "rtnApplicable CHAR(1) NOT NULL,"
                + "rtnTs BIGINT,"
                + "rtnCause INTEGER,"
                + "alarmLevel INTEGER NOT NULL,"
                + "message TEXT,"
                + "ackTs BIGINT,"
                + "ackUserId INTEGER,"
                + "alternateAckSource INTEGER,"
                + "shortMessage TEXT,"
                + "assigneeTs BIGINT,"
                + "assigneeUsername VARCHAR(40),"
                + "typeRef3 INTEGER NOT NULL"
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
                + "create table IF NOT EXISTS pointhierarchy ("
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
                    "INSERT INTO systemsettings (settingname, settingvalue) " +
                            "VALUES (?, ?) " +
                            "ON CONFLICT (settingname) DO UPDATE SET settingvalue = EXCLUDED.settingvalue",
                    "databaseSchemaVersion", Common.getVersion()
            );
        }
    }
}