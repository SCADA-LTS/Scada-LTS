<%@include file="/WEB-INF/tags/decl.tagf" %><%--
--%><%@tag body-content="empty" %><%--
--%><%@attribute name="comments" type="java.util.List" required="true" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:choose>
    <c:when test="${!empty comments[fn:length(comments)-1].username}">
      <tr>
        <td valign="top" width="16"><tag:img png="comment" title="notes.note"/></td>
        <td valign="top">
          <span class="copyTitle">
            ${comments[fn:length(comments)-1].prettyTime} <fmt:message key="notes.by"/>
             ${comments[fn:length(comments)-1].username}
          </span><br/>
          ${comments[fn:length(comments)-1].comment}
        </td>
      </tr>
    </c:when>
</c:choose>