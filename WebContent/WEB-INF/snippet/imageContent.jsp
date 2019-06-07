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
<c:choose>
  <c:when test="${empty image}"><tag:img png="icon_comp" title="common.noImage"/></c:when>
  <c:otherwise>
    <img src="${image}" width="${pointComponent.width}" height="${pointComponent.height}" alt=""/>
  </c:otherwise>
</c:choose>
<c:if test="${pointComponent.displayText}">
  <div style="position:absolute;left:${pointComponent.textX}px;top:${pointComponent.textY}px;">
    <%@ include file="/WEB-INF/snippet/basicContent.jsp" %>
  </div>
</c:if>