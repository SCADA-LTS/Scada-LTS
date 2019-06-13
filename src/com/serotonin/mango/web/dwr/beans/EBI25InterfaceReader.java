/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.dwr.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataSource.ebro.EBI25Constants;
import com.serotonin.mango.rt.dataSource.ebro.ExceptionResultException;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.view.text.AnalogRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.ebro.EBI25DataSourceVO;
import com.serotonin.mango.vo.dataSource.ebro.EBI25PointLocatorVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class EBI25InterfaceReader {
    private static final Log LOG = LogFactory.getLog(EBI25InterfaceReader.class);

    private ModbusMaster modbusMaster = null;
    private String errorMessage;
    private String serialNumber;
    private String productionDate;
    private String hardwareVersion;
    private String firmwareVersion;
    private String systemTime;
    private final List<EBI25LoggerInfo> loggerInfo = new ArrayList<EBI25LoggerInfo>();
    private List<DataPointVO> points;
    private DataPointDao dataPointDao;

    public EBI25InterfaceReader(ResourceBundle bundle, EBI25DataSourceVO dataSource, String host, int port,
            boolean keepAlive, int timeout, int retries) {
        if (StringUtils.isEmpty(host)) {
            errorMessage = I18NUtils.getMessage(bundle, "dsEdit.ebi25.read.host");
            return;
        }

        try {
            modbusMaster = EBI25Constants.initModbusMaster(host, port, keepAlive, timeout, retries, null);
            execute();

            // Save the header data into the data source
            dataSource.setSerialNumber(serialNumber);
            dataSource.setProductionDate(productionDate);
            dataSource.setHardwareVersion(hardwareVersion);
            dataSource.setFirmwareVersion(firmwareVersion);

            dataPointDao = new DataPointDao();
            RuntimeManager rtm = Common.ctx.getRuntimeManager();

            // Get the list of existing data points for the data source. We remove points from this list as they are
            // needed so that when we are done all that is left in the list is points that can be disabled.
            List<DataPointVO> existingPoints = dataPointDao.getDataPoints(dataSource.getId(), null);

            for (EBI25LoggerInfo info : loggerInfo) {
                EBI25PointLocatorVO locator = null;

                //
                // Create or update the logger value point.
                DataPointVO dp = removeOrCreatePoint(existingPoints, info, EBI25PointLocatorVO.TYPE_VALUE,
                        dataSource.getId());

                locator = dp.getPointLocator();
                locator.setUnit(info.getUnitType());
                locator.setSampleRate(info.getSampleRate());
                locator.setLowLimit(locator.translateFromRawValue(info.getLowLimit()));
                locator.setHighLimit(locator.translateFromRawValue(info.getHighLimit()));

                // Define low and high limit detectors.
                PointEventDetectorVO ped = findOrCreateLimitDetector(dp, false);
                ped.setLimit(locator.getLowLimit());

                ped = findOrCreateLimitDetector(dp, true);
                ped.setLimit(locator.getHighLimit());

                // Update the text renderer
                dp.setTextRenderer(new AnalogRenderer("0.0", locator.getSuffix()));

                rtm.saveDataPoint(dp);

                //
                // Create or update the logger battery point.
                dp = removeOrCreatePoint(existingPoints, info, EBI25PointLocatorVO.TYPE_BATTERY, dataSource.getId());
                dp.setTextRenderer(new AnalogRenderer("0", "%"));
                rtm.saveDataPoint(dp);

                //
                // Create or update the logger signal point.
                dp = removeOrCreatePoint(existingPoints, info, EBI25PointLocatorVO.TYPE_SIGNAL, dataSource.getId());
                dp.setTextRenderer(new AnalogRenderer("0", "%"));
                rtm.saveDataPoint(dp);
            }

            // Disable anything left over
            for (DataPointVO dp : existingPoints) {
                dp.setEnabled(false);
                rtm.saveDataPoint(dp);
            }
        }
        catch (ModbusInitException e) {
            errorMessage = new LocalizableMessage("dsEdit.ebi25.read.init", e.getMessage()).getLocalizedMessage(bundle);
            LOG.warn("Modbus initialization", e);
        }
        catch (ModbusTransportException e) {
            errorMessage = new LocalizableMessage("dsEdit.ebi25.read.transport", e.getMessage())
                    .getLocalizedMessage(bundle);
            LOG.warn("Modbus transport", e);
        }
        catch (ErrorResponseException e) {
            errorMessage = new LocalizableMessage("dsEdit.ebi25.read.response", e.getErrorResponse()
                    .getExceptionMessage()).getLocalizedMessage(bundle);
            LOG.warn("Modbus error response: " + e.getErrorResponse().getExceptionMessage());
        }
        catch (ExceptionResultException e) {
            errorMessage = new LocalizableMessage("dsEdit.ebi25.read.response", e.getExceptionResult()
                    .getExceptionMessage()).getLocalizedMessage(bundle);
            LOG.warn("Modbus error response in '" + e.getKey() + "': " + e.getExceptionResult().getExceptionMessage());
        }
        finally {
            EBI25Constants.destroyModbusMaster(modbusMaster);
        }
    }

    private DataPointVO removeOrCreatePoint(List<DataPointVO> points, EBI25LoggerInfo info, int type, int dsid) {
        for (DataPointVO dp : points) {
            EBI25PointLocatorVO locator = dp.getPointLocator();
            if (locator.getIndex() == info.getIndex() && locator.getType() == type) {
                points.remove(dp);
                return dp;
            }
        }

        String suffix = "";
        if (type == EBI25PointLocatorVO.TYPE_BATTERY)
            suffix = "-Battery";
        else if (type == EBI25PointLocatorVO.TYPE_SIGNAL)
            suffix = "-Signal";

        DataPointVO dp = new DataPointVO();
        dp.setXid(dataPointDao.generateUniqueXid());
        dp.setName("EBI 25-" + (info.getIndex() + 1) + suffix);
        dp.setDataSourceId(dsid);
        dp.setEnabled(true);
        dp.setEventDetectors(new ArrayList<PointEventDetectorVO>());

        EBI25PointLocatorVO locator = new EBI25PointLocatorVO();
        locator.setIndex(info.getIndex());
        locator.setType(type);
        locator.setSerialNumber(info.getSerialNumber());
        locator.setProductionDate(info.getProductionDate());
        locator.setCalibrationDate(info.getCalibrationDate());
        locator.setHardwareVersion(info.getHardwareVersion());
        locator.setFirmwareVersion(info.getFirmwareVersion());

        dp.setPointLocator(locator);

        return dp;
    }

    private PointEventDetectorVO findOrCreateLimitDetector(DataPointVO dp, boolean high) {
        int type;
        String xid;

        if (high) {
            type = PointEventDetectorVO.TYPE_ANALOG_HIGH_LIMIT;
            xid = "loggerHigh";
        }
        else {
            type = PointEventDetectorVO.TYPE_ANALOG_LOW_LIMIT;
            xid = "loggerLow";
        }

        for (PointEventDetectorVO ped : dp.getEventDetectors()) {
            if (xid.equals(ped.getXid()))
                return ped;
        }

        PointEventDetectorVO ped = new PointEventDetectorVO();
        ped.setId(Common.NEW_ID);
        ped.setXid(xid);
        ped.setAlias("");
        ped.setDetectorType(type);
        ped.setAlarmLevel(AlarmLevels.URGENT);

        dp.getEventDetectors().add(ped);
        ped.njbSetDataPoint(dp);

        return ped;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private void execute() throws ModbusTransportException, ErrorResponseException, ExceptionResultException {
        // Read the header
        BatchRead<String> batch = new BatchRead<String>();
        EBI25Constants.addLocator(batch, "serial", 0, true);
        EBI25Constants.addLocator(batch, "proddate", 2, true);
        EBI25Constants.addLocator(batch, "hwver", 4, false);
        EBI25Constants.addLocator(batch, "fwver", 5, false);
        EBI25Constants.addLocator(batch, "systime", EBI25Constants.OFFSET_SYS_TIME, true);

        BatchResults<String> results = modbusMaster.send(batch);

        serialNumber = EBI25Constants.getStringResult(results, "serial");
        productionDate = EBI25Constants.getDateResult(results, "proddate");
        hardwareVersion = EBI25Constants.getVersionResult(results, "hwver");
        firmwareVersion = EBI25Constants.getVersionResult(results, "fwver");
        systemTime = EBI25Constants.getDateResult(results, "systime");

        // Read the loggers
        for (int i = 0; i < EBI25Constants.MAX_DATA_LOGGERS; i++) {
            // See if a logger is present
            int baseAddress = EBI25Constants.calculateBaseAddress(i);
            Long loggerSerial = null;
            try {
                loggerSerial = modbusMaster.getValue(
                        new NumericLocator(EBI25Constants.SLAVE_NODE, EBI25Constants.RANGE, baseAddress,
                                EBI25Constants.FOUR_BYTE)).longValue();
            }
            catch (ErrorResponseException e) {
                // Assumed to be an error regarding an illegal address. Just quit.
                break;
            }

            if (loggerSerial == 0xffffffff)
                continue;

            batch = createLoggerLocators(baseAddress);
            results = modbusMaster.send(batch);

            EBI25LoggerInfo info = extractLoggerInfo(results, i, loggerSerial.toString());
            if (EBI25Constants.UNIT_CODES.isValidId(info.getUnitType()))
                loggerInfo.add(info);
        }
    }

    private BatchRead<String> createLoggerLocators(int baseAddress) {
        BatchRead<String> batch = new BatchRead<String>();

        EBI25Constants.addLocator(batch, "proddate", baseAddress + 2, true);
        EBI25Constants.addLocator(batch, "caldate", baseAddress + 4, true);
        EBI25Constants.addLocator(batch, "hwver", baseAddress + 6, false);
        EBI25Constants.addLocator(batch, "fwver", baseAddress + 7, false);
        EBI25Constants.addLocator(batch, "unit", baseAddress + EBI25Constants.OFFSET_UNIT, false);
        EBI25Constants.addLocator(batch, "rate", baseAddress + EBI25Constants.OFFSET_SAMPLE_RATE, false);
        EBI25Constants.addLocator(batch, "low", baseAddress + EBI25Constants.OFFSET_LOW_LIMIT, false);
        EBI25Constants.addLocator(batch, "high", baseAddress + EBI25Constants.OFFSET_HIGH_LIMIT, false);

        return batch;
    }

    private EBI25LoggerInfo extractLoggerInfo(BatchResults<String> results, int index, String serialNumber)
            throws ExceptionResultException {
        EBI25LoggerInfo info = new EBI25LoggerInfo();

        info.setIndex(index);
        info.setSerialNumber(serialNumber);
        info.setProductionDate(EBI25Constants.getDateResult(results, "proddate"));
        info.setCalibrationDate(EBI25Constants.getDateResult(results, "caldate"));
        info.setHardwareVersion(EBI25Constants.getVersionResult(results, "hwver"));
        info.setFirmwareVersion(EBI25Constants.getVersionResult(results, "fwver"));
        info.setUnitType(EBI25Constants.getIntResult(results, "unit"));
        info.setSampleRate(EBI25Constants.getIntResult(results, "rate"));
        info.setLowLimit(EBI25Constants.getIntResult(results, "low"));
        info.setHighLimit(EBI25Constants.getIntResult(results, "high"));

        return info;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public int getLoggerCount() {
        return loggerInfo.size();
    }

    public List<DataPointVO> getPoints() {
        return points;
    }

    public void setPoints(List<DataPointVO> points) {
        this.points = points;
    }
}
