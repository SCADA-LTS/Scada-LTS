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
<%--
  This snippet supports all data types.
--%>
<%@ include file="/WEB-INF/snippet/common.jsp" %>
<c:if test="${!empty disabled}">
  <tag:img png="warn" title="common.pointWarning"/> <fmt:message key="common.pointWarning"/><br/>
</c:if>
<c:if test="${pointRT.attributes.UNRELIABLE || pointRT.attributes.DP_UPDATE_ERROR}">
  <tag:img png="exclamation" title="common.valueUnreliable"/> <fmt:message key="common.valueUnreliable"/>
  <tag:img png="arrow_refresh" title="common.refresh" onclick="MiscDwr.forcePointRead(${point.id})"/><br/>
</c:if>
<c:forEach items="${events}" var="event">
	<c:if test="${event.alarmLevel>0}">
	  <tag:eventIcon event="${event}"/>
	  ${sst:time(event.activeTimestamp)} - <sst:i18n message="${event.message}"/>
	  <tag:alarmAck event="${event}"/><br/>
	 </c:if>
</c:forEach>