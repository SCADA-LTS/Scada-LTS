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
<%@page import="com.serotonin.modbus4j.serial.SerialMaster"%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO"%>

<script type="text/javascript">
  function scanImpl() {
      DataSourceEditDwr.modbusSerialScan($get("timeout"), $get("retries"), $get("commPortId"), $get("baudRate"),
              $get("flowControlIn"), $get("flowControlOut"), $get("dataBits"), $get("stopBits"), $get("parity"), 
              $get("encoding"), $get("concurrency"), scanCB);
  }
  
  function locatorTestImpl(locator) {
      DataSourceEditDwr.testModbusSerialLocator($get("timeout"), $get("retries"), $get("commPortId"), $get("baudRate"),
              $get("flowControlIn"), $get("flowControlOut"), $get("dataBits"), $get("stopBits"), $get("parity"), 
              $get("encoding"), $get("concurrency"), locator, locatorTestCB);
  }
  
  function dataTestImpl(slaveId, range, offset, length) {
      DataSourceEditDwr.testModbusSerialData($get("timeout"), $get("retries"), $get("commPortId"), $get("baudRate"),
              $get("flowControlIn"), $get("flowControlOut"), $get("dataBits"), $get("stopBits"), $get("parity"), 
              $get("encoding"), $get("concurrency"), slaveId, range, offset, length, dataTestCB);
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveModbusSerialDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"), 
              $get("updatePeriodType"), $get("quantize"), $get("timeout"), $get("retries"), $get("contiguousBatches"),
              $get("createSlaveMonitorPoints"), $get("maxReadBitCount"), $get("maxReadRegisterCount"),
              $get("maxWriteRegisterCount"), $get("commPortId"), $get("baudRate"), $get("flowControlIn"),
              $get("flowControlOut"), $get("dataBits"), $get("stopBits"), $get("parity"), $get("encoding"),
              $get("echo"), $get("concurrency"), saveDataSourceCB);
  }
</script>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.port"/></td>
  <td class="formField">
    <c:choose>
      <c:when test="${!empty commPortError}">
        <input id="commPortId" type="hidden" value=""/>
        <span class="formError">${commPortError}</span>
      </c:when>
      <c:otherwise>
        <sst:select id="commPortId" value="${dataSource.commPortId}">
          <c:forEach items="${commPorts}" var="port">
            <sst:option value="${port.name}">${port.name}</sst:option>
          </c:forEach>
        </sst:select>
      </c:otherwise>
    </c:choose>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.baud"/></td>
  <td class="formField">
    <sst:select id="baudRate" value="${dataSource.baudRate}">
      <sst:option>110</sst:option>
      <sst:option>300</sst:option>
      <sst:option>1200</sst:option>
      <sst:option>2400</sst:option>
      <sst:option>4800</sst:option>
      <sst:option>9600</sst:option>
      <sst:option>19200</sst:option>
      <sst:option>38400</sst:option>
      <sst:option>57600</sst:option>
      <sst:option>115200</sst:option>
      <sst:option>230400</sst:option>
      <sst:option>460800</sst:option>
      <sst:option>921600</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.flowIn"/></td>
  <td class="formField">
    <sst:select id="flowControlIn" value="${dataSource.flowControlIn}">
      <sst:option value="0"><fmt:message key="dsEdit.modbusSerial.flow.none"/></sst:option>
      <sst:option value="1"><fmt:message key="dsEdit.modbusSerial.flow.rtsCts"/></sst:option>
      <sst:option value="4"><fmt:message key="dsEdit.modbusSerial.flow.xonXoff"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.flowOut"/></td>
  <td class="formField">
    <sst:select id="flowControlOut" value="${dataSource.flowControlOut}">
      <sst:option value="0"><fmt:message key="dsEdit.modbusSerial.flow.none"/></sst:option>
      <sst:option value="2"><fmt:message key="dsEdit.modbusSerial.flow.rtsCts"/></sst:option>
      <sst:option value="8"><fmt:message key="dsEdit.modbusSerial.flow.xonXoff"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.dataBits"/></td>
  <td class="formField">
    <sst:select id="dataBits" value="${dataSource.dataBits}">
      <sst:option value="5">5</sst:option>
      <sst:option value="6">6</sst:option>
      <sst:option value="7">7</sst:option>
      <sst:option value="8">8</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.stopBits"/></td>
  <td class="formField">
    <sst:select id="stopBits" value="${dataSource.stopBits}">
      <sst:option value="1">1</sst:option>
      <sst:option value="3">1.5</sst:option>
      <sst:option value="2">2</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.parity"/></td>
  <td class="formField">
    <sst:select id="parity" value="${dataSource.parity}">
      <sst:option value="0"><fmt:message key="dsEdit.modbusSerial.parity.none"/></sst:option>
      <sst:option value="1"><fmt:message key="dsEdit.modbusSerial.parity.odd"/></sst:option>
      <sst:option value="2"><fmt:message key="dsEdit.modbusSerial.parity.even"/></sst:option>
      <sst:option value="3"><fmt:message key="dsEdit.modbusSerial.parity.mark"/></sst:option>
      <sst:option value="4"><fmt:message key="dsEdit.modbusSerial.parity.space"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.encoding"/></td>
  <td class="formField">
    <sst:select id="encoding" value="${dataSource.encoding}">
      <sst:option value="<%= ModbusSerialDataSourceVO.EncodingType.RTU.toString() %>"><fmt:message key="dsEdit.modbusSerial.encoding.rtu"/></sst:option>
      <sst:option value="<%= ModbusSerialDataSourceVO.EncodingType.ASCII.toString() %>"><fmt:message key="dsEdit.modbusSerial.encoding.ascii"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.echo"/></td>
  <td class="formField">
    <sst:select id="echo" value="${dataSource.echo}">
      <sst:option value="false"><fmt:message key="dsEdit.modbusSerial.echo.off"/></sst:option>
      <sst:option value="true"><fmt:message key="dsEdit.modbusSerial.echo.on"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.modbusSerial.concurrency"/></td>
  <td class="formField">
    <sst:select id="concurrency" value="${dataSource.concurrency}">
      <sst:option value="<%= Integer.toString(SerialMaster.SYNC_TRANSPORT) %>"><fmt:message key="dsEdit.modbusSerial.concurrency.transport"/></sst:option>
      <sst:option value="<%= Integer.toString(SerialMaster.SYNC_SLAVE) %>"><fmt:message key="dsEdit.modbusSerial.concurrency.slave"/></sst:option>
      <sst:option value="<%= Integer.toString(SerialMaster.SYNC_FUNCTION) %>"><fmt:message key="dsEdit.modbusSerial.concurrency.function"/></sst:option>
    </sst:select>
  </td>
</tr>