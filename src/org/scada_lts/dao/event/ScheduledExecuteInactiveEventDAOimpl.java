package org.scada_lts.dao.event;

import com.mysql.jdbc.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.utils.QueryUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ScheduledExecuteInactiveEventDAOimpl implements ScheduledExecuteInactiveEventDAO {

    private static final String COLUMN_NAME_MAILING_LIST_ID = "mailingListId";
    private static final String COLUMN_NAME_SOURCE_EVENT_ID = "sourceEventId";
    private static final String COLUMN_NAME_EVENT_HANDLER_ID = "eventHandlerId";
    private static final String TABLE_NAME = "scheduledExecuteInactiveEvent";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE = ""
            + "select "
            + COLUMN_NAME_MAILING_LIST_ID + ", "
            + COLUMN_NAME_SOURCE_EVENT_ID + ", "
            + COLUMN_NAME_EVENT_HANDLER_ID + " "
            + "from "
            + TABLE_NAME + " "
            + "where "
            + COLUMN_NAME_MAILING_LIST_ID + "=? ";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT = ""
            + "select "
            + COLUMN_NAME_MAILING_LIST_ID + ", "
            + COLUMN_NAME_SOURCE_EVENT_ID + ", "
            + COLUMN_NAME_EVENT_HANDLER_ID + " "
            + "from "
            + TABLE_NAME;

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_INSERT = ""
            + "insert into "
            + TABLE_NAME + " ("
            + COLUMN_NAME_MAILING_LIST_ID + ", "
            + COLUMN_NAME_SOURCE_EVENT_ID + ", "
            + COLUMN_NAME_EVENT_HANDLER_ID + ") "
            + "values (?,?,?)";

    private static final String SCHEDULED_INACTIVE_COMMUNICATION_EVENT_DELETE = ""
            + "delete from "
            + TABLE_NAME + " "
            + "where "
            + COLUMN_NAME_MAILING_LIST_ID + "=? "
            + "and "
            + COLUMN_NAME_SOURCE_EVENT_ID + "=? "
            + "and "
            + COLUMN_NAME_EVENT_HANDLER_ID + "=? ";

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
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select(int mailingListId) mailingListId:" + mailingListId);
        }

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE, new Object[] {mailingListId},
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId, int limit) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select(int mailingListId) mailingListId:" + mailingListId);
        }

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE + " LIMIT " + limit, new Object[] {mailingListId},
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId,
                                                                     List<ScheduledExecuteInactiveEvent> exclude,
                                                                     int limit) {
        if(!exclude.isEmpty())
            selectWithNotIn(mailingListId, exclude, limit);
        return selectByMailingListId(mailingListId, limit);
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> select(int limit) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select():");
        }

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT + " LIMIT " + limit,
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public ScheduledExecuteInactiveEvent insert(ScheduledExecuteInactiveEvent scheduledExecuteInactiveEvent) {

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
                        scheduledExecuteInactiveEvent.getEventHandlerId()
                }).setValues(preparedStatement);
                return preparedStatement;
            }
        }, keyHolder);

        return scheduledExecuteInactiveEvent;
    }

    @Override
    public void delete(ScheduledExecuteInactiveEvent event) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("delete(ScheduledInactiveCommunicationEvent getMailingListId) getMailingListId:" + event);
        }

        jdbcTemplate.update(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_DELETE, event.getMailingListId(), event.getSourceEventId(), event.getEventHandlerId());
    }

    private static class ScheduledExecuteInactiveEventRowMapper implements RowMapper<ScheduledExecuteInactiveEvent> {

        @Override
        public ScheduledExecuteInactiveEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ScheduledExecuteInactiveEvent(rs.getInt(COLUMN_NAME_EVENT_HANDLER_ID),
                    rs.getInt(COLUMN_NAME_SOURCE_EVENT_ID),
                    rs.getInt(COLUMN_NAME_MAILING_LIST_ID));
        }
    }

    private List<ScheduledExecuteInactiveEvent> selectWithNotIn(int mailingListId, List<ScheduledExecuteInactiveEvent> exclude, int limit) {
        Exclude excludeEventHandlers = new Exclude(COLUMN_NAME_EVENT_HANDLER_ID, getEventHandlerIds(exclude));
        Exclude excludeEvents = new Exclude(COLUMN_NAME_SOURCE_EVENT_ID, getEventIds(exclude));

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE
                        + " AND " + excludeEventHandlers.toSql()
                        + " AND " + excludeEvents.toSql()
                        + " ORDER BY " + COLUMN_NAME_SOURCE_EVENT_ID + " DESC"
                        + " LIMIT " + limit, mergeWithOrder(mailingListId,
                excludeEventHandlers.getArgs(), excludeEvents.getArgs(  )),
                new ScheduledExecuteInactiveEventRowMapper());
    }

    private static List<Integer> getEventHandlerIds(List<ScheduledExecuteInactiveEvent> exclude) {
        return exclude.stream().map(ScheduledExecuteInactiveEvent::getEventHandlerId).collect(Collectors.toList());
    }

    private static List<Integer> getEventIds(List<ScheduledExecuteInactiveEvent> exclude) {
        return exclude.stream().map(ScheduledExecuteInactiveEvent::getSourceEventId).collect(Collectors.toList());
    }

    private static class Exclude {
        private String columnName;
        private List<Integer> args;

        public Exclude(String columnName, List<Integer> args) {
            this.columnName = columnName;
            this.args = args;
        }

        public String toSql() {
            return columnName + " NOT IN (" + QueryUtils.getArgsIn(args.size()) + ")";
        }

        public List<Integer> getArgs() {
            return args;
        }

        @Override
        public String toString() {
            return toSql();
        }
    }

    private Integer[] mergeWithOrder(Integer id, List<Integer>... ids) {
        List<Integer> result = new ArrayList<>();
        result.add(id);
        for(List<Integer> list: ids) {
            result.addAll(list);
        }
        return result.toArray(new Integer[]{});
    }
 }
