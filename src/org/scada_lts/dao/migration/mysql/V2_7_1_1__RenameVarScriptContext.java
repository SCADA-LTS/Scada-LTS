package org.scada_lts.dao.migration.mysql;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.vo.DataPointVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class V2_7_1_1__RenameVarScriptContext extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_1__RenameVarScriptContext.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            migrateScripts(jdbcTmp);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void migrateScripts(JdbcTemplate jdbcTmp) {
        List<ContextualizedScriptVO> scripts = jdbcTmp.query("SELECT id, script, data FROM scripts", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                ContextualizedScriptVO scriptVO = (ContextualizedScriptVO) objectInputStream.readObject();
                scriptVO.setId(resultSet.getInt("id"));
                String updatedScript = updateScript(resultSet.getString("script"), scriptVO.getPointsOnContext());
                jdbcTmp.update("UPDATE scripts set script = ? WHERE id = ?",
                        updatedScript, resultSet.getInt("id"));
                updatePointsOnContext(scriptVO.getPointsOnContext());

                return scriptVO;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        });

        boolean isNull = scripts.stream().anyMatch(Objects::isNull);
        if (isNull) {
            throw new IllegalStateException("ContextualizedScriptVO is null!");
        }

        for (ContextualizedScriptVO script : scripts) {
            jdbcTmp.update("UPDATE scripts set data = ? WHERE id = ?",
                    new SerializationData().writeObject(script), script.getId());
        }
    }

    private String updateScript(String script, List<IntValuePair> pointsOnContext) {
        List<IntValuePair> sorted = new ArrayList<>(pointsOnContext);
        sorted.sort(Comparator.comparing(IntValuePair::getKey));
        Collections.reverse(sorted);
        for (IntValuePair point : sorted) {
            if (point.getValue().matches("^p[0-9]+$")) {
                String xid = new DataPointDAO().getDataPoint(point.getKey()).getXid();
                script = script.replaceAll(point.getValue(), xid.toLowerCase().trim());
            }
        }
        return script;
    }

    private void updatePointsOnContext(List<IntValuePair> pointsOnContext) {
        for (IntValuePair point : pointsOnContext) {
            if (point.getValue().matches("^p[0-9]+$")) {
                DataPointVO dataPointVO = new DataPointDAO().getDataPoint(point.getKey());
                point.setValue(dataPointVO.getXid().toLowerCase().trim());
            }
        }
    }

}