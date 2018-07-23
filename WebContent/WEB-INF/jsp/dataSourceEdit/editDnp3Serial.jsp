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
<%@page import="br.org.scadabr.vo.dataSource.dnp3.Dnp3SerialDataSourceVO"%>

<script type="text/javascript">
	function scanImpl() {
		DataSourceEditDwr.dnp3SerialScan($get("timeout"), $get("retries"),
				$get("commPortId"), $get("baudRate"), $get("dataBits"),
				$get("stopBits"), $get("parity"), scanCB);
	}

	function saveDataSourceImpl() {
		DataSourceEditDwr.saveDNP3SerialDataSource($get("dataSourceName"),
				$get("dataSourceXid"), $get("sourceAddress"),
				$get("slaveAddress"), $get("commPortId"), $get("baudRate"),
				$get("staticPollPeriods"), $get("rbePollPeriods"),
				$get("rbePeriodType"), $get("quantize"), $get("timeout"), $get("retries"),
				saveDataSourceCB);
	}
</script>

<tr>
	<td class="formLabelRequired"><fmt:message
		key="dsEdit.dnp3Serial.commPortId" /></td>
	<td class="formField"><c:choose>
		<c:when test="${!empty commPortError}">
			<input id="commPortId" type="hidden" value="" />
			<span class="formError">${commPortError}</span>
		</c:when>
		<c:otherwise>
			<sst:select id="commPortId" value="${dataSource.commPortId}">
				<c:forEach items="${commPorts}" var="port">
					<sst:option value="${port.name}">${port.name}</sst:option>
				</c:forEach>
			</sst:select>
		</c:otherwise>
	</c:choose></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
		key="dsEdit.dnp3Serial.baud" /></td>
	<td class="formField"><sst:select id="baudRate"
		value="${dataSource.baudRate}">
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
	</sst:select></td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Serial.dataBits"/></td>
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
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Serial.stopBits"/></td>
  <td class="formField">
    <sst:select id="stopBits" value="${dataSource.stopBits}">
      <sst:option value="1">1</sst:option>
      <sst:option value="3">1.5</sst:option>
      <sst:option value="2">2</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Serial.parity"/></td>
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