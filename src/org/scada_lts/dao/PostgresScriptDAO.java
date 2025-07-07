package org.scada_lts.dao;

import br.org.scadabr.vo.scripting.ScriptVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.JsonScript;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.List;

public class PostgresScriptDAO implements IScriptDAO {

    private static final Log LOG = LogFactory.getLog(PostgresScriptDAO.class);

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_NAME_SCRIPT = "script";
    private static final String COLUMN_NAME_USERID = "userId";
    private static final String COLUMN_NAME_DATA = "data";

    // @formatter:off
    private static final String SCRIPT_SELECT = ""
            + "select "
            + COLUMN_NAME_ID +","
            + COLUMN_NAME_XID +","
            + COLUMN_NAME_NAME +","
            + COLUMN_NAME_SCRIPT + ","
            + COLUMN_NAME_USERID + ","
            + COLUMN_NAME_DATA + " "
            + "from " +
            "scripts ";


    private static final String SCRIPT_INSERT = ""
            + "insert into scripts ("
            + COLUMN_NAME_XID + ","
            + COLUMN_NAME_NAME + ","
            + COLUMN_NAME_SCRIPT + ","
            + COLUMN_NAME_USERID + ","
            + COLUMN_NAME_DATA
            +") "
            + "values (?,?,?,?,?) RETURNING id";

    private static final String SCRIPT_UPDATE = ""
            + "update scripts set "
            + COLUMN_NAME_XID+"=?,"
            + COLUMN_NAME_NAME+"=?,"
            + COLUMN_NAME_SCRIPT+"=?,"
            + COLUMN_NAME_USERID+"=?,"
            + COLUMN_NAME_DATA+"=? "
            + "where "
            + COLUMN_NAME_ID+"=?";

    private static final String SCRIPT_DELETE= ""
            + "delete "
            + "from "
            + "scripts "
            + "where "
            + COLUMN_NAME_ID+"=?";

    private static final String SCRIPT_SELECT_ONE=""
            + SCRIPT_SELECT
            + " where "
            + COLUMN_NAME_ID+"=?";

    private static final String SCRIPT_SELECT_BASE_ON_XID=""
            + SCRIPT_SELECT
            + "where "
            + COLUMN_NAME_XID+"=?";

    // @formatter:on

    //RowMapper
    private class ScriptRowMapper implements RowMapper<ScriptVO<?>> {
        @Override
        public ScriptVO<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScriptVO<?> script;

            try (InputStream in = rs.getBinaryStream(COLUMN_NAME_DATA)) {
                script = (ScriptVO<?>) new SerializationData().readObject(in);
            } catch (Exception e) {
                throw new SQLException("Deserialization error of ScriptVO.data", e);
            }

            script.setId(rs.getInt(COLUMN_NAME_ID));
            script.setXid(rs.getString(COLUMN_NAME_XID));
            script.setName(rs.getString(COLUMN_NAME_NAME));
            script.setScript(rs.getString(COLUMN_NAME_SCRIPT));
            script.setUserId(rs.getInt(COLUMN_NAME_USERID));

            return script;
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public int insert(final ScriptVO<?> vo) {

        if (LOG.isTraceEnabled()) {
            LOG.trace(vo);
        }

        final ByteArrayInputStream byteStream = new SerializationData().writeObject(vo);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(SCRIPT_INSERT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, vo.getXid());
                ps.setString(2, vo.getName());
                ps.setString(3, vo.getScript());
                ps.setInt(4, vo.getUserId());
                ps.setBinaryStream(5, byteStream, byteStream.available());
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void update(final ScriptVO<?> vo) {

        if (LOG.isTraceEnabled()) {
            LOG.trace(vo);
        }

        DAO.getInstance().getJdbcTemp().update(SCRIPT_UPDATE, new Object[]{
                vo.getXid(),
                vo.getName(),
                vo.getScript(),
                vo.getUserId(),
                new SerializationData().writeObject(vo),
                vo.getId()});
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void delete(final int id) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("delete script id:" + id);
        }

        DAO.getInstance().getJdbcTemp().update(SCRIPT_DELETE, new Object[]{id});

    }

    @Override
    public ScriptVO<?> getScript(int id) {
        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(SCRIPT_SELECT_ONE, new Object[]{id}, new PostgresScriptDAO.ScriptRowMapper());
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public List<ScriptVO<?>> getScripts() {
        return (List<ScriptVO<?>>) DAO.getInstance().getJdbcTemp().query(SCRIPT_SELECT, new Object[]{}, new PostgresScriptDAO.ScriptRowMapper());
    }

    @Override
    public ScriptVO<?> getScript(String xid) {
        try {
            return DAO.getInstance().getJdbcTemp().queryForObject(SCRIPT_SELECT_BASE_ON_XID, new Object[]{
                    xid}, new PostgresScriptDAO.ScriptRowMapper());
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public String generateUniqueXid() {
        return DAO.getInstance().generateUniqueXid(ScriptVO.XID_PREFIX, "scripts");
    }

    @Override
    public boolean isXidUnique(String xid, int excludeId) {
        return DAO.getInstance().isXidUnique(xid, excludeId, "scripts");
    }

    @Override
    public JsonScript findScriptsByPage(String query) {
        return (JsonScript) DAO.getInstance().getJdbcTemp().query(SCRIPT_SELECT + " ", new Object[]{}, new PostgresScriptDAO.ScriptRowMapper());
    }
}
