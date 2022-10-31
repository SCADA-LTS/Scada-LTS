package org.scada_lts.dao.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScadaObjectIdentifierRowMapper implements RowMapper<ScadaObjectIdentifier> {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";

    private final String idColumnName;
    private final String xidColumnName;
    private final String nameColumnName;

    private ScadaObjectIdentifierRowMapper(String idColumnName, String xidColumnName, String nameColumnName) {
        this.idColumnName = idColumnName;
        this.xidColumnName = xidColumnName;
        this.nameColumnName = nameColumnName;
    }

    public static class Builder {
        private String idColumnName;
        private String xidColumnName;
        private String nameColumnName;

        public Builder idColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
            return this;
        }

        public Builder xidColumnName(String xidColumnName) {
            this.xidColumnName = xidColumnName;
            return this;
        }

        public Builder nameColumnName(String nameColumnName) {
            this.nameColumnName = nameColumnName;
            return this;
        }

        public ScadaObjectIdentifierRowMapper build() {
            return new ScadaObjectIdentifierRowMapper(idColumnName, xidColumnName, nameColumnName);
        }
    }

    public static ScadaObjectIdentifierRowMapper withDefaultNames() {
        return new ScadaObjectIdentifierRowMapper.Builder()
                .idColumnName(COLUMN_NAME_ID)
                .xidColumnName(COLUMN_NAME_XID)
                .nameColumnName(COLUMN_NAME_NAME)
                .build();
    }
    public String selectScadaObjectIdFrom(String tableName) {
        return "select " +
                idColumnName + ", " +
                xidColumnName + ", " +
                nameColumnName + " " +
                "FROM " + tableName  + " order by " + idColumnName;
    }
    @Override
    public ScadaObjectIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ScadaObjectIdentifier(
                rs.getInt(idColumnName),
                rs.getString(xidColumnName),
                rs.getString(nameColumnName)
        );
    }
}
