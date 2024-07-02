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
<%@page import="com.serotonin.modbus4j.code.RegisterRange"%>
<%@page import="com.serotonin.modbus4j.code.DataType"%>

<script type="text/javascript">

  function initImpl() {
      scanButtons(false);
      changeRange('test_');
  }
  
  function scan() {
      $set("scanMessage", "<fmt:message key="dsEdit.modbus.startScan"/>");
      dwr.util.removeAllOptions("scanNodes");
      scanButtons(true);
      scanImpl();
  }
  
  function scanCB(msg) {
      if (msg)
          alert(msg);
      else
          setTimeout(scanUpdate, 1000);
  }

  function scanUpdate() {
      DataSourceEditDwr.modbusScanUpdate(scanUpdateCB);
  }
  
  function scanUpdateCB(result) {
      if (result) {
          $set("scanMessage", result.message);
          dwr.util.removeAllOptions("scanNodes");
          dwr.util.addOptions("scanNodes", result.nodes);
          if (!result.finished)
              scanCB();
          else
              scanButtons(false);
      }
  }
  
  function scanCancel() {
      DataSourceEditDwr.cancelTestingUtility(scanButtons);
  }
  
  function scanButtons(scanning) {
      setDisabled("scanBtn", scanning);
      setDisabled("scanCancelBtn", !scanning);
  }

  function validateLocatorTest(locator){

      let messages = [];

      validateValue("test_slaveId", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, locator.slaveId, messages);
      validateValue("test_range", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, locator.range, messages);
      validateValue("test_modbusDataType", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, locator.modbusDataType, messages);
      validateValue("test_offset", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, locator.offset, messages);
      validateValue("test_bit", "<fmt:message key='validate.invalidValue'/>", isPositiveByte, locator.bit, messages);
      validateValue("test_registerCount", "<fmt:message key='badIntegerFormat'/>", isPositiveInt, locator.registerCount, messages);

      return messages;
  }

  function validateValue(htmlId, messageText, validate, value, messages) {
      if(!validate(value)) {
          let message = createValidationMessage(htmlId, messageText);
          messages.push(message);
      }
  }

  function createLocatorConfigTemp(){
      let temp = {};
      temp.slaveId = $get("test_slaveId");
      temp.range = $get("test_range");
      temp.modbusDataType = $get("test_modbusDataType");
      temp.offset = $get("test_offset");
      temp.bit = $get("test_bit");
      temp.registerCount = $get("test_registerCount");
      temp.charset = $get("test_charset");

      return temp;
  }

  
  function locatorTest() {

      let locatorConfigTemp =  createLocatorConfigTemp();
      let modbusConfigTemp = createModbusConfigTemp();

      hideGenericMessages("locatorTestGeneric");
      hideContextualMessages("locatorTestDiv");
      hideContextualMessages("dataSourceProperties");

      let modbusMessages = validateModbusConfig(modbusConfigTemp);

      let messagesLocator = validateLocatorTest(locatorConfigTemp);

      let messages = modbusMessages.concat(messagesLocator);

      if(messages.length > 0) {
          stopImageFader("dsSaveImg");
          showDwrMessages(messages);
      } else {
          let locator = {};
          locator.slaveId = parseInt(locatorConfigTemp.slaveId);
          locator.range = parseInt(locatorConfigTemp.range);
          locator.modbusDataType = parseInt(locatorConfigTemp.modbusDataType);
          locator.offset = parseInt(locatorConfigTemp.offset);
          locator.bit = parseInt(locatorConfigTemp.bit);
          locator.registerCount = parseInt(locatorConfigTemp.registerCount);
          locator.charset = locatorConfigTemp.charset;

          locatorTestImpl(locator);
      }
  }
  
  function locatorTestCB(response) {
      hideContextualMessages("locatorTestDiv");
      hideGenericMessages("locatorTestGeneric");
      if (response.hasMessages) {
          // Add the prefix to the contextual messages.
          for (var i=0; i<response.messages.length; i++) {
              if (response.messages[i].contextKey)
                  response.messages[i].contextKey = "test_"+ response.messages[i].contextKey;
          }
          showDwrMessages(response.messages, "locatorTestGeneric");
          $set("locatorTestResult");
      }
      else
          $set("locatorTestResult", response.data.result);
      setDisabled("locatorTestBtn", false);
  }
  
  function dataTest() {
      setDisabled("dataTestBtn", true);
      dataTestImpl($get("dataTest_slaveId"), $get("dataTest_range"), $get("dataTest_offset"), $get("dataTest_length"));
      hideGenericMessages("dataTestGeneric");
  }
  
  function dataTestCB(response) {
	  if (response.data.length)
		  $set("dataTest_length", response.data.length);
	  
      if (response.hasMessages) {
          showDwrMessages(response.messages, "dataTestGeneric");
          hide("dataTestResults");
      }
      else {
    	  var results = "";
    	  for (var i=0; i<response.data.results.length; i++)
    		  results += response.data.results[i] +"<br/>";
          $set("dataTestResults", results);
          show("dataTestResults");
      }

      setDisabled("dataTestBtn", false);
  }
  
  function addPointImpl() {
	  DataSourceEditDwr.getPoint(-1, function(point) {
		  editPointCB(point);
	      $set("slaveId", $get("test_slaveId"));
	      $set("range", $get("test_range"));
	      $set("modbusDataType", $get("test_modbusDataType"));
	      $set("offset", $get("test_offset"));
	      $set("bit", $get("test_bit"));
	      $set("registerCount", $get("test_registerCount"));
	      $set("charset", $get("test_charset"));
          changeRange('');
	  });
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.modbus.slave"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.slaveId; };
      
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.modbus.range"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) {
          if (p.pointLocator.slaveMonitor)
              return "<fmt:message key="dsEdit.modbus.slaveMonitor"/>";
          if (p.pointLocator.socketMonitor)
              return "<fmt:message key="dsEdit.modbus.socketMonitor"/>";
          return p.pointLocator.rangeMessage;
      };
      
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.modbus.offset"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) {
    	  if (p.pointLocator.slaveMonitor)
    		  return "";
    	  if (isBinary('', p.pointLocator.modbusDataType) && !isBinaryRange('', p.pointLocator.range))
              return p.pointLocator.offset +'/'+ p.pointLocator.bit;
    	  return p.pointLocator.offset;
  	  };
  }
  
  function editPointCBImpl(locator) {
      $set("slaveId", locator.slaveId);
      $set("range", locator.range);
      $set("modbusDataType", locator.modbusDataType);
      $set("offset", locator.offset);
      $set("bit", locator.bit);
      $set("registerCount", locator.registerCount);
      $set("charset", locator.charset);
      $set("settableOverride", locator.settableOverride);
      $set("multiplier", locator.multiplier);
      $set("additive", locator.additive);

      if (locator.socketMonitor){
          hide("nonSlaveMonitor");
          hide("slaveIdRow", true);
      }
      else if (locator.slaveMonitor) {
          hide("nonSlaveMonitor");
          setDisabled("slaveId", true);
      }
      else {
          setDisabled("slaveId", false);
          show("nonSlaveMonitor");
          changeRange('');
      }
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.rangeMessage;
      delete locator.dataTypeId;
      delete locator.relinquishable;
      
      locator.slaveId = $get("slaveId");
      locator.range = $get("range");
      locator.modbusDataType = $get("modbusDataType");
      locator.offset = $get("offset");
      locator.bit = $get("bit");
      locator.registerCount = $get("registerCount");
      locator.charset = $get("charset");
      locator.settableOverride = $get("settableOverride");
      locator.multiplier = $get("multiplier");
      locator.additive = $get("additive");
      
      DataSourceEditDwr.saveModbusPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function changeRange(prefix) {
      if (isBinaryRange(prefix)) {
          $set(prefix +"modbusDataType", "<c:out value="<%= DataType.BINARY %>"/>");
          setDisabled(prefix +"modbusDataType", true);
      }
      else
          setDisabled(prefix +"modbusDataType", false);
      changeDataType(prefix);
      
      var rangeId = $get(prefix +"range");
      if (rangeId == "<c:out value="<%= RegisterRange.COIL_STATUS %>"/>" || rangeId == "<c:out value="<%= RegisterRange.HOLDING_REGISTER %>"/>")
          maybeSetDisabled(prefix +"settableOverride", false);
      else {
    	  maybeSetDisabled(prefix +"settableOverride", true);
          $set(prefix +"settableOverride", false);
      }
  }
  
  function isBinary(prefix, dt) {
	  if (!dt)
		  dt = $get(prefix +"modbusDataType");
      return dt == "<c:out value="<%= DataType.BINARY %>"/>";
  }
  
  function isBinaryRange(prefix, r) {
	  if (!r)
	      r = $get(prefix +"range");
      return r == "<c:out value="<%= RegisterRange.COIL_STATUS %>"/>" || r == "<c:out value="<%= RegisterRange.INPUT_STATUS %>"/>";
  }
  
  function isString(prefix) {
	  var dt = $get(prefix +"modbusDataType");
      return dt == "<c:out value="<%= DataType.CHAR %>"/>" || dt == "<c:out value="<%= DataType.VARCHAR %>"/>";
  }
  
  function changeDataType(prefix) {
	  if (isBinary(prefix)) {
          setDisabled(prefix +"bit", isBinaryRange(prefix));
          setDisabled(prefix +"registerCount", true);
          setDisabled(prefix +"charset", true);
          maybeSetDisabled(prefix +"multiplier", true);
          maybeSetDisabled(prefix +"additive", true);
	  }
	  else if (isString(prefix)) {
          setDisabled(prefix +"bit", true);
          setDisabled(prefix +"registerCount", false);
          setDisabled(prefix +"charset", false);
          maybeSetDisabled(prefix +"multiplier", true);
          maybeSetDisabled(prefix +"additive", true);
	  }
	  else {
          setDisabled(prefix +"bit", true);
          setDisabled(prefix +"registerCount", true);
          setDisabled(prefix +"charset", true);
          maybeSetDisabled(prefix +"multiplier", false);
          maybeSetDisabled(prefix +"additive", false);
	  }
  }
  
  function maybeSetDisabled(nodeName, val) {
	  var node = $(nodeName);
	  if (node)
		  setDisabled(node, val);
  }
</script>

<c:choose>
  <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.MODBUS_SERIAL']}">
    <c:set var="dsDesc"><fmt:message key="dsEdit.modbus.descSerial"/></c:set>
    <c:set var="dsHelpId" value="modbusSerialDS"/>
  </c:when>
  <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.MODBUS_IP']}">
    <c:set var="dsDesc"><fmt:message key="dsEdit.modbus.descIp"/></c:set>
    <c:set var="dsHelpId" value="modbusIpDS"/>
  </c:when>
</c:choose>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
          <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.timeout"/></td>
          <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.retries"/></td>
          <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.contiguousBatches"/></td>
          <td class="formField"><sst:checkbox id="contiguousBatches" selectedValue="${dataSource.contiguousBatches}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.createSlaveMonitorPoints"/></td>
          <td class="formField"><sst:checkbox id="createSlaveMonitorPoints" selectedValue="${dataSource.createSlaveMonitorPoints}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.maxReadBitCount"/></td>
          <td class="formField"><input type="text" id="maxReadBitCount" value="${dataSource.maxReadBitCount}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.maxReadRegisterCount"/></td>
          <td class="formField"><input type="text" id="maxReadRegisterCount" value="${dataSource.maxReadRegisterCount}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.maxWriteRegisterCount"/></td>
          <td class="formField"><input type="text" id="maxWriteRegisterCount" value="${dataSource.maxWriteRegisterCount}"/></td>
        </tr>
              
        <c:choose>
          <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.MODBUS_SERIAL']}">
            <%@ include file="/WEB-INF/jsp/dataSourceEdit/editModbusSerial.jsp" %>
          </c:when>
          <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.MODBUS_IP']}">
            <%@ include file="/WEB-INF/jsp/dataSourceEdit/editModbusIp.jsp" %>
          </c:when>
        </c:choose>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB marR" style="float:left;">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.modbus.nodeScan"/></td></tr>
        <tr>
          <td colspan="2" align="center">
            <input id="scanBtn" type="button" value="<fmt:message key="dsEdit.modbus.scanForNodes"/>" onclick="scan();"/>
            <input id="scanCancelBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="scanCancel();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="scanMessage" class="formError"></td></tr>
        
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.modbus.nodesFound"/></td>
          <td class="formField"><select id="scanNodes" size="8"></select></td>
        </tr>
      </table>
    </div>
    
    <div class="borderDiv marB marR" style="float:left;">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.modbus.dataTest"/></td></tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.slaveId"/></td>
          <td class="formField"><input type="text" id="dataTest_slaveId" value="1"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.registerRange"/></td>
          <td class="formField">
            <select id="dataTest_range">
              <option value="<c:out value="<%= RegisterRange.COIL_STATUS %>"/>"><fmt:message key="dsEdit.modbus.coilStatus"/></option>
              <option value="<c:out value="<%= RegisterRange.INPUT_STATUS %>"/>"><fmt:message key="dsEdit.modbus.inputStatus"/></option>
              <option value="<c:out value="<%= RegisterRange.HOLDING_REGISTER %>"/>"><fmt:message key="dsEdit.modbus.holdingRegister"/></option>
              <option value="<c:out value="<%= RegisterRange.INPUT_REGISTER %>"/>"><fmt:message key="dsEdit.modbus.inputRegister"/></option>
            </select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.offset"/></td>
          <td class="formField"><input type="text" id="dataTest_offset" value="0"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.registerCount"/></td>
          <td class="formField"><input type="text" id="dataTest_length" value="100"/></td>
        </tr>
        
        <tr>
          <td colspan="2" align="center">
            <input id="dataTestBtn" type="button" value="<fmt:message key="dsEdit.modbus.dataTest.read"/>" onclick="dataTest();"/>
          </td>
        </tr>
        
        <tr><td colspan="2"><div id="dataTestResults" style="height: 200px; width: 100%; overflow: scroll; display: none;"></div></td></tr>
        
        <tr><td colspan="2"><table><tbody id="dataTestGeneric"></tbody></table></td></tr> 
      </table>
    </div>
    
    <div class="borderDiv marB" id="locatorTestDiv" style="clear:both;">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.modbus.locatorTest"/></td></tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.slaveId"/></td>
          <td class="formField"><input type="text" id="test_slaveId" value="1"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.registerRange"/></td>
          <td class="formField">
            <select id="test_range" onchange="changeRange('test_')">
              <option value="<c:out value="<%= RegisterRange.COIL_STATUS %>"/>"><fmt:message key="dsEdit.modbus.coilStatus"/></option>
              <option value="<c:out value="<%= RegisterRange.INPUT_STATUS %>"/>"><fmt:message key="dsEdit.modbus.inputStatus"/></option>
              <option value="<c:out value="<%= RegisterRange.HOLDING_REGISTER %>"/>"><fmt:message key="dsEdit.modbus.holdingRegister"/></option>
              <option value="<c:out value="<%= RegisterRange.INPUT_REGISTER %>"/>"><fmt:message key="dsEdit.modbus.inputRegister"/></option>
            </select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.modbusDataType"/></td>
          <td class="formField">
            <select id="test_modbusDataType" onchange="changeDataType('test_')">
              <option value="<c:out value="<%= DataType.BINARY %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.binary"/></option>
              <option value="<c:out value="<%= DataType.TWO_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.2bUnsigned"/></option>
              <option value="<c:out value="<%= DataType.TWO_BYTE_INT_SIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.2bSigned"/></option>
              <option value="<c:out value="<%= DataType.TWO_BYTE_BCD %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.2bBcd"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bUnsigned"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_SIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bSigned"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bUnsignedSwapped"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_SIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bSignedSwapped"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_FLOAT %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bFloat"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_FLOAT_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bFloatSwapped"/></option>
              <option value="<c:out value="<%= DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bFloatSwappedInverted"/></option>            
              <option value="<c:out value="<%= DataType.FOUR_BYTE_BCD %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bBcd"/></option>
              <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bUnsigned"/></option>
              <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_SIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bSigned"/></option>
              <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bUnsignedSwapped"/></option>
              <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bSignedSwapped"/></option>
              <option value="<c:out value="<%= DataType.EIGHT_BYTE_FLOAT %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bFloat"/></option>
              <option value="<c:out value="<%= DataType.EIGHT_BYTE_FLOAT_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bFloatSwapped"/></option>
              <option value="<c:out value="<%= DataType.CHAR %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.char"/></option>
              <option value="<c:out value="<%= DataType.VARCHAR %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.varchar"/></option>
            </select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.offset"/></td>
          <td class="formField"><input type="text" id="test_offset" value="0"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.bit"/></td>
          <td class="formField"><input id="test_bit" type="text" value="0"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.registerCount"/></td>
          <td class="formField"><input id="test_registerCount" type="text" value="0"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.charset"/></td>
          <td class="formField"><input id="test_charset" type="text" value="ASCII"/></td>
        </tr>
        
        <tr>
          <td colspan="2" align="center">
            <input id="locatorTestBtn" type="button" value="<fmt:message key="dsEdit.modbus.locatorTest.test"/>" onclick="locatorTest();"/>
            <input type="button" value="<fmt:message key="dsEdit.modbus.addPoint"/>" onclick="addPoint();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="locatorTestResult" class="formError"></td></tr>
        <tr><td colspan="2"><table><tbody id="locatorTestGeneric"></tbody></table></td></tr> 
        
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="modbusPP">
  <tr id="slaveIdRow">
    <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.slaveId"/></td>
    <td class="formField"><input type="text" id="slaveId"/></td>
  </tr>
  
  <tbody id="nonSlaveMonitor">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.registerRange"/></td>
      <td class="formField">
        <select id="range" onchange="changeRange('')">
          <option value="<c:out value="<%= RegisterRange.COIL_STATUS %>"/>"><fmt:message key="dsEdit.modbus.coilStatus"/></option>
          <option value="<c:out value="<%= RegisterRange.INPUT_STATUS %>"/>"><fmt:message key="dsEdit.modbus.inputStatus"/></option>
          <option value="<c:out value="<%= RegisterRange.HOLDING_REGISTER %>"/>"><fmt:message key="dsEdit.modbus.holdingRegister"/></option>
          <option value="<c:out value="<%= RegisterRange.INPUT_REGISTER %>"/>"><fmt:message key="dsEdit.modbus.inputRegister"/></option>
        </select>
      </td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.modbusDataType"/></td>
      <td class="formField">
        <select id="modbusDataType" onchange="changeDataType('')">
          <option value="<c:out value="<%= DataType.BINARY %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.binary"/></option>
          <option value="<c:out value="<%= DataType.TWO_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.2bUnsigned"/></option>
          <option value="<c:out value="<%= DataType.TWO_BYTE_INT_SIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.2bSigned"/></option>
          <option value="<c:out value="<%= DataType.TWO_BYTE_BCD %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.2bBcd"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bUnsigned"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_SIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bSigned"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_UNSIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bUnsignedSwapped"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_INT_SIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bSignedSwapped"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_FLOAT %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bFloat"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_FLOAT_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bFloatSwapped"/></option>
          <option value="<c:out value="<%= DataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bFloatSwappedInverted"/></option>            
          <option value="<c:out value="<%= DataType.FOUR_BYTE_BCD %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.4bBcd"/></option>
          <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bUnsigned"/></option>
          <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_SIGNED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bSigned"/></option>
          <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_UNSIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bUnsignedSwapped"/></option>
          <option value="<c:out value="<%= DataType.EIGHT_BYTE_INT_SIGNED_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bSignedSwapped"/></option>
          <option value="<c:out value="<%= DataType.EIGHT_BYTE_FLOAT %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bFloat"/></option>
          <option value="<c:out value="<%= DataType.EIGHT_BYTE_FLOAT_SWAPPED %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.8bFloatSwapped"/></option>
          <option value="<c:out value="<%= DataType.CHAR %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.char"/></option>
          <option value="<c:out value="<%= DataType.VARCHAR %>"/>"><fmt:message key="dsEdit.modbus.modbusDataType.varchar"/></option>
        </select>
      </td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.offset"/></td>
      <td class="formField"><input type="text" id="offset"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.bit"/></td>
      <td class="formField"><input id="bit" type="text"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.registerCount"/></td>
      <td class="formField"><input id="registerCount" type="text"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.charset"/></td>
      <td class="formField"><input id="charset" type="text"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.settableOverride"/></td>
      <td class="formField"><input id="settableOverride" type="checkbox"/></td>
    </tr>
    
    <tr>
      <td class="formLabel"><fmt:message key="dsEdit.modbus.multiplier"/></td>
      <td class="formField"><input type="text" id="multiplier"/></td>
    </tr>
    
    <tr>
      <td class="formLabel"><fmt:message key="dsEdit.modbus.additive"/></td>
      <td class="formField"><input type="text" id="additive"/></td>
    </tr>
  </tbody>
</tag:pointList>