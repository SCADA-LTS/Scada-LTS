package org.scada_lts.dao.migration.postgres;

import com.serotonin.mango.view.View;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_8_0__AddAssigneeColumnsToEvents extends BaseJavaMigration {

    private static final Logger LOG = LoggerFactory.getLogger(V2_8_0__AddAssigneeColumnsToEvents.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        addAssigneeColumn(jdbcTemplate);
        updateViewComponents(jdbcTemplate);
    }


    private void addAssigneeColumn(JdbcTemplate jdbcTemplate) {
        String checkColumnSql = "SELECT 1 FROM information_schema.columns WHERE table_name = ? AND column_name = ?";

        boolean hasAssigneeTs = !jdbcTemplate.query(
                checkColumnSql,
                new Object[]{"events", "assigneets"},
                (rs, rowNum) -> rs.getInt(1)
        ).isEmpty();

        boolean hasAssigneeUsername = !jdbcTemplate.query(
                checkColumnSql,
                new Object[]{"events", "assigneeusername"},
                (rs, rowNum) -> rs.getInt(1)
        ).isEmpty();

        if (!hasAssigneeTs) {
            jdbcTemplate.execute("ALTER TABLE events ADD COLUMN assigneeTs BIGINT DEFAULT NULL");
        }

        if (!hasAssigneeUsername) {
            jdbcTemplate.execute("ALTER TABLE events ADD COLUMN assigneeUsername VARCHAR(40) DEFAULT NULL");
        }
    }

    private void updateViewComponents(JdbcTemplate jdbcTemplate) {
        List<View> views = jdbcTemplate.query("SELECT id, data FROM mangoViews", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                View view = (View) objectInputStream.readObject();
                view.setId(resultSet.getInt("id"));
                return view;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error("Deserialization error for mangoView id=" + resultSet.getInt("id"), ex);
                return null;
            }
        });

        if (views.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one View is null!");
        }

        for (View view : views) {
            jdbcTemplate.update("UPDATE mangoViews SET data = ? WHERE id = ?",
                    new SerializationData().writeObject(view), view.getId());
        }
    }


}
