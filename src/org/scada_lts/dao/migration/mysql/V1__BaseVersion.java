/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.dao.migration.mysql;

import java.sql.PreparedStatement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.serotonin.mango.Common;

/**
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class V1__BaseVersion extends BaseJavaMigration {

	@Override
	public void migrate(Context context) throws Exception {

		final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

		// @formatter:off
		
        jdbcTemplate.execute("alter database default character set utf8");
        jdbcTemplate.execute("alter database default character set utf8");

      //System settings
        
        final String settingSQL = ""
        		+ "create table systemSettings ("
        			+ "settingName varchar(32) not null,"
        			+ "settingValue longtext,"
        			+ "primary key (settingName)"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(settingSQL);

        //Users
        final String usersSQL = ""
        		+ "create table users ("
        			+ "id int not null auto_increment,"
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
        		+ ") ENGINE=InnoDB;";
        		
        jdbcTemplate.execute(usersSQL);

        //userCommets
        final String userCommentsSQL = ""
        		+ "create table userComments ("
        			+ "userId int,"
        			+ "commentType int not null,"
        			+ "typeKey int not null,"
        			+ "ts bigint not null,"
        			+ "commentText varchar(1024) not null"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(userCommentsSQL);
        
        final String constraintUserCommentsFk1 = ""
        		+ "alter table userComments "
        			+ "add constraint userCommentsFk1 foreign key (userId) "
        			+ "references users(id);";
        
        jdbcTemplate.execute(constraintUserCommentsFk1);

        //Mailing lists
        final String mailingListsSQL = ""
        		+ "create table mailingLists (  "
        			+ "id int not null auto_increment,  "
        			+ "xid varchar(50) not null,  "
        			+ "name varchar(40) not null,  "
        		+ "primary key (id)) ENGINE=InnoDB;";
        
        jdbcTemplate.execute(mailingListsSQL);
        
        jdbcTemplate.execute("alter table mailingLists add constraint mailingListsUn1 unique (xid);");

        final String mailingListInactiveSQL = ""
        		+ "create table mailingListInactive ("
        			+ "mailingListId int not null,"
        			+ "inactiveInterval int not null"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(mailingListInactiveSQL);
        
        jdbcTemplate.execute("alter table mailingListInactive add constraint mailingListInactiveFk1 foreign key (mailingListId) references mailingLists(id) on delete cascade;");
        
        final String mailingListMembersSQL=""
        		+ "create table mailingListMembers ("
        			+ "mailingListId int not null,"
        			+ "typeId int not null,"
        			+ "userId int,"
        			+ "address varchar(255)"
        		+ ") ENGINE=InnoDB;";
        		
        jdbcTemplate.execute(mailingListMembersSQL);
        jdbcTemplate.execute("alter table mailingListMembers add constraint mailingListMembersFk1 foreign key (mailingListId) references mailingLists(id) on delete cascade;");
        
        
        // Data Sources
        final String dataSourcesSQL = ""
        		+ "create table dataSources ("
        			+ "id int not null auto_increment, "
        			+ "xid varchar(50) not null, "
        			+ "name varchar(40) not null, "
        			+ "dataSourceType int not null, "
        			+ "data longblob not null, "
        			+ "primary key (id)"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(dataSourcesSQL);

        jdbcTemplate.execute("alter table dataSources add constraint dataSourcesUn1 unique (xid);");

        // Data source permissions

        final String dataSourcePermissionsSQL = ""
        		+ "create table dataSourceUsers (dataSourceId int not null,userId int not null) ENGINE=InnoDB;";
        		
        jdbcTemplate.execute(dataSourcePermissionsSQL);
        
        jdbcTemplate.execute("alter table dataSourceUsers add constraint dataSourceUsersFk1 foreign key (dataSourceId) references dataSources(id);");
        jdbcTemplate.execute("alter table dataSourceUsers add constraint dataSourceUsersFk2 foreign key (userId) references users(id) on delete cascade;");

        // Scripts
        
        final String scriptsSQL = ""
        		+ "create table scripts ("
        			+ "id int not null auto_increment,"
        			+ "userId int not null,"
        			+ "xid varchar(50) not null,"
        			+ "name varchar(40) not null,"
        			+ "script varchar(16384) not null,"
        			+ "data longblob not null,"
        			+ "primary key (id)"
        		+ ") ENGINE=InnoDB;";

        jdbcTemplate.execute(scriptsSQL);
        jdbcTemplate.execute("alter table scripts add constraint scriptsUn1 unique (xid);");
        jdbcTemplate.execute("alter table scripts add constraint scriptsFk1 foreign key (userId) references users(id);");

        // FlexProjects
        
        final String flexProjectsSQL = ""
        	  + "create table flexProjects ("
        	  	+ "id int not null auto_increment,"
        	  	+ "name varchar(40) not null,"
        	  	+ "description varchar(1024),"
        	  	+ "xmlConfig varchar(16384) not null,"
        	  	+ "primary key (id)"
        	  + ") ENGINE=InnoDB;";

        jdbcTemplate.execute(flexProjectsSQL);
        
        // Data Points

        final String dataPointsSQL = ""
        		+ "create table dataPoints ("
        			+ "id int not null auto_increment,"
        			+ "xid varchar(50) not null,"
        			+ "dataSourceId int not null,"
        			+ "data longblob not null,"
        			+ "primary key (id)"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(dataPointsSQL);
        jdbcTemplate.execute("alter table dataPoints add constraint dataPointsUn1 unique (xid);");
        jdbcTemplate.execute("alter table dataPoints add constraint dataPointsFk1 foreign key (dataSourceId) references dataSources(id);");

        // Data point permissions
        
        final String dataPointUsersSql = ""
        		+ "create table dataPointUsers ("
        			+ "dataPointId int not null,"
        			+ "userId int not null,"
        			+ "permission int not null"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(dataPointUsersSql);
        
        jdbcTemplate.execute("alter table dataPointUsers add constraint dataPointUsersFk1 foreign key (dataPointId) references dataPoints(id);");
        jdbcTemplate.execute("alter table dataPointUsers add constraint dataPointUsersFk2 foreign key (userId) references users(id) on delete cascade;");

        // Views

        final String viewsSql = ""
        		+ "create table mangoViews ("
        			+ "id int not null auto_increment,"
        			+ "xid varchar(50) not null,"
        			+ "name varchar(100) not null,"
        			+ "background varchar(255),"
        			+ "userId int not null,"
        			+ "anonymousAccess int not null,"
        			+ "data longblob not null,"
        			+ "primary key (id)"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(viewsSql);
        jdbcTemplate.execute("alter table mangoViews add constraint mangoViewsUn1 unique (xid);");
        jdbcTemplate.execute("alter table mangoViews add constraint mangoViewsFk1 foreign key (userId) references users(id) on delete cascade;");


        final String mangoViewUsersSql = ""
        		+ "create table mangoViewUsers ("
        			+ "mangoViewId int not null,"
        			+ "userId int not null,"
        			+ "accessType int not null,"
        			+ "primary key (mangoViewId, userId)"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(mangoViewUsersSql);
        jdbcTemplate.execute("alter table mangoViewUsers add constraint mangoViewUsersFk1 foreign key (mangoViewId) references mangoViews(id) on delete cascade;");
        jdbcTemplate.execute("alter table mangoViewUsers add constraint mangoViewUsersFk2 foreign key (userId) references users(id) on delete cascade;");
        
        // Point Values (historical dataf)
        
        final String pointValuesSQL = ""
        		+ "create table pointValues ("
        			+ "id bigint not null auto_increment,"
        			+ "dataPointId int not null,"
        			+ "dataType int not null,"
        			+ "pointValue double,"
        			+ "ts bigint not null,"
        			+ "primary key (id)"
        		+ ") ENGINE=InnoDB;";
        
        jdbcTemplate.execute(pointValuesSQL);
        jdbcTemplate.execute("alter table pointValues add constraint pointValuesFk1 foreign key (dataPointId) references dataPoints(id) on delete cascade;");
        jdbcTemplate.execute("create index pointValuesIdx1 on pointValues (ts, dataPointId);");
        jdbcTemplate.execute("create index pointValuesIdx2 on pointValues (dataPointId, ts);");
        
        final String pointValueAnnotations = ""
        		+ "create table pointValueAnnotations ("
        			+ "pointValueId bigint not null,"
        			+ "textPointValueShort varchar(128),"
        			+ "textPointValueLong longtext,"
        			+ "sourceType smallint,"
        		+ "sourceId int) ENGINE=InnoDB;";
        
        jdbcTemplate.execute(pointValueAnnotations);
        jdbcTemplate.execute("alter table pointValueAnnotations add constraint pointValueAnnotationsFk1 foreign key (pointValueId) references pointValues(id) on delete cascade;");

        // Watch list
        
        final String watchListSQL = ""
        		+ "create table watchLists ("
        			+ "id int not null auto_increment,"
        			+ "xid varchar(50) not null,"
        			+ "userId int not null,"
        			+ "name varchar(50),"
        			+ "primary key (id)"
        		+ ") ENGINE=InnoDB;";

        jdbcTemplate.execute(watchListSQL);
        jdbcTemplate.execute("alter table watchLists add constraint watchListsUn1 unique (xid);");
        jdbcTemplate.execute("alter table watchLists add constraint watchListsFk1 foreign key (userId) references users(id) on delete cascade;");
        
        final String watchListPointsSQL = ""
        		+ "create table watchListPoints ("
        			+ "watchListId int not null,"
        			+ "dataPointId int not null,"
        			+ "sortOrder int not null"
        		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(watchListPointsSQL);
       jdbcTemplate.execute("alter table watchListPoints add constraint watchListPointsFk1 foreign key (watchListId) references watchLists(id) on delete cascade;");
       jdbcTemplate.execute("alter table watchListPoints add constraint watchListPointsFk2 foreign key (dataPointId) references dataPoints(id);");

       final String watchListUsersSQL = ""
       		+ "create table watchListUsers ("
       			+ "watchListId int not null,"
       			+ "userId int not null,"
       			+ "accessType int not null,"
       			+ "primary key (watchListId, userId)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(watchListUsersSQL);
       jdbcTemplate.execute("alter table watchListUsers add constraint watchListUsersFk1 foreign key (watchListId) references watchLists(id) on delete cascade;");
       jdbcTemplate.execute("alter table watchListUsers add constraint watchListUsersFk2 foreign key (userId) references users(id) on delete cascade;");
       
       //Point event detectors
       
       final String pointEventDetectorsSQL = ""
       		+ "create table pointEventDetectors ("
       			+ "id int not null auto_increment,"
       			+ "xid varchar(50) not null,"
       			+ "alias varchar(255),"
       			+ "dataPointId int not null,"
       			+ "detectorType int not null,"
       			+ "alarmLevel int not null,"
       			+ "stateLimit double,"
       			+ "duration int,"
       			+ "durationType int,"
       			+ "binaryState char(1),"
       			+ "multistateState int,"
       			+ "changeCount int,"
       			+ "alphanumericState varchar(128),"
       			+ "weight double,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";

       jdbcTemplate.execute(pointEventDetectorsSQL);
       jdbcTemplate.execute("alter table pointEventDetectors add constraint pointEventDetectorsUn1 unique (xid, dataPointId);");
       jdbcTemplate.execute("alter table pointEventDetectors add constraint pointEventDetectorsFk1 foreign key (dataPointId) references dataPoints(id);");

       // Events

       final String eventsSQL = ""
       		+ "create table events ("
       			+ "id int not null auto_increment,"
       			+ "typeId int not null,"
       			+ "typeRef1 int not null,"
       			+ "typeRef2 int not null,"
       			+ "activeTs bigint not null,"
       			+ "rtnApplicable char(1) not null,"
       			+ "rtnTs bigint,rtnCause int,"
       			+ "alarmLevel int not null,"
       			+ "message longtext,"
       			+ "ackTs bigint,"
       			+ "ackUserId int,"
       			+ "alternateAckSource int,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(eventsSQL);
       jdbcTemplate.execute("alter table events add constraint eventsFk1 foreign key (ackUserId) references users(id);");
       
       final String userEventsSQL = ""
       		+ "create table userEvents ("
       			+ "eventId int not null,"
       			+ "userId int not null,"
       			+ "silenced char(1) not null,"
       			+ "primary key (eventId, userId)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(userEventsSQL);
       jdbcTemplate.execute("alter table userEvents add constraint userEventsFk1 foreign key (eventId) references events(id) on delete cascade;");
       jdbcTemplate.execute("alter table userEvents add constraint userEventsFk2 foreign key (userId) references users(id) on delete cascade;");

       // Event handlers
       
       final String eventHandlersSQL = ""
       		+ "create table eventHandlers ("
       			+ "id int not null auto_increment,"
       			+ "xid varchar(50) not null,"
       			+ "alias varchar(255),"
       			//+ " -- Event type, see events  "
       			+ "eventTypeId int not null,"
       			+ "eventTypeRef1 int not null,"
       			+ "eventTypeRef2 int not null,"
       			+ "data longblob not null,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       		
       jdbcTemplate.execute(eventHandlersSQL);
       jdbcTemplate.execute("alter table eventHandlers add constraint eventHandlersUn1 unique (xid);");
       
       final String scheduledEventsSQL = ""
       		+ "create table scheduledEvents ("
       			+ "id int not null auto_increment,"
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
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(scheduledEventsSQL);
       jdbcTemplate.execute("alter table scheduledEvents add constraint scheduledEventsUn1 unique (xid);");
       
       // Point Hierarchy
       
       final String pointHierarchySQL = ""
       		+ "create table pointHierarchy ("
       			+ "id int not null auto_increment,"
       			+ "parentId int,"
       			+ "name varchar(100),"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";

       jdbcTemplate.execute(pointHierarchySQL);

       // Compound events detectors

       final String compoundEventsDetectorsSQL = ""
       			+ "create table compoundEventDetectors ("
       				+ "id int not null auto_increment,"
       				+ "xid varchar(50) not null,"
       				+ "name varchar(100),"
       				+ "alarmLevel int not null,"
       				+ "returnToNormal char(1) not null,"
       				+ "disabled char(1) not null,"
       				+ "conditionText varchar(256) not null,"
       			+ "primary key (id)) ENGINE=InnoDB;";
       
       jdbcTemplate.execute(compoundEventsDetectorsSQL);
       jdbcTemplate.execute("alter table compoundEventDetectors add constraint compoundEventDetectorsUn1 unique (xid);");

       // Reports
       
       final String reportsSQL = ""
       		+ "create table reports ("
       			+ "id int not null auto_increment,"
       			+ "userId int not null,"
       			+ "name varchar(100) not null,"
       			+ "data longblob not null,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportsSQL);
       jdbcTemplate.execute("alter table reports add constraint reportsFk1 foreign key (userId) references users(id) on delete cascade;");

       final String reportInstancesSQL = ""
       		+ "create table reportInstances ("
       			+ "id int not null auto_increment,"
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
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportInstancesSQL);
       jdbcTemplate.execute("alter table reportInstances add constraint reportInstancesFk1 foreign key (userId) references users(id) on delete cascade;");
       
       final String reportInstancePointsSQL = ""
       		+ "create table reportInstancePoints ("
       			+ "id int not null auto_increment,"
       			+ "reportInstanceId int not null,"
       			+ "dataSourceName varchar(40) not null,"
       			+ "pointName varchar(100) not null,"
       			+ "dataType int not null,"
       			+ "startValue varchar(4096),"
       			+ "textRenderer longblob,"
       			+ "colour varchar(6),"
       			+ "consolidatedChart char(1),"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportInstancePointsSQL);
       jdbcTemplate.execute("alter table reportInstancePoints add constraint reportInstancePointsFk1 foreign key (reportInstanceId) references reportInstances(id) on delete cascade;");

       final String reportInstanceDataSQL = ""
       		+ "create table reportInstanceData ("
       			+ "pointValueId bigint not null,"
       			+ "reportInstancePointId int not null,"
       			+ "pointValue double,"
       			+ "ts bigint not null,"
       			+ "primary key (pointValueId, reportInstancePointId)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportInstanceDataSQL);
       jdbcTemplate.execute("alter table reportInstanceData add constraint reportInstanceDataFk1 foreign key (reportInstancePointId) references reportInstancePoints(id) on delete cascade;");

       final String reportInstanceDataAnnotationsSQL = ""
       		+ "create table reportInstanceDataAnnotations ("
       			+ "pointValueId bigint not null,"
       			+ "reportInstancePointId int not null,"
       			+ "textPointValueShort varchar(128),"
       			+ "textPointValueLong longtext,"
       			+ "sourceValue varchar(128),"
       			+ "primary key (pointValueId,"
       			+ "reportInstancePointId)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportInstanceDataAnnotationsSQL);
       jdbcTemplate.execute("alter table reportInstanceDataAnnotations add constraint reportInstanceDataAnnotationsFk1 foreign key (pointValueId, reportInstancePointId) references reportInstanceData(pointValueId, reportInstancePointId) on delete cascade;");

       final String reportInstanceEventsSQL = ""
       		+ "create table reportInstanceEvents ("
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
       			+ "message longtext,"
       			+ "ackTs bigint,"
       			+ "ackUsername varchar(40),"
       			+ "alternateAckSource int,"
       			+ "primary key (eventId, reportInstanceId)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportInstanceEventsSQL);
       jdbcTemplate.execute("alter table reportInstanceEvents add constraint reportInstanceEventsFk1 foreign key (reportInstanceId) references reportInstances(id) on delete cascade;");
       
       final String reportInstaceUserCommentsSQL = ""
       		+ "create table reportInstanceUserComments ("
       			+ "reportInstanceId int not null,"
       			+ "username varchar(40),"
       			+ "commentType int not null,"
       			+ "typeKey int not null,"
       			+ "ts bigint not null,"
       			+ "commentText varchar(1024) not null"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(reportInstaceUserCommentsSQL);
       jdbcTemplate.execute("alter table reportInstanceUserComments add constraint reportInstanceUserCommentsFk1 foreign key (reportInstanceId) references reportInstances(id) on delete cascade;");

       //Publishers
       
       final String publishersSQL = ""
       		+ "create table publishers ("
       			+ "id int not null auto_increment,"
       			+ "xid varchar(50) not null,"
       			+ "data longblob not null,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(publishersSQL);
       jdbcTemplate.execute("alter table publishers add constraint publishersUn1 unique (xid);");

       // Point Links
       
       final String pointLinksSQL = ""
       		+ "create table pointLinks ("
       			+ "id int not null auto_increment,"
       			+ "xid varchar(50) not null,"
       			+ "sourcePointId int not null,"
       			+ "targetPointId int not null,"
       			+ "script longtext,"
       			+ "eventType int not null,"
       			+ "disabled char(1) not null,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(pointLinksSQL);
       jdbcTemplate.execute("alter table pointLinks add constraint pointLinksUn1 unique (xid);");


       // Maintenance events
       
       final String maintenanceEventsSQL = ""
       		+ "create table maintenanceEvents ("
       			+ "id int not null auto_increment,"
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
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(maintenanceEventsSQL);
       jdbcTemplate.execute("alter table maintenanceEvents add constraint maintenanceEventsUn1 unique (xid);");
       jdbcTemplate.execute("alter table maintenanceEvents add constraint maintenanceEventsFk1 foreign key (dataSourceId) references dataSources(id);");

       // Event Detector Templates

       final String eventDetectorTemplatesSQL = ""
       		+ "CREATE TABLE eventDetectorTemplates ("
       			+ "id int NOT NULL auto_increment,"
       			+ "name varchar(255) NOT NULL,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(eventDetectorTemplatesSQL);
       
       final String templatesDetectorsSQL = ""
       		+ "CREATE TABLE templatesDetectors ("
       			+ "id int NOT NULL auto_increment,"
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
       			+ "primary key (id)"
       		+ ");";
       
       jdbcTemplate.execute(templatesDetectorsSQL);
       jdbcTemplate.execute("ALTER TABLE templatesDetectors ADD CONSTRAINT templatesDetectorsFk1 FOREIGN KEY (eventDetectorTemplateId) REFERENCES eventDetectorTemplates (id);");
       
       final String usersProfilesSQL = ""
       		+ "CREATE TABLE usersProfiles ("
       			+ "id int NOT NULL auto_increment,"
       			+ "xid varchar(50) not null,"
       			+ "name varchar(255) NOT NULL,"
       			+ "primary key (id)"
       		+ ") ENGINE=InnoDB;";

       jdbcTemplate.execute(usersProfilesSQL);
       jdbcTemplate.execute("alter table usersProfiles add constraint usersProfilesUn1 unique (xid);");

       // Data source permissions
       
       final String dataSourceUsersProfilesSQL = ""
       		+ "create table dataSourceUsersProfiles ("
       			+ "dataSourceId int not null,"
       			+ "userProfileId int not null"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(dataSourceUsersProfilesSQL);
       jdbcTemplate.execute("alter table dataSourceUsersProfiles add constraint dataSourceUsersProfilesFk1 foreign key (dataSourceId) references dataSources(id) on delete cascade;");
       jdbcTemplate.execute("alter table dataSourceUsersProfiles add constraint dataSourceUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");

       // Data point permissions
       
       final String dataPointUsersProfilesSQL = ""
       		+ "create table dataPointUsersProfiles ("
       			+ "dataPointId int not null,"
       			+ "userProfileId int not null,"
       			+ "permission int not null"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(dataPointUsersProfilesSQL);
       jdbcTemplate.execute("alter table dataPointUsersProfiles add constraint dataPointUsersProfilesFk1 foreign key (dataPointId) references dataPoints(id) on delete cascade;");
       jdbcTemplate.execute("alter table dataPointUsersProfiles add constraint dataPointUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");

       //Data source permissions
       
       final String usersUsersProfilesSQL = ""
       		+ "create table usersUsersProfiles ("
       			+ "userProfileId int not null,"
       			+ "userId int not null"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(usersUsersProfilesSQL);
       jdbcTemplate.execute("alter table usersUsersProfiles add constraint usersUsersProfilesFk1 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
       jdbcTemplate.execute("alter table usersUsersProfiles add constraint usersUsersProfilesFk2 foreign key (userId) references users(id) on delete cascade;");
       
       // Watchlist permissions
       
       final String watchListUsersProfilesSQL = ""
       		+ "create table watchListUsersProfiles ("
       			+ "watchlistId int not null,"
       			+ "userProfileId int not null,"
       			+ "permission int not null"
       		+ ") ENGINE=InnoDB;";
       		
       jdbcTemplate.execute(watchListUsersProfilesSQL);
       jdbcTemplate.execute("alter table watchListUsersProfiles add constraint watchlistUsersProfilesFk1 foreign key (watchlistId) references watchLists(id) on delete cascade;");
       jdbcTemplate.execute("alter table watchListUsersProfiles add constraint watchlistUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");

       // View Users Profiles 

       final String viewUsersProfilesSQL = ""
       		+ "create table viewUsersProfiles ("
       			+ "viewId int not null,"
       			+ "userProfileId int not null,"
       			+ "permission int not null"
       		+ ") ENGINE=InnoDB;";
       
       jdbcTemplate.execute(viewUsersProfilesSQL);
       jdbcTemplate.execute("alter table viewUsersProfiles add constraint viewUsersProfilesFk1 foreign key (viewId) references mangoViews(id) on delete cascade;");
       jdbcTemplate.execute("alter table viewUsersProfiles add constraint viewUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;");
       
       
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
		   jdbcTemplate.update("insert into systemSettings values (?,?)", new Object[]{"databaseSchemaVersion", Common.getVersion()});
       }
		   
   	   
       //@formatter:on
    }


}
