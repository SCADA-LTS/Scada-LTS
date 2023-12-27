package org.scada_lts.dao.migration.mysql;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Objects;

public class V2_7_5_4_1__UpdateAbsoluteToRelativeUrlInLinkComponent extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_4_1__UpdateAbsoluteToRelativeUrlInLinkComponent.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            migrate(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

    }

    private void migrate(JdbcTemplate jdbcTmp) {
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
            for(ViewComponent viewComponent: view.getViewComponents()) {
                if(viewComponent instanceof LinkComponent) {
                    LinkComponent linkComponent = (LinkComponent) viewComponent;
                    String link = linkComponent.getLink();
                    if(link != null) {
                        int index = link.lastIndexOf("/");
                        if(index != -1)
                            linkComponent.setLink(link.substring(index + 1));
                    }
                }
            }
        }

        for (View view : views) {
            jdbcTmp.update("UPDATE mangoViews set data = ? WHERE id = ?",
                    new SerializationData().writeObject(view), view.getId());
        }
    }
}
