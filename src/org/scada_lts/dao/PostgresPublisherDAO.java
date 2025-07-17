package org.scada_lts.dao;

import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.List;

public class PostgresPublisherDAO implements IPublisherDAO {

    private static final Log LOG = LogFactory.getLog(PublisherDAO.class);

    private static final String COLUMN_NAME_ID = "id";
    private static final String COLUMN_NAME_XID = "xid";
    private static final String COLUMN_NAME_DATA = "data";

    private static final String COLUMN_NAME_EH_EVENT_TYPE_ID = "eventTypeId";
    private static final String COLUMN_NAME_EH_EVENT_TYPE_REF = "eventTypeRef1";

    // @formatter:off
    private static final String PUBLISHER_SELECT = ""
            + "select "
            + COLUMN_NAME_ID + ", "
            + COLUMN_NAME_XID + ", "
            + COLUMN_NAME_DATA + " "
            + "from publishers ";

    private static final String PUBLISHER_INSERT = ""
            + "insert into publishers ("
            + COLUMN_NAME_XID + ", "
            + COLUMN_NAME_DATA + ") "
            + "values (?,?) ";

    private static final String PUBLISHER_UPDATE = ""
            + "update publishers set "
            + COLUMN_NAME_XID + "=?, "
            + COLUMN_NAME_DATA + "=? "
            + "where "
            + COLUMN_NAME_ID + "=? ";

    private static final String PUBLISHER_DELETE = ""
            + "delete from publishers where "
            + COLUMN_NAME_ID + "=? ";

    private static final String EVENT_HANDLER_DELETE = ""
            + "delete from eventHandlers where "
            + COLUMN_NAME_EH_EVENT_TYPE_ID + "="
            + EventType.EventSources.PUBLISHER
            + " and "
            + COLUMN_NAME_EH_EVENT_TYPE_REF + "=? ";
    // @formatter:on

    private class PublisherRowMapper implements RowMapper<PublisherVO<? extends PublishedPointVO>> {
        @Override
        public PublisherVO<? extends PublishedPointVO> mapRow(ResultSet rs, int rowNum) throws SQLException {
            byte[] dataBytes = rs.getBytes("data");

            ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
            PublisherVO<? extends PublishedPointVO> publisher =
                    (PublisherVO<? extends PublishedPointVO>) new SerializationData().readObject(bais);

            publisher.setId(rs.getInt("id"));
            publisher.setXid(rs.getString("xid"));
            return publisher;
        }
    }

    @Override
    public PublisherVO<? extends PublishedPointVO> getPublisher(int id) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("getPublisher(int id) id:" + id);
        }

        String templateSelectWhere = PUBLISHER_SELECT + "where " + COLUMN_NAME_ID + "=? ";

        PublisherVO<?> publisher;
        try {
            publisher = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhere, new Object[] {id}, new PostgresPublisherDAO.PublisherRowMapper());
        } catch (EmptyResultDataAccessException e) {
            publisher = null;
        }
        return publisher;
    }

    @Override
    public PublisherVO<? extends PublishedPointVO> getPublisher(String xid) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("getPublisher(String xid) xid:" + xid);
        }

        String templateSelectWhere = PUBLISHER_SELECT + "where " + COLUMN_NAME_XID + "=? ";

        PublisherVO<?> publisher;
        try {
            publisher = DAO.getInstance().getJdbcTemp().queryForObject(templateSelectWhere, new Object[] {xid}, new PostgresPublisherDAO.PublisherRowMapper());;
        } catch (EmptyResultDataAccessException e) {
            publisher = null;
        }
        return publisher;
    }

    @Override
    public List<PublisherVO<? extends PublishedPointVO>> getPublishers() {

        if (LOG.isTraceEnabled()) {
            LOG.trace("getPublishers()");
        }

        return DAO.getInstance().getJdbcTemp().query(PUBLISHER_SELECT, new PostgresPublisherDAO.PublisherRowMapper());
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public int insert(final PublisherVO<? extends PublishedPointVO> publisher) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("insert(final PublisherVO<? extends PublishedPointVO> publisher) publisher:" + publisher.toString());
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(PUBLISHER_INSERT, new String[]{"id"});
                ps.setString(1, publisher.getXid());

                ByteArrayInputStream bais = new SerializationData().writeObject(publisher);
                byte[] serialized = bais.readAllBytes();

                ps.setBytes(2, serialized);

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }




    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void update(PublisherVO<? extends PublishedPointVO> publisher) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("update(PublisherVO<? extends PublishedPointVO> publisher) publisher:" + publisher.toString());
        }

        DAO.getInstance().getJdbcTemp().update(PUBLISHER_UPDATE, new Object[] {publisher.getXid(), new SerializationData().writeObject(publisher), publisher.getId()});
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void delete(int id) {

        if (LOG.isTraceEnabled()) {
            LOG.trace("delete(int id) id:" + id);
        }

        DAO.getInstance().getJdbcTemp().update(EVENT_HANDLER_DELETE, new Object[] {id});
        DAO.getInstance().getJdbcTemp().update(PUBLISHER_DELETE, new Object[] {id});
    }
}
