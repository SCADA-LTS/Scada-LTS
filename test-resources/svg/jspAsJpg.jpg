<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<tag:page>
  <div class="smallTitle"><fmt:message key="common.help"/></div>
  <br/>
  
  <div id="help">
    <c:if test="${sessionUser.firstLogin}"><h2><fmt:message key="dox.welcomeToMango"/>!</h2></c:if>
    <c:set var="filepath">/WEB-INF/dox/<fmt:message key="dox.dir"/>/help.html</c:set>
    <jsp:include page="${filepath}"/>
  </div>
</tag:page>