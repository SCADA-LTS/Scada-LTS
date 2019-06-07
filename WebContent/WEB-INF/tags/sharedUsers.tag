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
--%><%@attribute name="doxId" required="true" %><%--
--%><%@attribute name="noUsersKey" required="true" %><%--
--%><%@attribute name="closeFunction" %>
<table>
  <tr>
    <td>
      <tag:img png="user" title="share.sharing"/>
      <span class="smallTitle"><fmt:message key="share.sharing"/></span>
      <tag:help id="${doxId}"/>
    </td>
    <td align="right">
      <c:if test="${!empty closeFunction}">
        <tag:img png="cross" onclick="${closeFunction}" title="common.close" style="display:inline;"/>
      </c:if>
    </td>
  </tr>
  
  <tr>
    <td colspan="2">
      <select id="allShareUsersList"></select>
      <tag:img png="add" onclick="mango.share.addUserToShared();" title="common.add"/>
    </td>
  </tr>
  
  <tr>
    <td colspan="2">
      <table cellspacing="1">
        <tbody id="sharedUsersTableEmpty" style="display:none;">
          <tr><th colspan="3"><fmt:message key="${noUsersKey}"/></th></tr>
        </tbody>
        <tbody id="sharedUsersTableHeaders" style="display:none;">
          <tr class="smRowHeader">
            <td><fmt:message key="share.userName"/></td>
            <td><fmt:message key="share.accessType"/></td>
            <td></td>
          </tr>
        </tbody>
        <tbody id="sharedUsersTable"></tbody>
      </table>
    </td>
  </tr>
</table>