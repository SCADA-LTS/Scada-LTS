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
<%@page import="com.serotonin.mango.vo.dataSource.onewire.OneWirePointLocatorVO"%>
<%@page import="com.serotonin.mango.vo.dataSource.onewire.OneWireDataSourceVO"%>

<script type="text/javascript">
  var networkInfo;
  var deviceInfo;
  
  function initImpl() {
      // Disable adding generic points.
      hide("editImg<%= + Common.NEW_ID %>");
      rescanChanged();
      getNetworkInfo();
  }
  
  function getNetworkInfo() {
      setDisabled("scanBtn", true);
      showMessage("scanMessage", "<fmt:message key="dsEdit.1wire.scanning"/>");
      hide("scanResults");
      DataSourceEditDwr.readOneWireNetwork($get("commPortId"), getNetworkInfoCB);
  }
  
  function getNetworkInfoCB(result) {
      networkInfo = null;
      setDisabled("scanBtn", false);
      if (result.hasMessages) {
          showMessage("scanMessage", result.messages[0].genericMessage);
          hide("scanDevicesNoneFound");
          hide("scanResults");
      }
      else {
          // Show the results of the scan
          showMessage("scanMessage", "<fmt:message key="dsEdit.1wire.scanComplete"/>");
          networkInfo = result.data.devices;
          
          dwr.util.removeAllRows("scanDevices");
          dwr.util.addRows("scanDevices", networkInfo, [
                      function(device) { return device.addressString; },
                      function(device) { return device.description; },
                      function(device) {
                          return writeImage("scanDeviceImg"+ device.addressString, null, "icon_comp_add",
                                  "<fmt:message key="common.add"/>", "addPoint('"+ device.addressString +"')");
                          }
                  ],
                  {
                      rowCreator: function(options) {
                          var tr = document.createElement("tr");
                          tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                          return tr;
                      }
                  });
          
          show("scanResults");
          display("scanDevicesNoneFound", networkInfo.length == 0);
          
          dwr.util.removeAllOptions("address");
          dwr.util.addOptions("address", networkInfo, "addressString");
          
          if (currentPoint)
              $set("address", currentPoint.pointLocator.address);
      }
      cancelEditPoint();
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveOneWireDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("commPortId"),
              $get("updatePeriodType"), $get("updatePeriods"), $get("rescanPeriodType"), $get("rescanPeriods"),
              saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.1wire.address"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.address; };
      
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.1wire.attribute"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = 
              function(p) { return p.pointLocator.attributeIndexDescription; };
  }
  
  function addPointImpl(address) {
      DataSourceEditDwr.addOneWirePoint(address, editPointCB);
  }
  
  function editPointCBImpl(locator) {
      if (!networkInfo) {
          alert("<fmt:message key="dsEdit.1wire.scanWarn"/>");
          return true;
      }
      
      deviceInfo = getElement(networkInfo, locator.address, "addressString");
      if (deviceInfo) {
          // Found the device in the current network information.
          $set("address", locator.address);
          addressChanged();
          $set("attributeId", locator.attributeId);
          attributeIdChanged();
          $set("index", locator.index);
          
          show("editableAttributes");
          hide("readonlyAttributes");
          show("pointSaveImg");
      }
      else {
          // Didn't find the device.
          hide("pointSaveImg");
          hide("editableAttributes");
          hide("indexRow");
          
          $set("roAddress", locator.address);
          $set("roAttributeDescription", locator.attributeDescription);
          $set("roIndex", locator.index);
          
          show("readonlyAttributes");
      }
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.attributeIndexDescription;
      delete locator.attributeDescription;
      delete locator.dataTypeId;
      
      locator.address = $get("address");
      locator.attributeId = $get("attributeId");
      locator.index = $get("index");
      
      DataSourceEditDwr.saveOneWirePointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function addressChanged() {
      deviceInfo = getElement(networkInfo, $get("address"), "addressString");
      
      $set("deviceType", deviceInfo.description);
      
      dwr.util.removeAllOptions("attributeId");
      dwr.util.addOptions("attributeId", deviceInfo.attributes, "id", "description");
      attributeIdChanged();
  }
  
  function attributeIdChanged() {
      var attributeId = $get("attributeId")
      
      var element = getElement(deviceInfo.attributes, attributeId);
      var startIndex = element.startIndex;
      var length = element.length;
      
      dwr.util.removeAllOptions("index");
      if (length == 0)
          hide("indexRow");
      else {
          if (attributeId == <%= OneWirePointLocatorVO.AttributeTypes.AD_VOLTAGE %>)
              $set("indexLabel", "<fmt:message key="dsEdit.1wire.attribute.adChannel"/>");
          else if (attributeId == <%= OneWirePointLocatorVO.AttributeTypes.LATCH_STATE %>)
              $set("indexLabel", "<fmt:message key="dsEdit.1wire.attribute.channel"/>");
          else if (attributeId == <%= OneWirePointLocatorVO.AttributeTypes.WIPER_POSITION %>)
              $set("indexLabel", "<fmt:message key="dsEdit.1wire.attribute.wiper"/>");
          else if (attributeId == <%= OneWirePointLocatorVO.AttributeTypes.COUNTER %>)
              $set("indexLabel", "<fmt:message key="dsEdit.1wire.attribute.counter"/>");
          
          var indexSel = $("index");
          for (var i=0; i<length; i++)
              indexSel.options[indexSel.options.length] = new Option(i + startIndex);
          show("indexRow");
      }
  }
  
  function rescanChanged() {
      var type = $get("rescanPeriodType");
      if (type == <%= OneWireDataSourceVO.RESCAN_NONE %>)
          setDisabled("rescanPeriods", true);
      else
          setDisabled("rescanPeriods", false);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.1wire.desc"/></c:set>
<c:set var="dsHelpId" value="1wireDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.port"/></td>
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
          <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.scheduledRescan"/></td>
          <td class="formField">
            <input type="text" id="rescanPeriods" value="${dataSource.rescanPeriods}" class="formShort"/>
            <sst:select id="rescanPeriodType" value="${dataSource.rescanPeriodType}" onchange="rescanChanged()">
              <sst:option value="<%= Integer.toString(OneWireDataSourceVO.RESCAN_NONE) %>"><fmt:message key="dsEdit.1wire.none"/></sst:option>
              <tag:timePeriodOptions sst="true" min="true" h="true" d="true" w="true"/>
            </sst:select>
          </td>
        </tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td class="smallTitle"><fmt:message key="dsEdit.1wire.scan"/></td></tr>
        
        <tr>
          <td align="center">
            <input id="scanBtn" type="button" value="<fmt:message key="dsEdit.1wire.runScan"/>" onclick="getNetworkInfo();"/>
          </td>
        </tr>
        
        <tr><td id="scanMessage" style="display:none;" class="formError"></td></tr>
        <tbody id="scanResults" style="display:none;"><tr><td><table cellspacing="1">
          <tr class="rowHeader">
            <td><fmt:message key="dsEdit.1wire.address"/></td>
            <td><fmt:message key="dsEdit.1wire.description"/></td>
            <td></td>
          </tr>
          <tbody id="scanDevicesNoneFound"><td colspan="3"><fmt:message key="dsEdit.1wire.noDevices"/></td></tbody>
          <tbody id="scanDevices"></tbody>
        </table></td></tr></tbody>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="1wirePP">
  <tbody id="editableAttributes">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.address"/></td>
      <td class="formField"><select id="address" onchange="addressChanged()"></select></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.deviceType"/></td>
      <td class="formField"><input type="text" id="deviceType" disabled="disabled" class="formLong"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.attribute"/></td>
      <td class="formField"><select id="attributeId" onchange="attributeIdChanged()"></select></td>
    </tr>
  </tbody>
  
  <tbody id="indexRow" style="display:none">
    <tr>
      <td id="indexLabel" class="formLabelRequired"></td>
      <td class="formField"><select id="index"></select></td>
    </tr>
  </tbody>
  
  <tbody id="readonlyAttributes">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.address"/></td>
      <td class="formField"><input type="text" id="roAddress" disabled="disabled"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.attribute"/></td>
      <td class="formField"><input type="text" id="roAttributeDescription" disabled="disabled"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.1wire.index"/></td>
      <td class="formField"><input type="text" id="roIndex" disabled="disabled"/></td>
    </tr>
    
    <tr><td colspan="2" class="formError"><fmt:message key="dsEdit.1wire.noInfo"/></td></tr>
  </tbody>
</tag:pointList>