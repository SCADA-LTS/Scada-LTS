package org.scada_lts.db;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class MysqlBinaryDataHandler implements BinaryDataHandler {

    @Override
    public InputStream getBinaryStream(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        return blob == null ? null : blob.getBinaryStream();
    }

    @Override
    public InputStream getBinaryStream(ResultSet rs, String columnLabel) throws SQLException {
        Blob blob = rs.getBlob(columnLabel);
        return blob == null ? null : blob.getBinaryStream();
    }

    @Override
    public int getBinarySqlType() {
        return Types.BLOB;
    }
}
