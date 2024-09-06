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
<%-- The snippet used for table charts in rollovers --%>
<%@ include file="/WEB-INF/snippet/common.jsp" %>
<c:choose>
  <c:when test="${empty chartData}"><spring:message code="common.noData"/></c:when>
  <c:otherwise>
    <c:forEach items="${chartData}" var="historyPointValue">
      ${mango:pointValueTime(historyPointValue)} - ${mango:htmlText(point, historyPointValue)}
      <c:if test="${historyPointValue.annotated}">
        <c:set value="" var="sourceDescriptionArgument"/>
        <c:choose>
            <c:when test="${empty historyPointValue.sourceDescriptionArgument}">
                <c:set value='<spring:message code="common.deleted"/>' var="sourceDescriptionArgument"/>
            </c:when>
            <c:otherwise>
                <c:set value="<c:out value='${historyPointValue.sourceDescriptionArgument}'/>" var="sourceDescriptionArgument"/>
            </c:otherwise>
        </c:choose>
        (<spring:message code="${historyPointValue.sourceDescriptionKey}" arguments="${sourceDescriptionArgument}"/>)
      </c:if>
      <br/>
    </c:forEach>
  </c:otherwise>
</c:choose>