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
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveModbusIpDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"), $get("quantize"), $get("timeout"), $get("retries"), $get("contiguousBatches"),
              $get("createSlaveMonitorPoints"), $get("maxReadBitCount"), $get("maxReadRegisterCount"), 
              $get("maxWriteRegisterCount"), $get("transportType"), $get("host"), $get("port"), $get("encapsulated"), $get("createSocketMonitorPoint"),
              saveDataSourceCB);
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
