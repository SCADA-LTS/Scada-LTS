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
--%><%@tag body-content="empty"%><%--
--%><%@attribute name="sst" type="java.lang.Boolean"%>
<c:choose>
  <c:when test="${sst}">
    <sst:option value="<%= Integer.toString(DateTimeConstants.JANUARY) %>"><spring:message code="common.month.jan"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.FEBRUARY) %>"><spring:message code="common.month.feb"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.MARCH) %>"><spring:message code="common.month.mar"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.APRIL) %>"><spring:message code="common.month.apr"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.MAY) %>"><spring:message code="common.month.may"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.JUNE) %>"><spring:message code="common.month.jun"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.JULY) %>"><spring:message code="common.month.jul"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.AUGUST) %>"><spring:message code="common.month.aug"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.SEPTEMBER) %>"><spring:message code="common.month.sep"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.OCTOBER) %>"><spring:message code="common.month.oct"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.NOVEMBER) %>"><spring:message code="common.month.nov"/></sst:option>
    <sst:option value="<%= Integer.toString(DateTimeConstants.DECEMBER) %>"><spring:message code="common.month.dec"/></sst:option>
  </c:when>
  <c:otherwise>
    <option value="<%= DateTimeConstants.JANUARY %>"><spring:message code="common.month.jan"/></option>
    <option value="<%= DateTimeConstants.FEBRUARY %>"><spring:message code="common.month.feb"/></option>
    <option value="<%= DateTimeConstants.MARCH %>"><spring:message code="common.month.mar"/></option>
    <option value="<%= DateTimeConstants.APRIL %>"><spring:message code="common.month.apr"/></option>
    <option value="<%= DateTimeConstants.MAY %>"><spring:message code="common.month.may"/></option>
    <option value="<%= DateTimeConstants.JUNE %>"><spring:message code="common.month.jun"/></option>
    <option value="<%= DateTimeConstants.JULY %>"><spring:message code="common.month.jul"/></option>
    <option value="<%= DateTimeConstants.AUGUST %>"><spring:message code="common.month.aug"/></option>
    <option value="<%= DateTimeConstants.SEPTEMBER %>"><spring:message code="common.month.sep"/></option>
    <option value="<%= DateTimeConstants.OCTOBER %>"><spring:message code="common.month.oct"/></option>
    <option value="<%= DateTimeConstants.NOVEMBER %>"><spring:message code="common.month.nov"/></option>
    <option value="<%= DateTimeConstants.DECEMBER %>"><spring:message code="common.month.dec"/></option>
  </c:otherwise>
</c:choose>