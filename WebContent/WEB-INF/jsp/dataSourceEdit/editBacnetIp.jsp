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
<%@page import="com.serotonin.bacnet4j.type.enumerated.ObjectType"%>

<script type="text/javascript">
  var deviceInfo;

  function initImpl() {
      whoIsButtons(false);
  }
  
  function sendWhoIs() {
      $set("whoIsMessage", "<fmt:message key="dsEdit.bacnetIp.sendingWhoIs"/>");
      dwr.util.removeAllRows("iamsReceived");
      whoIsButtons(true);
      DataSourceEditDwr.sendBACnetWhoIs($get("deviceId"), $get("broadcastAddress"), $get("port"), $get("timeout"), 
              $get("segTimeout"), $get("segWindow"), $get("retries"), $get("whoIsPort"),
              $get("maxReadMultipleReferencesSegmented"), $get("maxReadMultipleReferencesNonsegmented"), sendWhoIsCB);
  }
  
  function sendWhoIsCB() {
      setTimeout(sendWhoIsUpdate, 1000);
  }
  
  function sendWhoIsUpdate() {
      DataSourceEditDwr.bacnetWhoIsUpdate(sendWhoIsUpdateCB);
  }
  
  function sendWhoIsUpdateCB(result) {
      if (result) {
          $set("whoIsMessage", result.message);
          dwr.util.removeAllRows("iamsReceived");
          dwr.util.addRows("iamsReceived", result.devices, [
                      function(device) { return device.value; },
                      function(device) {
                          return writeImage("deviceDetailsImg"+ device.key, null, "control_play_blue",
                                  "<fmt:message key="dsEdit.bacnetIp.getDetails"/>", "getDeviceDetails("+ device.key +")");
                      }
                  ],
                  {
                      rowCreator: function(options) {
                          var tr = document.createElement("tr");
                          tr.id = "deviceIndex"+ options.rowData.key;
                          tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                          return tr;
                      }
                  });
          
          if (result.deviceDetails) {
              stopImageFader("deviceDetailsImg"+ result.deviceIndex);
              show("deviceDetails");
              hide("deviceDetailsMessage");
              drawDetails(result, "deviceDetailsTree");
          }
          
          if (!result.finished)
              sendWhoIsCB();
          else
              whoIsButtons(false);
      }
  }

  function drawDetails(result, treeId) {
      deviceInfo = result;
      var tree = dojo.widget.manager.getWidgetById(treeId);
      
      // Remove all of the old results.
      while (tree.children.length > 0)
          tree.removeNode(tree.children[0]);
      
      // Add the new stuff.
      var parentNode = dojo.widget.createWidget("TreeNode", {
    	      title: "<b>"+ result.deviceDescription +"</b> ("+ result.deviceName +"/" + result.deviceInstanceNumber +")",
              isFolder: "true"
      });
      tree.addChild(parentNode);
      
      var obj, objectNode;
      for (var i=0; i<result.deviceDetails.length; i++) {
          obj = result.deviceDetails[i];
          objectNode = dojo.widget.createWidget("TreeNode", {
                  title: obj.objectName +" ("+ obj.objectTypeDescription +") "+ writeImageSQuote(null, null,
                          "icon_comp_add", "<fmt:message key="dsEdit.bacnetIp.addPoint"/>", "addPoint("+ i +")"),
                  isFolder: "true"});
          parentNode.addChild(objectNode);
          
          objectNode.addChild(dojo.widget.createWidget("TreeNode",
                  { title: "<fmt:message key="dsEdit.bacnetIp.presentValue"/>: "+ obj.prettyPresentValue}));
      }
      
      parentNode.expand();
  }

  function getDeviceDetails(index) {
      startImageFader("deviceDetailsImg"+ index, true);
      hide("deviceDetails");
      show("deviceDetailsMessage");
      DataSourceEditDwr.getBACnetDeviceDetails(index, getDeviceDetailsCB);
  }
  
  function getDeviceDetailsCB(failureIndex) {
      if (failureIndex != -1)
          stopImageFader("deviceDetailsImg"+ failureIndex);
  }
  
  function cancelWhoIs() {
      DataSourceEditDwr.cancelTestingUtility(cancelWhoIsCB);
  }
  
  function cancelWhoIsCB() {
      $set("whoIsMessage", "<fmt:message key="dsEdit.bacnetIp.listenerStopped"/>");
      whoIsButtons(false);
  }
  
  function whoIsButtons(running) {
      setDisabled("sendWhoIsBtn", running);
      setDisabled("cancelWhoIsBtn", !running);
  }

  function sendObjListRequest() {
      cancelWhoIs();
      setDisabled("sendObjListBtn", true);
      hide("objectList");
      DataSourceEditDwr.sendObjectListRequest($get("deviceId"), $get("broadcastAddress"), $get("port"), $get("timeout"), 
              $get("segTimeout"), $get("segWindow"), $get("retries"), $get("maxReadMultipleReferencesSegmented"),
              $get("maxReadMultipleReferencesNonsegmented"), $get("objListIp"), $get("objListPort"),
              $get("objListNetNumber"), $get("objListNetAddr"), $get("objListDevId"), function(result) {
          if (result.data.error)
        	  $set("objListMessage", result.data.error);
          else {
              $set("objListMessage");
              show("objectList");
              drawDetails(result.data, "objectListTree");
          }
          
          setDisabled("sendObjListBtn", false);
      });
  }

  function toggleDataSourceImpl() {
      cancelWhoIs();
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveBACnetIpDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"), $get("deviceId"), $get("broadcastAddress"), $get("port"), $get("timeout"),
              $get("segTimeout"), $get("segWindow"), $get("retries"), $get("covSubscriptionTimeoutMinutes"),
              $get("maxReadMultipleReferencesSegmented"), $get("maxReadMultipleReferencesNonsegmented"),
              saveDataSourceCB);
  }
  
  function addPointImpl(objIndex) {
      var obj = deviceInfo.deviceDetails[objIndex];
      delete obj.prettyPresentValue;
      if (deviceInfo.deviceNetworkNumber == null)
    	  deviceInfo.deviceNetworkNumber = 0;
      DataSourceEditDwr.addBacnetPoint(deviceInfo.deviceIp, deviceInfo.devicePort, deviceInfo.deviceNetworkNumber,
    		  deviceInfo.deviceNetworkAddress, deviceInfo.deviceInstanceNumber, obj, editPointCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.bacnetIp.device"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.remoteDeviceIp; };
  }

  function editPointCBImpl(locator) {
      $set("remoteDeviceIp", locator.remoteDeviceIp);
      $set("remoteDevicePort", locator.remoteDevicePort);
      $set("networkNumber", locator.networkNumber);
      $set("networkAddress", locator.networkAddress);
      $set("remoteDeviceInstanceNumber", locator.remoteDeviceInstanceNumber);
      $set("objectTypeId", locator.objectTypeId);
      $set("objectInstanceNumber", locator.objectInstanceNumber);
      $set("useCovSubscription", locator.useCovSubscription);
      $set("settable", locator.settable);
      $set("writePriority", locator.writePriority);
      $set("dataTypeId", locator.dataTypeId);
      objectTypeChanged();
  }
  
  function savePointImpl(locator) {
	  delete locator.relinquishable;
      locator.remoteDeviceIp = $get("remoteDeviceIp");
      locator.remoteDevicePort = $get("remoteDevicePort");
      locator.networkNumber = $get("networkNumber");
      locator.networkAddress = $get("networkAddress");
      locator.remoteDeviceInstanceNumber = $get("remoteDeviceInstanceNumber");
      locator.objectTypeId = $get("objectTypeId");
      locator.objectInstanceNumber = $get("objectInstanceNumber");
      locator.useCovSubscription = $get("useCovSubscription");
      locator.settable = $get("settable");
      locator.writePriority = $get("writePriority");
      locator.dataTypeId = $get("dataTypeId");
      
      DataSourceEditDwr.saveBACnetIPPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function objectTypeChanged() {
      var type = $get("objectTypeId");
      
      if (type == <c:out value="<%= ObjectType.analogOutput.intValue() %>"/> || 
              type == <c:out value="<%= ObjectType.analogValue.intValue() %>"/> || 
              type == <c:out value="<%= ObjectType.binaryOutput.intValue() %>"/> || 
              type == <c:out value="<%= ObjectType.binaryValue.intValue() %>"/> || 
              type == <c:out value="<%= ObjectType.multiStateOutput.intValue() %>"/> || 
              type == <c:out value="<%= ObjectType.multiStateValue.intValue() %>"/>) {
          setDisabled("settable", false);
      }
      else {
          setDisabled("settable", true);
          $set("settable", false);
      }
      settableChanged();
  }
  
  function settableChanged() {
      setDisabled("writePriority", !$get("settable"));
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.bacnetIp.desc"/></c:set>
<c:set var="dsHelpId" value="bacnetIpDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
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
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.deviceId"/></td>
          <td class="formField"><input id="deviceId" type="text" value="${dataSource.deviceId}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.broadcastAddress"/></td>
          <td class="formField"><input id="broadcastAddress" type="text" value="${dataSource.broadcastAddress}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.port"/></td>
          <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.timeout"/></td>
          <td class="formField"><input id="timeout" type="text" value="${dataSource.timeout}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.segmentTimeout"/></td>
          <td class="formField"><input id="segTimeout" type="text" value="${dataSource.segTimeout}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.segmentWindow"/></td>
          <td class="formField"><input id="segWindow" type="text" value="${dataSource.segWindow}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.retries"/></td>
          <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.covLease"/></td>
          <td class="formField"><input id="covSubscriptionTimeoutMinutes" type="text" 
                  value="${dataSource.covSubscriptionTimeoutMinutes}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.maxReadMultSeg"/></td>
          <td class="formField"><input id="maxReadMultipleReferencesSegmented" type="text" 
                  value="${dataSource.maxReadMultipleReferencesSegmented}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.maxReadMultNonseg"/></td>
          <td class="formField"><input id="maxReadMultipleReferencesNonsegmented" type="text" 
                  value="${dataSource.maxReadMultipleReferencesNonsegmented}"/></td>
        </tr>
      </table>
      
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.bacnetIp.discovery"/></td></tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.port"/></td>
          <td class="formField"><input id="whoIsPort" type="text" class="formShort"/></td>
        </tr>
        <tr>
          <td colspan="2" align="center">
            <input id="sendWhoIsBtn" type="button" value="<fmt:message key="dsEdit.bacnetIp.sendWhoIs"/>" onclick="sendWhoIs();"/>
            <input id="cancelWhoIsBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="cancelWhoIs();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="whoIsMessage" class="formError"></td></tr>
        
        <tr>
          <td colspan="2">
            <table cellspacing="1">
              <tr class="rowHeader">
                <td><fmt:message key="dsEdit.bacnetIp.deviceAddress"/></td>
                <td></td>
              </tr>
              <tbody id="iamsReceived"></tbody>
            </table>
          </td>
        </tr>
        
        <tbody id="deviceDetailsMessage" style="display:none;" class="formError">
          <tr><td colspan="2"><fmt:message key="dsEdit.bacnetIp.gettingDeviceDetails"/></td></tr>
        </tbody>
        <tbody id="deviceDetails">
          <tr><td colspan="2"><div dojoType="Tree" toggle="wipe" widgetId="deviceDetailsTree"></div></td></tr>
        </tbody>
      </table>
    </div>
    
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.bacnetIp.objectList"/></td></tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDeviceIp"/></td>
          <td class="formField"><input id="objListIp" type="text"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDevicePort"/></td>
          <td class="formField"><input id="objListPort" type="text" class="formShort"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.bacnetIp.remoteDeviceNetworkNumber"/></td>
          <td class="formField"><input id="objListNetNumber" type="text" class="formShort"/></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.bacnetIp.remoteDeviceNetworkAddress"/></td>
          <td class="formField"><input id="objListNetAddr" type="text" class="formShort"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDeviceInstanceNumber"/></td>
          <td class="formField"><input id="objListDevId" type="text" class="formShort"/></td>
        </tr>
        <tr>
          <td colspan="2" align="center">
            <input id="sendObjListBtn" type="button" value="<fmt:message key="dsEdit.bacnetIp.sendObjList"/>" onclick="sendObjListRequest();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="objListMessage" class="formError"></td></tr>
        <tbody id="objectList">
          <tr><td colspan="2"><div dojoType="Tree" toggle="wipe" widgetId="objectListTree"></div></td></tr>
        </tbody>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="bacnetIpPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDeviceIp"/></td>
    <td class="formField"><input type="text" id="remoteDeviceIp"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDevicePort"/></td>
    <td class="formField"><input type="text" id="remoteDevicePort"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDeviceNetworkNumber"/></td>
    <td class="formField"><input type="text" id="networkNumber"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDeviceNetworkAddress"/></td>
    <td class="formField"><input type="text" id="networkAddress"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.remoteDeviceInstanceNumber"/></td>
    <td class="formField"><input type="text" id="remoteDeviceInstanceNumber"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.objectType"/></td>
    <td class="formField">
      <select name="objectTypeId" onchange="objectTypeChanged()">
        <option value="<c:out value="<%= ObjectType.accessDoor.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.accessDoor"/></option>
        <option value="<c:out value="<%= ObjectType.accumulator.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.accumulator"/></option>
        <option value="<c:out value="<%= ObjectType.analogInput.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.analogInput"/></option>
        <option value="<c:out value="<%= ObjectType.analogOutput.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.analogOutput"/></option>
        <option value="<c:out value="<%= ObjectType.analogValue.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.analogValue"/></option>
        <option value="<c:out value="<%= ObjectType.averaging.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.averaging"/></option>
        <option value="<c:out value="<%= ObjectType.binaryInput.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.binaryInput"/></option>
        <option value="<c:out value="<%= ObjectType.binaryOutput.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.binaryOutput"/></option>
        <option value="<c:out value="<%= ObjectType.binaryValue.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.binaryValue"/></option>
        <option value="<c:out value="<%= ObjectType.lifeSafetyPoint.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.lifeSafetyPoint"/></option>
        <option value="<c:out value="<%= ObjectType.lifeSafetyZone.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.lifeSafetyZone"/></option>
        <option value="<c:out value="<%= ObjectType.loop.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.loop"/></option>
        <option value="<c:out value="<%= ObjectType.multiStateInput.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.multiStateInput"/></option>
        <option value="<c:out value="<%= ObjectType.multiStateOutput.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.multiStateOutput"/></option>
        <option value="<c:out value="<%= ObjectType.multiStateValue.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.multiStateValue"/></option>
        <option value="<c:out value="<%= ObjectType.pulseConverter.intValue() %>"/>"><fmt:message key="dsEdit.bacnetIp.objectType.pulseConverter"/></option>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.objectInstanceNumber"/></td>
    <td class="formField"><input type="text" id="objectInstanceNumber"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.useCov"/></td>
    <td class="formField"><input type="checkbox" id="useCovSubscription"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable" onclick="settableChanged()"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.bacnetIp.writePriority"/></td>
    <td class="formField"><input type="text" id="writePriority"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
</tag:pointList>