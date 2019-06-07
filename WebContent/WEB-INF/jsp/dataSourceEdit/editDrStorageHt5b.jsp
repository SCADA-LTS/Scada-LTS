<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
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
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>

<script type="text/javascript">
  var currentChangeType;

  function scanImpl() {
		DataSourceEditDwr.DrStorageHt5bScan($get("timeout"), $get("retries"),
				$get("commPortId"), $get("baudRate"), $get("dataBits"),
				$get("stopBits"), $get("parity"), scanCB);
  }
	
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveDrStorageHt5bDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"),	$get("commPortId"), $get("baudRate"), $get("dataBits"),
				$get("stopBits"), $get("parity"), $get("timeout"), $get("retries"),$get("initString"), 
				$get("quantize"), saveDataSourceCB);
  }

  function editPointCBImpl(locator) {
	  $set("pointType", locator.pointType);
  }
  
  function savePointImpl(locator) {

	locator.pointType = $get("pointType");

 	DataSourceEditDwr.saveDrStorageHt5bPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function initImpl() {
  }
  
  function changeDataType() {
      DataSourceEditDwr.getChangeTypes($get("dataTypeId"), changeDataTypeCB);
  }
  
  function changeDataTypeCB(changeTypes) {
      var changeTypeDD = $("changeTypeId");
      var savedType = changeTypeDD.value;
      dwr.util.removeAllOptions(changeTypeDD);
      dwr.util.addOptions(changeTypeDD, changeTypes, "key", "message");
      changeTypeDD.value = savedType;
      changeChangeType();
  }
  
  // List manipulation.
  function addListValue(prefix) {
  }
  
  function removeListValue(theValue, prefix) {
  }
  
  function refreshValueList(prefix, arr) {
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.drStorageHt5b.desc"/></c:set>
<c:set var="dsHelpId" value="drStorageHt5bDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>

<tr>
	<td class="formLabelRequired"><fmt:message
		key="dsEdit.asciiSerial.commPortId" /></td>
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
		key="dsEdit.asciiSerial.baud" /></td>
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
  <td class="formLabelRequired"><fmt:message key="dsEdit.asciiSerial.dataBits"/></td>
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
  <td class="formLabelRequired"><fmt:message key="dsEdit.asciiSerial.stopBits"/></td>
  <td class="formField">
    <sst:select id="stopBits" value="${dataSource.stopBits}">
      <sst:option value="1">1</sst:option>
      <sst:option value="3">1.5</sst:option>
      <sst:option value="2">2</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.asciiSerial.parity"/></td>
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
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort" />
      <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
        <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiSerial.retries"/></td>
    <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiSerial.timeout"/></td>
    <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
    <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiSerial.initString"/></td>
    <td class="formField">
    	<input id="initString" type="text" value=""/>
    </td>
  </tr>
  
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="drStorageHt5bPP">

	 <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.drStorageHt5b.pointType"/></td>
    <td class="formField">
    	<sst:select id="pointType"
			value="${dataPoint.pointType}">
			<sst:option value="Temperature" ><fmt:message key="dsEdit.drStorageHt5b.type.Temperature"/></sst:option>
			<sst:option value="Humidity" ><fmt:message key="dsEdit.drStorageHt5b.type.Humidity"/></sst:option>
		</sst:select>
    </td>
  </tr>

</tag:pointList>