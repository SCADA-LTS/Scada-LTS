package org.scada_lts.dao.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScadaObjectIdentifierRowMapper implements RowMapper<ScadaObjectIdentifier> {

    private final String idColumnName;
    private final String xidColumnName;
    private final String nameColumnName;

    public ScadaObjectIdentifierRowMapper(String idColumnName, String xidColumnName, String nameColumnName) {
        this.idColumnName = idColumnName;
        this.xidColumnName = xidColumnName;
        this.nameColumnName = nameColumnName;
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
