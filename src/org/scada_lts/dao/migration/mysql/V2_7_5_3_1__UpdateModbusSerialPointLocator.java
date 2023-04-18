package org.scada_lts.dao.migration.mysql;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialPointLocatorVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class V2_7_5_3_1__UpdateModbusSerialPointLocator extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_5_3_1__UpdateModbusSerialPointLocator.class);

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
        List<ModbusSerialDataSourceVO> dataSources = new ArrayList<>();
        List<DataPointVO> dataPoints = new ArrayList<>();
        try {
            dataSources.addAll(jdbcTmp.query("SELECT id, xid, name, data FROM dataSources where dataSourceType = 2", (resultSet, i) -> {
                try (InputStream inputStream = resultSet.getBinaryStream("data");
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    ModbusSerialDataSourceVO dataSourceVO = (ModbusSerialDataSourceVO) objectInputStream.readObject();
                    dataSourceVO.setId(resultSet.getInt("id"));
                    dataSourceVO.setXid(resultSet.getString("xid"));
                    dataSourceVO.setName(resultSet.getString("name"));
                    return dataSourceVO;
                } catch (IOException | ClassNotFoundException ex) {
                    LOG.error(ex.getMessage(), ex);
                    return null;
                }
            }));
            boolean isNull = dataSources.stream().anyMatch(Objects::isNull);
            if (isNull) {
                throw new IllegalStateException("DataSourceVO is null!");
            }
            if(!dataSources.isEmpty()) {
                dataPoints.addAll(jdbcTmp.query("SELECT id, data FROM dataPoints WHERE dataSourceId IN (" + getArgsIn(dataSources.size()) + ")", dataSources.stream().map(DataSourceVO::getId).toArray(),
                        (resultSet, i) -> {
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
                        }));
                isNull = dataPoints.stream().anyMatch(Objects::isNull);
                if (isNull) {
                    throw new IllegalStateException("DataPointVO is null!");
                }
                for (DataPointVO dataPoint : dataPoints) {
                    PointLocatorVO locator = dataPoint.getPointLocator();
                    if (locator instanceof ModbusPointLocatorVO) {
                        ModbusPointLocatorVO metaPointLocator = new ModbusSerialPointLocatorVO((ModbusPointLocatorVO) locator);
                        dataPoint.setPointLocator(metaPointLocator);
                        jdbcTmp.update("UPDATE dataPoints set data = ? WHERE id = ?",
                                new SerializationData().writeObject(dataPoint), dataPoint.getId());
                    }
                }
            }
        } finally {
            dataSources.clear();
            dataPoints.clear();
        }
    }

    private static String getArgsIn(int size) {
        StringBuilder args = new StringBuilder();
        for(int i = 0 ; i < size; i++) {
            args.append("?").append(",");
        }
        args.delete(args.length() - 1, args.length());
        return args.toString();
    }
}
