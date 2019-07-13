package org.scada_lts.mango.service;

class DataSourceService_Sql_Commands {

    static final String DATASOURCES_SELECT = "select rtdata from dataSources where id=?";
    static final String DATASOURCES_UPDATE = "update dataSources set rtdata=? where id=?";
}
