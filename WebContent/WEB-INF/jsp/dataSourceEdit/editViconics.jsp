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

<script type="text/javascript">
  function initImpl() {
      hide("editImg"+ <c:out value="<%= Common.NEW_ID %>"/>);
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveViconicsDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("commPortId"),
              $get("panId"), $get("channel"), $get("timeout"), $get("retries"), $get("networkTimeoutSeconds"),
              $get("deviceWarningTimeoutSeconds"), $get("deviceRemoveTimeoutSeconds"),
              $get("pointValueMinimumFreshnessSeconds"), $get("convertToCelsius"), saveDataSourceCB);
  }
  
  function editPointCBImpl(locator) {
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.dataTypeId;
      
      DataSourceEditDwr.saveViconicsPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function refreshInfo() {
      $set("infoMessage", "<fmt:message key="dsEdit.viconics.refreshing"/>");
      setDisabled("updateBtn", true);
      
      DataSourceEditDwr.getRfModuleInfo(function(result) {
          if (result.hasMessages) {
              $set("infoMessage", result.messages[0].genericMessage);
              hide("infoData");
              hide("infoDevices");
          }
          else {
              $set("infoMessage", "");
              $set("rfmFirmware", result.data.rfm.firmware);
              $set("rfmNetworkAddr", result.data.rfm.networkAddr);
              $set("rfmIeee", result.data.rfm.ieee);
              $set("rfmChipRevision", result.data.rfm.chipRevision);
              
              dwr.util.removeAllRows("infoDevices");
              dwr.util.addRows("infoDevices", result.data.devices, [
                      function(device) { return device.commAddr; },
                      function(device) { return device.modelNumber; },
                      function(device) { return device.firmware; },
                      function(device) { return device.zigbeeFirmware; },
                      function(device) { return device.zigbeeNetworkAddr; },
                      function(device) { return device.ieee; },
                      function(device) { return device.chipRevision; },
                      function(device) { return device.trss; },
                      function(device) { return device.crss; }
                  ],
                  {
                      rowCreator: function(options) {
                          var tr = document.createElement("tr");
                          tr.id = "deviceIndex"+ options.rowData.key;
                          tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                          return tr;
                      }
                  }
              );
              
              show("infoData");
              show("infoDevices");
          }
          
          setDisabled("updateBtn", false);
      });
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.viconics.desc"/></c:set>
<c:set var="dsHelpId" value="viconicsDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.port"/></td>
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
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.panId"/></td>
          <td class="formField"><input id="panId" type="text" value="${dataSource.panId}"/></td>
        </tr>
      
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.channel"/></td>
          <td class="formField"><input id="channel" type="text" value="${dataSource.channel}"/></td>
        </tr>
      
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.timeout"/></td>
          <td class="formField"><input id="timeout" type="text" value="${dataSource.timeout}"/></td>
        </tr>
      
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.retries"/></td>
          <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>
      
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.networkTimeout"/></td>
          <td class="formField"><input id="networkTimeoutSeconds" type="text" value="${dataSource.networkTimeoutSeconds}"/></td>
        </tr>
      
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.deviceWarning"/></td>
          <td class="formField"><input id="deviceWarningTimeoutSeconds" type="text" value="${dataSource.deviceWarningTimeoutSeconds}"/></td>
        </tr>
      
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.deviceRemove"/></td>
          <td class="formField"><input id="deviceRemoveTimeoutSeconds" type="text" value="${dataSource.deviceRemoveTimeoutSeconds}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.pointFreshness"/></td>
          <td class="formField"><input id="pointValueMinimumFreshnessSeconds" type="text" value="${dataSource.pointValueMinimumFreshnessSeconds}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.viconics.convertCelsius"/></td>
          <td class="formField"><sst:checkbox id="convertToCelsius" selectedValue="${dataSource.convertToCelsius}"/></td>
        </tr>
      </table>
      
      <tag:dsEvents/>
    </div>
  </td>

  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="8" class="smallTitle"><fmt:message key="dsEdit.viconics.networkInfo"/></td></tr>
        
        <tr><td colspan="8" id="infoMessage" class="formError"></td></tr>
        
        <tbody id="infoData" style="display:none;">
          <tr>
            <td colspan="8">
              <b><fmt:message key="dsEdit.viconics.rfModule"/></b><br/>
              <div style="padding-left:20px;float:left;">
                <fmt:message key="dsEdit.viconics.zigbeeFirmwareRevision"/>:<br/>
                <fmt:message key="dsEdit.viconics.zigbeeNetworkAddress"/>:<br/>
                <fmt:message key="dsEdit.viconics.ieee"/>:<br/>
                <fmt:message key="dsEdit.viconics.chipRevision"/>:<br/>
              </div>
              <div style="padding-left:10px;float:left;">
                <span id="rfmFirmware"></span><br/>
                <span id="rfmNetworkAddr"></span><br/>
                <span id="rfmIeee"></span><br/>
                <span id="rfmChipRevision"></span><br/>
              </div>
            </td>
          </tr>
          
          <tr><td colspan="8"><b><fmt:message key="dsEdit.viconics.devices"/></b></td></tr>
          <tr class="rowHeader">
            <td><fmt:message key="dsEdit.viconics.commAddress"/></td>
            <td><fmt:message key="dsEdit.viconics.modelNumber"/></td>
            <td><fmt:message key="dsEdit.viconics.firmwareRevision"/></td>
            <td><fmt:message key="dsEdit.viconics.zigbeeFirmwareRevisionBr"/></td>
            <td><fmt:message key="dsEdit.viconics.zigbeeNetworkAddressBr"/></td>
            <td><fmt:message key="dsEdit.viconics.ieee"/></td>
            <td><fmt:message key="dsEdit.viconics.chipRevisionBr"/></td>
            <td><fmt:message key="dsEdit.viconics.trss"/></td>
            <td><fmt:message key="dsEdit.viconics.crss"/></td>
          </tr>
        </tbody>
          
        <tbody id="infoDevices" style="display:none;"></tbody>
          
        <tr>
          <td colspan="8" align="center">
            <input id="updateBtn" type="button" value="<fmt:message key="common.refresh"/>" onclick="refreshInfo();"/>
          </td>
        </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="viconicsPP">
</tag:pointList>