package org.scada_lts.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

@Repository
public class SynopticPanelDAO implements GenericDAO<SynopticPanel> {

    private Log LOG = LogFactory.getLog(SynopticPanelDAO.class);

    private static final String COLUMN_NAME_SP_ID = "id";
    private static final String COLUMN_NAME_SP_XID = "xid";
    private static final String COLUMN_NAME_SP_NAME = "name";
    private static final String COLUMN_NAME_SP_VECTOR_IMAGE = "vectorImage";
    private static final String COLUMN_NAME_SP_COMPONENT_DATA = "componentData";

    private static final String SP_SELECT_LIST = ""
            + "select "
            + COLUMN_NAME_SP_ID + ", "
            + COLUMN_NAME_SP_XID + ", "
            + COLUMN_NAME_SP_NAME + " "
            + "from "
            + "synopticPanels";

    private static final String SP_SELECT = ""
            + "select "
            + COLUMN_NAME_SP_ID + ", "
            + COLUMN_NAME_SP_XID + ", "
            + COLUMN_NAME_SP_NAME + ", "
            + COLUMN_NAME_SP_VECTOR_IMAGE + ", "
            + COLUMN_NAME_SP_COMPONENT_DATA + " "
            + "from "
            + "synopticPanels";

    private static final String SP_INSERT = ""
            + "insert synopticPanels("
            + COLUMN_NAME_SP_XID + ", "
            + COLUMN_NAME_SP_NAME + ", "
            + COLUMN_NAME_SP_VECTOR_IMAGE + ", "
            + COLUMN_NAME_SP_COMPONENT_DATA + ") "
            + "values (?,?,?,?)";

    private static final String SP_UPDATE = ""
            + "update synopticPanels set "
            + COLUMN_NAME_SP_XID + "=?, "
            + COLUMN_NAME_SP_NAME + "=?, "
            + COLUMN_NAME_SP_VECTOR_IMAGE + "=?, "
            + COLUMN_NAME_SP_COMPONENT_DATA + "=? "
            + "where "
            + COLUMN_NAME_SP_ID + "=?";

    private static final String SP_DELETE = ""
            + "delete from synopticPanels where "
            + COLUMN_NAME_SP_ID + "=?";

    //create table synopticPanels (id int, xid varchar(255), name varchar(255), vectorImage TEXT, componentData TEXT);
    // mysql> create table synopticPanels (id int NOT NULL AUTO_INCREMENT, xid varchar(255), name varchar(255), vectorImage TEXT, componentData TEXT, CONSTRAINT synopticPanels_pk PRIMARY KEY (id));
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public void update(SynopticPanel entity) {
        DAO.getInstance().getJdbcTemp().update(SP_UPDATE, new Object[]{
                entity.getXid(),
                entity.getName(),
                entity.getVectorImage(),
                entity.getComponentData(),
                entity.getId(),
        });

    }

    @Override
    public void delete(SynopticPanel entity) {
        DAO.getInstance().getJdbcTemp().update(SP_DELETE, new Object[] { entity.getId() });
    }

    class SynopticRowMapper implements RowMapper<SynopticPanel> {
        public SynopticPanel mapRow(ResultSet rs, int rowNum) throws SQLException {
            SynopticPanel sp = new SynopticPanel();
            sp.setId(rs.getInt(COLUMN_NAME_SP_ID));
            sp.setXid(rs.getString(COLUMN_NAME_SP_XID));
            sp.setName(rs.getString(COLUMN_NAME_SP_NAME));
            sp.setVectorImage(rs.getString(COLUMN_NAME_SP_VECTOR_IMAGE));
            sp.setComponentData(rs.getString(COLUMN_NAME_SP_COMPONENT_DATA));
            return sp;
        }
    }

    class SynopticListRowMapper implements RowMapper<SynopticPanel> {
        public SynopticPanel mapRow(ResultSet rs, int rowNum) throws SQLException {
            SynopticPanel sp = new SynopticPanel();
            sp.setId(rs.getInt(COLUMN_NAME_SP_ID));
            sp.setXid(rs.getString(COLUMN_NAME_SP_XID));
            sp.setName(rs.getString(COLUMN_NAME_SP_NAME));
            return sp;
        }
    }

    public List<SynopticPanel> findAll() {
        return (List<SynopticPanel>) DAO.getInstance().getJdbcTemp().query(SP_SELECT_LIST, new Object[]{}, new SynopticRowMapper());
    }

    @Override
    public SynopticPanel findById(Object[] pk) {
        try {
            return (SynopticPanel) DAO.getInstance().getJdbcTemp().queryForObject(SP_SELECT + " where " + COLUMN_NAME_SP_ID + "=?", pk, new SynopticRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<SynopticPanel> filtered(String filter, Object[] argsFilter, long limit) {
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    public Object[] create(final SynopticPanel entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(SP_INSERT, Statement.RETURN_GENERATED_KEYS);
                new ArgumentPreparedStatementSetter(new Object[]{
                        entity.getXid(),
                        entity.getName(),
                        entity.getVectorImage(),
                        entity.getComponentData()
                }).setValues(ps);
                return ps;
            }
        }, keyHolder);
        entity.setId(keyHolder.getKey().intValue());
        return new Object[]{keyHolder.getKey().intValue()};
    }

    public List<SynopticPanel> getPanelSelectList() {
        return DAO.getInstance().getJdbcTemp().query(SP_SELECT_LIST, new SynopticListRowMapper());
    }
}
