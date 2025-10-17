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
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<c:if test="${!empty invalid || !empty disabled || !empty events || pointRT.attributes.UNRELIABLE || pointRT.attributes.DP_UPDATE_ERROR}">
  <table width="200" cellspacing="0" cellpadding="0">
    <c:choose>
      <c:when test="${!empty invalid}">
        <tr>
          <td valign="top"><tag:img png="warn" title="common.warning"/></td>
          <td colspan="3"><spring:message code="common.pointInvalid"/></td>
        </tr>
      </c:when>
      <c:when test="${!empty disabled}">
        <tr>
          <td valign="top"><tag:img png="warn" title="common.warning"/></td>
          <td colspan="3"><spring:message code="common.pointWarning"/></td>
        </tr>
      </c:when>
    </c:choose>
    <c:if test="${pointRT.attributes.UNRELIABLE || pointRT.attributes.DP_UPDATE_ERROR}">
      <tr>
        <td><tag:img png="exclamation" title="common.valueUnreliable"/></td>
        <td style="white-space:nowrap;" colspan="3">
          <spring:message code="common.valueUnreliable"/>
          <tag:img png="arrow_refresh" title="common.refresh" onclick="MiscDwr.forcePointRead(${point.id})" style="display:inline"/>
        </td>
      </tr>
    </c:if>
    <c:if test="${!empty events}">
      <c:forEach items="${events}" var="event">
        <c:if test="${event.alarmLevel>0}">
            <tr>
              <td><tag:eventIcon event="${event}"/></td>
              <td style="white-space:nowrap;">&nbsp;<tag:alarmAck event="${event}"/></td>
              <td>${sst:time(event.activeTimestamp)}</td>
              <td style="white-space:nowrap;">&nbsp;<sst:i18n message="${event.message}"/></td>
            </tr>
        </c:if>
      </c:forEach>
    </c:if>
  </table>
</c:if>