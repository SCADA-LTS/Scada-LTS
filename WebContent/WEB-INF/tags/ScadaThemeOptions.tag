<%@include file="/WEB-INF/tags/decl.tagf" %>
<%@tag body-content="empty" %>
<%@ tag import="com.serotonin.mango.vo.ScadaTheme" %>
<option value="<%= ScadaTheme.DEFAULT %>"><fmt:message key="users.theme.default"/></option>
<!-- <option value="<%= ScadaTheme.DARK %>"><fmt:message key="users.theme.dark"/></option> -->
<option value="<%= ScadaTheme.MODERN %>"><fmt:message key="users.theme.modern"/></option>
