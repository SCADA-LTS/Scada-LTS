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
<tag:page onload="setFocus">
  <script type="text/javascript">
    compatible = false;
     
    function setFocus() {
        $("username").focus();
        BrowserDetect.init();
        
        $set("browser", BrowserDetect.browser +" "+ BrowserDetect.version +" <fmt:message key="login.browserOnPlatform"/> "+ BrowserDetect.OS);
        
        if (checkCombo(BrowserDetect.browser, BrowserDetect.version, BrowserDetect.OS)) {
            $("browserImg").src = "images/accept.png";
            show("okMsg");
            compatible = true;
        }
        else {
            $("browserImg").src = "images/thumb_down.png";
            show("warnMsg");
        }
    }
    
    function nag() {
        if (!compatible)
            alert('<fmt:message key="login.nag"/>');
    }
  </script>
  
  <table cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td>
        <form action="login.htm" method="post" onclick="nag()">
          <table>
            <spring:bind path="login.username">
              <tr>
                <td class="formLabelRequired"><fmt:message key="login.userId"/></td>
                <td class="formField">
                  <input id="username" type="text" name="username" value="${status.value}" maxlength="40"/>
                </td>
                <td class="formError">${status.errorMessage}</td>
              </tr>
            </spring:bind>
            
            <spring:bind path="login.password">
              <tr>
                <td class="formLabelRequired"><fmt:message key="login.password"/></td>
                <td class="formField">
                  <input id="password" type="password" name="password" value="${status.value}" maxlength="20"/>
                </td>
                <td class="formError">${status.errorMessage}</td>
              </tr>
            </spring:bind>
                
            <spring:bind path="login">
              <c:if test="${status.error}">
                <td colspan="3" class="formError">
                  <c:forEach items="${status.errorMessages}" var="error">
                    <c:out value="${error}"/><br/>
                  </c:forEach>
                </td>
              </c:if>
            </spring:bind>
            
            <tr>
              <td colspan="2" align="center">
                <input type="submit" value="<fmt:message key="login.loginButton"/>"/>
                <tag:help id="welcomeToMango"/>
              </td>
              <td></td>
            </tr>
          </table>
        </form>
        <br/>
      </td>
      <td valign="top">
        <table>
          <tr>
            <td valign="top"><img id="browserImg" src="images/magnifier.png"/></td>
            <td><b><span id="browser"><fmt:message key="login.unknownBrowser"/></span></b></td>
          </tr>
          <tr>
            <td valign="top" colspan="2">
              <span id="okMsg" style="display:none"><fmt:message key="login.supportedBrowser"/></span>
              <span id="warnMsg" style="display:none"><fmt:message key="login.unsupportedBrowser"/></span>
            </td>
          </tr>
        </table>
        <br/><br/>
      </td>
    </tr>
  </table>
</tag:page>