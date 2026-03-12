package org.scada_lts.db;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface BinaryDataHandler {

    InputStream getBinaryStream(ResultSet rs, int columnIndex) throws SQLException;

    InputStream getBinaryStream(ResultSet rs, String columnLabel) throws SQLException;

    int getBinarySqlType();
}
