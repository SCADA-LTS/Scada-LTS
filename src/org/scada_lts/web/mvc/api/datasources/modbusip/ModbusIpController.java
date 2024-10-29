package org.scada_lts.web.mvc.api.datasources.modbusip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.modbus.ModbusDataSource;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.SlaveIdLimit255ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import org.scada_lts.web.beans.ApplicationBeans;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/datasources/modbusip")
public class ModbusIpController {

    private class PointLocatorTest {
        private ModbusIpDataSourceJson datasource;
        private ModbusIpPointLocatorJson pointLocator;

        public PointLocatorTest() {
        }

        public PointLocatorTest(ModbusIpDataSourceJson datasource, ModbusIpPointLocatorJson pointLocator) {
            this.datasource = datasource;
            this.pointLocator = pointLocator;
        }

        public ModbusIpDataSourceJson getDatasource() {
            return datasource;
        }

        public void setDatasource(ModbusIpDataSourceJson datasource) {
            this.datasource = datasource;
        }

        public ModbusIpPointLocatorJson getPointLocator() {
            return pointLocator;
        }

        public void setPointLocator(ModbusIpPointLocatorJson pointLocator) {
            this.pointLocator = pointLocator;
        }
    }

    @PostMapping(value = "/testpointlocator")
    public ResponseEntity<Map<String, Object>> testPointLocator(
            @RequestBody ObjectNode data,
            HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            if(user != null) {
                ObjectMapper mapper = ApplicationBeans.getObjectMapper();
                ModbusIpDataSourceJson ds = mapper.convertValue(data.get("datasource"), ModbusIpDataSourceJson.class);
                ModbusIpPointLocatorJson pl = mapper.convertValue(data.get("pointLocator"), ModbusIpPointLocatorJson.class);
                ModbusPointLocatorVO pointLocator = pl.parsePointLocatorData();
                ModbusMaster modbusMaster =  createModbusIpMaster(
                        ds.getTimeout(),
                        ds.getRetries(),
                        ds.getTransportType(),
                        ds.getHost(),
                        ds.getPort(),
                        ds.isEncapsulated()
                );
                return new ResponseEntity<>(testModbusPointLocator(modbusMaster, pointLocator), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ModbusMaster createModbusIpMaster(
            int timeout, int retires, String transport, String host, int port, boolean encapsulated) {

        IpParameters params = new IpParameters();
        params.setHost(host);
        params.setPort(port);
        params.setEncapsulated(encapsulated);

        ModbusIpDataSourceVO.TransportType transportType = ModbusIpDataSourceVO.TransportType.valueOf(transport);
        ModbusMaster modbusMaster;
        if(transportType == ModbusIpDataSourceVO.TransportType.TCP_LISTENER)
            modbusMaster = new ModbusFactory().createTcpListener(params);
        else if (transportType == ModbusIpDataSourceVO.TransportType.UDP)
            modbusMaster = new ModbusFactory().createUdpMaster(params);
        else
            modbusMaster = new ModbusFactory().createTcpMaster(params, transportType == ModbusIpDataSourceVO.TransportType.TCP_KEEP_ALIVE);
        modbusMaster.setTimeout(timeout);
        modbusMaster.setRetries(retires);
        return new SlaveIdLimit255ModbusMaster(modbusMaster);
    }

    private Map<String, Object> testModbusPointLocator(ModbusMaster modbusMaster, ModbusPointLocatorVO locator) {
        Map<String, Object> response = new HashMap<>();
        try {
            BaseLocator<?> baseLocator = ModbusDataSource.createModbusLocator(locator);
            modbusMaster.init();
            Object result = modbusMaster.getValue(baseLocator);
            response.put("status", "success");
            response.put("value", result);
        } catch (ModbusInitException e) {
            response.put("status", "failed");
            response.put("description", "ModbusIP not initialized");
        } catch (ErrorResponseException e) {
            response.put("status", "failed");
            response.put("description", "Failed to receive a response");
        } catch (ModbusTransportException e) {
            response.put("status", "failed");
            response.put("description", "Failed to establish a connection");
        } catch (IllegalCharsetNameException e) {
            response.put("status", "failed");
            response.put("description", "Illegal charset");
        } finally {
            modbusMaster.destroy();
        }
        return response;

    }

}
