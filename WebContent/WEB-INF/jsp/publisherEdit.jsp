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
<tag:page dwr="PublisherEditDwr" onload="initPublisher">
  <script type="text/javascript">
    function savePublisher() {
        hide("nameMsg");
        hide("cacheWarningSizeMsg");
        hide("message");
        
        savePublisherImpl($get("name"), $get("xid"), $get("enabled"), $get("cacheWarningSize"),
                $get("changesOnly") == "true", $get("sendSnapshot"), $get("snapshotSendPeriods"),
                $get("snapshotSendPeriodType"));
    }
    
    function savePublisherCB(response) {
        if (response.hasMessages) {
            for (var i=0; i<response.messages.length; i++)
                showMessage(response.messages[i].contextKey +"Msg", response.messages[i].contextualMessage);
        }
        else
            showMessage("message", "<fmt:message key="publisherEdit.saved"/>");
    }
    
    function sendSnapshotChanged() {
        if ($get("sendSnapshot")) {
            setDisabled("snapshotSendPeriods", false);
            setDisabled("snapshotSendPeriodType", false);
        }
        else {
            setDisabled("snapshotSendPeriods", true);
            setDisabled("snapshotSendPeriodType", true);
        }
    }
    
    function initPublisher() {
        sendSnapshotChanged();
    }
  </script>
  
  <table cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td>
        <c:if test="${!empty publisherEvents}">
          <table class="borderDiv marB">
            <tr><td class="smallTitle"><fmt:message key="publisherEdit.currentAlarms"/></td></tr>
            <c:forEach items="${publisherEvents}" var="event">
              <tr><td class="formError">
                <tag:eventIcon eventBean="${event}"/>
                ${event.prettyActiveTimestamp}:
                ${event.message}
              </td></tr>
            </c:forEach>
          </table>
        </c:if>
        
        <div id="message" class="formError" style="display:none;"></div>
        
        <div class="borderDiv marR marB">
          <table>
            <tr>
              <td colspan="2" class="smallTitle">
                <tag:img png="transmit" title="common.edit"/>
                <fmt:message key="publisherEdit.generalProperties"/> <tag:help id="generalPublisherProperties"/>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="publisherEdit.name"/></td>
              <td class="formField">
                <input type="text" id="name" value="${publisher.name}"/>
                <div id="nameMsg" class="formError" style="display:none;"></div>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
              <td class="formField">
                <input type="text" id="xid" value="${publisher.xid}"/>
                <div id="xidMsg" class="formError" style="display:none;"></div>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.enabled"/></td>
              <td class="formField"><sst:checkbox id="enabled" selectedValue="${publisher.enabled}"/></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="publisherEdit.cacheWarning"/></td>
              <td class="formField">
                <input type="text" id="cacheWarningSize" value="${publisher.cacheWarningSize}" class="formShort"/>
                <div id="cacheWarningSizeMsg" class="formError" style="display:none;"></div>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="publisherEdit.updateEvent"/></td>
              <td class="formField">
                <sst:select id="changesOnly" value="${publisher.changesOnly}">
                  <sst:option value="false"><fmt:message key="publisherEdit.updateEvent.all"/></sst:option>
                  <sst:option value="true"><fmt:message key="publisherEdit.updateEvent.changes"/></sst:option>
                </sst:select>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="publisherEdit.snapshot"/></td>
              <td class="formField"><sst:checkbox id="sendSnapshot" onclick="sendSnapshotChanged()"
                      selectedValue="${publisher.sendSnapshot}"/></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="publisherEdit.snapshotPeriod"/></td>
              <td class="formField">
                <input type="text" id="snapshotSendPeriods" value="${publisher.snapshotSendPeriods}" class="formShort"/>
                <sst:select id="snapshotSendPeriodType" value="${publisher.snapshotSendPeriodType}">
                  <tag:timePeriodOptions sst="true" s="true" min="true" h="true"/>
                </sst:select>
                <div id="snapshotSendPeriodsMsg" class="formError" style="display:none;"></div>
              </td>
            </tr>
          </table>
        </div>
        
        <div>
          <c:choose>
            <c:when test="${publisher.type.id == applicationScope['constants.PublisherVO.Types.HTTP_SENDER']}">
              <%@ include file="/WEB-INF/jsp/publisherEdit/editHttpSender.jsp" %>
            </c:when>
            <c:when test="${publisher.type.id == applicationScope['constants.PublisherVO.Types.PACHUBE']}">
              <%@ include file="/WEB-INF/jsp/publisherEdit/editPachube.jsp" %>
            </c:when>
            <c:when test="${publisher.type.id == applicationScope['constants.PublisherVO.Types.PERSISTENT']}">
              <%@ include file="/WEB-INF/jsp/publisherEdit/editPersistent.jsp" %>
            </c:when>
          </c:choose>
        </div>
      </td>
    </tr>
    
    <tr><td>&nbsp;</td></tr>
    
    <tr>
      <td align="center">
        <input type="button" value="<fmt:message key="common.save"/>" onclick="savePublisher()"/>
        <input type="button" value="<fmt:message key="common.cancel"/>" onclick="window.location='publishers.shtm'"/>
      </td>
    </tr>
  </table>
</tag:page>