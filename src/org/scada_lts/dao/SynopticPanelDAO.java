package org.scada_lts.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifierRowMapper;
import org.scada_lts.service.model.SynopticPanel;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Synoptic Panel Database Access Object
 *
 * @author Radoslaw Jajko <rjajko@softq.pl>
 * @version 1.0.0
 */
@Repository
public class SynopticPanelDAO implements CrudOperations<SynopticPanel> {

    private static final Log LOG = LogFactory.getLog(SynopticPanelDAO.class);

    private static final String COLUMN_NAME_SP_ID = "id";
    private static final String COLUMN_NAME_SP_XID = "xid";
    private static final String COLUMN_NAME_SP_NAME = "name";
    private static final String COLUMN_NAME_SP_VECTOR_IMAGE = "vectorImage";
    private static final String COLUMN_NAME_SP_COMPONENT_DATA = "componentData";

    private static final String TABLE_NAME = "synopticPanels";

    private static final String SP_SELECT_LIST = ""
            + "select "
            + COLUMN_NAME_SP_ID + ", "
            + COLUMN_NAME_SP_XID + ", "
            + COLUMN_NAME_SP_NAME + " "
            + "from "
            + TABLE_NAME;

    private static final String SP_SELECT = ""
            + "select "
            + COLUMN_NAME_SP_ID + ", "
            + COLUMN_NAME_SP_XID + ", "
            + COLUMN_NAME_SP_NAME + ", "
            + COLUMN_NAME_SP_VECTOR_IMAGE + ", "
            + COLUMN_NAME_SP_COMPONENT_DATA + " "
            + "from "
            + TABLE_NAME;

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

    @Override
    public SynopticPanel create(SynopticPanel entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        DAO.getInstance().getJdbcTemp().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SP_INSERT, Statement.RETURN_GENERATED_KEYS);
            new ArgumentPreparedStatementSetter(new Object[]{
                    entity.getXid(),
                    entity.getName(),
                    entity.getVectorImage(),
                    entity.getComponentData()
            }).setValues(ps);
            return ps;
        }, keyHolder);
        entity.setId(keyHolder.getKey().intValue());
        return entity;
    }

    @Override
    public List<ScadaObjectIdentifier> getSimpleList() {
        ScadaObjectIdentifierRowMapper mapper = ScadaObjectIdentifierRowMapper.withDefaultNames();

        return DAO.getInstance().getJdbcTemp()
                .query(mapper.selectScadaObjectIdFrom(TABLE_NAME), mapper);
    }

    @Override
    public List<SynopticPanel> getAll() {
        return DAO.getInstance().getJdbcTemp()
                .query(SP_SELECT_LIST, new SynopticPanelRowMapper());
    }

    @Override
    public SynopticPanel getById(int id) throws EmptyResultDataAccessException {
        try {
            return DAO.getInstance().getJdbcTemp()
                    .queryForObject(
                            SP_SELECT + " where " + COLUMN_NAME_SP_ID + "=?",
                            new Object[]{id},
                            new SynopticPanelRowMapper()
                    );
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Synoptic Panel entity with id= " + id + " does not exists!");
            return null;
        }
    }

    @Override
    public SynopticPanel update(SynopticPanel entity) throws EmptyResultDataAccessException {
        try {
            DAO.getInstance().getJdbcTemp().update(
                    SP_UPDATE,
                    entity.getXid(),
                    entity.getName(),
                    entity.getVectorImage(),
                    entity.getComponentData(),
                    entity.getId());
            return entity;
        } catch (EmptyResultDataAccessException e) {
            LOG.error("Synoptic Panel entity with id= " + entity.getId() + " does not exists!");
            throw e;
        }
    }

    @Override
    public int delete(int id) {
        try {
            DAO.getInstance().getJdbcTemp().update(SP_DELETE, id);
            return 0;
        } catch (Exception e) {
            String message = "FAILED ON DELETING Synoptic Panel with ID: ";
            LOG.error(message + id);
            LOG.error(e);
            return -1;
        }
    }

    static class SynopticPanelRowMapper implements RowMapper<SynopticPanel> {
        public SynopticPanel mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SynopticPanel(
                    rs.getInt(COLUMN_NAME_SP_ID),
                    rs.getString(COLUMN_NAME_SP_XID),
                    rs.getString(COLUMN_NAME_SP_NAME),
                    rs.getString(COLUMN_NAME_SP_VECTOR_IMAGE),
                    rs.getString(COLUMN_NAME_SP_COMPONENT_DATA)
            );
        }
    }
}