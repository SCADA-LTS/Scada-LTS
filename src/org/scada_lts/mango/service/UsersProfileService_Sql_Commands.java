package org.scada_lts.mango.service;

class UsersProfileService_Sql_Commands {

    static final String SELECT_DATA_SOURCE_PERMISSIONS = "select dataSourceId from dataSourceUsersProfiles where userProfileId=?";
    static final String SELECT_DATA_POINT_PERMISSIONS = "select dataPointId, permission from dataPointUsersProfiles where userProfileId=?";
    static final String SELECT_WATCHLIST_PERMISSIONS = "select watchlistId, permission from watchListUsersProfiles where userProfileId=?";
    static final String SELECT_VIEW_PERMISSIONS = "select viewId, permission from viewUsersProfiles where userProfileId=?";
    static final String USERS_PROFILES_SELECT = "select userProfileId, userId from usersUsersProfiles u";
    static final String USERS_PROFILES_USERS_SELECT = "select userId from usersUsersProfiles u";

    static final String PROFILES_SELECT = "select u.id, u.name, u.xid from usersProfiles u";
    static final String PROFILES_SELECT_ORDER_BY_NAME = "select u.id, u.name, u.xid from usersProfiles u order by u.name";
    static final String PROFILES_INSERT = "insert into usersProfiles (xid, name) values (?, ?)";
    static final String PROFILES_UPDATE = "update usersProfiles set name=? " + "where id=?";
    static final String PROFILES_DELETE = "delete from usersProfiles where id = (?)";
    static final String USERSUSERSPROFILES_DELETE_WHERE_USERID = "delete from usersUsersProfiles where userId=?";
    static final String USERSUSERSPROFILES_INSERT = "insert into usersUsersProfiles (userProfileId, userId) values (?,?)";

    static final String DATASOURCEUSERSPROFILES_DELETE = "delete from dataSourceUsersProfiles where userProfileId=?";
    static final String DATAPOINTUSERSPROFILES_DELETE = "delete from dataPointUsersProfiles where userProfileId=?";
    static final String WATCHLISTUSERSPROFILES_DELETE = "delete from watchListUsersProfiles where userProfileId=?";
    static final String VIEWUSERSPROFILES_DELETE = "delete from viewUsersProfiles where userProfileId=?";

    static final String DATASOURCEUSERSPROFILES_INSERT = "insert into dataSourceUsersProfiles (dataSourceId, userProfileId) values (?,?)";
    static final String DATAPOINTUSERSPROFILES_INSERT = "insert into dataPointUsersProfiles (dataPointId, userProfileId, permission) values (?,?,?)";
    static final String WATCHLISTUSERSPROFILES_INSERT = "insert into watchListUsersProfiles (watchlistId, userProfileId, permission) values (?,?,?)";
    static final String VIEWUSERSPROFILES_INSERT = "insert into viewUsersProfiles (viewId, userProfileId, permission) values (?,?,?)";
    static final String USERSUSERSPROFILES_DELETE = "delete from usersUsersProfiles where userId=?";
}
