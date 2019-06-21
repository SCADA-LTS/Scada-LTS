package org.scada_lts.dao;

public class SharedSQLs {
    public static final String INSERT_INTO_DATASOURCES = "INSERT INTO datasources (xid, name, dataSourceType, data) ";
    public static final String INSERT_INTO_DATAPOINTS = "INSERT INTO datapoints (`xid`,`dataSourceId`,`data`)";


}
