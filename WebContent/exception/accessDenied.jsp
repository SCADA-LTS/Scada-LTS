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

request.setAttribute("stackTrace", sw.toString());
%>

<br/>
<span class="bigTitle">Permission denied!</span><br/>
<br/>
You do not have sufficient authority to access the resource you requested. Sadly, this exception must be logged
for review by a system administrator.<br/>
<br/>

<log:error message="${stackTrace}"/>
