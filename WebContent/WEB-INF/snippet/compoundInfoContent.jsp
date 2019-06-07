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
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<b>${compoundComponent.name}</b><br/>
<c:forEach items="${childData}" var="child">
  <c:if test="${!empty child.point}">
    &nbsp;&nbsp;&nbsp;
    <c:if test="${!empty sessionUser}">
      <tag:img png="icon_comp" title="watchlist.pointDetails" style="display:inline"
              onclick="window.location='data_point_details.shtm?dpid=${child.point.id}'"/>
    </c:if>
    ${child.name}: <span class="infoData">${mango:htmlText(child.point, child.pointValue)}</span><br/>
  </c:if>
</c:forEach>