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
<%@ include file="/WEB-INF/snippet/common.jsp" %>
<c:set var="content"><%--
  --%><c:choose><%--
    --%><c:when test="${displayPointName}">${pointComponent.name}:&nbsp;<b>${mango:htmlText(point, pointValue)}</b></c:when><%--
    --%><c:otherwise>${mango:htmlText(point, pointValue)}</c:otherwise><%--
  --%></c:choose><%--
--%></c:set>
<c:if test="${!empty styleAttribute}"><div style="${styleAttribute}"></c:if>
<c:choose>
  <c:when test='${!empty viewComponent}'>
    <c:choose>
      <c:when test='${empty viewComponent.bkgdColorOverride}'>
        <span class="simpleRenderer"/>${content}</span>
      </c:when>
      <c:when test='${viewComponent.bkgdColorOverride == "transparent"}'>
        <span class="simpleRenderer" style="background:transparent;border:0;"/>${content}</span>
      </c:when>
      <c:otherwise>
        <span class="simpleRenderer" style="background-color:${viewComponent.bkgdColorOverride};"/>${content}</span>
      </c:otherwise>
    </c:choose>
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test='${empty pointComponent.bkgdColorOverride}'>
        <span class="simpleRenderer"/>${content}</span>
      </c:when>
      <c:when test='${pointComponent.bkgdColorOverride == "transparent"}'>
        <span class="simpleRenderer" style="background:transparent;border:0;"/>${content}</span>
      </c:when>
      <c:otherwise>
        <span class="simpleRenderer" style="background-color:${pointComponent.bkgdColorOverride};"/>${content}</span>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>
<c:if test="${!empty styleAttribute}"></div></c:if>