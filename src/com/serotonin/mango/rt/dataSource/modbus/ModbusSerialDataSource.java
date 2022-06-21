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
package com.serotonin.mango.rt.dataSource.modbus;

import com.fazecast.jSerialComm.SerialPort;

import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO.EncodingType;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.web.i18n.LocalizableMessage;

import org.scada_lts.serial.SerialPortParameters;
import org.scada_lts.serial.SerialPortService;

import javax.comm.NoSuchPortException;

public class ModbusSerialDataSource extends ModbusDataSource {

	private final ModbusSerialDataSourceVO configuration;
	private ModbusMaster modbusMaster;
	private final SerialPortService serialPortService;
	private boolean connProblem = false;
	private boolean firstTime = true;

	public ModbusSerialDataSource(ModbusSerialDataSourceVO configuration) {
		super(configuration);
		this.configuration = configuration;
		this.serialPortService = SerialPortService
				.newService(SerialPortParameters
						.newParameters("Modbus Serial Data Source", configuration));
	}

	//
	// /
	// / Lifecycle
	// /
	//
	@Override
	public void initialize() {
		if (configuration.getEncoding() == EncodingType.ASCII)
			modbusMaster = new ModbusFactory().createAsciiMaster(this.serialPortService);
		else
			modbusMaster = new ModbusFactory().createRtuMaster(this.serialPortService);

		super.initialize(modbusMaster);
	}

	@Override
	protected void doPoll(long time) {

		if (!serialPortService.isOpen()) {

			if (firstTime) {
				modbusMaster.destroy();
				firstTime = false;
			}

			connProblem = true;
			return;
		}

		if (connProblem) {
			connProblem = false;
			firstTime = true;
			initialize();
		}

		super.doPoll(time);

	}

	@Deprecated
	public SerialPort getPort(String port) {
		SerialPort[] portList = SerialPort.getCommPorts();
		for(SerialPort serialPort: portList) {
			String portName = serialPort.getSystemPortName();
			if(portName.equals(port))
				return serialPort;
		}
		return null;
	}

	@Deprecated
	public boolean verifyPort(String port) {
		SerialPort[] portList = SerialPort.getCommPorts();
		for(SerialPort serialPort: portList) {
			String portName = serialPort.getSystemPortName();
			if(portName.equals(port))
				return true;
		}
		return false;
	}

	@Override
	protected LocalizableMessage getLocalExceptionMessage(Exception e) {
		if (e instanceof ModbusInitException) {
			Throwable cause = e.getCause();
			if (cause instanceof NoSuchPortException)
				return new LocalizableMessage("event.serial.portOpenError",
						configuration.getCommPortId());
		}

		return DataSourceRT.getExceptionMessage(e);
	}
}
