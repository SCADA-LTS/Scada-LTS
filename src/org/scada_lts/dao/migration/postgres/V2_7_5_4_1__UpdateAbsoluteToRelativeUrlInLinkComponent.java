package org.scada_lts.dao.migration.postgres;

import br.org.scadabr.view.component.LinkComponent;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.ViewComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class V2_7_5_4_1__UpdateAbsoluteToRelativeUrlInLinkComponent extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_4_1__UpdateAbsoluteToRelativeUrlInLinkComponent.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();

        try {
            updateAbsoluteToRelativeUrlInLinkComponent(jdbcTemplate);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

    }

    private void updateAbsoluteToRelativeUrlInLinkComponent (JdbcTemplate jdbcTemplate) {
        List<View> views = jdbcTemplate.query("SELECT id, data FROM mangoViews", (rs, rowNum) -> {
            try (InputStream is = rs.getBinaryStream("data");
                 ObjectInputStream ois = new ObjectInputStream(is)) {
                View view = (View) ois.readObject();
                view.setId(rs.getInt("id"));
                return view;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error("Deserialization error for view ID: " + rs.getInt("id"), ex);
                return null;
            }
        });

        if (views.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("At least one View is null!");
        }

        for (View view : views) {
            for (ViewComponent component : view.getViewComponents()) {
                if (component instanceof LinkComponent) {
                    LinkComponent linkComponent = (LinkComponent) component;
                    String link = linkComponent.getLink();
                    if (link != null) {
                        int index = link.lastIndexOf("/");
                        if (index != -1) {
                            linkComponent.setLink(link.substring(index + 1));
                        }
                    }
                }
            }
        }

        for (View view : views) {
            ByteArrayInputStream bais = new SerializationData().writeObject(view);
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement("UPDATE mangoViews SET data = ? WHERE id = ?");
                ps.setBinaryStream(1, bais, bais.available());
                ps.setInt(2, view.getId());
                return ps;
            });
        }
    }
}
