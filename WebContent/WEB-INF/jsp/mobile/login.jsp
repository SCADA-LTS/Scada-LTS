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
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<mobileTag:page>
  <form action="mobile_login.htm" method="post">
    <table>
      <spring:bind path="login.username">
        <tr>
          <td class="formLabelRequired"><fmt:message key="login.userId"/></td>
          <td class="formField">
            <input id="username" type="text" name="username" value="${status.value}" maxlength="40"/>
          </td>
        </tr>
        <tr><td colspan="2" class="formError">${status.errorMessage}</td></tr>
      </spring:bind>
      
      <spring:bind path="login.password">
        <tr>
          <td class="formLabelRequired"><fmt:message key="login.password"/></td>
          <td class="formField">
            <input id="password" type="password" name="password" value="${status.value}" maxlength="20"/>
          </td>
          
        </tr>
        <tr><td colspan="2" class="formError">${status.errorMessage}</td></tr>
      </spring:bind>
          
      <spring:bind path="login">
        <c:if test="${status.error}">
          <td colspan="2" class="formError">
            <c:forEach items="${status.errorMessages}" var="error">
              <c:out value="${error}"/><br/>
            </c:forEach>
          </td>
        </c:if>
      </spring:bind>
      
      <tr>
        <td colspan="2" align="center">
          <input type="submit" value="<fmt:message key="login.loginButton"/>"/>
        </td>
        <td></td>
      </tr>
    </table>
  </form>
  <br/>
  <br/>
</mobileTag:page>