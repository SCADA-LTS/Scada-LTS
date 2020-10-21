package org.scada_lts.dao.alarms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationDAO {

    private static final Log LOG = LogFactory.getLog(NotificationDAO.class);


    private static final String TABLE_NAME_SCHEDULERS = "schedulers";
    private static final String TABLE_NAME_RANGES = "ranges";
    private static final String TABLE_NAME_NOTIFICATIONS = "notifications";
    private static final String TABLE_NAME_SCHEDULERS_DEFPOINTS = "schedulers_defpoints";
    private static final String TABLE_NAME_SCHEDULERS_USERS = "schedulers_users";

    private static final String VIEW_NAME_SCHEDULERS = "schedulers_view";

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_PER_MAIL = "per_mail";
    private static final String COLUMN_NAME_PER_SMS = "per_sms";
    private static final String COLUMN_NAME_MTIME = "mtime";
    private static final String COLUMN_NAME_HOUR_START = "hour_start";
    private static final String COLUMN_NAME_HOUR_STOP = "hour_stop";
    private static final String COLUMN_NAME_DESCRIPTION = "description";
    private static final String COLUMN_NAME_RANGES_ID = "ranges_id";
    private static final String COLUMN_NAME_NOTIFICATIONS_ID = "notifications_id";
    private static final String COLUMN_NAME_SCHEDULERS_ID = "schedulers_id";
    private static final String COLUMN_NAME_DATAPOINTS_ID = "dataPoints_id";
    private static final String COLUMN_NAME_USERS_ID = "users_id";

    private static final String SELECT_FROM_SCHEDULERS_VIEW_WHERE_ID = "" +
            "SELECT " +
            COLUMN_NAME_ID + ", " +
            COLUMN_NAME_PER_MAIL + ", " +
            COLUMN_NAME_PER_SMS + ", " +
            COLUMN_NAME_MTIME + ", " +
            COLUMN_NAME_HOUR_START + ", " +
            COLUMN_NAME_HOUR_STOP + ", " +
            COLUMN_NAME_DESCRIPTION + " " +
            "FROM " + VIEW_NAME_SCHEDULERS +
            "WHERE " + COLUMN_NAME_ID + "=?" +
            ";";

    private static final String INSERT_INTO_SCHEDULERS = "" +
            "INSERT INTO " + TABLE_NAME_SCHEDULERS + " (" +
            COLUMN_NAME_RANGES_ID + ", " +
            COLUMN_NAME_NOTIFICATIONS_ID + ") " +
            "VALUES (?,?);";

    private static final String SELECT_FROM_RANGES = "" +
            "SELECT " +
            COLUMN_NAME_ID + ", " +
            COLUMN_NAME_HOUR_START + ", " +
            COLUMN_NAME_HOUR_STOP + ", " +
            COLUMN_NAME_DESCRIPTION + " " +
            "FROM " + TABLE_NAME_RANGES +
            ";";

    private static final String INSERT_INTO_RANGES = "" +
            "INSERT INTO " + TABLE_NAME_RANGES + " (" +
            COLUMN_NAME_HOUR_START + ", " +
            COLUMN_NAME_HOUR_STOP + ", " +
            COLUMN_NAME_DESCRIPTION + ") " +
            "VALUES (?,?,?);";

    private static final String SELECT_FROM_NOTIFICATIONS = "" +
            "SELECT " +
            COLUMN_NAME_ID + ", " +
            COLUMN_NAME_PER_MAIL + ", " +
            COLUMN_NAME_PER_SMS + ", " +
            COLUMN_NAME_MTIME + " " +
            "FROM " + TABLE_NAME_NOTIFICATIONS +
            ";";

    private static final String INSERT_INTO_NOTIFICATIONS = "" +
            "INSERT INTO " + TABLE_NAME_NOTIFICATIONS + " (" +
            COLUMN_NAME_PER_MAIL + ", " +
            COLUMN_NAME_PER_SMS + ", " +
            COLUMN_NAME_MTIME + ") " +
            "VALUES (?,?,?);";

    private static final String INSERT_INTO_SCHEDULERS_DEFPOINTS = "" +
            "INSERT INTO " + TABLE_NAME_SCHEDULERS_DEFPOINTS + " (" +
            COLUMN_NAME_DATAPOINTS_ID + ", " +
            COLUMN_NAME_SCHEDULERS_ID + ") " +
            "VALUES (?,?)";

    private static final String SELECT_SCHEDULER_FROM_DEFPOINTS_BY_DATAPOINT_ID = "" +
            "SELECT " +
            COLUMN_NAME_ID + ", " +
            COLUMN_NAME_PER_MAIL + ", " +
            COLUMN_NAME_PER_SMS + ", " +
            COLUMN_NAME_MTIME + ", " +
            COLUMN_NAME_HOUR_START + ", " +
            COLUMN_NAME_HOUR_STOP + ", " +
            COLUMN_NAME_DESCRIPTION + " " +
//            COLUMN_NAME_DATAPOINTS_ID + " " +
            "FROM " + VIEW_NAME_SCHEDULERS + " AS s " +
            "INNER JOIN " + TABLE_NAME_SCHEDULERS_DEFPOINTS + " AS d " +
            "ON s." + COLUMN_NAME_ID + "=d." + COLUMN_NAME_SCHEDULERS_ID +
            " WHERE d." + COLUMN_NAME_DATAPOINTS_ID + "=?";


    private final JdbcTemplate jdbcTemplate;

    public NotificationDAO() {
        jdbcTemplate = DAO.getInstance().getJdbcTemp();
    }

    public List<Scheduler> getSchedulerById(Long id) {
        try {
            LOG.info(SELECT_FROM_SCHEDULERS_VIEW_WHERE_ID);
            return jdbcTemplate.query(SELECT_FROM_SCHEDULERS_VIEW_WHERE_ID,
                    new Object[]{id},
                    new SchedulerRowMapper() {
                    });
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public Long createScheduler(final Long rangeId, final Long notificationId) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_INTO_NOTIFICATIONS, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        rangeId,
                        notificationId
                }).setValues(ps);
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public List<Range> getAllRanges() {
        try {
            return jdbcTemplate.query(SELECT_FROM_RANGES,
                    new RangeRowMapper(){});
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public Range insertRange(final Range range) {
        if(LOG.isTraceEnabled()) {
            LOG.trace(range);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_INTO_RANGES, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        range.getHourStart(),
                        range.getHourStop(),
                        range.getDescription()
                }).setValues(ps);
                return ps;
            }
        }, keyHolder);
        range.setId(keyHolder.getKey().longValue());
        return range;
    }

    public List<Notification> getAllNotifications() {
        try {
            return jdbcTemplate.query(SELECT_FROM_NOTIFICATIONS,
                    new NotificationRowMapper(){});
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public Notification insertNotification(final Notification notification) {
        if(LOG.isTraceEnabled()) {
            LOG.trace(notification);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_INTO_NOTIFICATIONS, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        notification.isPerEmail() ? 1 : 0,
                        notification.isPerSms() ? 1 : 0,
                        notification.getMtime()
                }).setValues(ps);
                return ps;
            }
        }, keyHolder);
        notification.setId(keyHolder.getKey().longValue());
        return notification;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    public void bindSchedulerWithDataPoint(final Long schedulerId, final Long dataPointId) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_INTO_SCHEDULERS_DEFPOINTS, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[] {
                        dataPointId,
                        schedulerId
                }).setValues(ps);
                return ps;
            }
        });
    }

    public List<Scheduler> getDataPointSchedulers(Long dataPointId) {
        try {
            return jdbcTemplate.query(SELECT_SCHEDULER_FROM_DEFPOINTS_BY_DATAPOINT_ID,
                    new Object[]{dataPointId},
                    new SchedulerRowMapper() {
                    });
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    /* -------- RowMappers -------- */

    private class SchedulerRowMapper implements RowMapper<Scheduler> {

        @Override
        public Scheduler mapRow(ResultSet resultSet, int i) throws SQLException {
            Scheduler scheduler = new Scheduler();
            scheduler.setId(resultSet.getLong(COLUMN_NAME_ID));
            scheduler.setPer_mail(resultSet.getInt(COLUMN_NAME_PER_MAIL));
            scheduler.setPer_sms(resultSet.getInt(COLUMN_NAME_PER_SMS));
            scheduler.setMtime(resultSet.getString(COLUMN_NAME_MTIME));
            scheduler.setHour_start(resultSet.getInt(COLUMN_NAME_HOUR_START));
            scheduler.setHour_stop(resultSet.getInt(COLUMN_NAME_HOUR_STOP));
            scheduler.setDescription(resultSet.getString(COLUMN_NAME_DESCRIPTION));
            return scheduler;
        }
    }

    private class RangeRowMapper implements RowMapper<Range> {

        @Override
        public Range mapRow(ResultSet resultSet, int i) throws SQLException {
            Range range = new Range();
            range.setId(resultSet.getLong(COLUMN_NAME_ID));
            range.setHourStart(resultSet.getInt(COLUMN_NAME_HOUR_START));
            range.setHourStop(resultSet.getInt(COLUMN_NAME_HOUR_STOP));
            range.setDescription(resultSet.getString(COLUMN_NAME_DESCRIPTION));
            return range;
        }
    }

    private class NotificationRowMapper implements RowMapper<Notification> {

        @Override
        public Notification mapRow(ResultSet resultSet, int i) throws SQLException {
            Notification notif = new Notification();
            notif.setId(resultSet.getLong(COLUMN_NAME_ID));
            notif.setPerEmail(resultSet.getInt(COLUMN_NAME_PER_MAIL) == 1);
            notif.setPerSms(resultSet.getInt(COLUMN_NAME_PER_SMS) == 1);
            notif.setMtime(resultSet.getString(COLUMN_NAME_MTIME));
            return notif;
        }
    }

}
