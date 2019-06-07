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
--%><%--
--%><%-- The snippet used for image charts in rollovers --%><%--
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %><%--
--%><c:choose><%--
  --%><c:when test="${empty pointValue}"><tag:img png="hourglass" title="common.noData"/></c:when><%--
  --%><c:otherwise><img src="chart/${pointValue.time}_${point.chartRenderer.duration}_${point.id}.png?w=400&h=150" width="400" height="150" alt="<fmt:message key="common.genChart"/>"/></c:otherwise><%--
--%></c:choose>