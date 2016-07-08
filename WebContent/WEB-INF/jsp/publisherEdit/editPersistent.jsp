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

<%@page import="com.serotonin.mango.vo.publish.persistent.PersistentSenderVO"%><script type="text/javascript">
  var allPoints = new Array();  
  var selectedPoints = new Array();  
  
  dojo.addOnLoad(function() { 
      PublisherEditDwr.initSender(function(response) {
          var i;
          var list = response.data.allPoints;
          for (var i=0; i<list.length; i++)
              allPoints[allPoints.length] = {
                      id: list[i].id, name: list[i].extendedName, enabled: list[i].enabled, type: list[i].dataTypeMessage};
              
          list = response.data.publisher.points;
          for (i=0; i<list.length; i++)
              addToSelectedArray(list[i].dataPointId);
          refreshSelectedPoints();
      });
  });
  
  function selectPoint() {
      var pointId = $get("availablePoints");
      addToSelectedArray(pointId);
      refreshSelectedPoints();
  }
  
  function addToSelectedArray(pointId) {
      var data = getElement(allPoints, pointId);
      
      if (data) {
          // Missing names imply that the point was deleted, so ignore.
          selectedPoints[selectedPoints.length] = {
              id : pointId,
              pointName : data.name,
              enabled : data.enabled,
              pointType : data.type
          };
      }
  }
  
  function removeFromSelectedPoints(pointId) {
      removeElement(selectedPoints, pointId);
      refreshSelectedPoints();
  }
  
  function refreshSelectedPoints() {
      dwr.util.removeAllRows("selectedPoints");
      if (selectedPoints.length == 0)
          show("selectedPointsEmpty");
      else {
          hide("selectedPointsEmpty");
          dwr.util.addRows("selectedPoints", selectedPoints,
              [
                  function(data) { return data.pointName; },
                  function(data) { return "<img src='images/"+ (data.enabled ? "brick_go" : "brick_stop") +".png'/>"; },
                  function(data) { return data.pointType; },
                  function(data) { 
                          return "<img src='images/bullet_delete.png' class='ptr' "+
                                  "onclick='removeFromSelectedPoints("+ data.id +")'/>";
                  }
              ],
              {
                  rowCreator: function(options) {
                      var tr = document.createElement("tr");
                      tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                      return tr;
                  },
                  cellCreator: function(options) {
                      var td = document.createElement("td");
                      if (options.cellNum == 1 || options.cellNum == 3)
                          td.align = "center";
                      return td;
                  } 
              });
      }
      refreshAvailablePoints();
  }
  
  function refreshAvailablePoints() {
      dwr.util.removeAllOptions("availablePoints");
      var availPoints = new Array();
      for (var i=0; i<allPoints.length; i++) {
          var found = false;
          for (var j=0; j<selectedPoints.length; j++) {
              if (selectedPoints[j].id == allPoints[i].id) {
                  found = true;
                  break;
              }
          }
          
          if (!found)
              availPoints[availPoints.length] = allPoints[i];
      }
      dwr.util.addOptions("availablePoints", availPoints, "id", "name");
  }
  
  function savePublisherImpl(name, xid, enabled, cacheWarningSize, changesOnly, sendSnapshot, snapshotSendPeriods,
          snapshotSendPeriodType) {
      // Clear messages.
      hide("hostMsg");
      hide("portMsg");
      hide("pointsMsg");
      
      var points = new Array();
      for (var i=0; i<selectedPoints.length; i++)
          points[points.length] = {dataPointId: selectedPoints[i].id};
      
      PublisherEditDwr.savePersistentSender(name, xid, enabled, points, $get("host"), $get("port"),
              $get("authorizationKey"), $get("syncType"), cacheWarningSize, changesOnly, sendSnapshot, 
              snapshotSendPeriods, snapshotSendPeriodType, savePublisherCB);
  }
</script>

<table cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <div class="borderDiv marR marB">
        <table>
          <tr>
            <td colspan="2" class="smallTitle"><fmt:message key="publisherEdit.persistent.props"/> <tag:help id="persistentPublishing"/></td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><fmt:message key="publisherEdit.persistent.host"/></td>
            <td class="formField">
              <input type="text" id="host" value="${publisher.host}" class="formLong"/>
              <div id="hostMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
          
            <td class="formLabelRequired"><fmt:message key="publisherEdit.persistent.port"/></td>
            <td class="formField">
              <input type="text" id="port" value="${publisher.port}"/>
              <div id="portMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><fmt:message key="publisherEdit.persistent.authorizationKey"/></td>
            <td class="formField"><input type="text" id="authorizationKey" value="${publisher.authorizationKey}"/></td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><fmt:message key="publisherEdit.persistent.sync"/></td>
            <td class="formField">
              <sst:select id="syncType" value="${publisher.syncType}">
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_NONE) %>"><fmt:message key="publisherEdit.persistent.sync.none"/></sst:option>
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_DAILY) %>"><fmt:message key="publisherEdit.persistent.sync.daily"/></sst:option>
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_WEEKLY) %>"><fmt:message key="publisherEdit.persistent.sync.weekly"/></sst:option>
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_MONTHLY) %>"><fmt:message key="publisherEdit.persistent.sync.monthly"/></sst:option>
              </sst:select>
            </td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
</table>

<table cellpadding="0" cellspacing="0"><tr><td>
  <div class="borderDiv">
    <table width="100%">
      <tr>
        <td class="smallTitle"><fmt:message key="publisherEdit.points"/></td>
        <td align="right">
          <select id="availablePoints"></select>
          <tag:img png="icon_comp_add" onclick="selectPoint()"/>
        </td>
      </tr>
    </table>
    
    <table cellspacing="1" cellpadding="0">
      <tr class="rowHeader">
        <td><fmt:message key="publisherEdit.point.name"/></td>
        <td><fmt:message key="publisherEdit.point.status"/></td>
        <td><fmt:message key="publisherEdit.point.type"/></td>
        <td></td>
      </tr>
      <tbody id="selectedPointsEmpty" style="display:none;"><tr><td colspan="5"><fmt:message key="publisherEdit.noPoints"/></td></tr></tbody>
      <tbody id="selectedPoints"></tbody>
    </table>
    <div id="pointsMsg" class="formError" style="display:none;"></div>
  </div>
</td></tr></table>