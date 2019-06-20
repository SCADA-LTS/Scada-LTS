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
  
  <div class="login-container" style="margin: 100px auto 10px auto; background-color: #77c055; width: 290px; border-radius: 10px;">

      <div class="login-browser" style="text-align: center; padding-top: 10px;">

          <b><span id="browser"><fmt:message key="login.unknownBrowser"/></span></b>
          <img id="browserImg" src="images/magnifier.png" style="height: 10px;width: auto;"/>
          <span id="okMsg" style="display:none"><fmt:message key="login.supportedBrowser"/></span>
          <span id="warnMsg" style="display:none"><fmt:message key="login.unsupportedBrowser"/></span>
      </div>


      <div class="login-box">
        <form action="login.htm" method="post" onclick="nag()" style="margin: 0 auto; width:200px;">


              <div class="form-box">
                <div class="formLabelRequired" style="color: #000000; font-size: 14px;"><fmt:message key="login.userId"/></div>
                <div class="formField">
                  <input id="username" type="text" name="username" value="${login.username}" maxlength="40" style="width: 200px; height: 20px;"/>
                </div>
              </div>

              <div class="form-box">
                <div class="formLabelRequired" style="color: #000000; font-size: 14px;"><fmt:message key="login.password"/></div>
                <div class="formField">
                  <input id="password" type="password" name="password" value="${login.password}" maxlength="20" style="width: 200px; height: 20px;"/>
                </div>
              </div>

                <div class="formError">
                  <c:forEach items="${errors}" var="error">
                    <fmt:message key="${error}"/><br/>
                  </c:forEach>
                </div>


              <div class="login-button" align="center">
                <input type="submit" value="<fmt:message key="login.loginButton"/>"  style="padding: 5px 20px; margin-left:30%; margin-top:10px;"/>
                <tag:help id="welcomeToMango"/>
              </div>



        </form>
        <br/>
      </div>


  </div>
</tag:page>