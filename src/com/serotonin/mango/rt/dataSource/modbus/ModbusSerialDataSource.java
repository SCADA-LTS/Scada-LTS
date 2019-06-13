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

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

import java.util.Enumeration;

import org.scada_lts.modbus.SerialParameters;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO.EncodingType;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.web.i18n.LocalizableMessage;

public class ModbusSerialDataSource extends ModbusDataSource {

	private final ModbusSerialDataSourceVO configuration;
	ModbusMaster modbusMaster;
	private Enumeration portList;
	private boolean connProblem = false;
	private boolean firstTime = true;

	public ModbusSerialDataSource(ModbusSerialDataSourceVO configuration) {
		super(configuration);
		this.configuration = configuration;
		portList = CommPortIdentifier.getPortIdentifiers();
	}

	//
	// /
	// / Lifecycle
	// /
	//
	@Override
	public void initialize() {
		SerialParameters params = new SerialParameters();
		params.setCommPortId(configuration.getCommPortId());
		params.setPortOwnerName("Mango Modbus Serial Data Source");
		params.setBaudRate(configuration.getBaudRate());
		params.setFlowControlIn(configuration.getFlowControlIn());
		params.setFlowControlOut(configuration.getFlowControlOut());
		params.setDataBits(configuration.getDataBits());
		params.setStopBits(configuration.getStopBits());
		params.setParity(configuration.getParity());

		if (configuration.getEncoding() == EncodingType.ASCII)
			modbusMaster = new ModbusFactory().createAsciiMaster(params);
		else
			modbusMaster = new ModbusFactory().createRtuMaster(params);

		super.initialize(modbusMaster);
	}

	@Override
	protected void doPoll(long time) {

		portList = CommPortIdentifier.getPortIdentifiers();

		// System.out.println("Configuration Port: "
		// + configuration.getCommPortId());

		if (!verifyPort(configuration.getCommPortId())) {

			// System.out.println("Porta nao detectada !");

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

	public SerialPort getPort(String port) {

		SerialPort serialPort = null;

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList
					.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(port)) {
					try {
						serialPort = (SerialPort) portId.open(
								configuration.getCommPortId(), 10000);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Erro ao abrir a porta !");
					}
				}
			}

		}

		return serialPort;
	}

	public boolean verifyPort(String port) {

		boolean p = false;

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList
					.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				// System.out.println(portId.getName());
				if (portId.getName().equals(port)) {
					p = true;
				} else
					p = false;
			}
		}

		return p;
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
