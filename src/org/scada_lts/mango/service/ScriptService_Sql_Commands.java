package org.scada_lts.mango.service;

class ScriptService_Sql_Commands {
    static final String SCRIPT_SELECT = "select id, xid, name, script, userId, data from scripts ";
    static final String SCRIPT_UPDATE = "update scripts set xid=?, name=?, script=?, userId=?, data=? where id=?";
    static final String SCRIPT_INSERT = "insert into scripts (xid, name,  script, userId, data) values (?,?,?,?,?)";
    static final String SCRIPT_DELETE = "delete from scripts where id=?";
}
