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
  var pointsContext;
  
  dojo.addOnLoad(function() { 
      PublisherEditDwr.initSender(function(response) {
          pointsContext = new PersistentPointsContext(new DataPointsSelect({
              selectHtmlId: "availablePoints",
              placeholderTextSingle: "<spring:message code='chosen.selector.selectPoint'/>",
              excludePointsArray: response.data.publisher.points,
              pointsArray: response.data.selectedPoints
          }), "selectedPoints");
      });
  });
  
  function selectPoint() {
      pointsContext.addPointToContext();
  }
  
  function savePublisherImpl(name, xid, enabled, cacheWarningSize, changesOnly, sendSnapshot, snapshotSendPeriods,
          snapshotSendPeriodType) {
      // Clear messages.
      hide("hostMsg");
      hide("portMsg");
      hide("pointsMsg");
      
      let points = pointsContext.convertToSave();
      
      PublisherEditDwr.savePersistentSender(name, xid, enabled, points, $get("host"), $get("port"),
              $get("authorizationKey"), xid, $get("syncType"), cacheWarningSize, changesOnly, sendSnapshot,
              snapshotSendPeriods, snapshotSendPeriodType, savePersistentCB);
  }

  function savePersistentCB(response) {
    savePublisherCB(response);
    if(!response.hasMessages) {
        pointsContext.setPointsArray(response.data.selectedPoints);
        pointsContext.init(response.data.publisher.points);
    }
  }
</script>

<table cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <div class="borderDiv marR marB">
        <table>
          <tr>
            <td colspan="2" class="smallTitle"><spring:message code="publisherEdit.persistent.props"/> <tag:help id="persistentPublishing"/></td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><spring:message code="publisherEdit.persistent.host"/></td>
            <td class="formField">
              <input type="text" id="host" value="${publisher.host}" class="formLong"/>
              <div id="hostMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
          
            <td class="formLabelRequired"><spring:message code="publisherEdit.persistent.port"/></td>
            <td class="formField">
              <input type="text" id="port" value="${publisher.port}"/>
              <div id="portMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><spring:message code="publisherEdit.persistent.authorizationKey"/></td>
            <td class="formField"><input type="text" id="authorizationKey" value="${publisher.authorizationKey}"/></td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><spring:message code="publisherEdit.persistent.sync"/></td>
            <td class="formField">
              <sst:select id="syncType" value="${publisher.syncType}">
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_NONE) %>"><spring:message code="publisherEdit.persistent.sync.none"/></sst:option>
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_DAILY) %>"><spring:message code="publisherEdit.persistent.sync.daily"/></sst:option>
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_WEEKLY) %>"><spring:message code="publisherEdit.persistent.sync.weekly"/></sst:option>
                <sst:option value="<%= Integer.toString(PersistentSenderVO.SYNC_TYPE_MONTHLY) %>"><spring:message code="publisherEdit.persistent.sync.monthly"/></sst:option>
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
        <td class="smallTitle"><spring:message code="publisherEdit.points"/></td>
        <td align="right">
          <select id="availablePoints" style="display:none;"></select>
          <tag:img id="icon_comp_add" png="icon_comp_add" onclick="selectPoint()" style="display:none;"/>
        </td>
      </tr>
    </table>
    
    <table cellspacing="1" cellpadding="0">
      <tr class="rowHeader">
        <td><spring:message code="publisherEdit.point.name"/></td>
        <td><spring:message code="publisherEdit.point.xid"/></td>
        <td><spring:message code="publisherEdit.point.status"/></td>
        <td><spring:message code="publisherEdit.point.type"/></td>
        <td></td>
      </tr>
      <tbody id="selectedPointsEmpty" style="display:none;"><tr><td colspan="5"><spring:message code="publisherEdit.noPoints"/></td></tr></tbody>
      <tbody id="selectedPoints"></tbody>
    </table>
    <div id="pointsMsg" class="formError" style="display:none;"></div>
  </div>
</td></tr></table>