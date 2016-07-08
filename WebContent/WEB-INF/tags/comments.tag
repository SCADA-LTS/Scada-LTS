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
--%><%@attribute name="comments" type="java.util.List" required="true" %><%--
--%><c:forEach items="${comments}" var="comment">
  <tr>
    <td valign="top" width="16"><tag:img png="comment" title="notes.note"/></td>
    <td valign="top">
      <span class="copyTitle">
        ${comment.prettyTime} <fmt:message key="notes.by"/>
        <c:choose>
          <c:when test="${empty comment.username}"><fmt:message key="common.deleted"/></c:when>
          <c:otherwise>${comment.username}</c:otherwise>
        </c:choose>
      </span><br/>
      ${comment.comment}
    </td>
  </tr>
</c:forEach>