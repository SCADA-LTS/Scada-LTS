package org.scada_lts.dao.event;

import com.mysql.jdbc.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

class ScheduledExecuteInactiveEventDAOimpl implements ScheduledExecuteInactiveEventDAO {

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_MAILING_LIST_ID = "mailingListId";
    private static final String COLUMN_NAME_SOURCE_EVENT_ID = "sourceEventId";
    private static final String COLUMN_NAME_EVENT_HANDLER_TYPE = "eventHandlerType";
    private static final String TABLE_NAME = "scheduledExecuteInactiveEvent";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE = ""
            + "select "
            + COLUMN_NAME_ID + ", "
            + COLUMN_NAME_MAILING_LIST_ID + ", "
            + COLUMN_NAME_SOURCE_EVENT_ID + ", "
            + COLUMN_NAME_EVENT_HANDLER_TYPE + " "
            + "from "
            + TABLE_NAME
            + "where "
            + COLUMN_NAME_MAILING_LIST_ID + "=? ";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT = ""
            + "select "
            + COLUMN_NAME_ID + ", "
            + COLUMN_NAME_MAILING_LIST_ID + ", "
            + COLUMN_NAME_SOURCE_EVENT_ID + ", "
            + COLUMN_NAME_EVENT_HANDLER_TYPE + " "
            + "from "
            + TABLE_NAME ;

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_INSERT = ""
            + "insert into "
            + TABLE_NAME + " ("
            + COLUMN_NAME_MAILING_LIST_ID + ", "
            + COLUMN_NAME_SOURCE_EVENT_ID + ", "
            + COLUMN_NAME_EVENT_HANDLER_TYPE + ") "
            + "values (?,?,?)";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_DELETE = ""
            + "delete from "
            + TABLE_NAME + " "
            + "where "
            + COLUMN_NAME_MAILING_LIST_ID + "=? "
            + "and "
            + COLUMN_NAME_SOURCE_EVENT_ID + "=? ";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_DELETE_LAST = ""
            + "delete from "
            + TABLE_NAME + " "
            + "where id=min("
            + COLUMN_NAME_ID + ") "
            + "and "
            + COLUMN_NAME_MAILING_LIST_ID + "=?";

    private static final Log LOG = LogFactory.getLog(ScheduledExecuteInactiveEventDAOimpl.class);

    private final JdbcTemplate jdbcTemplate;

    private static class LazyHolder {
        public static final ScheduledExecuteInactiveEventDAO INSTANCE = new ScheduledExecuteInactiveEventDAOimpl();
    }

    ScheduledExecuteInactiveEventDAOimpl() {
        this.jdbcTemplate = DAO.getInstance().getJdbcTemp();
    }

    ScheduledExecuteInactiveEventDAOimpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    static ScheduledExecuteInactiveEventDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> select(int mailingListId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select(int mailingListId) mailingListId:" + mailingListId);
        }

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE, new Object[] {mailingListId},
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> select() {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select():");
        }

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT,
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public int insert(ScheduledExecuteInactiveEvent scheduledExecuteInactiveEvent) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("insert(ScheduledExecuteInactiveEvent scheduledExecuteInactiveEvent): " + scheduledExecuteInactiveEvent);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_INSERT,
                        Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        scheduledExecuteInactiveEvent.getMailingListId(),
                        scheduledExecuteInactiveEvent.getSourceEventId(),
                        scheduledExecuteInactiveEvent.getEventHandlerType()
                }).setValues(preparedStatement);
                return preparedStatement;
            }
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public void delete(ScheduledExecuteInactiveEvent event) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("delete(ScheduledInactiveCommunicationEvent getMailingListId) getMailingListId:" + event);
        }

        jdbcTemplate.update(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_DELETE, event.getMailingListId(), event.getSourceEventId());
    }

    @Override
    public void deleteLast(int mailingListId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("delete(ScheduledInactiveCommunicationEvent getMailingListId) getMailingListId:" + mailingListId);
        }

        jdbcTemplate.update(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_DELETE_LAST, mailingListId);
    }

    private static class ScheduledExecuteInactiveEventRowMapper implements RowMapper<ScheduledExecuteInactiveEvent> {

        @Override
        public ScheduledExecuteInactiveEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScheduledExecuteInactiveEvent mailingList = new ScheduledExecuteInactiveEvent();
            mailingList.setId(rs.getInt(COLUMN_NAME_ID));
            mailingList.setMailingListId(rs.getInt(COLUMN_NAME_MAILING_LIST_ID));
            mailingList.setSourceEventId(rs.getInt(COLUMN_NAME_SOURCE_EVENT_ID));
            mailingList.setEventHandlerType(rs.getInt(COLUMN_NAME_EVENT_HANDLER_TYPE));
            return mailingList;
        }
    }
}
