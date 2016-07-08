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

<table cellspacing="1" cellpadding="0" border="0" style="width: ${width}px;">
    <tr class="rowHeader">
	    <c:if test="${!hideIdColumn}"><td><fmt:message key="events.id"/></td></c:if>
	    <c:if test="${!hideAlarmLevelColumn}"><td><fmt:message key="common.alarmLevel"/></td></c:if>
	    <c:if test="${!hideTimestampColumn}"><td><fmt:message key="common.time"/></td></c:if>
	    <td><fmt:message key="events.msg"/></td>
	    <c:if test="${!hideInactivityColumn}"><td><fmt:message key="common.inactiveTime"/></td></c:if>
	    <c:if test="${!hideAckColumn}"> <td>&nbsp;</td></c:if>  
    </tr>
    <c:if test="${empty events}"><tr><td colspan="6"><b><fmt:message key="events.emptyList"/></b></td></tr></c:if>
    <c:forEach items="${events}" var="event" varStatus="status">
      <tr class="row<c:if test="${status.index % 2 == 1}">Alt</c:if>">
      	<c:if test="${!hideIdColumn}"><td align="center">${event.id}</td></c:if>
      	<c:if test="${!hideAlarmLevelColumn}"><td align="center"><tag:eventIcon event="${event}"/></td></c:if>
        <c:if test="${!hideTimestampColumn}"><td align="center">${sst:time(event.activeTimestamp)}</td></c:if>
        <td> <b><sst:i18n message="${event.message}"/></b></td>
        <c:if test="${!hideInactivityColumn}">
	        <td>
	          <c:choose>
	            <c:when test="${event.active}">
	              <fmt:message key="common.active"/>
	              <a href="events.shtm"><tag:img png="flag_white" title="common.active"/></a>
	            </c:when>
	            <c:when test="${!event.rtnApplicable}"><fmt:message key="common.nortn"/></c:when>
	            <c:otherwise>
	              ${sst:time(event.rtnTimestamp)} - <sst:i18n message="${event.rtnMessage}"/>
	            </c:otherwise>
	          </c:choose>
	        </td>
        </c:if>
        <c:if test="${!hideAckColumn}"> <td> <tag:alarmAck event="${event}"/></td></c:if>  
      </tr>
    </c:forEach>
  </table>