--
--    Mango - Open Source M2M - http://mango.serotoninsoftware.com
--    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
--    @author Matthew Lohbihler
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

-- Run this script using com.serotonin.mango.db.ScriptRunner to create 
-- SCADABR tables in a oracle schema

-- Creating Sequence events_id_SEQ ...
CREATE SEQUENCE  events_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence pointeventdetectors_id_SEQ ...
CREATE SEQUENCE  pointeventdetectors_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence scheduledevents_id_SEQ ...
CREATE SEQUENCE  scheduledevents_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence scripts_id_SEQ ...
CREATE SEQUENCE  scripts_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence pointlinks_id_SEQ ...
CREATE SEQUENCE  pointlinks_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence publishers_id_SEQ ...
CREATE SEQUENCE  publishers_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence users_id_SEQ ...
CREATE SEQUENCE  users_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence flexprojects_id_SEQ ...
CREATE SEQUENCE  flexprojects_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence eventhandlers_id_SEQ ...
CREATE SEQUENCE  eventhandlers_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence datasources_id_SEQ ...
CREATE SEQUENCE  datasources_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence mangoviews_id_SEQ ...
CREATE SEQUENCE  mangoviews_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence mailinglists_id_SEQ ...
CREATE SEQUENCE  mailinglists_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence watchlists_id_SEQ ...
CREATE SEQUENCE  watchlists_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence reportinstancepoints_id_SEQ ...
CREATE SEQUENCE  reportinstancepoints_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence reportinstances_id_SEQ ...
CREATE SEQUENCE  reportinstances_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence compoundeventdetectors_id_SEQ ...
CREATE SEQUENCE  compoundeventdetectors_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence pointvalues_id_SEQ ...
CREATE SEQUENCE  pointvalues_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence datapoints_id_SEQ ...
CREATE SEQUENCE  datapoints_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence pointhierarchy_id_SEQ ...
CREATE SEQUENCE  pointhierarchy_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence reports_id_SEQ ...
CREATE SEQUENCE  reports_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Sequence maintenanceevents_id_SEQ ...
CREATE SEQUENCE  maintenanceevents_id_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Creating Table mailinglists ...
CREATE TABLE mailinglists (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  name VARCHAR2(40 CHAR) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_14 on table mailinglists ... 
ALTER TABLE mailinglists
ADD CONSTRAINT PRIMARY_14 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint mailingListsUn1 on table mailinglists
ALTER TABLE mailinglists
ADD CONSTRAINT mailingListsUn1 UNIQUE (
  xid
)
ENABLE
;

-- Creating Table maintenanceevents ...
CREATE TABLE maintenanceevents (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  dataSourceId NUMBER(10,0) NOT NULL,
  alias VARCHAR2(255 CHAR),
  alarmLevel NUMBER(10,0) NOT NULL,
  scheduleType NUMBER(10,0) NOT NULL,
  disabled CHAR(1 CHAR) NOT NULL,
  activeYear NUMBER(10,0),
  activeMonth NUMBER(10,0),
  activeDay NUMBER(10,0),
  activeHour NUMBER(10,0),
  activeMinute NUMBER(10,0),
  activeSecond NUMBER(10,0),
  activeCron VARCHAR2(25 CHAR),
  inactiveYear NUMBER(10,0),
  inactiveMonth NUMBER(10,0),
  inactiveDay NUMBER(10,0),
  inactiveHour NUMBER(10,0),
  inactiveMinute NUMBER(10,0),
  inactiveSecond NUMBER(10,0),
  inactiveCron VARCHAR2(25 CHAR)
);

-- Creating Unique Constraint maintenanceEventsUn1 on table maintenanceevents
ALTER TABLE maintenanceevents
ADD CONSTRAINT maintenanceEventsUn1 UNIQUE (
  xid
)
ENABLE
;
-- Creating Primary Key Constraint PRIMARY_12 on table maintenanceevents ... 
ALTER TABLE maintenanceevents
ADD CONSTRAINT PRIMARY_12 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index maintenanceEventsFk1 on maintenanceevents ...
CREATE INDEX maintenanceEventsFk1 ON maintenanceevents
(
  dataSourceId
) 
;

-- Creating Table mangoviews ...
CREATE TABLE mangoviews (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  name VARCHAR2(100 CHAR) NOT NULL,
  background VARCHAR2(255 CHAR),
  userId NUMBER(10,0) NOT NULL,
  anonymousAccess NUMBER(10,0) NOT NULL,
  data BLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_16 on table mangoviews ... 
ALTER TABLE mangoviews
ADD CONSTRAINT PRIMARY_16 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint mangoViewsUn1 on table mangoviews
ALTER TABLE mangoviews
ADD CONSTRAINT mangoViewsUn1 UNIQUE (
  xid
)
ENABLE
;
-- Creating Index mangoViewsFk1 on mangoviews ...
CREATE INDEX mangoViewsFk1 ON mangoviews
(
  userId
) 
;

-- Creating Table mangoviewusers ...
CREATE TABLE mangoviewusers (
  mangoViewId NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  accessType NUMBER(10,0) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_13 on table mangoviewusers ... 
ALTER TABLE mangoviewusers
ADD CONSTRAINT PRIMARY_13 PRIMARY KEY
(
  mangoViewId,
  userId
)
ENABLE
;
-- Creating Index mangoViewUsersFk2 on mangoviewusers ...
CREATE INDEX mangoViewUsersFk2 ON mangoviewusers
(
  userId
) 
;

-- Creating Table pointeventdetectors ...
CREATE TABLE pointeventdetectors (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  alias VARCHAR2(255 CHAR),
  dataPointId NUMBER(10,0) NOT NULL,
  detectorType NUMBER(10,0) NOT NULL,
  alarmLevel NUMBER(10,0) NOT NULL,
  stateLimit FLOAT,
  duration NUMBER(10,0),
  durationType NUMBER(10,0),
  binaryState CHAR(1 CHAR),
  multistateState NUMBER(10,0),
  changeCount NUMBER(10,0),
  alphanumericState VARCHAR2(128 CHAR),
  weight FLOAT
);

-- Creating Primary Key Constraint PRIMARY_17 on table pointeventdetectors ... 
ALTER TABLE pointeventdetectors
ADD CONSTRAINT PRIMARY_17 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint pointEventDetectorsUn1 on table pointeventdetectors
ALTER TABLE pointeventdetectors
ADD CONSTRAINT pointEventDetectorsUn1 UNIQUE (
  xid,
  dataPointId
)
ENABLE
;
-- Creating Index pointEventDetectorsFk1 on pointeventdetectors ...
CREATE INDEX pointEventDetectorsFk1 ON pointeventdetectors
(
  dataPointId
) 
;

-- Creating Table pointhierarchy ...
CREATE TABLE pointhierarchy (
  id NUMBER(10,0) NOT NULL,
  parentId NUMBER(10,0),
  name VARCHAR2(100 CHAR)
);

-- Creating Primary Key Constraint PRIMARY_18 on table pointhierarchy ... 
ALTER TABLE pointhierarchy
ADD CONSTRAINT PRIMARY_18 PRIMARY KEY
(
  id
)
ENABLE
;

-- Creating Table pointlinks ...
CREATE TABLE pointlinks (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  sourcePointId NUMBER(10,0) NOT NULL,
  targetPointId NUMBER(10,0) NOT NULL,
  script CLOB,
  eventType NUMBER(10,0) NOT NULL,
  disabled CHAR(1 CHAR) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_19 on table pointlinks ... 
ALTER TABLE pointlinks
ADD CONSTRAINT PRIMARY_19 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint pointLinksUn1 on table pointlinks
ALTER TABLE pointlinks
ADD CONSTRAINT pointLinksUn1 UNIQUE (
  xid
)
ENABLE
;

-- Creating Table pointvalueannotations ...
CREATE TABLE pointvalueannotations (
  pointValueId NUMBER(24,0) NOT NULL,
  textPointValueShort VARCHAR2(128 CHAR),
  textPointValueLong CLOB,
  sourceType NUMBER(5,0),
  sourceId NUMBER(10,0)
);

-- Creating Index pointValueAnnotationsFk1 on pointvalueannotations ...
CREATE INDEX pointValueAnnotationsFk1 ON pointvalueannotations
(
  pointValueId
) 
;

-- Creating Table compoundeventdetectors ...
CREATE TABLE compoundeventdetectors (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  name VARCHAR2(100 CHAR),
  alarmLevel NUMBER(10,0) NOT NULL,
  returnToNormal CHAR(1 CHAR) NOT NULL,
  disabled CHAR(1 CHAR) NOT NULL,
  conditionText VARCHAR2(256 CHAR) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_20 on table compoundeventdetectors ... 
ALTER TABLE compoundeventdetectors
ADD CONSTRAINT PRIMARY_20 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint compoundEventDetectorsUn1 on table compoundeventdetectors
ALTER TABLE compoundeventdetectors
ADD CONSTRAINT compoundEventDetectorsUn1 UNIQUE (
  xid
)
ENABLE
;

-- Creating Table datapoints ...
CREATE TABLE datapoints (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  dataSourceId NUMBER(10,0) NOT NULL,
  data BLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_21 on table datapoints ... 
ALTER TABLE datapoints
ADD CONSTRAINT PRIMARY_21 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint dataPointsUn1 on table datapoints
ALTER TABLE datapoints
ADD CONSTRAINT dataPointsUn1 UNIQUE (
  xid
)
ENABLE
;
-- Creating Index dataPointsFk1 on datapoints ...
CREATE INDEX dataPointsFk1 ON datapoints
(
  dataSourceId
) 
;

-- Creating Table datapointusers ...
CREATE TABLE datapointusers (
  dataPointId NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  permission NUMBER(10,0) NOT NULL
);

-- Creating Index dataPointUsersFk1 on datapointusers ...
CREATE INDEX dataPointUsersFk1 ON datapointusers
(
  dataPointId
) 
;
-- Creating Index dataPointUsersFk2 on datapointusers ...
CREATE INDEX dataPointUsersFk2 ON datapointusers
(
  userId
) 
;

-- Creating Table datasources ...
CREATE TABLE datasources (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  name VARCHAR2(40 CHAR) NOT NULL,
  dataSourceType NUMBER(10,0) NOT NULL,
  data BLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_22 on table datasources ... 
ALTER TABLE datasources
ADD CONSTRAINT PRIMARY_22 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint dataSourcesUn1 on table datasources
ALTER TABLE datasources
ADD CONSTRAINT dataSourcesUn1 UNIQUE (
  xid
)
ENABLE
;

-- Creating Table datasourceusers ...
CREATE TABLE datasourceusers (
  dataSourceId NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL
);

-- Creating Index dataSourceUsersFk1 on datasourceusers ...
CREATE INDEX dataSourceUsersFk1 ON datasourceusers
(
  dataSourceId
) 
;
-- Creating Index dataSourceUsersFk2 on datasourceusers ...
CREATE INDEX dataSourceUsersFk2 ON datasourceusers
(
  userId
) 
;

-- Creating Table eventhandlers ...
CREATE TABLE eventhandlers (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  alias VARCHAR2(255 CHAR),
  eventTypeId NUMBER(10,0) NOT NULL,
  eventTypeRef1 NUMBER(10,0) NOT NULL,
  eventTypeRef2 NUMBER(10,0) NOT NULL,
  data BLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_23 on table eventhandlers ... 
ALTER TABLE eventhandlers
ADD CONSTRAINT PRIMARY_23 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint eventHandlersUn1 on table eventhandlers
ALTER TABLE eventhandlers
ADD CONSTRAINT eventHandlersUn1 UNIQUE (
  xid
)
ENABLE
;

-- Creating Table events ...
CREATE TABLE events (
  id NUMBER(10,0) NOT NULL,
  typeId NUMBER(10,0) NOT NULL,
  typeRef1 NUMBER(10,0) NOT NULL,
  typeRef2 NUMBER(10,0) NOT NULL,
  activeTs NUMBER(24,0) NOT NULL,
  rtnApplicable CHAR(1 CHAR) NOT NULL,
  rtnTs NUMBER(24,0),
  rtnCause NUMBER(10,0),
  alarmLevel NUMBER(10,0) NOT NULL,
  message CLOB,
  ackTs NUMBER(24,0),
  ackUserId NUMBER(10,0),
  alternateAckSource NUMBER(10,0)
);

-- Creating Primary Key Constraint PRIMARY_24 on table events ... 
ALTER TABLE events
ADD CONSTRAINT PRIMARY_24 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index eventsFk1 on events ...
CREATE INDEX eventsFk1 ON events
(
  ackUserId
) 
;

-- Creating Table flexprojects ...
CREATE TABLE flexprojects (
  id NUMBER(10,0) NOT NULL,
  name VARCHAR2(40 CHAR) NOT NULL,
  description VARCHAR2(1024 CHAR),
  xmlConfig CLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_25 on table flexprojects ... 
ALTER TABLE flexprojects
ADD CONSTRAINT PRIMARY_25 PRIMARY KEY
(
  id
)
ENABLE
;

-- Creating Table mailinglistinactive ...
CREATE TABLE mailinglistinactive (
  mailingListId NUMBER(10,0) NOT NULL,
  inactiveInterval NUMBER(10,0) NOT NULL
);

-- Creating Index mailingListInactiveFk1 on mailinglistinactive ...
CREATE INDEX mailingListInactiveFk1 ON mailinglistinactive
(
  mailingListId
) 
;

-- Creating Table mailinglistmembers ...
CREATE TABLE mailinglistmembers (
  mailingListId NUMBER(10,0) NOT NULL,
  typeId NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0),
  address VARCHAR2(255 CHAR)
);

-- Creating Index mailingListMembersFk1 on mailinglistmembers ...
CREATE INDEX mailingListMembersFk1 ON mailinglistmembers
(
  mailingListId
) 
;

-- Creating Table pointvalues ...
CREATE TABLE pointvalues (
  id NUMBER(24,0) NOT NULL,
  dataPointId NUMBER(10,0) NOT NULL,
  dataType NUMBER(10,0) NOT NULL,
  pointValue FLOAT,
  ts NUMBER(24,0) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_26 on table pointvalues ... 
ALTER TABLE pointvalues
ADD CONSTRAINT PRIMARY_26 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index pointValuesIdx1 on pointvalues ...
CREATE INDEX pointValuesIdx1 ON pointvalues
(
  ts,
  dataPointId
) 
;
-- Creating Index pointValuesIdx2 on pointvalues ...
CREATE INDEX pointValuesIdx2 ON pointvalues
(
  dataPointId,
  ts
) 
;

-- Creating Table publishers ...
CREATE TABLE publishers (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  data BLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_2 on table publishers ... 
ALTER TABLE publishers
ADD CONSTRAINT PRIMARY_2 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Unique Constraint publishersUn1 on table publishers
ALTER TABLE publishers
ADD CONSTRAINT publishersUn1 UNIQUE (
  xid
)
ENABLE
;

-- Creating Table reportinstancedata ...
CREATE TABLE reportinstancedata (
  pointValueId NUMBER(24,0) NOT NULL,
  reportInstancePointId NUMBER(10,0) NOT NULL,
  pointValue FLOAT,
  ts NUMBER(24,0) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY on table reportinstancedata ... 
ALTER TABLE reportinstancedata
ADD CONSTRAINT PRIMARY PRIMARY KEY
(
  pointValueId,
  reportInstancePointId
)
ENABLE
;
-- Creating Index reportInstanceDataFk1 on reportinstancedata ...
CREATE INDEX reportInstanceDataFk1 ON reportinstancedata
(
  reportInstancePointId
) 
;

-- Creating Table reportinstancedataannotations ...
CREATE TABLE reportinstancedataannotations (
  pointValueId NUMBER(24,0) NOT NULL,
  reportInstancePointId NUMBER(10,0) NOT NULL,
  textPointValueShort VARCHAR2(128 CHAR),
  textPointValueLong CLOB,
  sourceValue VARCHAR2(128 CHAR)
);

-- Creating Primary Key Constraint PRIMARY_1 on table reportinstancedataannotations ... 
ALTER TABLE reportinstancedataannotations
ADD CONSTRAINT PRIMARY_1 PRIMARY KEY
(
  pointValueId,
  reportInstancePointId
)
ENABLE
;

-- Creating Table reportinstanceevents ...
CREATE TABLE reportinstanceevents (
  eventId NUMBER(10,0) NOT NULL,
  reportInstanceId NUMBER(10,0) NOT NULL,
  typeId NUMBER(10,0) NOT NULL,
  typeRef1 NUMBER(10,0) NOT NULL,
  typeRef2 NUMBER(10,0) NOT NULL,
  activeTs NUMBER(24,0) NOT NULL,
  rtnApplicable CHAR(1 CHAR) NOT NULL,
  rtnTs NUMBER(24,0),
  rtnCause NUMBER(10,0),
  alarmLevel NUMBER(10,0) NOT NULL,
  message CLOB,
  ackTs NUMBER(24,0),
  ackUsername VARCHAR2(40 CHAR),
  alternateAckSource NUMBER(10,0)
);

-- Creating Primary Key Constraint PRIMARY_27 on table reportinstanceevents ... 
ALTER TABLE reportinstanceevents
ADD CONSTRAINT PRIMARY_27 PRIMARY KEY
(
  eventId,
  reportInstanceId
)
ENABLE
;
-- Creating Index reportInstanceEventsFk1 on reportinstanceevents ...
CREATE INDEX reportInstanceEventsFk1 ON reportinstanceevents
(
  reportInstanceId
) 
;

-- Creating Table reportinstancepoints ...
CREATE TABLE reportinstancepoints (
  id NUMBER(10,0) NOT NULL,
  reportInstanceId NUMBER(10,0) NOT NULL,
  dataSourceName VARCHAR2(40 CHAR) NOT NULL,
  pointName VARCHAR2(100 CHAR) NOT NULL,
  dataType NUMBER(10,0) NOT NULL,
  startValue CLOB,
  textRenderer BLOB,
  colour VARCHAR2(6 CHAR),
  consolidatedChart CHAR(1 CHAR)
);

-- Creating Primary Key Constraint PRIMARY_3 on table reportinstancepoints ... 
ALTER TABLE reportinstancepoints
ADD CONSTRAINT PRIMARY_3 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index reportInstancePointsFk1 on reportinstancepoints ...
CREATE INDEX reportInstancePointsFk1 ON reportinstancepoints
(
  reportInstanceId
) 
;

-- Creating Table reportinstances ...
CREATE TABLE reportinstances (
  id NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  name VARCHAR2(100 CHAR) NOT NULL,
  includeEvents NUMBER(10,0) NOT NULL,
  includeUserComments CHAR(1 CHAR) NOT NULL,
  reportStartTime NUMBER(24,0) NOT NULL,
  reportEndTime NUMBER(24,0) NOT NULL,
  runStartTime NUMBER(24,0),
  runEndTime NUMBER(24,0),
  recordCount NUMBER(10,0),
  preventPurge CHAR(1 CHAR)
);

-- Creating Primary Key Constraint PRIMARY_4 on table reportinstances ... 
ALTER TABLE reportinstances
ADD CONSTRAINT PRIMARY_4 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index reportInstancesFk1 on reportinstances ...
CREATE INDEX reportInstancesFk1 ON reportinstances
(
  userId
) 
;

-- Creating Table reportinstanceusercomments ...
CREATE TABLE reportinstanceusercomments (
  reportInstanceId NUMBER(10,0) NOT NULL,
  username VARCHAR2(40 CHAR),
  commentType NUMBER(10,0) NOT NULL,
  typeKey NUMBER(10,0) NOT NULL,
  ts NUMBER(24,0) NOT NULL,
  commentText VARCHAR2(1024 CHAR) NOT NULL
);

-- Creating Index reportInstanceUserCommentsFk1 on reportinstanceusercomments ...
CREATE INDEX reportInstanceUserCommentsFk1 ON reportinstanceusercomments
(
  reportInstanceId
) 
;

-- Creating Table reports ...
CREATE TABLE reports (
  id NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  name VARCHAR2(100 CHAR) NOT NULL,
  data BLOB NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_5 on table reports ... 
ALTER TABLE reports
ADD CONSTRAINT PRIMARY_5 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index reportsFk1 on reports ...
CREATE INDEX reportsFk1 ON reports
(
  userId
) 
;

-- Creating Table scheduledevents ...
CREATE TABLE scheduledevents (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  alias VARCHAR2(255 CHAR),
  alarmLevel NUMBER(10,0) NOT NULL,
  scheduleType NUMBER(10,0) NOT NULL,
  returnToNormal CHAR(1 CHAR) NOT NULL,
  disabled CHAR(1 CHAR) NOT NULL,
  activeYear NUMBER(10,0),
  activeMonth NUMBER(10,0),
  activeDay NUMBER(10,0),
  activeHour NUMBER(10,0),
  activeMinute NUMBER(10,0),
  activeSecond NUMBER(10,0),
  activeCron VARCHAR2(25 CHAR),
  inactiveYear NUMBER(10,0),
  inactiveMonth NUMBER(10,0),
  inactiveDay NUMBER(10,0),
  inactiveHour NUMBER(10,0),
  inactiveMinute NUMBER(10,0),
  inactiveSecond NUMBER(10,0),
  inactiveCron VARCHAR2(25 CHAR)
);

-- Creating Unique Constraint scheduledEventsUn1 on table scheduledevents
ALTER TABLE scheduledevents
ADD CONSTRAINT scheduledEventsUn1 UNIQUE (
  xid
)
ENABLE
;
-- Creating Primary Key Constraint PRIMARY_6 on table scheduledevents ... 
ALTER TABLE scheduledevents
ADD CONSTRAINT PRIMARY_6 PRIMARY KEY
(
  id
)
ENABLE
;

-- Creating Table scripts ...
CREATE TABLE scripts (
  id NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  name VARCHAR2(40 CHAR) NOT NULL,
  script CLOB NOT NULL,
  data BLOB NOT NULL
);

-- Creating Unique Constraint scriptsUn1 on table scripts
ALTER TABLE scripts
ADD CONSTRAINT scriptsUn1 UNIQUE (
  xid
)
ENABLE
;
-- Creating Primary Key Constraint PRIMARY_7 on table scripts ... 
ALTER TABLE scripts
ADD CONSTRAINT PRIMARY_7 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index scriptsFk1 on scripts ...
CREATE INDEX scriptsFk1 ON scripts
(
  userId
) 
;

-- Creating Table systemsettings ...
CREATE TABLE systemsettings (
  settingName VARCHAR2(32 CHAR) NOT NULL,
  settingValue CLOB
);

-- Creating Primary Key Constraint PRIMARY_8 on table systemsettings ... 
ALTER TABLE systemsettings
ADD CONSTRAINT PRIMARY_8 PRIMARY KEY
(
  settingName
)
ENABLE
;

-- Creating Table usercomments ...
CREATE TABLE usercomments (
  userId NUMBER(10,0),
  commentType NUMBER(10,0) NOT NULL,
  typeKey NUMBER(10,0) NOT NULL,
  ts NUMBER(24,0) NOT NULL,
  commentText VARCHAR2(1024 CHAR) NOT NULL
);

-- Creating Index userCommentsFk1 on usercomments ...
CREATE INDEX userCommentsFk1 ON usercomments
(
  userId
) 
;

-- Creating Table userevents ...
CREATE TABLE userevents (
  eventId NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  silenced CHAR(1 CHAR) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_9 on table userevents ... 
ALTER TABLE userevents
ADD CONSTRAINT PRIMARY_9 PRIMARY KEY
(
  eventId,
  userId
)
ENABLE
;
-- Creating Index userEventsFk2 on userevents ...
CREATE INDEX userEventsFk2 ON userevents
(
  userId
) 
;

-- Creating Table users ...
CREATE TABLE users (
  id NUMBER(10,0) NOT NULL,
  username VARCHAR2(40 CHAR) NOT NULL,
  password VARCHAR2(30 CHAR) NOT NULL,
  email VARCHAR2(255 CHAR) NOT NULL,
  phone VARCHAR2(40 CHAR),
  admin CHAR(1 CHAR) NOT NULL,
  disabled CHAR(1 CHAR) NOT NULL,
  lastLogin NUMBER(24,0),
  selectedWatchList NUMBER(10,0),
  homeUrl VARCHAR2(255 CHAR),
  receiveAlarmEmails NUMBER(10,0) NOT NULL,
  receiveOwnAuditEvents CHAR(1 CHAR) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_10 on table users ... 
ALTER TABLE users
ADD CONSTRAINT PRIMARY_10 PRIMARY KEY
(
  id
)
ENABLE
;

-- Creating Table watchlistpoints ...
CREATE TABLE watchlistpoints (
  watchListId NUMBER(10,0) NOT NULL,
  dataPointId NUMBER(10,0) NOT NULL,
  sortOrder NUMBER(10,0) NOT NULL
);

-- Creating Index watchListPointsFk1 on watchlistpoints ...
CREATE INDEX watchListPointsFk1 ON watchlistpoints
(
  watchListId
) 
;
-- Creating Index watchListPointsFk2 on watchlistpoints ...
CREATE INDEX watchListPointsFk2 ON watchlistpoints
(
  dataPointId
) 
;

-- Creating Table watchlists ...
CREATE TABLE watchlists (
  id NUMBER(10,0) NOT NULL,
  xid VARCHAR2(50 CHAR) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  name VARCHAR2(50 CHAR)
);

-- Creating Unique Constraint watchListsUn1 on table watchlists
ALTER TABLE watchlists
ADD CONSTRAINT watchListsUn1 UNIQUE (
  xid
)
ENABLE
;
-- Creating Primary Key Constraint PRIMARY_11 on table watchlists ... 
ALTER TABLE watchlists
ADD CONSTRAINT PRIMARY_11 PRIMARY KEY
(
  id
)
ENABLE
;
-- Creating Index watchListsFk1 on watchlists ...
CREATE INDEX watchListsFk1 ON watchlists
(
  userId
) 
;

-- Creating Table watchlistusers ...
CREATE TABLE watchlistusers (
  watchListId NUMBER(10,0) NOT NULL,
  userId NUMBER(10,0) NOT NULL,
  accessType NUMBER(10,0) NOT NULL
);

-- Creating Primary Key Constraint PRIMARY_15 on table watchlistusers ... 
ALTER TABLE watchlistusers
ADD CONSTRAINT PRIMARY_15 PRIMARY KEY
(
  userId,
  watchListId
)
ENABLE
;
-- Creating Index watchListUsersFk2 on watchlistusers ...
CREATE INDEX watchListUsersFk2 ON watchlistusers
(
  userId
) 
;

-- Creating Foreign Key Constraint maintenanceEventsFk1 on table datasources...
ALTER TABLE maintenanceevents
ADD CONSTRAINT maintenanceEventsFk1 FOREIGN KEY
(
  dataSourceId
)
REFERENCES datasources
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint mangoViewsFk1 on table users...
ALTER TABLE mangoviews
ADD CONSTRAINT mangoViewsFk1 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint mangoViewUsersFk1 on table mangoviews...
ALTER TABLE mangoviewusers
ADD CONSTRAINT mangoViewUsersFk1 FOREIGN KEY
(
  mangoViewId
)
REFERENCES mangoviews
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint mangoViewUsersFk2 on table users...
ALTER TABLE mangoviewusers
ADD CONSTRAINT mangoViewUsersFk2 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint dataPointsFk1 on table datasources...
ALTER TABLE datapoints
ADD CONSTRAINT dataPointsFk1 FOREIGN KEY
(
  dataSourceId
)
REFERENCES datasources
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint mailingListInactiveFk1 on table mailinglists...
ALTER TABLE mailinglistinactive
ADD CONSTRAINT mailingListInactiveFk1 FOREIGN KEY
(
  mailingListId
)
REFERENCES mailinglists
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint reportsFk1 on table users...
ALTER TABLE reports
ADD CONSTRAINT reportsFk1 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint pointEventDetectorsFk1 on table datapoints...
ALTER TABLE pointeventdetectors
ADD CONSTRAINT pointEventDetectorsFk1 FOREIGN KEY
(
  dataPointId
)
REFERENCES datapoints
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint pointValueAnnotationsFk1 on table pointvalues...
ALTER TABLE pointvalueannotations
ADD CONSTRAINT pointValueAnnotationsFk1 FOREIGN KEY
(
  pointValueId
)
REFERENCES pointvalues
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint dataPointUsersFk1 on table datapoints...
ALTER TABLE datapointusers
ADD CONSTRAINT dataPointUsersFk1 FOREIGN KEY
(
  dataPointId
)
REFERENCES datapoints
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint dataPointUsersFk2 on table users...
ALTER TABLE datapointusers
ADD CONSTRAINT dataPointUsersFk2 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint dataSourceUsersFk1 on table datasources...
ALTER TABLE datasourceusers
ADD CONSTRAINT dataSourceUsersFk1 FOREIGN KEY
(
  dataSourceId
)
REFERENCES datasources
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint dataSourceUsersFk2 on table users...
ALTER TABLE datasourceusers
ADD CONSTRAINT dataSourceUsersFk2 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint eventsFk1 on table users...
ALTER TABLE events
ADD CONSTRAINT eventsFk1 FOREIGN KEY
(
  ackUserId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint mailingListMembersFk1 on table mailinglists...
ALTER TABLE mailinglistmembers
ADD CONSTRAINT mailingListMembersFk1 FOREIGN KEY
(
  mailingListId
)
REFERENCES mailinglists
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint pointValuesFk1 on table datapoints...
ALTER TABLE pointvalues
ADD CONSTRAINT pointValuesFk1 FOREIGN KEY
(
  dataPointId
)
REFERENCES datapoints
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint reportInstanceDataFk1 on table reportinstancepoints...
ALTER TABLE reportinstancedata
ADD CONSTRAINT reportInstanceDataFk1 FOREIGN KEY
(
  reportInstancePointId
)
REFERENCES reportinstancepoints
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint reportInstanceDataAnnotationsF on table reportinstancedata...
ALTER TABLE reportinstancedataannotations
ADD CONSTRAINT reportInstanceDataAnnotationsF FOREIGN KEY
(
  pointValueId,
  reportInstancePointId
)
REFERENCES reportinstancedata
(
  pointValueId,
  reportInstancePointId
)
ENABLE
;

-- Creating Foreign Key Constraint reportInstanceEventsFk1 on table reportinstances...
ALTER TABLE reportinstanceevents
ADD CONSTRAINT reportInstanceEventsFk1 FOREIGN KEY
(
  reportInstanceId
)
REFERENCES reportinstances
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint reportInstancePointsFk1 on table reportinstances...
ALTER TABLE reportinstancepoints
ADD CONSTRAINT reportInstancePointsFk1 FOREIGN KEY
(
  reportInstanceId
)
REFERENCES reportinstances
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint reportInstancesFk1 on table users...
ALTER TABLE reportinstances
ADD CONSTRAINT reportInstancesFk1 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint reportInstanceUserCommentsFk1 on table reportinstances...
ALTER TABLE reportinstanceusercomments
ADD CONSTRAINT reportInstanceUserCommentsFk1 FOREIGN KEY
(
  reportInstanceId
)
REFERENCES reportinstances
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint scriptsFk1 on table users...
ALTER TABLE scripts
ADD CONSTRAINT scriptsFk1 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint userCommentsFk1 on table users...
ALTER TABLE usercomments
ADD CONSTRAINT userCommentsFk1 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint userEventsFk1 on table events...
ALTER TABLE userevents
ADD CONSTRAINT userEventsFk1 FOREIGN KEY
(
  eventId
)
REFERENCES events
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint userEventsFk2 on table users...
ALTER TABLE userevents
ADD CONSTRAINT userEventsFk2 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint watchListPointsFk1 on table watchlists...
ALTER TABLE watchlistpoints
ADD CONSTRAINT watchListPointsFk1 FOREIGN KEY
(
  watchListId
)
REFERENCES watchlists
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint watchListPointsFk2 on table datapoints...
ALTER TABLE watchlistpoints
ADD CONSTRAINT watchListPointsFk2 FOREIGN KEY
(
  dataPointId
)
REFERENCES datapoints
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint watchListsFk1 on table users...
ALTER TABLE watchlists
ADD CONSTRAINT watchListsFk1 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint watchListUsersFk1 on table watchlists...
ALTER TABLE watchlistusers
ADD CONSTRAINT watchListUsersFk1 FOREIGN KEY
(
  watchListId
)
REFERENCES watchlists
(
  id
)
ENABLE
;

-- Creating Foreign Key Constraint watchListUsersFk2 on table users...
ALTER TABLE watchlistusers
ADD CONSTRAINT watchListUsersFk2 FOREIGN KEY
(
  userId
)
REFERENCES users
(
  id
)
ENABLE
;

-- Setting delimiter for triggers
DELIMITER $$ 

CREATE OR REPLACE TRIGGER datapoints_id_TRG BEFORE INSERT ON datapoints
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  datapoints_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM datapoints;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT datapoints_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER pointlinks_id_TRG BEFORE INSERT ON pointlinks
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  pointlinks_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM pointlinks;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT pointlinks_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER reportinstancepoints_id_TRG BEFORE INSERT ON reportinstancepoints
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  reportinstancepoints_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM reportinstancepoints;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT reportinstancepoints_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER reports_id_TRG BEFORE INSERT ON reports
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  reports_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM reports;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT reports_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER flexprojects_id_TRG BEFORE INSERT ON flexprojects
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  flexprojects_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM flexprojects;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT flexprojects_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER publishers_id_TRG BEFORE INSERT ON publishers
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  publishers_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM publishers;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT publishers_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER reportinstances_id_TRG BEFORE INSERT ON reportinstances
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  reportinstances_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM reportinstances;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT reportinstances_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER watchlists_id_TRG BEFORE INSERT ON watchlists
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  watchlists_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM watchlists;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT watchlists_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER maintenanceevents_id_TRG BEFORE INSERT ON maintenanceevents
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  maintenanceevents_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM maintenanceevents;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT maintenanceevents_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER pointvalues_id_TRG BEFORE INSERT ON pointvalues
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  pointvalues_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM pointvalues;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT pointvalues_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER datasources_id_TRG BEFORE INSERT ON datasources
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  datasources_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM datasources;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT datasources_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER events_id_TRG BEFORE INSERT ON events
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  events_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM events;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT events_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER pointeventdetectors_id_TRG BEFORE INSERT ON pointeventdetectors
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  pointeventdetectors_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM pointeventdetectors;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT pointeventdetectors_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER pointhierarchy_id_TRG BEFORE INSERT ON pointhierarchy
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  pointhierarchy_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM pointhierarchy;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT pointhierarchy_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER scheduledevents_id_TRG BEFORE INSERT ON scheduledevents
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  scheduledevents_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM scheduledevents;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT scheduledevents_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER eventhandlers_id_TRG BEFORE INSERT ON eventhandlers
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  eventhandlers_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM eventhandlers;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT eventhandlers_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER users_id_TRG BEFORE INSERT ON users
REFERENCING new as new
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  users_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM users;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT users_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER mangoviews_id_TRG BEFORE INSERT ON mangoviews
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  mangoviews_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM mangoviews;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT mangoviews_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER compoundeventdetectors_id_TRG BEFORE INSERT ON compoundeventdetectors
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  compoundeventdetectors_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM compoundeventdetectors;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT compoundeventdetectors_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER mailinglists_id_TRG BEFORE INSERT ON mailinglists
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  mailinglists_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM mailinglists;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT mailinglists_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

CREATE OR REPLACE TRIGGER scripts_id_TRG BEFORE INSERT ON scripts
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.id IS NULL THEN
    SELECT  scripts_id_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(id),0) INTO v_newVal FROM scripts;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT scripts_id_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.id := v_newVal;
  END IF;
END; $$

-- Setting original delimiter 
DELIMITER ;
