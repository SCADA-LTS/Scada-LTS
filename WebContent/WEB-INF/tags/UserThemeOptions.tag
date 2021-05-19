<%@include file="/WEB-INF/tags/decl.tagf" %>
<%@tag body-content="empty" %>
<%@ tag import="com.serotonin.mango.vo.User" %>
<option value="<%= User.UserTheme.DEFAULT %>"><fmt:message key="users.theme.default"/></option>
<option value="<%= User.UserTheme.MODERN %>"><fmt:message key="users.theme.modern"/></option>
