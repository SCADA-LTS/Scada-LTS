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
--%><%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@attribute name="optionList" type="java.util.List" required="true"%><%--
--%><%@attribute name="sst" type="java.lang.Boolean"%><%--
--%><c:forEach items="${optionList}" var="option">
  <c:choose>
    <c:when test="${sst}"><sst:option value="${option.key}"><fmt:message key="${option.value}"/></sst:option></c:when>
    <c:otherwise><option value="${option.key}"><fmt:message key="${option.value}"/></option></c:otherwise>
  </c:choose>
</c:forEach>