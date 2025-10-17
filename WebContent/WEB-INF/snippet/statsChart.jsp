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
<c:if test="${!empty startsAndRuntimes}">
  <b><spring:message code="common.stats.start"/></b>: ${sst:fullTime(start)}<br/>
  <b><spring:message code="common.stats.end"/></b>: ${sst:fullTime(end)}<br/>
  <table>
    <tr>
      <th><spring:message code="common.value"/></th>
      <th><spring:message code="common.stats.starts"/></th>
      <th><spring:message code="common.stats.runtime"/></th>
    </tr>
  <c:forEach items="${startsAndRuntimes}" var="sar">
    <tr>
      <td>${mango:htmlTextValue(point, sar.mangoValue)}</td>
      <td align="right">${sar.starts}</td>
      <td align="right"><fmt:formatNumber value="${sar.proportion}" pattern="0%"/></td>
    </tr>
  </c:forEach>
  </table>
</c:if>
<c:if test="${!empty average}">
  <c:choose>
    <c:when test="${noData}">
      <b><spring:message code="common.noData"/></b><br/>
    </c:when>
    <c:otherwise>
      <b><spring:message code="common.stats.start"/></b>: ${sst:fullTime(start)}<br/>
      <b><spring:message code="common.stats.end"/></b>: ${sst:fullTime(end)}<br/>
      <b><spring:message code="common.stats.min"/></b>: ${mango:specificHtmlTextValue(point, minimum)} @ ${sst:fullTime(minTime)}<br/>
      <b><spring:message code="common.stats.max"/></b>: ${mango:specificHtmlTextValue(point, maximum)} @ ${sst:fullTime(maxTime)}<br/>
      <b><spring:message code="common.stats.avg"/></b>: ${mango:specificHtmlTextValue(point, average)}<br/>
      <c:if test="${!empty sum}">
        <b><spring:message code="common.stats.sum"/></b>: ${mango:specificHtmlTextValue(point, sum)}<br/>
      </c:if>
    </c:otherwise>
  </c:choose>
</c:if>
<b><spring:message code="common.stats.logEntries"/></b>: ${logEntries}