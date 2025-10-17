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
--%><%@tag import="org.joda.time.DateTimeConstants"%><%--
--%><%@attribute name="unitsMap" type="java.util.Map" required="true" rtexprvalue="true"%><%--
--%><%@tag body-content="empty"%>

<c:forEach items="${unitsMap}" var="entry">
    <optgroup label="<spring:message code="${entry.key}"/>">
        <c:forEach items="${entry.value}" var="entryValue">
          <sst:option value="${entryValue.value}"><spring:message code="${entryValue.key}"/> <c:out value="${entryValue.suffix}"/> </sst:option>
        </c:forEach>
    </optgroup>
</c:forEach>