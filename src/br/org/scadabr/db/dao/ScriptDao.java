package br.org.scadabr.db.dao;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.util.SerializationHelper;
import org.scada_lts.dao.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptDao extends BaseDao {
	private static final String SCRIPT_SELECT = "select id, xid, name, script, userId, data from scripts ";

	public void saveScript(final ScriptVO<?> vo) {
		// Decide whether to insert or update.
		if (vo.getId() == Common.NEW_ID)
			insertScript(vo);
		else
			updateScript(vo);
	}

	private void insertScript(final ScriptVO<?> vo) {
                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
                    try {
                        Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                                    Common.getEnvironmentProfile().getString("db.username"),
                                                    Common.getEnvironmentProfile().getString("db.password"));
                        PreparedStatement preStmt = conn.prepareStatement("insert into scripts (xid, name,  script, userId, data) values (?,?,?,?,?)");
                        preStmt.setString(1, vo.getXid());
                        preStmt.setString(2, vo.getName());
                        preStmt.setString(3, vo.getScript());
                        preStmt.setInt(4, vo.getUserId());
                        preStmt.setBytes(5, SerializationHelper.writeObjectToArray(vo));
                        preStmt.executeUpdate();
                        
                        ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('scripts_id_seq')");
                        resSEQ.next();
                        int id = resSEQ.getInt(1);

                        conn.close(); 
                        
                        vo.setId(id);
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(FlexProjectDao.class.getName()).log(Level.SEVERE, null, ex);
                        vo.setId(0);
                    }
                }
                else{

					DAO.getInstance().getJdbcTemp().update("insert into scripts (xid, name,  script, userId, data) values (?,?,?,?,?)", new Object[] { vo.getXid(), vo.getName(),
									vo.getScript(), vo.getUserId(),
									SerializationHelper.writeObject(vo) },
							new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
									Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB } );

					vo.setId(DAO.getInstance().getId());

                }
	}

	@SuppressWarnings("unchecked")
	private void updateScript(final ScriptVO<?> vo) {
		ScriptVO<?> old = getScript(vo.getId());
		DAO.getInstance().getJdbcTemp().update(	"update scripts set xid=?, name=?, script=?, userId=?, data=? where id=?",
				new Object[] { vo.getXid(), vo.getName(),
						vo.getScript(), vo.getUserId(),
						SerializationHelper.writeObject(vo), vo.getId() },
				new int[] { Types.VARCHAR, Types.VARCHAR,
						Types.VARCHAR, Types.INTEGER, Common.getEnvironmentProfile().getString("db.type").equals("postgres") ? Types.BINARY: Types.BLOB,
						Types.INTEGER });

	}

	public void deleteScript(final int scriptId) {
		ScriptVO<?> vo = getScript(scriptId);
		//final ExtendedJdbcTemplate ejt2 = ejt;
		if (vo != null) {
			getTransactionTemplate().execute(
					new TransactionCallbackWithoutResult() {
						@Override
						protected void doInTransactionWithoutResult(
								TransactionStatus status) {
							DAO.getInstance().getJdbcTemp().update("delete from scripts where id=?",
									scriptId);

						}
					});
		}
	}

	public ScriptVO<?> getScript(int id) {
		return (ScriptVO<?>)DAO.getInstance().getJdbcTemp().queryForObject(SCRIPT_SELECT + " where id=?",new Object[] { id }, new ScriptRowMapper() );
	}

	public List<ScriptVO<?>> getScripts() {

		List<ScriptVO<?>> scripts = DAO.getInstance().getJdbcTemp().query(SCRIPT_SELECT, new ScriptRowMapper()); //query(SCRIPT_SELECT, new ScriptRowMapper());
		return scripts;
	}

	class ScriptRowMapper implements RowMapper<ScriptVO<?>> {
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


	public boolean isXidUnique(String xid, int excludeId) {
		return isXidUnique(xid, excludeId, "scripts");
	}

	public ScriptVO<?> getScript(String xid) {
		try {
			return (ScriptVO<?>)DAO.getInstance().getJdbcTemp().query(SCRIPT_SELECT + " where xid=?",
					new Object[] { xid }, new ScriptRowMapper());

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
