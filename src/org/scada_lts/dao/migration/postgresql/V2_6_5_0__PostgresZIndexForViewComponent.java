package org.scada_lts.dao.migration.postgresql;

import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.ViewComponent;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

public class V2_6_5_0__PostgresZIndexForViewComponent extends BaseJavaMigration {

    private static final int Z_INDEX_DEFAULT = 2;
    private static final int Z_INDEX_MIN = 1;

    @Override
    public void migrate(Context context) throws Exception {
        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        migrateViews(jdbcTemplate);
    }

    private void migrateViews(JdbcTemplate jdbcTemplate) {
        List<View> views = jdbcTemplate.query(
                "SELECT id, data FROM mangoViews",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        View v = (View) ois.readObject();
                        v.setId(rs.getInt("id"));
                        return v;
                    } catch (IOException | ClassNotFoundException ex) {
                        throw new RuntimeException("view deserialization error id=" + rs.getInt("id"), ex);
                    }
                }
        );

        if (views.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one View is null!");
        }

        for (View v : views) {
            for (ViewComponent c : v.getViewComponents()) {
                if (c.getZ() < Z_INDEX_MIN) {
                    c.setZ(Z_INDEX_DEFAULT);
                }
            }
        }

        for (View v : views) {
            ByteArrayInputStream bais = new SerializationData().writeObject(v);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE mangoViews SET data = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, v.getId());
                return ps;
            });
        }
    }
}
