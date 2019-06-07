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
--%><%@attribute name="excludeBinary" type="java.lang.Boolean" %><%--
--%><%@attribute name="excludeMultistate" type="java.lang.Boolean" %><%--
--%><%@attribute name="excludeNumeric" type="java.lang.Boolean" %><%--
--%><%@attribute name="excludeAlphanumeric" type="java.lang.Boolean" %><%--
--%><%@attribute name="excludeImage" type="java.lang.Boolean" %><%--
--%><%@tag import="com.serotonin.mango.DataTypes"%><%--
--%><c:if test="${!excludeBinary}"><option value="<%= DataTypes.BINARY %>"><fmt:message key="common.dataTypes.binary"/></option></c:if>
<c:if test="${!excludeMultistate}"><option value="<%= DataTypes.MULTISTATE %>"><fmt:message key="common.dataTypes.multistate"/></option></c:if>
<c:if test="${!excludeNumeric}"><option value="<%= DataTypes.NUMERIC %>"><fmt:message key="common.dataTypes.numeric"/></option></c:if>
<c:if test="${!excludeAlphanumeric}"><option value="<%= DataTypes.ALPHANUMERIC %>"><fmt:message key="common.dataTypes.alphanumeric"/></option></c:if>
<c:if test="${!excludeImage}"><option value="<%= DataTypes.IMAGE %>"><fmt:message key="common.dataTypes.image"/></option></c:if>