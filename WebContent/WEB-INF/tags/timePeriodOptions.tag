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
--%><%@attribute name="sst" type="java.lang.Boolean" %><%--
--%><%@attribute name="ms" type="java.lang.Boolean" %><%--
--%><%@attribute name="s" type="java.lang.Boolean" %><%--
--%><%@attribute name="min" type="java.lang.Boolean" %><%--
--%><%@attribute name="h" type="java.lang.Boolean" %><%--
--%><%@attribute name="d" type="java.lang.Boolean" %><%--
--%><%@attribute name="w" type="java.lang.Boolean" %><%--
--%><%@attribute name="mon" type="java.lang.Boolean" %><%--
--%><%@attribute name="y" type="java.lang.Boolean" %><%--
--%><%@attribute name="singular" type="java.lang.Boolean" %><%--
--%><%@tag import="com.serotonin.mango.Common"%><%--
--%><c:choose>
  <c:when test="${sst}">
    <c:choose>
      <c:when test="${singular}">
        <c:if test="${ms}"><sst:option value="<%= Integer.toString(Common.TimePeriods.MILLISECONDS) %>"><fmt:message key="common.tp.millisecond"/></sst:option></c:if>
        <c:if test="${s}"><sst:option value="<%= Integer.toString(Common.TimePeriods.SECONDS) %>"><fmt:message key="common.tp.second"/></sst:option></c:if>
        <c:if test="${min}"><sst:option value="<%= Integer.toString(Common.TimePeriods.MINUTES) %>"><fmt:message key="common.tp.minute"/></sst:option></c:if>
        <c:if test="${h}"><sst:option value="<%= Integer.toString(Common.TimePeriods.HOURS) %>"><fmt:message key="common.tp.hour"/></sst:option></c:if>
        <c:if test="${d}"><sst:option value="<%= Integer.toString(Common.TimePeriods.DAYS) %>"><fmt:message key="common.tp.day"/></sst:option></c:if>
        <c:if test="${w}"><sst:option value="<%= Integer.toString(Common.TimePeriods.WEEKS) %>"><fmt:message key="common.tp.week"/></sst:option></c:if>
        <c:if test="${mon}"><sst:option value="<%= Integer.toString(Common.TimePeriods.MONTHS) %>"><fmt:message key="common.tp.month"/></sst:option></c:if>
        <c:if test="${y}"><sst:option value="<%= Integer.toString(Common.TimePeriods.YEARS) %>"><fmt:message key="common.tp.year"/></sst:option></c:if>
      </c:when>
      <c:otherwise>
        <c:if test="${ms}"><sst:option value="<%= Integer.toString(Common.TimePeriods.MILLISECONDS) %>"><fmt:message key="common.tp.milliseconds"/></sst:option></c:if>
        <c:if test="${s}"><sst:option value="<%= Integer.toString(Common.TimePeriods.SECONDS) %>"><fmt:message key="common.tp.seconds"/></sst:option></c:if>
        <c:if test="${min}"><sst:option value="<%= Integer.toString(Common.TimePeriods.MINUTES) %>"><fmt:message key="common.tp.minutes"/></sst:option></c:if>
        <c:if test="${h}"><sst:option value="<%= Integer.toString(Common.TimePeriods.HOURS) %>"><fmt:message key="common.tp.hours"/></sst:option></c:if>
        <c:if test="${d}"><sst:option value="<%= Integer.toString(Common.TimePeriods.DAYS) %>"><fmt:message key="common.tp.days"/></sst:option></c:if>
        <c:if test="${w}"><sst:option value="<%= Integer.toString(Common.TimePeriods.WEEKS) %>"><fmt:message key="common.tp.weeks"/></sst:option></c:if>
        <c:if test="${mon}"><sst:option value="<%= Integer.toString(Common.TimePeriods.MONTHS) %>"><fmt:message key="common.tp.months"/></sst:option></c:if>
        <c:if test="${y}"><sst:option value="<%= Integer.toString(Common.TimePeriods.YEARS) %>"><fmt:message key="common.tp.years"/></sst:option></c:if>
      </c:otherwise>
    </c:choose>  
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${singular}">
        <c:if test="${ms}"><option value="<%= Common.TimePeriods.MILLISECONDS %>"><fmt:message key="common.tp.millisecond"/></option></c:if>
        <c:if test="${s}"><option value="<%= Common.TimePeriods.SECONDS %>"><fmt:message key="common.tp.second"/></option></c:if>
        <c:if test="${min}"><option value="<%= Common.TimePeriods.MINUTES %>"><fmt:message key="common.tp.minute"/></option></c:if>
        <c:if test="${h}"><option value="<%= Common.TimePeriods.HOURS %>"><fmt:message key="common.tp.hour"/></option></c:if>
        <c:if test="${d}"><option value="<%= Common.TimePeriods.DAYS %>"><fmt:message key="common.tp.day"/></option></c:if>
        <c:if test="${w}"><option value="<%= Common.TimePeriods.WEEKS %>"><fmt:message key="common.tp.week"/></option></c:if>
        <c:if test="${mon}"><option value="<%= Common.TimePeriods.MONTHS %>"><fmt:message key="common.tp.month"/></option></c:if>
        <c:if test="${y}"><option value="<%= Common.TimePeriods.YEARS %>"><fmt:message key="common.tp.year"/></option></c:if>
      </c:when>
      <c:otherwise>
        <c:if test="${ms}"><option value="<%= Common.TimePeriods.MILLISECONDS %>"><fmt:message key="common.tp.milliseconds"/></option></c:if>
        <c:if test="${s}"><option value="<%= Common.TimePeriods.SECONDS %>"><fmt:message key="common.tp.seconds"/></option></c:if>
        <c:if test="${min}"><option value="<%= Common.TimePeriods.MINUTES %>"><fmt:message key="common.tp.minutes"/></option></c:if>
        <c:if test="${h}"><option value="<%= Common.TimePeriods.HOURS %>"><fmt:message key="common.tp.hours"/></option></c:if>
        <c:if test="${d}"><option value="<%= Common.TimePeriods.DAYS %>"><fmt:message key="common.tp.days"/></option></c:if>
        <c:if test="${w}"><option value="<%= Common.TimePeriods.WEEKS %>"><fmt:message key="common.tp.weeks"/></option></c:if>
        <c:if test="${mon}"><option value="<%= Common.TimePeriods.MONTHS %>"><fmt:message key="common.tp.months"/></option></c:if>
        <c:if test="${y}"><option value="<%= Common.TimePeriods.YEARS %>"><fmt:message key="common.tp.years"/></option></c:if>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>