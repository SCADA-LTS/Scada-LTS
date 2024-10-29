package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.view.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_8_0__AddAssigneeColumnsToEvents extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_8_0__AddAssigneeColumnsToEvents.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            addAssigneeColumn(jdbcTmp);
            updateViewComponents(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void addAssigneeColumn(JdbcTemplate jdbcTmp) {

        boolean existsAssigneeTsColumn = jdbcTmp.queryForObject("SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='events' AND `COLUMN_NAME`='assigneeTs') IS NOT NULL;", boolean.class);
        boolean existsAssigneeUsernameColumn = jdbcTmp.queryForObject("SELECT (SELECT `TABLE_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`= DATABASE() AND `TABLE_NAME`='events' AND `COLUMN_NAME`='assigneeUsername') IS NOT NULL;", boolean.class);

        if(!existsAssigneeTsColumn)
            jdbcTmp.update("ALTER TABLE events ADD COLUMN assigneeTs bigint DEFAULT NULL;");

        if(!existsAssigneeUsernameColumn)
            jdbcTmp.update("ALTER TABLE events ADD COLUMN assigneeUsername VARCHAR(40) DEFAULT NULL;");
    }

    private void updateViewComponents(JdbcTemplate jdbcTmp) {
        List<View> views = jdbcTmp.query("SELECT id, data FROM mangoViews", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                View view = (View) objectInputStream.readObject();
                view.setId(resultSet.getInt("id"));
                return view;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        });

        boolean isNull = views.stream().anyMatch(Objects::isNull);
        if (isNull) {
            throw new IllegalStateException("View is null!");
        }

        for (View view : views) {
            jdbcTmp.update("UPDATE mangoViews set data = ? WHERE id = ?",
                    new SerializationData().writeObject(view), view.getId());
        }
    }
}
