package org.scada_lts.dao.event;

import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.AuditEventUtils;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

public class PostgresCompoundEventDetectorDAO implements ICompoundEventDetectorDAO {

    private Log LOG = LogFactory.getLog(PostgresCompoundEventDetectorDAO.class);

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_NAME_ALARM_LEVEL = "alarmLevel";
    private static final String COLUMN_NAME_RETURN_TO_NORMAL = "returnToNormal";
    private static final String COLUMN_NAME_DISABLED = "disabled";
    private static final String COLUMN_NAME_CONDITION_TEXT = "conditionText";

    // @formatter:off
    private static final String COMPOUND_EVENT_DETECTOR_SELECT = ""
            + "select "
            + COLUMN_NAME_ID+", "
            + COLUMN_NAME_XID+", "
            + COLUMN_NAME_NAME+", "
            + COLUMN_NAME_ALARM_LEVEL+", "
            + COLUMN_NAME_RETURN_TO_NORMAL+", "
            + COLUMN_NAME_DISABLED+", "
            + COLUMN_NAME_CONDITION_TEXT+" "
            + "from "
            + "compoundEventDetectors ";

    private static final String COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_ID = ""
            +COLUMN_NAME_ID+"=?";

    private static final String COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_XID = ""
            +COLUMN_NAME_ID+"=?";


    private static final String COMPOUND_EVENT_INSERT = ""
            +"insert into compoundEventDetectors ("
            + COLUMN_NAME_XID+", "
            + COLUMN_NAME_NAME+", "
            + COLUMN_NAME_ALARM_LEVEL+", "
            + COLUMN_NAME_RETURN_TO_NORMAL+", "
            + COLUMN_NAME_DISABLED+", "
            + COLUMN_NAME_CONDITION_TEXT+""
            + ") "
            + "values (?,?,?,?,?,?) RETURNING id";

    private static final String COMPOUND_EVENT_UPDATE = ""
            +"update compoundEventDetectors set "
            + COLUMN_NAME_XID+"=?, "
            + COLUMN_NAME_NAME+"=?, "
            + COLUMN_NAME_ALARM_LEVEL+"=?, "
            + COLUMN_NAME_RETURN_TO_NORMAL+"=?, "
            + COLUMN_NAME_DISABLED+"=?, "
            + COLUMN_NAME_CONDITION_TEXT+"=? "
            + "where "
            + COLUMN_NAME_ID+"=?";

    private static final String COMPOUND_EVENT_DELETE = ""
            +"delete "
            + "from "
            + "compoundEventDetectors "
            + "where "
            + COLUMN_NAME_ID+"=?";

    private static final String COMPOUND_EVENT_DELETE_EVENT_HANLDERS=""
            + "delete "
            + "from "
            + "eventHandlers "
            + "where "
            + "eventTypeId=" + EventType.EventSources.COMPOUND+" and "
            + "eventTypeRef1=?";


    // @formatter:on

    //RowMapper
    class CompoundEventDetectorRowMapper implements RowMapper<CompoundEventDetectorVO> {
        public CompoundEventDetectorVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CompoundEventDetectorVO ced = new CompoundEventDetectorVO();
            ced.setId(rs.getInt(COLUMN_NAME_ID));
            ced.setXid(rs.getString(COLUMN_NAME_XID));
            ced.setName(rs.getString(COLUMN_NAME_NAME));
            ced.setAlarmLevel(rs.getInt(COLUMN_NAME_ALARM_LEVEL));
            ced.setReturnToNormal(DAO.charToBool(rs.getString(COLUMN_NAME_RETURN_TO_NORMAL)));
            ced.setDisabled(DAO.charToBool(rs.getString(COLUMN_NAME_DISABLED)));
            ced.setCondition(rs.getString(COLUMN_NAME_CONDITION_TEXT));
            return ced;
        }
    }

    @Override
    public List<CompoundEventDetectorVO> findAll() {
        return (List<CompoundEventDetectorVO>) DAO.getInstance().getJdbcTemp().query(COMPOUND_EVENT_DETECTOR_SELECT+" order by name", new Object[]{}, new PostgresCompoundEventDetectorDAO.CompoundEventDetectorRowMapper());
    }

    @Override
    public CompoundEventDetectorVO findById(Object[] pk) {
        try {
            return (CompoundEventDetectorVO) DAO.getInstance().getJdbcTemp().queryForObject(COMPOUND_EVENT_DETECTOR_SELECT+ " where " + COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_ID, pk , new PostgresCompoundEventDetectorDAO.CompoundEventDetectorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public CompoundEventDetectorVO findByXId(Object[] pk) {
        try {
            return (CompoundEventDetectorVO) DAO.getInstance().getJdbcTemp().queryForObject(COMPOUND_EVENT_DETECTOR_SELECT+ " where " + COMPOUND_EVENT_DETECTOR_FILTERED_BASE_ON_XID, pk , new PostgresCompoundEventDetectorDAO.CompoundEventDetectorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<CompoundEventDetectorVO> filtered(String filter, Object[] argsFilter, long limit) {
        String myLimit="";
        Object[] args;
        if (limit != NO_LIMIT) {
            myLimit = LIMIT+" ? ";
            args = DAO.getInstance().appendValue(argsFilter, String.valueOf(limit));
        } else {
            args=argsFilter;
        }

        return (List<CompoundEventDetectorVO>) DAO.getInstance().getJdbcTemp().query(COMPOUND_EVENT_DETECTOR_SELECT+" where "+ filter + myLimit, args,  new PostgresCompoundEventDetectorDAO.CompoundEventDetectorRowMapper());
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public Object[] create(final CompoundEventDetectorVO entity) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(entity);
        }

        Integer id = DAO.getInstance().getJdbcTemp().queryForObject(
                COMPOUND_EVENT_INSERT,
                new Object[] {
                        entity.getXid(),
                        entity.getName(),
                        entity.getAlarmLevel(),
                        DAO.boolToChar(entity.isReturnToNormal()),
                        DAO.boolToChar(entity.isDisabled()),
                        entity.getCondition()
                },
                Integer.class
        );

        entity.setId(id);
        return new Object[] { id };
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void update(CompoundEventDetectorVO entity) {

        DAO.getInstance().getJdbcTemp().update(COMPOUND_EVENT_UPDATE, new Object[]{
                entity.getXid(),
                entity.getName(),
                entity.getAlarmLevel(),
                DAO.boolToChar(entity.isReturnToNormal()),
                DAO.boolToChar(entity.isDisabled()),
                entity.getCondition(),
                entity.getId()
        });
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void delete(CompoundEventDetectorVO entity) {
        DAO.getInstance().getJdbcTemp().update(COMPOUND_EVENT_DELETE_EVENT_HANLDERS,new Object[]{entity.getId()});
        DAO.getInstance().getJdbcTemp().update(COMPOUND_EVENT_DELETE, new Object[]{entity.getId()});
        AuditEventUtils.raiseDeletedEvent(AuditEventType.TYPE_COMPOUND_EVENT_DETECTOR, entity);
    }

}
