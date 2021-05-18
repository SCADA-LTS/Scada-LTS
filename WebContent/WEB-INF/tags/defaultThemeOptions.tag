

<%@include file="/WEB-INF/tags/decl.tagf" %>
<%@tag body-content="empty" %>
<%@ tag import="com.serotonin.mango.vo.User" %>
<option value="<%= User.DefaultTheme.STANDARD %>"><fmt:message key="users.defaultTheme.standard"/></option>
<option value="<%= User.DefaultTheme.MODERN %>"><fmt:message key="users.defaultTheme.modern"/></option>
