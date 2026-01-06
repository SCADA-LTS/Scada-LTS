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
<script type="text/javascript">
  var allPoints = new Array();  
  var pointsContext;
  
  dojo.addOnLoad(function() {
      PublisherEditDwr.initSender(function(response) {

        let ref = {}
        ref.excludePointsArray = response.data.publisher.points;
        ref.selectHtmlId = "availablePoints";
        ref.placeholderTextSingle = "<spring:message code='chosen.selector.selectPoint'/>";
        ref.pointsArray = response.data.selectedPoints;
        ref.dataTypes = [];
        pointsContext = new PachubePointsContext(ref, "selectedPoints");
      });
  });
  
  function selectPoint() {
      pointsContext.addPointToContext();
  }
  
  function savePublisherImpl(name, xid, enabled, cacheWarningSize, changesOnly, sendSnapshot, snapshotSendPeriods,
          snapshotSendPeriodType) {
      // Clear messages.
      hide("apiKeyMsg");
      hide("timeoutSecondsMsg");
      hide("retriesMsg");
      hide("pointsMsg");
      
      let points = pointsContext.convertToSave();
      
      PublisherEditDwr.savePachubeSender(name, xid, enabled, points, $get("apiKey"), $get("timeoutSeconds"),
              $get("retries"), cacheWarningSize, changesOnly, sendSnapshot, snapshotSendPeriods, snapshotSendPeriodType,
              savePachubeCB);
  }

  function savePachubeCB(response) {
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
            <td colspan="2" class="smallTitle"><spring:message code="publisherEdit.pachube.props"/> <tag:help id="pachubePublishing"/></td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><spring:message code="publisherEdit.pachube.apiKey"/></td>
            <td class="formField">
              <input type="text" id="apiKey" value="${publisher.apiKey}" class="formLong"/>
              <div id="apiKeyMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><spring:message code="publisherEdit.pachube.timeoutSeconds"/></td>
            <td class="formField">
              <input type="text" id="timeoutSeconds" value="${publisher.timeoutSeconds}"/>
              <div id="timeoutSecondsMsg" class="formError" style="display:none;"></div>
            </td>
          </tr>
          
          <tr>
            <td class="formLabelRequired"><spring:message code="publisherEdit.pachube.retries"/></td>
            <td class="formField">
              <input type="text" id="retries" value="${publisher.retries}"/>
              <div id="retriesMsg" class="formError" style="display:none;"></div>
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
          <select id="availablePoints"></select>
          <tag:img png="icon_comp_add" onclick="selectPoint()"/>
        </td>
      </tr>
    </table>
    
    <table cellspacing="1" cellpadding="0">
      <tr class="rowHeader">
        <td><spring:message code="publisherEdit.point.name"/></td>
        <td><spring:message code="publisherEdit.point.xid"/></td>
        <td><spring:message code="publisherEdit.point.status"/></td>
        <td><spring:message code="publisherEdit.point.type"/></td>
        <td><spring:message code="publisherEdit.pachube.point.feedId"/></td>
        <td><spring:message code="publisherEdit.pachube.point.dataStreamId"/></td>
        <td></td>
      </tr>
      <tbody id="selectedPointsEmpty" style="display:none;"><tr><td colspan="5"><spring:message code="publisherEdit.noPoints"/></td></tr></tbody>
      <tbody id="selectedPoints"></tbody>
    </table>
    <div id="pointsMsg" class="formError" style="display:none;"></div>
  </div>
</td></tr></table>