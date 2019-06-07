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
<%@page import="com.serotonin.mango.DataTypes"%>

<script type="text/javascript">
  var ipWhiteList = new Array();
  var deviceIdWhiteList = new Array();
  
  function initImpl() {
      <c:forEach items="${dataSource.ipWhiteList}" var="ip">
        ipWhiteList[ipWhiteList.length] = "${ip}";
      </c:forEach>
      ipWhiteList.sort();
      refreshIpWhiteList();
      
      <c:forEach items="${dataSource.deviceIdWhiteList}" var="deviceId">
        deviceIdWhiteList[deviceIdWhiteList.length] = "${deviceId}";
      </c:forEach>
      deviceIdWhiteList.sort();
      refreshDeviceIdWhiteList();
  }
  
  function addIpMask() {
      var ipMask = $get("ipMask");
      
      for (var i=0; i<ipWhiteList.length; i++) {
          if (ipWhiteList[i] == ipMask) {
              alert("<fmt:message key="dsEdit.httpReceiver.containsIpMask"/> "+ ipMask);
              return;
          }
      }
      
      DataSourceEditDwr.validateIpMask(ipMask, function(error) {
          if (error)
              alert(error);
          else {
              ipWhiteList[ipWhiteList.length] = $get("ipMask");
              ipWhiteList.sort();
              refreshIpWhiteList();
          }
      });
  }
  
  function addDeviceIdMask() {
      var deviceIdMask = $get("deviceIdMask");
      
      for (var i=0; i<deviceIdWhiteList.length; i++) {
          if (deviceIdWhiteList[i] == deviceIdMask) {
              alert("<fmt:message key="dsEdit.httpReceiver.containsDeviceMask"/> "+ deviceIdMask);
              return;
          }
      }
      
      deviceIdWhiteList[deviceIdWhiteList.length] = deviceIdMask;
      deviceIdWhiteList.sort();
      refreshDeviceIdWhiteList();
  }
  
  function removeIpMask(ipMask) {
      for (var i=ipWhiteList.length-1; i>=0; i--) {
          if (ipWhiteList[i] == ipMask)
              ipWhiteList.splice(i, 1);
      }
      refreshIpWhiteList();
  }
  
  function removeDeviceIdMask(deviceIdMask) {
      for (var i=deviceIdWhiteList.length-1; i>=0; i--) {
          if (deviceIdWhiteList[i] == deviceIdMask)
              deviceIdWhiteList.splice(i, 1);
      }
      refreshDeviceIdWhiteList();
  }
  
  function refreshIpWhiteList() {
      dwr.util.removeAllRows("ipWhiteList");
      if (ipWhiteList.length == 0)
          show("noAddressesMessage");
      else {
          hide("noAddressesMessage");
          dwr.util.addRows("ipWhiteList", ipWhiteList, [
                  function(data) { return ""; },
                  function(data) {
                      return data +" <img src='images/bullet_delete.png' onclick='removeIpMask(\""+ data + "\");' "+
                              "class='ptr' alt='<fmt:message key="common.delete"/>' title='<fmt:message key="common.delete"/>'/>";
                  }
                  ], null);
      }
  }
  
  function refreshDeviceIdWhiteList() {
      dwr.util.removeAllRows("deviceIdWhiteList");
      if (deviceIdWhiteList.length == 0)
          show("noDevicesMessage");
      else {
          hide("noDevicesMessage");
          dwr.util.addRows("deviceIdWhiteList", deviceIdWhiteList, [
                  function(data) { return ""; },
                  function(data) {
                      return data +" <img src='images/bullet_delete.png' onclick='removeDeviceIdMask(\""+ data 
                              +"\");' class='ptr' alt='<fmt:message key="common.delete"/>' "+
                              "title='<fmt:message key="common.delete"/>'/>";
                  }
                  ], null);
      }
  }
  
  function saveDataSourceImpl() {
      httpListenCancel();
      DataSourceEditDwr.saveHttpReceiverDataSource($get("dataSourceName"), $get("dataSourceXid"), ipWhiteList,
              deviceIdWhiteList, saveDataSourceCB);
  }
  
  function httpListen() {
      $set("httpListenMessage", "<fmt:message key="dsEdit.httpReceiver.listening"/>");
      $set("httpListenData");
      httpListenButtons(true);
      DataSourceEditDwr.httpReceiverListenForData(ipWhiteList, deviceIdWhiteList, httpListenCB);
  }
  
  function httpListenButtons(listening) {
      setDisabled("httpListenBtn", listening);
      setDisabled("httpListenCancelBtn", !listening);
  }
  
  function httpListenCB() {
      setTimeout(httpListenUpdate, 2000);
  }
  
  function httpListenUpdate() {
      DataSourceEditDwr.httpReceiverListenerUpdate(function(result) {
          if (result) {
              $set("httpListenMessage", result.message);
              if (result.remoteIp) {
                  var data = "<b><fmt:message key="dsEdit.httpReceiver.source"/>: "+ result.remoteIp +"</b><br/>";
                  if (result.deviceId)
                      data += "<b><fmt:message key="dsEdit.httpReceiver.deviceId"/>: "+ result.deviceId +"</b><br/>";
                  else
                      data += "<b><fmt:message key="dsEdit.httpReceiver.deviceId"/>: -</b><br/>";
                  data += "<b><fmt:message key="dsEdit.httpReceiver.time"/>: "+ result.time +"</b><br/>";
                  for (var i=0; i<result.data.length; i++) {
                      var sample = result.data[i];
                      data += sample.key.replace(/</, "&lt;") +"="+ sample.value.replace(/</, "&lt;");
                      if (sample.prettyTime)
                          data += " (@ "+ sample.prettyTime +")";
                      data += "<br/>"
                  }
                  $set("httpListenData", data);
              }
              httpListenCB();
          }
      });
  }
  
  function httpListenCancel() {
      DataSourceEditDwr.cancelTestingUtility(function() {
          httpListenButtons(false);
          $set("httpListenMessage", "<fmt:message key="common.cancelled"/>");
      });
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.httpReceiver.parameter"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.parameterName; };
  }
  
  function editPointCBImpl(locator) {
      $set("parameterName", locator.parameterName);
      $set("dataTypeId", locator.dataTypeId);
      $set("binary0Value", locator.binary0Value);
      changeDataTypeId();
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      
      locator.parameterName = $get("parameterName");
      locator.dataTypeId = $get("dataTypeId");
      locator.binary0Value = $get("binary0Value");
      
      DataSourceEditDwr.saveHttpReceiverPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function changeDataTypeId() {
      var dataTypeId = $get("dataTypeId");
      if (dataTypeId == <%= DataTypes.BINARY %>)
          setDisabled($("binary0Value"), false);
      else
          setDisabled($("binary0Value"), true);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.httpReceiver.desc"/></c:set>
<c:set var="dsHelpId" value="httpReceiverDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.httpReceiver.ipWhiteList"/></td>
          <td class="formField">
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td><input id="ipMask" type="text"/></td>
                <td width="10"></td>
                <td><tag:img png="add" onclick="addIpMask()" title="common.add"/></td>
              </tr>
            </table>
          </td>
        </tr>

        <tr id="noAddressesMessage" style="display:none;">
          <td></td>
          <td><fmt:message key="dsEdit.httpReceiver.noIpAddresses"/></td>
        </tr>
        <tbody id="ipWhiteList"></tbody>

        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.httpReceiver.deviceWhiteList"/></td>
          <td class="formField">
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td><input id="deviceIdMask" type="text"/></td>
                <td width="10"></td>
                <td><tag:img png="add" onclick="addDeviceIdMask()" title="common.add"/></td>
              </tr>
            </table>
          </td>
        </tr>

        <tr id="noDevicesMessage" style="display:none;">
          <td></td>
          <td><fmt:message key="dsEdit.httpReceiver.noDevices"/></td>
        </tr>
        <tbody id="deviceIdWhiteList"></tbody>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td class="smallTitle"><fmt:message key="dsEdit.httpReceiver.receiverListener"/></td></tr>
        <tr>
          <td align="center">
            <input id="httpListenBtn" type="button" value="<fmt:message key="dsEdit.httpReceiver.startListener"/>" onclick="httpListen();"/>
            <input id="httpListenCancelBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="httpListenCancel();"/>
          </td>
        </tr>
        <tr><td id="httpListenMessage" class="formError"></td></tr>
        <tr><td class="formField" id="httpListenData"></td></tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="httpReceiverPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpReceiver.httpParamName"/></td>
    <td class="formField"><input type="text" id="parameterName"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId" onchange="changeDataTypeId();">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpReceiver.binaryZeroValue"/></td>
    <td class="formField"><input id="binary0Value" type="text"/></td>
  </tr>
</tag:pointList>