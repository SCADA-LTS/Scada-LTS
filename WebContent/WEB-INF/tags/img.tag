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
--%><%@attribute name="id" rtexprvalue="true"%><%--
--%><%@attribute name="src"%><%--
--%><%@attribute name="png"%><%--
--%><%@attribute name="title"%><%--
--%><%@attribute name="onclick" rtexprvalue="true"%><%--
--%><%@attribute name="onmouseover"%><%--
--%><%@attribute name="onmouseout"%><%--
--%><%@attribute name="style"%><%--
--%><img<c:if test="${!empty id}"> id="${id}"</c:if><%--
--%><c:if test="${!empty src}"> src="${src}"</c:if><%--
--%><c:if test="${!empty png && empty src}"> src="images/${png}.png"</c:if><%--
--%><c:if test="${!empty title}"> alt="<fmt:message key="${title}"/>" title="<fmt:message key="${title}"/>"</c:if><%--
--%><c:if test="${!empty onclick}"> class="ptr" onclick="${onclick}"</c:if><%--
--%><c:if test="${!empty onmouseover}"> onmouseover="${onmouseover}"</c:if><%--
--%><c:if test="${!empty onmouseout}"> onmouseout="${onmouseout}"</c:if><%--
--%><c:if test="${!empty style}"> style="${style}"</c:if><%--
--%> border="0"/>