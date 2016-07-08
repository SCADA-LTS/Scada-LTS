--
--    Mango - Open Source M2M - http://mango.serotoninsoftware.com
--    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
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
--

--
-- This is un-maintained code. Use with caution
--

--
-- Delete a user
delete from watchListPoints where watchListId in (select id from watchLists where userId=?);
delete from watchLists where userId=?;
delete from pointValueAnnotations where sourceType=1 and sourceId=?;
delete from mangoViews where userId=?;
delete from dataPointUsers where userId=?;
delete from dataSourceUsers where userId=?;
delete from mailingListMembers where typeId=2 and userId=?;
delete from userComments where userId=?;
delete from users where id=?;



--
-- Convert MySQL database from latin1 to utf8;
alter database default character set utf8;

alter table systemSettings default character set utf8;
alter table systemSettings modify settingName varchar(32) character set utf8;
alter table systemSettings modify settingValue text character set utf8;

alter table users default character set utf8;
alter table users modify username varchar(40) character set utf8;
alter table users modify password varchar(30) character set utf8;
alter table users modify email varchar(255) character set utf8;
alter table users modify phone varchar(40) character set utf8;
alter table users modify admin char(1) character set utf8;
alter table users modify disabled char(1) character set utf8;

alter table userComments default character set utf8;
alter table userComments modify commentText varchar(1024) character set utf8;

alter table mailingLists default character set utf8;
alter table mailingLists modify name varchar(40) character set utf8;

alter table mailingListInactive default character set utf8;

alter table mailingListMembers default character set utf8;
alter table mailingListMembers modify address varchar(255) character set utf8;

alter table dataSources default character set utf8;
alter table dataSources modify xid varchar(50) character set utf8;
alter table dataSources modify name varchar(40) character set utf8;

alter table dataSourceUsers default character set utf8;

alter table dataPoints default character set utf8;
alter table dataPoints modify xid varchar(50) character set utf8;

alter table dataPointUsers default character set utf8;

alter table mangoViews default character set utf8;
alter table mangoViews modify name varchar(100) character set utf8;
alter table mangoViews modify background varchar(255) character set utf8;

alter table mangoViewUsers default character set utf8;

alter table pointValues default character set utf8;

alter table pointValueAnnotations default character set utf8;
alter table pointValueAnnotations modify textPointValueShort varchar(128) character set utf8;
alter table pointValueAnnotations modify textPointValueLong text character set utf8;

alter table watchLists default character set utf8;
alter table watchLists modify name varchar(50) character set utf8;

alter table watchListPoints default character set utf8;

alter table pointEventDetectors default character set utf8;
alter table pointEventDetectors modify xid varchar(50) character set utf8;
alter table pointEventDetectors modify alias varchar(255) character set utf8;
alter table pointEventDetectors modify binaryState char(1) character set utf8;
alter table pointEventDetectors modify alphanumericState varchar(128) character set utf8;

alter table events default character set utf8;
alter table events modify rtnApplicable char(1) character set utf8;
alter table events modify message text character set utf8;

alter table userEvents default character set utf8;
alter table userEvents modify silenced char(1) character set utf8;

alter table eventHandlers default character set utf8;
alter table eventHandlers modify alias varchar(255) character set utf8;

alter table scheduledEvents default character set utf8;
alter table scheduledEvents modify alias varchar(255)character set utf8; 
alter table scheduledEvents modify returnToNormal char(1) character set utf8;
alter table scheduledEvents modify disabled char(1) character set utf8;
alter table scheduledEvents modify activeCron varchar(25) character set utf8;
alter table scheduledEvents modify inactiveCron varchar(25) character set utf8;

alter table pointHierarchy default character set utf8;
alter table pointHierarchy modify name varchar(100) character set utf8;

alter table compoundEventDetectors default character set utf8;
alter table compoundEventDetectors modify name varchar(100) character set utf8;
alter table compoundEventDetectors modify returnToNormal char(1) character set utf8;
alter table compoundEventDetectors modify disabled char(1) character set utf8;
alter table compoundEventDetectors modify conditionText varchar(256) character set utf8;

alter table reports default character set utf8;
alter table reports modify name varchar(100) character set utf8;

alter table reportInstances default character set utf8;
alter table reportInstances modify name varchar(100) character set utf8;
alter table reportInstances modify preventPurge char(1) character set utf8;

alter table reportInstancePoints default character set utf8;
alter table reportInstancePoints modify pointName varchar(100) character set utf8;
alter table reportInstancePoints modify dataSourceName varchar(40) character set utf8;
alter table reportInstancePoints modify startValue varchar(4096) character set utf8;

alter table reportInstanceData default character set utf8;

alter table reportInstanceDataAnnotations default character set utf8;
alter table reportInstanceDataAnnotations modify textPointValueShort varchar(128) character set utf8;
alter table reportInstanceDataAnnotations modify textPointValueLong text character set utf8;
alter table reportInstanceDataAnnotations modify sourceValue varchar(128) character set utf8;

alter table reportInstanceEvents default character set utf8;
alter table reportInstanceEvents modify rtnApplicable char(1) character set utf8;
alter table reportInstanceEvents modify message text character set utf8;

alter table publishers default character set utf8;

alter table pointLinks default character set utf8;
alter table pointLinks modify xid varchar(50) character set utf8;
alter table pointLinks modify script longtext character set utf8;
alter table pointLinks modify disabled char(1) character set utf8;




--
-- Drop all tables
--
drop table pointLinks;
drop table publishers;
drop table reportInstanceUserComments;
drop table reportInstanceEvents;
drop table reportInstanceDataAnnotations;
drop table reportInstanceData;
drop table reportInstancePoints;
drop table reportInstances;
drop table reports;
drop table compoundEventDetectors;
drop table pointHierarchy;
drop table scheduledEvents;
drop table eventHandlers;
drop table userEvents;
drop table events;
drop table pointEventDetectors;
drop table watchListUsers;
drop table watchListPoints;
drop table watchLists;
drop table pointValueAnnotations;
drop table pointValues;
drop table mangoViewUsers;
drop table mangoViews;
drop table dataPointUsers;
drop table dataPoints;
drop table dataSourceUsers;
drop table dataSources;
drop table mailingListMembers;
drop table mailingListInactive;
drop table mailingLists;
drop table userComments;
drop table users;
drop table systemSettings;
