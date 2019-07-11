package org.scada_lts.mango.service;

import com.serotonin.db.DaoUtils;
import com.serotonin.db.spring.ArgPreparedStatementSetter;
import com.serotonin.db.spring.ArgTypePreparedStatementSetter;
import com.serotonin.mango.Common;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class BaseService extends DaoUtils {

    private static final String DEFAULT_GENERATED_KEY_COLUMN_NAME = "id";

    /**
     * Public constructor for code that needs to get stuff from the database.
     */
    public BaseService() {
        super(Common.ctx.getDatabaseAccess().getDataSource());
    }

    protected BaseService(DataSource dataSource) {
        super(dataSource);
    }

    //
    // Convenience methods for storage of booleans.
    //
    protected static String boolToChar(boolean b) {
        return b ? "Y" : "N";
    }

    protected static boolean charToBool(String s) {
        return "Y".equals(s);
    }

    protected void deleteInChunks(String sql, List<Integer> ids) {
        int chunk = 1000;
        for (int i = 0; i < ids.size(); i += chunk) {
            String idStr = createDelimitedList(ids, i, i + chunk, ",", null);
            ejt.update(sql + " (" + idStr + ")");
        }
    }

    //
    // XID convenience methods
    //
    protected String generateUniqueXid(String prefix, String tableName) {
        String xid = Common.generateXid(prefix);
        while (!isXidUnique(xid, -1, tableName)) {
            xid = Common.generateXid(prefix);
        }
        return xid;
    }

    protected boolean isXidUnique(String xid, int excludeId, String tableName) {
        return ejt.queryForInt("select count(*) from " + tableName
                + " where xid=? and id<>?", new Object[] { xid, excludeId }) == 0;
    }

    /**
     * Return the column name of the auto-generated key. Wheiter the column name
     * is different of the default generated key column name, the DAO should
     * override this method.
     *
     * @return the column name of the generated key
     */
    protected String getGeneratedKeyName() {
        return DEFAULT_GENERATED_KEY_COLUMN_NAME;
    }

    /**
     * Implements the prepared statement creator, to get the prepared statement
     * to insert operation that returning auto-generated key
     *
     * @param sql
     *            an SQL statement that may contain one or more '?' IN parameter
     *            placeholders
     * @param setter
     *            parameter setter that will set the statement parameters
     * @return
     */
    protected PreparedStatementCreator getPreparedStatementCreator(String sql,
                                                                   PreparedStatementSetter setter) {

        final String sqlS = sql;
        final PreparedStatementSetter setterS = setter;
        final String generatedKey = getGeneratedKeyName();

        PreparedStatementCreator creator = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(
                    Connection connection) throws SQLException {
                PreparedStatement preStmt;
                preStmt = Common.ctx.getDatabaseAccess().prepareStatement(
                        connection, sqlS, generatedKey);
                setterS.setValues(preStmt);

                return preStmt;
            }
        };

        return creator;
    }

    private int executeInsert(String sql, PreparedStatementSetter pss) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        ejt.update(getPreparedStatementCreator(sql, pss), keyHolder);
        return keyHolder.getKey().intValue();
    }

    private long executeInsertLong(String sql, PreparedStatementSetter pss) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        ejt.update(getPreparedStatementCreator(sql, pss), keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    protected int doInsert(String sql, Object params[]) {
        return executeInsert(sql, new ArgPreparedStatementSetter(params));
    }

    @Override
    protected int doInsert(String sql, Object params[], int types[]) {
        return executeInsert(sql, new ArgTypePreparedStatementSetter(params,
                types));
    }

    @Override
    protected int doInsert(String sql, PreparedStatementSetter pss) {
        return executeInsert(sql, pss);
    }

    @Override
    protected long doInsertLong(String sql, Object params[]) {
        return executeInsertLong(sql, new ArgPreparedStatementSetter(params));
    }

    @Override
    protected long doInsertLong(String sql, Object params[], int types[]) {
        return executeInsertLong(sql, new ArgTypePreparedStatementSetter(
                params, types));
    }

    @Override
    protected long doInsertLong(String sql, PreparedStatementSetter pss) {
        return executeInsertLong(sql, pss);
    }
    // TODO VANIA - FAZER TESTES EM TODOS METODOS DAO, com todos drivers jdbc

}
