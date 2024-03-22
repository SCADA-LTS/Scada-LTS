package org.scada_lts.dao.event;

import java.sql.Statement;
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
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE
                        + " ORDER BY " + COLUMN_NAME_SOURCE_EVENT_ID + " ASC", new Object[] {mailingListId},
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId, int limit) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select(int mailingListId) mailingListId:" + mailingListId);
        }

        if(limit <= 0)
            return Collections.emptyList();

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE
                        + " ORDER BY " + COLUMN_NAME_SOURCE_EVENT_ID + " ASC"
                        + " LIMIT " + limit, new Object[] {mailingListId},
                new ScheduledExecuteInactiveEventRowMapper());
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> selectByMailingListId(int mailingListId,
                                                                     List<ScheduledExecuteInactiveEvent> exclude,
                                                                     int limit) {
        if(limit <= 0)
            return Collections.emptyList();
        if(!exclude.isEmpty())
            return selectWithNot(mailingListId, exclude, limit);
        return selectByMailingListId(mailingListId, limit);
    }

    @Override
    public List<ScheduledExecuteInactiveEvent> select(int limit) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("select():");
        }

        if(limit <= 0)
            return Collections.emptyList();

        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT
                        + " ORDER BY " + COLUMN_NAME_SOURCE_EVENT_ID + " ASC"
                        + " LIMIT " + limit,
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

    private List<ScheduledExecuteInactiveEvent> selectWithNot(int mailingListId, List<ScheduledExecuteInactiveEvent> exclude, int limit) {
        ExcludePair excludePair = new ExcludePair(COLUMN_NAME_EVENT_HANDLER_ID, COLUMN_NAME_SOURCE_EVENT_ID,
                toPairs(getEventHandlerIds(exclude), getEventIds(exclude)));
        return jdbcTemplate.query(SCHEDULED_INACTIVE_COMMUNICATION_EVENT_SELECT_WHERE
                        + excludePair.toSql()
                        + " ORDER BY " + COLUMN_NAME_SOURCE_EVENT_ID + " ASC"
                        + " LIMIT " + limit, mergeWithOrder(mailingListId, excludePair.getArgs()),
                new ScheduledExecuteInactiveEventRowMapper());
    }

    private static Set<Integer> getEventHandlerIds(List<ScheduledExecuteInactiveEvent> exclude) {
        return exclude.stream().map(ScheduledExecuteInactiveEvent::getEventHandlerId).collect(Collectors.toSet());
    }

    private static Set<Integer> getEventIds(List<ScheduledExecuteInactiveEvent> exclude) {
        return exclude.stream().map(ScheduledExecuteInactiveEvent::getSourceEventId).collect(Collectors.toSet());
    }

    private static class Pair {
        private Integer value1;
        private Integer value2;

        public Pair(Integer value1, Integer value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair pair = (Pair) o;
            return Objects.equals(value1, pair.value1) &&
                    Objects.equals(value2, pair.value2);
        }

        @Override
        public int hashCode() {

            return Objects.hash(value1, value2);
        }
    }

    private static class ExcludePair {
        private String columnName1;
        private String columnName2;
        private List<Pair> pairs;

        public ExcludePair(String columnName1, String columnName2, List<Pair> pairs) {
            this.columnName1 = columnName1;
            this.columnName2 = columnName2;
            this.pairs = pairs;
        }

        public String toSql() {
            StringBuilder toSql = new StringBuilder();
            String andNot = MessageFormat.format( " AND NOT ({0}=? AND {1}=?)", columnName1, columnName2);
            for(int i = 0; i < pairs.size(); i++)
                toSql.append(andNot);
            return toSql.toString();
        }

        public List<Integer> getArgs() {
            return pairs.stream()
                    .flatMap(a -> Stream.of(a.value1, a.value2))
                    .collect(Collectors.toList());
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

    private List<Pair> toPairs(Set<Integer> args1, Set<Integer> args2) {
        List<Pair> pairs = new ArrayList<>();
        for(Integer arg1: args1) {
            for(Integer arg2: args2) {
                pairs.add(new Pair(arg1, arg2));
            }
        }
        return pairs;
    }
 }
