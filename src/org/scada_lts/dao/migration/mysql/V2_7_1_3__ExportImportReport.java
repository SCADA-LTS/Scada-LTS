package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class V2_7_1_3__ExportImportReport extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_3__ExportImportReport.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTmp = DAO.getInstance().getJdbcTemp();
        try {
            List<ReportVO> reports = getReports(jdbcTmp);
            try {
                createXidColumn(jdbcTmp);
                setMissingFields(jdbcTmp, reports);
                updateReports(jdbcTmp, reports);
            } finally {
                reports.clear();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private static List<ReportVO> getReports(JdbcTemplate jdbcTmp) {
        return jdbcTmp.query("SELECT id, userId, name, data FROM reports", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                ReportVO report = (ReportVO) objectInputStream.readObject();
                report.setId(resultSet.getInt("id"));
                report.setUserId(resultSet.getInt("userId"));
                report.setName(resultSet.getString("name"));
                return report;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        });
    }

    private static void setMissingFields(JdbcTemplate jdbcTmp, List<ReportVO> reports) {
        Map<Integer, String> dataPointIdXid = toMapDataPointIdXid(jdbcTmp);
        try {
            for (ReportVO report : reports) {
                setUsername(jdbcTmp, report);
                setDataPointXid(dataPointIdXid, report);
                if (StringUtils.isEmpty(report.getXid()))
                    report.setXid(ReportVO.generateXid());
            }
        } finally {
            dataPointIdXid.clear();
        }
    }

    private static void createXidColumn(JdbcTemplate jdbcTmp) {
        jdbcTmp.execute("ALTER TABLE reports ADD xid VARCHAR(50) DEFAULT NULL;");
    }

    private static void updateReports(JdbcTemplate jdbcTmp, List<ReportVO> reports) {
        for (ReportVO report : reports) {
            jdbcTmp.update("UPDATE reports set data = ?, xid = ? WHERE id = ?", new SerializationData().writeObject(report), report.getXid(), report.getId());
        }
    }

    private static void setUsername(JdbcTemplate jdbcTmp, ReportVO report) {
        String username = jdbcTmp.queryForObject("SELECT username FROM users WHERE id=?", new Object[]{report.getUserId()}, String.class);
        report.setUsername(username);
    }

    private static void setDataPointXid(Map<Integer, String> dataPointIdXid, ReportVO report) {
        for(ReportPointVO point: report.getPoints()) {
            int dataPointId = point.getPointId();
            point.setPointXid(dataPointIdXid.get(dataPointId));
        }
    }
    
    private static Map<Integer, String> toMapDataPointIdXid(JdbcTemplate jdbcTmp) {
        List<DataPointVO> dataPoints = jdbcTmp.query("SELECT id, xid, data FROM dataPoints", (resultSet, i) -> {
            try (InputStream inputStream = resultSet.getBinaryStream("data");
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                DataPointVO dataPoint = (DataPointVO) objectInputStream.readObject();
                dataPoint.setId(resultSet.getInt("id"));
                dataPoint.setXid(resultSet.getString("xid"));
                return dataPoint;
            } catch (IOException | ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
                return null;
            }
        });
        return dataPoints.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DataPointVO::getId, DataPointVO::getXid));
    }
}
