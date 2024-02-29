<%@ include file="/WEB-INF/snippet/common.jsp" %>
<c:choose>
    <c:when test="${!empty invalid || !empty disabled || pointRT.attributes.UNRELIABLE || pointRT.attributes.DP_UPDATE_ERROR}">
      <tag:img png="exclamation" title="common.valueUnreliable"/>
    </c:when>
    <c:otherwise>
      <tag:img png="warn" title="common.warning"/>
    </c:otherwise>
</c:choose>