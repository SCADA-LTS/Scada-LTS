package org.scada_lts.dao.rowmappers;

import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScadaObjectIdRowMapper implements RowMapper<ScadaObjectIdentifier> {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";

    @Override
    public ScadaObjectIdentifier mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return new ScadaObjectIdentifier(
                resultSet.getInt(COLUMN_NAME_ID),
                resultSet.getString(COLUMN_NAME_XID),
                resultSet.getString(COLUMN_NAME_NAME)
        );
    }

    public static String selectScadaObjectIdFrom(String tableName) {
        return "select " +
                COLUMN_NAME_ID + ", " +
                COLUMN_NAME_XID + ", " +
                COLUMN_NAME_NAME + " " +
                "FROM " + tableName;
    }

}
