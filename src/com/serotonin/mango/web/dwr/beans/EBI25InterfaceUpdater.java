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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.mango.rt.dataSource.ebro.EBI25Constants;
import com.serotonin.mango.vo.dataSource.ebro.EBI25PointLocatorVO;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class EBI25InterfaceUpdater {
    private static final Log LOG = LogFactory.getLog(EBI25InterfaceUpdater.class);

    public LocalizableMessage updateLogger(String host, int port, int timeout, int retries, EBI25PointLocatorVO locator) {
        if (StringUtils.isEmpty(host))
            return new LocalizableMessage("dsEdit.ebi25.read.host");

        ModbusMaster modbusMaster = null;

        int index = locator.getIndex();
        try {
            modbusMaster = EBI25Constants.initModbusMaster(host, port, false, timeout, retries, null);
            modbusMaster.setValue(EBI25Constants.createLocator(index, EBI25Constants.OFFSET_SAMPLE_RATE, false),
                    locator.getSampleRate());
            modbusMaster.setValue(EBI25Constants.createLocator(index, EBI25Constants.OFFSET_LOW_LIMIT, false), locator
                    .translateToRawValue(locator.getLowLimit()));
            modbusMaster.setValue(EBI25Constants.createLocator(index, EBI25Constants.OFFSET_HIGH_LIMIT, false), locator
                    .translateToRawValue(locator.getHighLimit()));
        }
        catch (ModbusInitException e) {
            LOG.warn("Modbus initialization", e);
            return new LocalizableMessage("dsEdit.ebi25.read.init", e.getMessage());
        }
        catch (ModbusTransportException e) {
            LOG.warn("Modbus transport", e);
            return new LocalizableMessage("dsEdit.ebi25.read.transport", e.getMessage());
        }
        catch (ErrorResponseException e) {
            LOG.warn("Modbus error response: " + e.getErrorResponse().getExceptionMessage());
            return new LocalizableMessage("dsEdit.ebi25.read.response", e.getErrorResponse().getExceptionMessage());
        }
        finally {
            EBI25Constants.destroyModbusMaster(modbusMaster);
        }

        return null;
    }

    public LocalizableMessage updateSysTime(String host, int port, int timeout, int retries) {
        if (StringUtils.isEmpty(host))
            return new LocalizableMessage("dsEdit.ebi25.read.host");

        ModbusMaster modbusMaster = null;

        try {
            modbusMaster = EBI25Constants.initModbusMaster(host, port, false, timeout, retries, null);
            modbusMaster.setValue(EBI25Constants.createLocator(EBI25Constants.OFFSET_SYS_TIME, true), System
                    .currentTimeMillis() / 1000);
        }
        catch (ModbusInitException e) {
            LOG.warn("Modbus initialization", e);
            return new LocalizableMessage("dsEdit.ebi25.read.init", e.getMessage());
        }
        catch (ModbusTransportException e) {
            LOG.warn("Modbus transport", e);
            return new LocalizableMessage("dsEdit.ebi25.read.transport", e.getMessage());
        }
        catch (ErrorResponseException e) {
            LOG.warn("Modbus error response: " + e.getErrorResponse().getExceptionMessage());
            return new LocalizableMessage("dsEdit.ebi25.read.response", e.getErrorResponse().getExceptionMessage());
        }
        finally {
            EBI25Constants.destroyModbusMaster(modbusMaster);
        }

        return null;
    }
}
