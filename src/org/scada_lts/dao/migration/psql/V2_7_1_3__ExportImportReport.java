package org.scada_lts.dao.migration.psql;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.report.ReportPointVO;
import com.serotonin.mango.vo.report.ReportVO;
import com.serotonin.util.StringUtils;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class V2_7_1_3__ExportImportReport extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_1_3__ExportImportReport.class);

    @Override
    public void migrate(Context context) throws Exception {

        final JdbcTemplate jdbcTemplate = DAO.getInstance().getJdbcTemp();
        try {
            List<ReportVO> reports = getReports(jdbcTemplate);
            try {
                createXidColumn(jdbcTemplate);
                setMissingFields(jdbcTemplate, reports);
                updateReports(jdbcTemplate, reports);
            } finally {
                reports.clear();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private static void createXidColumn(JdbcTemplate jdbcTemplate) {
        String checkAndAddXidToReports = "ALTER TABLE reports ADD COLUMN IF NOT EXISTS xid VARCHAR(50)";

        jdbcTemplate.execute(checkAndAddXidToReports);
    }

    private static void updateReports(JdbcTemplate jdbcTemplate, List<ReportVO> reports) {
        for (ReportVO report : reports) {
            ByteArrayInputStream bais = new SerializationData().writeObject(report);

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "UPDATE reports SET data = ?, xid = ? WHERE id = ?"
                );
                ps.setBinaryStream(1, bais, bais.available());
                ps.setString(2, report.getXid());
                ps.setInt(3, report.getId());
                return ps;
            });
        }
    }

    private static Map<Integer, String> toMapDataPointIdXid(JdbcTemplate jdbcTemplate) {
        List<DataPointVO> dataPoints = jdbcTemplate.query(
                "SELECT id, xid, data FROM dataPoints",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        DataPointVO dp = (DataPointVO) ois.readObject();
                        dp.setId(rs.getInt("id"));
                        dp.setXid(rs.getString("xid"));
                        return dp;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize datapoint id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );

        return dataPoints.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(DataPointVO::getId, DataPointVO::getXid));
    }

    private static void setUsername(JdbcTemplate jdbcTemplate, ReportVO report) {
        String username = jdbcTemplate.queryForObject(
                "SELECT username FROM users WHERE id = ?",
                new Object[]{report.getUserId()},
                String.class
        );
        report.setUsername(username);
    }

    private static List<ReportVO> getReports(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(
                "SELECT id, userId, name, data FROM reports",
                (rs, rowNum) -> {
                    try (InputStream is = rs.getBinaryStream("data");
                         ObjectInputStream ois = new ObjectInputStream(is)) {
                        ReportVO report = (ReportVO) ois.readObject();
                        report.setId(rs.getInt("id"));
                        report.setUserId(rs.getInt("userId"));
                        report.setName(rs.getString("name"));
                        return report;
                    } catch (IOException | ClassNotFoundException ex) {
                        LOG.error("Failed to deserialize report id=" + rs.getInt("id"), ex);
                        return null;
                    }
                }
        );
    }

    private static void setMissingFields(JdbcTemplate jdbcTemplate, List<ReportVO> reports) {
        Map<Integer, String> dataPointIdXid = toMapDataPointIdXid(jdbcTemplate);
        try {
            for (ReportVO report : reports) {
                setUsername(jdbcTemplate, report);
                setDataPointXid(dataPointIdXid, report);
                if (StringUtils.isEmpty(report.getXid())) {
                    report.setXid(ReportVO.generateXid());
                }
            }
        } finally {
            dataPointIdXid.clear();
        }
    }

    private static void setDataPointXid(Map<Integer, String> dataPointIdXid, ReportVO report) {
        for (ReportPointVO point : report.getPoints()) {
            int dataPointId = point.getPointId();
            point.setPointXid(dataPointIdXid.get(dataPointId));
        }
    }

}
