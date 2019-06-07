--    ScadaBR - Automação para Todos - http://www.scadabr.org.br
--    Copyright (C) 2009-2014 Sensorweb Ltda.
--    @author Diego R. Ferreira
--    
--    This program is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    This program is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with this program.  If not, see <http://www.gnu.org/licenses/>.
--
--

--
-- System settings
create table systemSettings (
  settingName varchar2(32) not null,
  settingValue clob,
  primary key (settingName)
) ;


--
-- Users
create table users (
  id number(10) not null,
  username varchar2(40) not null,
  password varchar2(30) not null,
  email varchar2(255) not null,
  phone varchar2(40),
  admin char(1) not null,
  disabled char(1) not null,
  lastLogin number(24),
  selectedWatchList number(10),
  homeUrl varchar2(255),
  receiveAlarmEmails number(10) not null,
  receiveOwnAuditEvents char(1) not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence users_seq start with 1 increment by 1;

create table userComments (
  userId number(10),
  commentType number(10) not null,
  typeKey number(10) not null,
  ts number(24) not null,
  commentText varchar2(1024) not null
) ;
alter table userComments add constraint userCommentsFk1 foreign key (userId) references users(id);


--
-- Mailing lists
create table mailingLists (
  id number(10) not null,
  xid varchar2(50) not null,
  name varchar2(40) not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence mailingLists_seq start with 1 increment by 1;

alter table mailingLists add constraint mailingListsUn1 unique (xid);

create table mailingListInactive (
  mailingListId number(10) not null,
  inactiveInterval number(10) not null
) ;
alter table mailingListInactive add constraint mailingListInactiveFk1 foreign key (mailingListId) 
  references mailingLists(id) on delete cascade;

create table mailingListMembers (
  mailingListId number(10) not null,
  typeId number(10) not null,
  userId number(10),
  address varchar2(255)
) ;
alter table mailingListMembers add constraint mailingListMembersFk1 foreign key (mailingListId) 
  references mailingLists(id) on delete cascade;




--
--
-- Data Sources
--
create table dataSources (
  id number(10) not null,
  xid varchar2(50) not null,
  name varchar2(40) not null,
  dataSourceType number(10) not null,
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence dataSources_seq start with 1 increment by 1;

alter table dataSources add constraint dataSourcesUn1 unique (xid);





-- Data source permissions
create table dataSourceUsers (
  dataSourceId number(10) not null,
  userId number(10) not null
) ;
alter table dataSourceUsers add constraint dataSourceUsersFk1 foreign key (dataSourceId) references dataSources(id);
alter table dataSourceUsers add constraint dataSourceUsersFk2 foreign key (userId) references users(id) on delete cascade;

--
--
-- Scripts
--
create table scripts (
  id number(10) not null,
  userId number(10) not null,
  xid varchar2(50) not null,
  name varchar2(40) not null,
  script clob not null,
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence scripts_seq start with 1 increment by 1;

alter table scripts add constraint scriptsUn1 unique (xid);
alter table scripts add constraint scriptsFk1 foreign key (userId) references users(id);

--
--
-- FlexProjects
--
create table flexProjects (
  id number(10) not null,
  name varchar2(40) not null,
  description varchar2(1024),
  xmlConfig clob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence flexProjects_seq start with 1 increment by 1;

--
--
-- Data Points
--
create table dataPoints (
  id number(10) not null,
  xid varchar2(50) not null,
  dataSourceId number(10) not null,
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence dataPoints_seq start with 1 increment by 1;

alter table dataPoints add constraint dataPointsUn1 unique (xid);
alter table dataPoints add constraint dataPointsFk1 foreign key (dataSourceId) references dataSources(id);


-- Data point permissions
create table dataPointUsers (
  dataPointId number(10) not null,
  userId number(10) not null,
  permission number(10) not null
) ;
alter table dataPointUsers add constraint dataPointUsersFk1 foreign key (dataPointId) references dataPoints(id);
alter table dataPointUsers add constraint dataPointUsersFk2 foreign key (userId) references users(id) on delete cascade;


--
--
-- Views
--
create table mangoViews (
  id number(10) not null,
  xid varchar2(50) not null,
  name varchar2(100) not null,
  background varchar2(255),
  userId number(10) not null,
  anonymousAccess number(10) not null,
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence mangoViews_seq start with 1 increment by 1;

alter table mangoViews add constraint mangoViewsUn1 unique (xid);
alter table mangoViews add constraint mangoViewsFk1 foreign key (userId) references users(id) on delete cascade;

create table mangoViewUsers (
  mangoViewId number(10) not null,
  userId number(10) not null,
  accessType number(10) not null,
  primary key (mangoViewId, userId)
) ;
alter table mangoViewUsers add constraint mangoViewUsersFk1 foreign key (mangoViewId) references mangoViews(id) on delete cascade;
alter table mangoViewUsers add constraint mangoViewUsersFk2 foreign key (userId) references users(id) on delete cascade;


--
--
-- Point Values (historical data)
--
create table pointValues (
  id number(24) not null,
  dataPointId number(10) not null,
  dataType number(10) not null,
  pointValue binary_double,
  ts number(24) not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence pointValues_seq start with 1 increment by 1;

alter table pointValues add constraint pointValuesFk1 foreign key (dataPointId) references dataPoints(id) on delete cascade;
create index pointValuesIdx1 on pointValues (ts, dataPointId);
create index pointValuesIdx2 on pointValues (dataPointId, ts);

create table pointValueAnnotations (
  pointValueId number(24) not null,
  textPointValueShort varchar2(128),
  textPointValueLong clob,
  sourceType number(5),
  sourceId number(10)
) ;
alter table pointValueAnnotations add constraint pointValueAnnotationsFk1 foreign key (pointValueId) 
  references pointValues(id) on delete cascade;


--
--
-- Watch list
--
create table watchLists (
  id number(10) not null,
  xid varchar2(50) not null,
  userId number(10) not null,
  name varchar2(50),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence watchLists_seq start with 1 increment by 1;

alter table watchLists add constraint watchListsUn1 unique (xid);
alter table watchLists add constraint watchListsFk1 foreign key (userId) references users(id) on delete cascade;

create table watchListPoints (
  watchListId number(10) not null,
  dataPointId number(10) not null,
  sortOrder number(10) not null
) ;
alter table watchListPoints add constraint watchListPointsFk1 foreign key (watchListId) references watchLists(id) on delete cascade;
alter table watchListPoints add constraint watchListPointsFk2 foreign key (dataPointId) references dataPoints(id);

create table watchListUsers (
  watchListId number(10) not null,
  userId number(10) not null,
  accessType number(10) not null,
  primary key (watchListId, userId)
) ;
alter table watchListUsers add constraint watchListUsersFk1 foreign key (watchListId) references watchLists(id) on delete cascade;
alter table watchListUsers add constraint watchListUsersFk2 foreign key (userId) references users(id) on delete cascade;


--
--
-- Point event detectors
--
create table pointEventDetectors (
  id number(10) not null,
  xid varchar2(50) not null,
  alias varchar2(255),
  dataPointId number(10) not null,
  detectorType number(10) not null,
  alarmLevel number(10) not null,
  stateLimit binary_double,
  duration number(10),
  durationType number(10),
  binaryState char(1),
  multistateState number(10),
  changeCount number(10),
  alphanumericState varchar2(128),
  weight binary_double,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence pointEventDetectors_seq start with 1 increment by 1;

alter table pointEventDetectors add constraint pointEventDetectorsUn1 unique (xid, dataPointId);
alter table pointEventDetectors add constraint pointEventDetectorsFk1 foreign key (dataPointId) 
  references dataPoints(id);


--
--
-- Events
--
create table events (
  id number(10) not null,
  typeId number(10) not null,
  typeRef1 number(10) not null,
  typeRef2 number(10) not null,
  activeTs number(24) not null,
  rtnApplicable char(1) not null,
  rtnTs number(24),
  rtnCause number(10),
  alarmLevel number(10) not null,
  message clob,
  ackTs number(24),
  ackUserId number(10),
  alternateAckSource number(10),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence events_seq start with 1 increment by 1;

alter table events add constraint eventsFk1 foreign key (ackUserId) references users(id);

create table userEvents (
  eventId number(10) not null,
  userId number(10) not null,
  silenced char(1) not null,
  primary key (eventId, userId)
) ;
alter table userEvents add constraint userEventsFk1 foreign key (eventId) references events(id) on delete cascade;
alter table userEvents add constraint userEventsFk2 foreign key (userId) references users(id) on delete cascade;


--
--
-- Event handlers
--
create table eventHandlers (
  id number(10) not null,
  xid varchar2(50) not null,
  alias varchar2(255),
  
  -- Event type, see events
  eventTypeId number(10) not null,
  eventTypeRef1 number(10) not null,
  eventTypeRef2 number(10) not null,
  
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence eventHandlers_seq start with 1 increment by 1;

alter table eventHandlers add constraint eventHandlersUn1 unique (xid);


--
--
-- Scheduled events
--
create table scheduledEvents (
  id number(10) not null,
  xid varchar2(50) not null,
  alias varchar2(255),
  alarmLevel number(10) not null,
  scheduleType number(10) not null,
  returnToNormal char(1) not null,
  disabled char(1) not null,
  activeYear number(10),
  activeMonth number(10),
  activeDay number(10),
  activeHour number(10),
  activeMinute number(10),
  activeSecond number(10),
  activeCron varchar2(25),
  inactiveYear number(10),
  inactiveMonth number(10),
  inactiveDay number(10),
  inactiveHour number(10),
  inactiveMinute number(10),
  inactiveSecond number(10),
  inactiveCron varchar2(25),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence scheduledEvents_seq start with 1 increment by 1;

alter table scheduledEvents add constraint scheduledEventsUn1 unique (xid);


--
--
-- Point Hierarchy
--
create table pointHierarchy (
  id number(10) not null,
  parentId number(10),
  name varchar2(100),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence pointHierarchy_seq start with 1 increment by 1;




--
--
-- Compound events detectors
--
create table compoundEventDetectors (
  id number(10) not null,
  xid varchar2(50) not null,
  name varchar2(100),
  alarmLevel number(10) not null,
  returnToNormal char(1) not null,
  disabled char(1) not null,
  conditionText varchar2(256) not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence compoundEventDetectors_seq start with 1 increment by 1;

alter table compoundEventDetectors add constraint compoundEventDetectorsUn1 unique (xid);


--
--
-- Reports
--
create table reports (
  id number(10) not null,
  userId number(10) not null,
  name varchar2(100) not null,
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence reports_seq start with 1 increment by 1;

alter table reports add constraint reportsFk1 foreign key (userId) references users(id) on delete cascade;

create table reportInstances (
  id number(10) not null,
  userId number(10) not null,
  name varchar2(100) not null,
  includeEvents number(10) not null,
  includeUserComments char(1) not null,
  reportStartTime number(24) not null,
  reportEndTime number(24) not null,
  runStartTime number(24),
  runEndTime number(24),
  recordCount number(10),
  preventPurge char(1),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence reportInstances_seq start with 1 increment by 1;

alter table reportInstances add constraint reportInstancesFk1 foreign key (userId) references users(id) on delete cascade;

create table reportInstancePoints (
  id number(10) not null,
  reportInstanceId number(10) not null,
  dataSourceName varchar2(40) not null,
  pointName varchar2(100) not null,
  dataType number(10) not null,
  startValue clob,
  textRenderer blob,
  colour varchar2(6),
  consolidatedChart char(1),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence reportInstancePoints_seq start with 1 increment by 1;

alter table reportInstancePoints add constraint reportInstancePointsFk1 foreign key (reportInstanceId) 
  references reportInstances(id) on delete cascade;

create table reportInstanceData (
  pointValueId number(24) not null,
  reportInstancePointId number(10) not null,
  pointValue binary_double,
  ts number(24) not null,
  primary key (pointValueId, reportInstancePointId)
) ;
alter table reportInstanceData add constraint reportInstanceDataFk1 foreign key (reportInstancePointId) 
  references reportInstancePoints(id) on delete cascade;

create table reportInstanceDataAnnotations (
  pointValueId number(24) not null,
  reportInstancePointId number(10) not null,
  textPointValueShort varchar2(128),
  textPointValueLong clob,
  sourceValue varchar2(128),
  primary key (pointValueId, reportInstancePointId)
) ;
alter table reportInstanceDataAnnotations add constraint reportInstanceDataAnnFk1 
  foreign key (pointValueId, reportInstancePointId) references reportInstanceData(pointValueId, reportInstancePointId) 
  on delete cascade;

create table reportInstanceEvents (
  eventId number(10) not null,
  reportInstanceId number(10) not null,
  typeId number(10) not null,
  typeRef1 number(10) not null,
  typeRef2 number(10) not null,
  activeTs number(24) not null,
  rtnApplicable char(1) not null,
  rtnTs number(24),
  rtnCause number(10),
  alarmLevel number(10) not null,
  message clob,
  ackTs number(24),
  ackUsername varchar2(40),
  alternateAckSource number(10),
  primary key (eventId, reportInstanceId)
) ;
alter table reportInstanceEvents add constraint reportInstanceEventsFk1 foreign key (reportInstanceId)
  references reportInstances(id) on delete cascade;

create table reportInstanceUserComments (
  reportInstanceId number(10) not null,
  username varchar2(40),
  commentType number(10) not null,
  typeKey number(10) not null,
  ts number(24) not null,
  commentText varchar2(1024) not null
) ;
alter table reportInstanceUserComments add constraint reportInstanceUserCommentsFk1 foreign key (reportInstanceId)
  references reportInstances(id) on delete cascade;


--
--
-- Publishers
--
create table publishers (
  id number(10) not null,
  xid varchar2(50) not null,
  data blob not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence publishers_seq start with 1 increment by 1;

alter table publishers add constraint publishersUn1 unique (xid);


--
--
-- Point links
--
create table pointLinks (
  id number(10) not null,
  xid varchar2(50) not null,
  sourcePointId number(10) not null,
  targetPointId number(10) not null,
  script clob,
  eventType number(10) not null,
  disabled char(1) not null,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence pointLinks_seq start with 1 increment by 1;

alter table pointLinks add constraint pointLinksUn1 unique (xid);


--
--
-- Maintenance events
--
create table maintenanceEvents (
  id number(10) not null,
  xid varchar2(50) not null,
  dataSourceId number(10) not null,
  alias varchar2(255),
  alarmLevel number(10) not null,
  scheduleType number(10) not null,
  disabled char(1) not null,
  activeYear number(10),
  activeMonth number(10),
  activeDay number(10),
  activeHour number(10),
  activeMinute number(10),
  activeSecond number(10),
  activeCron varchar2(25),
  inactiveYear number(10),
  inactiveMonth number(10),
  inactiveDay number(10),
  inactiveHour number(10),
  inactiveMinute number(10),
  inactiveSecond number(10),
  inactiveCron varchar2(25),
  primary key (id)
) ;

-- Generate ID using sequence and trigger
create sequence maintenanceEvents_seq start with 1 increment by 1;

alter table maintenanceEvents add constraint maintenanceEventsUn1 unique (xid);
alter table maintenanceEvents add constraint maintenanceEventsFk1 foreign key (dataSourceId) references dataSources(id);

--
--
-- Event Detector Templates
--
CREATE TABLE eventDetectorTemplates (
  id number(10) NOT NULL,
  name varchar2(255) NOT NULL,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE eventDetectorTemplates_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE templatesDetectors (
  id number(10) NOT NULL,
  xid varchar2(50) NOT NULL,
  alias varchar2(255),
  detectorType number(10) NOT NULL,
  alarmLevel number(10) NOT NULL,
  stateLimit BINARY_DOUBLE,
  duration number(10),
  durationType number(10),
  binaryState char(1),
  multistateState number(10),
  changeCount number(10),
  alphanumericState varchar2(128),
  weight binary_double,
  threshold binary_double,
  eventDetectorTemplateId number(10) NOT NULL,
  primary key (id)
);

-- Generate ID using sequence and trigger
CREATE SEQUENCE templatesDetectors_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE templatesDetectors ADD CONSTRAINT templatesDetectorsFk1 FOREIGN KEY (eventDetectorTemplateId) REFERENCES eventDetectorTemplates (id);

CREATE TABLE usersProfiles (
  id number(10) NOT NULL,
  xid varchar2(50) NOT NULL,
  name varchar2(255) NOT NULL,
  primary key (id)
) ;

-- Generate ID using sequence and trigger
CREATE SEQUENCE usersProfiles_seq START WITH 1 INCREMENT BY 1;

-- Data source permissions
create table dataSourceUsersProfiles (
  dataSourceId number(10) not null,
  userProfileId number(10) not null
) ;
alter table dataSourceUsersProfiles add constraint dataSourceUsersProfilesFk1 foreign key (dataSourceId) references dataSources(id) on delete cascade;
alter table dataSourceUsersProfiles add constraint dataSourceUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;

-- Data point permissions
create table dataPointUsersProfiles (
  dataPointId number(10) not null,
  userProfileId number(10) not null,
  permission number(10) not null
) ;
alter table dataPointUsersProfiles add constraint dataPointUsersProfilesFk1 foreign key (dataPointId) references dataPoints(id) on delete cascade;
alter table dataPointUsersProfiles add constraint dataPointUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;

-- Data source permissions
create table usersUsersProfiles (
  userProfileId number(10) not null,
  userId number(10) not null
) ;
alter table usersUsersProfiles add constraint usersUsersProfilesFk1 foreign key (userProfileId) references usersProfiles(id);
alter table usersUsersProfiles add constraint usersUsersProfilesFk2 foreign key (userId) references users(id) on delete cascade;

-- Watchlist permissions
create table watchlistUsersProfiles (
  watchlistId number(10) not null,
  userProfileId number(10) not null,
  permission number(10) not null
) ;
alter table watchlistUsersProfiles add constraint watchlistUsersProfilesFk1 foreign key (watchlistId) references watchLists(id) on delete cascade;
alter table watchlistUsersProfiles add constraint watchlistUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;

-- Watchlist permissions
create table viewUsersProfiles (
  viewId number(10) not null,
  userProfileId number(10) not null,
  permission number(10) not null
) ;
alter table viewUsersProfiles add constraint viewUsersProfilesFk1 foreign key (viewId) references mangoViews(id) on delete cascade;
alter table viewUsersProfiles add constraint viewUsersProfilesFk2 foreign key (userProfileId) references usersProfiles(id) on delete cascade;

-- Setting delimiter for triggers
DELIMITER $$ 

create or replace trigger users_seq_tr
 before insert on users for each row
 when (new.id is null)
begin
 select users_seq.nextval into :new.id from dual;
end; $$

create or replace trigger mailingLists_seq_tr
 before insert on mailingLists for each row
 when (new.id is null)
begin
 select mailingLists_seq.nextval into :new.id from dual;
end; $$

create or replace trigger dataSources_seq_tr
 before insert on dataSources for each row
 when (new.id is null)
begin
 select dataSources_seq.nextval into :new.id from dual;
end; $$

create or replace trigger scripts_seq_tr
 before insert on scripts for each row
 when (new.id is null)
begin
 select scripts_seq.nextval into :new.id from dual;
end; $$

create or replace trigger flexProjects_seq_tr
 before insert on flexProjects for each row
 when (new.id is null)
begin
 select flexProjects_seq.nextval into :new.id from dual;
end; $$

create or replace trigger dataPoints_seq_tr
 before insert on dataPoints for each row
 when (new.id is null)
begin
 select dataPoints_seq.nextval into :new.id from dual;
end; $$

create or replace trigger mangoViews_seq_tr
 before insert on mangoViews for each row
 when (new.id is null)
begin
 select mangoViews_seq.nextval into :new.id from dual;
end; $$

create or replace trigger pointValues_seq_tr
 before insert on pointValues for each row
 when (new.id is null)
begin
 select pointValues_seq.nextval into :new.id from dual;
end; $$

create or replace trigger watchLists_seq_tr
 before insert on watchLists for each row
 when (new.id is null)
begin
 select watchLists_seq.nextval into :new.id from dual;
end; $$

create or replace trigger pointEventDetectors_seq_tr
 before insert on pointEventDetectors for each row
 when (new.id is null)
begin
 select pointEventDetectors_seq.nextval into :new.id from dual;
end; $$

create or replace trigger events_seq_tr
 before insert on events for each row
 when (new.id is null)
begin
 select events_seq.nextval into :new.id from dual;
end; $$

create or replace trigger eventHandlers_seq_tr
 before insert on eventHandlers for each row
 when (new.id is null)
begin
 select eventHandlers_seq.nextval into :new.id from dual;
end; $$

create or replace trigger scheduledEvents_seq_tr
 before insert on scheduledEvents for each row
 when (new.id is null)
begin
 select scheduledEvents_seq.nextval into :new.id from dual;
end; $$

create or replace trigger pointHierarchy_seq_tr
 before insert on pointHierarchy for each row
 when (new.id is null)
begin
 select pointHierarchy_seq.nextval into :new.id from dual;
end; $$

create or replace trigger compoundEventDetectors_seq_tr
 before insert on compoundEventDetectors for each row
 when (new.id is null)
begin
 select compoundEventDetectors_seq.nextval into :new.id from dual;
end; $$

create or replace trigger reports_seq_tr
 before insert on reports for each row
 when (new.id is null)
begin
 select reports_seq.nextval into :new.id from dual;
end; $$

create or replace trigger reportInstances_seq_tr
 before insert on reportInstances for each row
 when (new.id is null)
begin
 select reportInstances_seq.nextval into :new.id from dual;
end; $$

create or replace trigger reportInstancePoints_seq_tr
 before insert on reportInstancePoints for each row
 when (new.id is null)
begin
 select reportInstancePoints_seq.nextval into :new.id from dual;
end; $$

create or replace trigger publishers_seq_tr
 before insert on publishers for each row
 when (new.id is null)
begin
 select publishers_seq.nextval into :new.id from dual;
end; $$

create or replace trigger pointLinks_seq_tr
 before insert on pointLinks for each row
 when (new.id is null)
begin
 select pointLinks_seq.nextval into :new.id from dual;
end; $$

create or replace trigger maintenanceEvents_seq_tr
 before insert on maintenanceEvents for each row
 when (new.id is null) 
begin
 select maintenanceEvents_seq.nextval into :new.id from dual;
end; $$

CREATE OR REPLACE TRIGGER eventDetectorTemplates_seq_tr
 BEFORE INSERT ON eventDetectorTemplates FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT eventDetectorTemplates_seq.NEXTVAL INTO :NEW.id FROM DUAL;
end; $$


CREATE OR REPLACE TRIGGER templatesDetectors_seq_tr
 BEFORE INSERT ON templatesDetectors FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT templatesDetectors_seq.NEXTVAL INTO :NEW.id FROM DUAL;
end; $$

CREATE OR REPLACE TRIGGER usersProfiles_seq_tr
 BEFORE INSERT ON usersProfiles FOR EACH ROW
 WHEN (NEW.id IS NULL)
BEGIN
 SELECT usersProfiles_seq.NEXTVAL INTO :NEW.id FROM DUAL;
end; $$


