<%--
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
    along with this program.  If not, see http://www.gnu.org/licenses/.
--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO"%>

<script type="text/javascript">
  function checkTransportType(transportType) {
	  if(transportType == "<%= ModbusIpDataSourceVO.TransportType.TCP_LISTENER.toString() %>"){
		  var host = document.getElementById("host");
		  host.value = "127.0.0.1";
		  host.disabled = true;
		  show("socketMonitorRow");
	  }
	  else if(transportType == "<%= ModbusIpDataSourceVO.TransportType.UDP.toString() %>"){
		  hide("socketMonitorRow");
	  }
	  else{
		  var host = document.getElementById("host");
		  show("socketMonitorRow");
		  host.disabled = false;
	  }
  }
  
  function initImpl() {
	  checkTransportType($get("transportType"));
  }

  function scanImpl() {
      DataSourceEditDwr.modbusIpScan($get("timeout"), $get("retries"), $get("transportType"), $get("host"), 
              $get("port"), $get("encapsulated"), scanCB);
  }
  
  function locatorTestImpl(locator) {
      DataSourceEditDwr.testModbusIpLocator($get("timeout"), $get("retries"), $get("transportType"), $get("host"), 
              $get("port"), $get("encapsulated"), locator, locatorTestCB);
  }
  
  function dataTestImpl(slaveId, range, offset, length) {
      DataSourceEditDwr.testModbusIpData($get("timeout"), $get("retries"), $get("transportType"), $get("host"), 
              $get("port"), $get("encapsulated"), slaveId, range, offset, length, dataTestCB);
  }

  function validateModbusConfig(temp){

      let messages = [];

      validateValue("updatePeriods", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.updatePeriods, messages);
      validateValue("updatePeriodType", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.updatePeriodType, messages);
      validateValue("timeout", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.timeout, messages);
      validateValue("retries", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.retries, messages);
      validateValue("maxReadBitCount", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.maxReadBitCount, messages);
      validateValue("maxReadRegisterCount", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.maxReadRegisterCount, messages);
      validateValue("maxWriteRegisterCount", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.maxWriteRegisterCount, messages);
      validateValue("port", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, temp.port, messages);

      return messages;
  }

  function createModbusConfigTemp(){
      let modbus = {};
      modbus.dataSourceName = $get("dataSourceName");
      modbus.dataSourceXid = $get("dataSourceXid");
      modbus.updatePeriods = $get("updatePeriods");
      modbus.updatePeriodType = $get("updatePeriodType");
      modbus.quantize = $get("quantize");
      modbus.timeout = $get("timeout");
      modbus.retries = $get("retries");
      modbus.contiguousBatches = $get("contiguousBatches");
      modbus.createSlaveMonitorPoints = $get("createSlaveMonitorPoints");
      modbus.maxReadBitCount = $get("maxReadBitCount");
      modbus.maxReadRegisterCount = $get("maxReadRegisterCount");
      modbus.maxWriteRegisterCount = $get("maxWriteRegisterCount");
      modbus.transportType = $get("transportType");
      modbus.host = $get("host");
      modbus.port = $get("port");
      modbus.encapsulated = $get("encapsulated");
      modbus.createSocketMonitorPoint = $get("createSocketMonitorPoint");

      return modbus;
  }
  
  function saveDataSourceImpl() {

      let temp = createModbusConfigTemp();

      let messages = validateModbusConfig(temp);

      if(messages.length > 0) {
		  showDwrMessages(messages);
      } else {
		  let dataSourceName = temp.dataSourceName;
		  let dataSourceXid = temp.dataSourceXid;
		  let updatePeriods = parseInt(temp.updatePeriods);
		  let updatePeriodType = parseInt(temp.updatePeriodType);
		  let quantize = temp.quantize;
		  let timeout = parseInt(temp.timeout);
		  let retries = parseInt(temp.retries);
		  let contiguousBatches = temp.contiguousBatches;
		  let createSlaveMonitorPoints = temp.createSlaveMonitorPoints;
		  let maxReadBitCount = parseInt(temp.maxReadBitCount);
		  let maxReadRegisterCount = parseInt(temp.maxReadRegisterCount);
		  let maxWriteRegisterCount = parseInt(temp.maxWriteRegisterCount);
		  let transportType = temp.transportType;
		  let host = temp.host;
		  let port = parseInt(temp.port);
		  let encapsulated = temp.encapsulated;
		  let createSocketMonitorPoint = temp.createSocketMonitorPoint;
		  DataSourceEditDwr.saveModbusIpDataSource(dataSourceName, dataSourceXid, updatePeriods,
              updatePeriodType, quantize, timeout, retries, contiguousBatches, createSlaveMonitorPoints, maxReadBitCount, maxReadRegisterCount,
                maxWriteRegisterCount, transportType, host, port, encapsulated, createSocketMonitorPoint, saveDataSourceCB);
    }
  }
</script>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusIp.transportType"/></td>
  <td class="formField">
    <sst:select id="transportType" value="${dataSource.transportType}" onchange="checkTransportType(this.value)">
      <sst:option value="<%= ModbusIpDataSourceVO.TransportType.TCP.toString() %>"><fmt:message key="dsEdit.modbusIp.transportType.tcp"/></sst:option>
      <sst:option value="<%= ModbusIpDataSourceVO.TransportType.TCP_KEEP_ALIVE.toString() %>"><fmt:message key="dsEdit.modbusIp.transportType.tcpKA"/></sst:option>
      <sst:option value="<%= ModbusIpDataSourceVO.TransportType.UDP.toString() %>"><fmt:message key="dsEdit.modbusIp.transportType.udp"/></sst:option>
      <sst:option value="<%= ModbusIpDataSourceVO.TransportType.TCP_LISTENER.toString() %>"><fmt:message key="dsEdit.modbusIp.transportType.tcpListener"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusIp.host"/></td>
  <td class="formField"><input id="host" type="text" value="${dataSource.host}"/></td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusIp.port"/></td>
  <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusIp.encapsulated"/></td>
  <td class="formField"><sst:checkbox id="encapsulated" selectedValue="${dataSource.encapsulated}"/></td>
</tr>

<tr id="socketMonitorRow">
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusIp.createSocketMonitorPoint"/></td>
  <td class="formField"><sst:checkbox id="createSocketMonitorPoint" selectedValue="${dataSource.createSocketMonitorPoint}"/></td>
</tr>
