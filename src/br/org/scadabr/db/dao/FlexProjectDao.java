package br.org.scadabr.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import br.org.scadabr.api.vo.FlexProject;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.BaseDao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlexProjectDao extends BaseDao {
	private static final String FLEX_PROJECT_SELECT = "select id, name, description, xmlConfig from flexProjects ";

	public int saveFlexProject(int id, String name, String description,
			String xmlConfig) {
		if (id == Common.NEW_ID)
			return insertFlexProject(id, name, description, xmlConfig);
		else
			return updateFlexProject(id, name, description, xmlConfig);
	}

	private int insertFlexProject(int id, String name, String description, String xmlConfig) {
                if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){                
                    try {
                        //id = doInsert(EVENT_INSERT, args, EVENT_INSERT_TYPES);
                        Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                                                    Common.getEnvironmentProfile().getString("db.username"),
                                                    Common.getEnvironmentProfile().getString("db.password"));
                        PreparedStatement preStmt = conn.prepareStatement("insert into flexProjects (name,  description, xmlConfig) values (?,?,?)");
                        preStmt.setString(1, name);
                        preStmt.setString(2, description);
                        preStmt.setString(3, xmlConfig);
                        preStmt.executeUpdate();
                        
                        ResultSet resSEQ = conn.createStatement().executeQuery("SELECT currval('flexprojects_id_seq')");
                        resSEQ.next();
                        id = resSEQ.getInt(1);

                        conn.close(); 
                        
                        return id;
                        
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        return 0;
                    }
                }
                else{
                    return doInsert(
                                    "insert into flexProjects (name,  description, xmlConfig) values (?,?,?)",
                                    new Object[] { name, description, xmlConfig }, new int[] {
                                                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR });
                }

	}

	private int updateFlexProject(int id, String name, String description,String xmlConfig) {
		ejt.update(
						"update flexProjects set name=?, description=?, xmlConfig=? where id=?",
						new Object[] { name, description, xmlConfig, id },
						new int[] { Types.VARCHAR, Types.VARCHAR,
								Types.VARCHAR, Types.INTEGER });
		return id;

	}

	public void deleteFlexProject(final int flexProjectId) {
		final ExtendedJdbcTemplate ejt2 = ejt;
		getTransactionTemplate().execute(
				new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus status) {
						ejt2.update("delete from flexProjects where id=?",
								new Object[] { flexProjectId });
					}
				});
	}

	public FlexProject getFlexProject(int id) {
		return queryForObject(FLEX_PROJECT_SELECT + " where id=?",
				new Object[] { id }, new FlexProjectRowMapper(), null);
	}

	public List<FlexProject> getFlexProjects() {
		List<FlexProject> flexProjects = query(FLEX_PROJECT_SELECT,
				new FlexProjectRowMapper());
		return flexProjects;
	}

	class FlexProjectRowMapper implements GenericRowMapper<FlexProject> {
		public FlexProject mapRow(ResultSet rs, int rowNum) throws SQLException {
			FlexProject project = new FlexProject();
			project.setId(rs.getInt(1));
			project.setName(rs.getString(2));
			project.setDescription(rs.getString(3));
			project.setXmlConfig(rs.getString(4));
			return project;
		}
	}

}
