package org.scada_lts.dao;

import br.org.scadabr.api.vo.FlexProject;
import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * DAO for FlexProject.
 *
 * @author mateusz kapron Abil'I.T. development team, sdt@abilit.eu
 */

public class FlexProjectDAO {

	private static final Log LOG = LogFactory.getLog(FlexProjectDAO.class);

	private static final String COLUMN_NAME_ID = "id";
	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_DESCRIPTION = "description";
	private static final String COLUMN_NAME_XMLCONFIG = "xmlConfig";

	private static final String FLEX_PROJECT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_ID + ", "
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DESCRIPTION + ", "
				+ COLUMN_NAME_XMLCONFIG + " "
			+ "from "
				+ "flexProjects";

	private static final String FLEX_PROJECT_INSERT = ""
			+ "insert into flexProjects ("
				+ COLUMN_NAME_NAME + ", "
				+ COLUMN_NAME_DESCRIPTION + ", "
				+ COLUMN_NAME_XMLCONFIG
			+ ") "
			+ "values (?,?,?)";

	private static final String FLEX_PROJECT_UPDATE = ""
			+ "update flexProjects set "
				+ COLUMN_NAME_NAME + "=?, "
				+ COLUMN_NAME_DESCRIPTION + "=?, "
				+ COLUMN_NAME_XMLCONFIG + "=? "
			+ "where "
				+ COLUMN_NAME_ID + "=?";

	private static final String FLEX_PROJECT_DELETE = ""
			+ "delete from flexProjects where"
				+ COLUMN_NAME_ID + "=?";

	public FlexProject getFlexProject(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getFlexProject(int id) id:" + id);
		}

		String templateSelectWhereId = FLEX_PROJECT_SELECT + " where " + COLUMN_NAME_ID + "=? ";

		FlexProject flexProject = (FlexProject) DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhereId, new Object[]{id},
				new RowMapper() {
					@Override
					public FlexProject mapRow(ResultSet resultSet, int i) throws SQLException {
						FlexProject fP = new FlexProject();
						fP.setId(resultSet.getInt(COLUMN_NAME_ID));
						fP.setName(resultSet.getString(COLUMN_NAME_NAME));
						fP.setDescription(resultSet.getString(COLUMN_NAME_DESCRIPTION));
						fP.setXmlConfig(resultSet.getString(COLUMN_NAME_XMLCONFIG));
						return fP;
					}
				}
		);

		return flexProject;
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(int id, String name, String description, String xmlConfig) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insertFlexProject(int id, String name, String description, String xmlConfig) id:" + id + ", name" + name + ", description:" + description + ", xmlConfig" + xmlConfig);
		}

		DAO.getInstance().getJdbcTemp().update(FLEX_PROJECT_INSERT, new Object[] {name, description, xmlConfig},
				new int [] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
		return id;

	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int update(int id, String name, String description, String xmlConfig) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("updateFlexProject(int id, String name, String descriptor, String xmlConfig) id:" + id + ", name" + name + ", description:" + description + ", xmlConfig" + xmlConfig);
		}

		DAO.getInstance().getJdbcTemp().update(FLEX_PROJECT_UPDATE, new Object[] {name, description, xmlConfig, id},
				new int [] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER});
		return id;
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public void delete(int id) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("deleteFlexProject(int id) id:" + id);
		}

		DAO.getInstance().getJdbcTemp().update(FLEX_PROJECT_DELETE, new Object[] {id},
				new int [] {Types.INTEGER});
	}

	public List<FlexProject> getFlexProjects() {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getFlexProjects()");
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<FlexProject> flexProjectList = DAO.getInstance().getJdbcTemp().query(FLEX_PROJECT_SELECT,
				new RowMapper() {
					@Override
					public FlexProject mapRow(ResultSet rs, int rowNum) throws SQLException {
						FlexProject fP = new FlexProject();
						fP.setId(rs.getInt(COLUMN_NAME_ID));
						fP.setName(rs.getString(COLUMN_NAME_NAME));
						fP.setDescription(rs.getString(COLUMN_NAME_DESCRIPTION));
						fP.setXmlConfig(rs.getString(COLUMN_NAME_XMLCONFIG));
						return fP;
					}
				}) ;

		return flexProjectList;
	}
}
