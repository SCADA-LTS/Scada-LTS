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
<c:if test="${!empty invalid || !empty disabled || !empty events || pointRT.attributes.UNRELIABLE || pointRT.attributes.DISCONNECT}">
<%-- <c:if test="${!empty invalid || !empty disabled || !empty events }"> --%>
  <table width="200" cellspacing="0" cellpadding="0">
    <c:choose>
      <c:when test="${pointRT.attributes.DISCONNECTED}">
        <tr>
          <td valign="top"><tag:img png="disconnect" title="common.noConnection"/></td>
          <td colspan="3"><fmt:message key="common.noConnection"/></td>
        </tr>
      </c:when>
      <c:when test="${!empty invalid}">
        <tr>
          <td valign="top"><tag:img png="warn" title="common.warning"/></td>
          <td colspan="3"><fmt:message key="common.pointInvalid"/></td>
        </tr>
      </c:when>
      <c:when test="${!empty disabled}">
        <tr>
          <td valign="top"><tag:img png="warn" title="common.warning"/></td>
          <td colspan="3"><fmt:message key="common.pointWarning"/></td>
        </tr>
      </c:when>
      <c:when test="${pointRT.attributes.UNRELIABLE}">
		<tr>
	      <td><tag:img png="warn" title="common.valueUnreliable"/></td>
	      <td style="white-space:nowrap;" colspan="3">
	      <fmt:message key="common.valueUnreliable"/>
	      <tag:img png="arrow_refresh" title="common.refresh" onclick="WatchListDwr.forcePointRead(${point.id})" style="display:inline"/>
	      </td>
	    </tr>
	  </c:when>
    </c:choose>
    <c:if test="${!empty events}">
      <c:forEach items="${events}" var="event">
        <tr>
          <td><tag:eventIcon event="${event}"/></td>
          <td style="white-space:nowrap;">&nbsp;<tag:alarmAck event="${event}"/></td>
          <td>${sst:time(event.activeTimestamp)}</td>
          <td style="white-space:nowrap;">&nbsp;<sst:i18n message="${event.message}"/></td>
        </tr>
      </c:forEach>
    </c:if>
  </table>
</c:if>