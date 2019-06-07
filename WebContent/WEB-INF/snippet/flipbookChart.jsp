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
<%-- The snippet used for histories of images --%>
<%@ include file="/WEB-INF/snippet/common.jsp" %>
<%@page import="com.serotonin.mango.web.servlet.ImageValueServlet"%>
<c:choose>
  <c:when test="${empty chartData}"><fmt:message key="common.noData"/></c:when>
  <c:otherwise>
    <table cellpadding="0" cellspacing="0">
      <tr><td>
        <c:forEach items="${chartData}" var="imageValue" varStatus="status">
          <img src="<%= ImageValueServlet.servletPath %>${imageValue.value.filename}?id=${point.id}w=50&h=50"/>
        </c:forEach>
      </td></tr>
    </table>
    <script type="text/javascript">
      var flipbookArray${componentId} = [];
        flipbookArray${componentId}[${status.index}] = "";
      $("flipbook${componentId}").src = "<%= ImageValueServlet.servletPath %>"+ flipbookArray${componentId}[flipbookArray${componentId}.length-1] +"?w=400&h=300";
      alert($("flipbook${componentId}").src);
    </script>
  </c:otherwise>
</c:choose>