package org.scada_lts.mango.service;

/*import br.org.scadabr.db.dao.FlexProjectDao;
import br.org.scadabr.db.dao.ScriptDao;*/
import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.util.SerializationHelper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.sql.*;
import java.util.List;

import static org.scada_lts.mango.service.ScriptService.ScriptService_SQL_Statements.*;

public class ScriptService extends BaseService{

    class ScriptService_SQL_Statements {
        static final String SCRIPT_SELECT = "select id, xid, name, script, userId, data from scripts ";
        static final String INSERT__SCRIPTS = "insert into scripts (xid, name,  script, userId, data) values (?,?,?,?,?)";
        static final String UPDATE__SCRIPTS = "update scripts set xid=?, name=?, script=?, userId=?, data=? where id=?";
        static final String SCRIPT_SELECT_WHERE_ID = SCRIPT_SELECT + " where id=?";
        static final String SCRIPT_SELECT_WHERE_XID = SCRIPT_SELECT + " where xid=?";
    }

    public void saveScript(final ScriptVO<?> vo) {
        // Decide whether to insert or update.
        if (vo.getId() == Common.NEW_ID)
            insertScript(vo);
        else
            updateScript(vo);
    }

    private void insertScript(final ScriptVO<?> vo) {
        if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){

            PreparedStatementsCommon preparedStatementsCommon = new PreparedStatementsCommon();
            preparedStatementsCommon.insertIntoScripts(vo);

        }
        else{
            vo.setId(doInsert(
                    INSERT__SCRIPTS,
                    new Object[] { vo.getXid(), vo.getName(),
                            vo.getScript(), vo.getUserId(),
                            SerializationHelper.writeObject(vo) },
                    new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
                            Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB }));
        }
    }

    @SuppressWarnings("unchecked")
    private void updateScript(final ScriptVO<?> vo) {
        ScriptVO<?> old = getScript(vo.getId());
        ejt
                .update(
                        UPDATE__SCRIPTS,
                        new Object[] { vo.getXid(), vo.getName(),
                                vo.getScript(), vo.getUserId(),
                                SerializationHelper.writeObject(vo), vo.getId() },
                        new int[] { Types.VARCHAR, Types.VARCHAR,
                                Types.VARCHAR, Types.INTEGER, Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB,
                                Types.INTEGER });
    }

    public void deleteScript(final int scriptId) {
        ScriptVO<?> vo = getScript(scriptId);
        final ExtendedJdbcTemplate ejt2 = ejt;
        if (vo != null) {
            getTransactionTemplate().execute(
                    new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(
                                TransactionStatus status) {
                            ejt2.update("delete from scripts where id=?",
                                    new Object[] { scriptId });
                        }
                    });
        }
    }

    public ScriptVO<?> getScript(int id) {
        return queryForObject(SCRIPT_SELECT_WHERE_ID,
                new Object[] { id }, new ScriptRowMapper(), null);
    }

    public List<ScriptVO<?>> getScripts() {
        return query(SCRIPT_SELECT, new ScriptRowMapper());
    }

    class ScriptRowMapper implements GenericRowMapper<ScriptVO<?>> {
        public ScriptVO<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScriptVO<?> script;
            if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){
                script = (ScriptVO<?>) SerializationHelper.readObject(rs.getBinaryStream(6));
            }
            else{
                script = (ScriptVO<?>) SerializationHelper.readObject(rs.getBlob(6).getBinaryStream());
            }
            script.setId(rs.getInt(1));
            script.setXid(rs.getString(2));
            script.setName(rs.getString(3));
            script.setScript(rs.getString(4));
            script.setUserId(rs.getInt(5));
            return script;
        }
    }

    public String generateUniqueXid() {
        return generateUniqueXid(ScriptVO.XID_PREFIX, "scripts");
    }

    public boolean isXidUnique(String xid, int excludeId) {
        return isXidUnique(xid, excludeId, "scripts");
    }

    public ScriptVO<?> getScript(String xid) {
        try {
            return queryForObject(SCRIPT_SELECT_WHERE_XID,
                    new Object[] { xid }, new ScriptRowMapper(), null);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
