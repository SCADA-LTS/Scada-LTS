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
<%@page import="com.serotonin.mango.Common"%>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.port"/></td>
  <td class="formField">
    <c:choose>
      <c:when test="${!empty commPortError}">
        <input id="commPortId" type="hidden" value=""/>
        <span class="formError">${commPortError}</span>
      </c:when>
      <c:otherwise>
          <sst:select id="commPortId" value="${serialPortSettings != null ? serialPortSettings.commPortId : \"\"}">
          <c:forEach items="${commPorts}" var="port">
            <sst:option value="${port.name}">${port.name}</sst:option>
          </c:forEach>
        </sst:select>
      </c:otherwise>
    </c:choose>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.baud"/></td>
  <td class="formField">
    <sst:select id="baudRate" value="${serialPortSettings != null ? serialPortSettings.baudRate : rxtxDefaultBaudrate}">
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
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.flowControlIn"/></td>
  <td class="formField">
    <sst:select id="flowControlIn" value="${serialPortSettings != null ? serialPortSettings.flowControlIn : rxtxDefaultFlowControl}">
      <sst:option value="NONE"><fmt:message key="dsEdit.serial.flow.none"/></sst:option>
      <sst:option value="RTS|CTS"><fmt:message key="dsEdit.serial.flow.rtsCts"/></sst:option>
      <sst:option value="XON|XOFF"><fmt:message key="dsEdit.serial.flow.xonXoff"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.flowOut"/></td>
  <td class="formField">
    <sst:select id="flowControlOut" value="${serialPortSettings != null ? serialPortSettings.flowControlOut : rxtxDefaultFlowControl}">
      <sst:option value="NONE"><fmt:message key="dsEdit.serial.flow.none"/></sst:option>
      <sst:option value="RTS|CTS"><fmt:message key="dsEdit.serial.flow.rtsCts"/></sst:option>
      <sst:option value="XON|XOFF"><fmt:message key="dsEdit.serial.flow.xonXoff"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.dataBits"/></td>
  <td class="formField">
    <sst:select id="dataBits" value="${serialPortSettings != null ? serialPortSettings.dataBits : rxtxDefaultDataBits}">
      <sst:option value="5">5</sst:option>
      <sst:option value="6">6</sst:option>
      <sst:option value="7">7</sst:option>
      <sst:option value="8">8</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.stopBits"/></td>
  <td class="formField">
    <sst:select id="stopBits" value="${serialPortSettings != null ? serialPortSettings.stopBits : rxtxDefaultStopBits}">
      <sst:option value="1">1</sst:option>
      <sst:option value="1.5">1.5</sst:option>
      <sst:option value="2">2</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.serial.parity"/></td>
  <td class="formField">
    <sst:select id="parity" value="${serialPortSettings != null ? serialPortSettings.parity : rxtxDefaultParity}">
      <sst:option value="NONE"><fmt:message key="dsEdit.serial.parity.none"/></sst:option>
      <sst:option value="ODD"><fmt:message key="dsEdit.serial.parity.odd"/></sst:option>
      <sst:option value="EVEN"><fmt:message key="dsEdit.serial.parity.even"/></sst:option>
      <sst:option value="MARK"><fmt:message key="dsEdit.serial.parity.mark"/></sst:option>
      <sst:option value="SPACE"><fmt:message key="dsEdit.serial.parity.space"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
    <td class="formLabelRequired" for="inputBufferSize" ><fmt:message key="dsEdit.serial.inputBufferSize"/></td>
    <td><input class="formShort" type="text" id="inputBufferSize" value="${serialPortSettings != null ? serialPortSettings.inputBufferSize : rxtxDefaultInputBufferSize}" /></td>
</tr>

<tr>
    <td class="formLabelRequired" for="outputBufferSize" ><fmt:message key="dsEdit.serial.outputBufferSize"/></td>
    <td><input class="formShort" type="text" id="outputBufferSize" value="${serialPortSettings != null ? serialPortSettings.outputBufferSize : rxtxDefaultOutputBufferSize}" /></td>
</tr>
