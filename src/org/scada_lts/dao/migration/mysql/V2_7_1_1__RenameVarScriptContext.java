package org.scada_lts.dao.migration.mysql;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
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
import java.util.regex.Pattern;

public class V2_7_1_1__RenameVarScriptContext extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_1__RenameVarScriptContext.class);

    private static final Pattern REGEX = Pattern.compile("^p[0-9]+$");

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();

        try {
            migrateScripts(jdbcTmp);
            migrateMetaDataPoints(jdbcTmp);
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

    private void migrateMetaDataPoints(JdbcTemplate jdbcTmp) {
        List<DataPointVO> datapoints = jdbcTmp.query("SELECT id, data FROM datapoints", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                DataPointVO dataPointVO = (DataPointVO) objectInputStream.readObject();
                dataPointVO.setId(resultSet.getInt("id"));
                if (dataPointVO.getPointLocator() instanceof MetaPointLocatorVO)
                    updateMetaDataPoint(dataPointVO.getPointLocator());

                return dataPointVO;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        });

        boolean isNull = datapoints.stream().anyMatch(Objects::isNull);
        if (isNull) {
            throw new IllegalStateException("ContextualizedScriptVO is null!");
        }

        for (DataPointVO datapoint : datapoints) {
            jdbcTmp.update("UPDATE datapoints set data = ? WHERE id = ?",
                    new SerializationData().writeObject(datapoint), datapoint.getId());
        }
    }

    private String updateScript(String script, List<IntValuePair> pointsOnContext) {
        String updated = script;
        for (IntValuePair point : pointsOnContext) {
            if (REGEX.matcher(point.getValue()).matches()) {
                DataPointVO dp = new DataPointDAO().getDataPoint(point.getKey());
                String xid = dp.getXid();
                updated = updated.replaceAll("(?<![a-zA-Z0-9])" + point.getValue() + "(?![a-zA-Z0-9])", xid.toLowerCase().trim());
            }
        }
        return updated;
    }

    private void updatePointsOnContext(List<IntValuePair> pointsOnContext) {
        for (IntValuePair point : pointsOnContext) {
            if (REGEX.matcher(point.getValue()).matches()) {
                DataPointVO dataPointVO = new DataPointDAO().getDataPoint(point.getKey());
                point.setValue(dataPointVO.getXid().toLowerCase().trim());
            }
        }
    }

    private void updateMetaDataPoint(MetaPointLocatorVO locator) {
        locator.setScript(updateScript(locator.getScript(), locator.getContext()));
        updatePointsOnContext(locator.getContext());
    }

}