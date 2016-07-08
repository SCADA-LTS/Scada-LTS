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
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page isErrorPage="true" %>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<%
// Store the stack trace as a request attribute.
java.io.StringWriter sw = new java.io.StringWriter();
if (exception != null)
    exception.printStackTrace(new java.io.PrintWriter(sw));

// Write the request url into the message.
sw.append("\r\nREQUEST URL\r\n");
sw.append(request.getRequestURL());

// Write the request parameters.
sw.append("\r\n\r\nREQUEST PARAMETERS\r\n");
java.util.Enumeration names = request.getParameterNames();
while (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    sw.append("   ").append(name).append('=').append(request.getParameter(name)).append("\r\n");
}

// Write the request headers.
sw.append("\r\n\r\nREQUEST HEADERS\r\n");
names = request.getHeaderNames();
while (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    sw.append("   ").append(name).append('=').append(request.getHeader(name)).append("\r\n");
}

// Write the page attributes.
//sw.append("\r\n\r\nPAGE ATTRIBUTES\r\n");
//names = pageContext.getAttributeNames();
//while (names.hasMoreElements()) {
//    String name = (String) names.nextElement();
//    sw.append("   ").append(name).append('=').append(pageContext.getAttribute(name)).append("\r\n");
//}

// Write the request attributes.
sw.append("\r\n\r\nREQUEST ATTRIBUTES\r\n");
names = request.getAttributeNames();
while (names.hasMoreElements()) {
    String name = (String) names.nextElement();
    sw.append("   ").append(name).append('=').append(String.valueOf(request.getAttribute(name))).append("\r\n");
}

if (request.getSession() != null) {
    // Write the session attributes.
    sw.append("\r\n\r\nSESSION ATTRIBUTES\r\n");
    names = session.getAttributeNames();
    while (names.hasMoreElements()) {
        String name = (String) names.nextElement();
        sw.append("   ").append(name).append('=').append(String.valueOf(session.getAttribute(name))).append("\r\n");
    }
}

if (request.getSession() != null) {
    // Write the context attributes.
    sw.append("\r\n\r\nCONTEXT ATTRIBUTES\r\n");
    names = session.getServletContext().getAttributeNames();
    while (names.hasMoreElements()) {
        String name = (String) names.nextElement();
        sw.append("   ").append(name).append('=').append(String.valueOf(session.getServletContext().getAttribute(name))).append("\r\n");
    }
}

request.setAttribute("stackTrace", sw.toString());
%>

<br/>
<span class="bigTitle">System exception!</span><br/>
<br/>
The server has experienced an exception processing your last request. The exception has been logged, and 
system administrators will be notified of this problem. You may continue to use the site. 
We apologize for the inconvenience.<br/>
<br/>

<script type="text/javascript">
  function toggleErrorData() {
      var ed = document.getElementById("errorData");
      if (ed.style.display == "none") {
          ed.style.display = "";
          document.getElementById("errorDataMessage").innerHTML = "Hide error details";
      }
      else {
          ed.style.display = "none";
          document.getElementById("errorDataMessage").innerHTML = "Show error details";
      }
      return false;
  }
</script>
<a href="#" onclick="return toggleErrorData();"/><div id="errorDataMessage">Show error details</div></a><br/>
<div id="errorData" style="display:none;"><pre>${stackTrace}</pre></div>

<log:error message="${stackTrace}"/>
