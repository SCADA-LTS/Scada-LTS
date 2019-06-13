package br.org.scadabr.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.eventDetectorTemplate.EventDetectorTemplateVO;

import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.db.dao.BaseDao;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

public class EventDetectorTemplateDao extends BaseDao {

	private static final String DETECTORS_SELECT = "select id, xid, alias, detectorType, alarmLevel, stateLimit, duration, durationType, binaryState, " //
			+ "  multistateState, changeCount, alphanumericState, weight, threshold " //
			+ "from templatesDetectors " //
			+ "where eventDetectorTemplateId=? " //
			+ "order by id";

	private static final String DETECTORS_INSERT = "insert into templatesDetectors "
			+ "  (xid, alias, detectorType, alarmLevel, stateLimit, duration, durationType, "
			+ "  binaryState, multistateState, changeCount, alphanumericState, weight, threshold, eventDetectorTemplateId) "
			+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String TEMPLATES_INSERT = "insert into eventDetectorTemplates (name) values (?)";

	private static final String TEMPLATES_SELECT = "select dp.id, dp.name "
			+ "from eventDetectorTemplates dp";

	public void insertEventDetectorTemplate(
			final EventDetectorTemplateVO eventDetectorTemplate)
			throws DAOException {

		if (this.getByEventDetectorTemplateByName(
				eventDetectorTemplate.getName()).size() > 0) {
			throw new DAOException();
		}

		eventDetectorTemplate.setId(doInsert(TEMPLATES_INSERT,
				new Object[] { eventDetectorTemplate.getName() }));

		saveEventDetectorReferences(eventDetectorTemplate);
	}

	private List<EventDetectorTemplateVO> getByEventDetectorTemplateByName(
			String name) {
		List<EventDetectorTemplateVO> edt = query(TEMPLATES_SELECT
				+ " where dp.name=? ", new Object[] { name },
				new EventDetectorTemplateRowMapper());

		return edt;
	}

	private void saveEventDetectorReferences(
			EventDetectorTemplateVO eventDetectorTemplate) {

		for (PointEventDetectorVO ped : eventDetectorTemplate
				.getEventDetectors()) {

			ped.setId(doInsert(
					DETECTORS_INSERT,
					new Object[] { ped.getXid(), ped.getAlias(),
							ped.getDetectorType(), ped.getAlarmLevel(),
							ped.getLimit(), ped.getDuration(),
							ped.getDurationType(),
							boolToChar(ped.isBinaryState()),
							ped.getMultistateState(), ped.getChangeCount(),
							ped.getAlphanumericState(), ped.getWeight() },
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
							Types.INTEGER, Types.DOUBLE, Types.INTEGER,
							Types.INTEGER, Types.VARCHAR, Types.INTEGER,
							Types.INTEGER, Types.VARCHAR, Types.DOUBLE,
							Types.DOUBLE, Types.INTEGER }));
		}

	}

	public EventDetectorTemplateVO getEventDetectorTemplate(int id) {
		EventDetectorTemplateVO edt = queryForObject(TEMPLATES_SELECT
				+ " where dp.id=? ", new Object[] { id },
				new EventDetectorTemplateRowMapper());
		setRelationalData(edt);
		return edt;
	}

	private void setRelationalData(EventDetectorTemplateVO template) {
		setEventDetectors(template);
	}

	private void setEventDetectors(EventDetectorTemplateVO template) {
		template.setEventDetectors(getEventDetectors(template));
	}

	private List<PointEventDetectorVO> getEventDetectors(
			EventDetectorTemplateVO template) {

		return query(DETECTORS_SELECT, new Object[] { template.getId() },
				new TemplateEventDetectorRowMapper(template));
	}

	class TemplateEventDetectorRowMapper implements
			GenericRowMapper<PointEventDetectorVO> {

		private final EventDetectorTemplateVO template;

		public TemplateEventDetectorRowMapper(EventDetectorTemplateVO template) {
			this.template = template;
		}

		public PointEventDetectorVO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PointEventDetectorVO detector = new PointEventDetectorVO();
			int i = 0;
			detector.setId(rs.getInt(++i));
			detector.setXid(rs.getString(++i));
			detector.setAlias(rs.getString(++i));
			detector.setDetectorType(rs.getInt(++i));
			detector.setAlarmLevel(rs.getInt(++i));
			detector.setLimit(rs.getDouble(++i));
			detector.setDuration(rs.getInt(++i));
			detector.setDurationType(rs.getInt(++i));
			detector.setBinaryState(charToBool(rs.getString(++i)));
			detector.setMultistateState(rs.getInt(++i));
			detector.setChangeCount(rs.getInt(++i));
			detector.setAlphanumericState(rs.getString(++i));
			detector.setWeight(rs.getDouble(++i));
			return detector;
		}
	}

	public List<EventDetectorTemplateVO> getEventDetectorTemplatesWithoutDetectors() {
		return query(TEMPLATES_SELECT, new EventDetectorTemplateRowMapper());

	}

	class EventDetectorTemplateRowMapper implements
			GenericRowMapper<EventDetectorTemplateVO> {

		public EventDetectorTemplateVO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			EventDetectorTemplateVO edt = new EventDetectorTemplateVO();
			edt.setId(rs.getInt(1));
			edt.setName(rs.getString(2));
			return edt;
		}
	}

}
