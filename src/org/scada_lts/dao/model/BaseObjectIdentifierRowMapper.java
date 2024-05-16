package org.scada_lts.dao.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseObjectIdentifierRowMapper implements RowMapper<BaseObjectIdentifier> {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";

    private final String idColumnName;
    private final String xidColumnName;

    private BaseObjectIdentifierRowMapper(String idColumnName, String xidColumnName) {
        this.idColumnName = idColumnName;
        this.xidColumnName = xidColumnName;
    }

    public static class Builder {
        private String idColumnName;
        private String xidColumnName;

        public Builder idColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
            return this;
        }

        public Builder xidColumnName(String xidColumnName) {
            this.xidColumnName = xidColumnName;
            return this;
        }

        public BaseObjectIdentifierRowMapper build() {
            return new BaseObjectIdentifierRowMapper(idColumnName, xidColumnName);
        }
    }

    public static BaseObjectIdentifierRowMapper withDefaultNames() {
        return new BaseObjectIdentifierRowMapper.Builder()
                .idColumnName(COLUMN_NAME_ID)
                .xidColumnName(COLUMN_NAME_XID)
                .build();
    }
    public String selectScadaObjectIdFrom(String tableName) {
        return "select " +
                idColumnName + ", " +
                xidColumnName + ", " +
                "FROM " + tableName  + " order by " + idColumnName;
    }
    @Override
    public BaseObjectIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BaseObjectIdentifier(
                rs.getInt(idColumnName),
                rs.getString(xidColumnName)
        );
    }
}
