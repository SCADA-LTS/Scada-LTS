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
<mobileTag:page>
  <table border="1">
    <c:forEach items="${watchListData}" var="state">
      <tr>
        <td>${state.name}</td>
        <c:choose>
          <c:when test="${state.disabled}">
            <td colspan="2"><fmt:message key="common.pointWarning"/></td>
          </c:when>
          <c:otherwise>
            <td align="center">${state.value}</td>
            <td>${state.time}</td>
          </c:otherwise>
        </c:choose>
      </tr>
    </c:forEach>
  </table>
  
  <fmt:message key="header.watchlist"/>:
  <sst:select id="watchListSelect" value="${selectedWatchList}"
          onchange="window.location='mobile_watch_list.shtm?watchListId='+ this.value">
    <c:forEach items="${watchLists}" var="wl">
      <sst:option value="${wl.key}">${wl.value}</sst:option>
    </c:forEach>
  </sst:select>

  <a href="mobile_watch_list.shtm"><fmt:message key="header.reload"/></a>
  
  <a href="mobile_logout.htm"><fmt:message key="header.logout"/></a>
</mobileTag:page>