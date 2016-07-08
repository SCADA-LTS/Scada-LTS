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
--%><%@include file="/WEB-INF/tags/decl.tagf" %><%--
--%><%@tag body-content="empty" %><%--
--%><%@attribute name="event" type="com.serotonin.mango.rt.event.EventInstance" rtexprvalue="true" %><%--
--%><%@attribute name="eventBean" type="com.serotonin.mango.web.dwr.beans.EventInstanceBean" %><%--
--%><c:if test="${empty event}"><c:set var="event" value="${eventBean}"/></c:if><%--
--%><c:choose>
  <c:when test="${event.active && event.alarmLevel == 0}"><tag:img png="flag_green" title="common.alarmLevel.none"/></c:when>
  <c:when test="${event.alarmLevel == 0}"><tag:img png="flag_green_off" title="common.alarmLevel.none.rtn"/></c:when>
  <c:when test="${event.active && event.alarmLevel == 1}"><tag:img png="flag_blue" title="common.alarmLevel.info"/></c:when>
  <c:when test="${event.alarmLevel == 1}"><tag:img png="flag_blue_off" title="common.alarmLevel.info.rtn"/></c:when>
  <c:when test="${event.active && event.alarmLevel == 2}"><tag:img png="flag_yellow" title="common.alarmLevel.urgent"/></c:when>
  <c:when test="${event.alarmLevel == 2}"><tag:img png="flag_yellow_off" title="common.alarmLevel.urgent.rtn"/></c:when>
  <c:when test="${event.active && event.alarmLevel == 3}"><tag:img png="flag_orange" title="common.alarmLevel.critical"/></c:when>
  <c:when test="${event.alarmLevel == 3}"><tag:img png="flag_orange_off" title="common.alarmLevel.critical.rtn"/></c:when>
  <c:when test="${event.active && event.alarmLevel == 4}"><tag:img png="flag_red" title="common.alarmLevel.lifeSafety"/></c:when>
  <c:when test="${event.alarmLevel == 4}"><tag:img png="flag_red_off" title="common.alarmLevel.lifeSafety.rtn"/></c:when>
  <c:otherwise>(<fmt:message key="common.alarmLevel.unknown"/> ${event.alarmLevel})</c:otherwise>
</c:choose>