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
--%><%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@attribute name="duration" type="java.lang.Integer" required="true"%><%--
--%><%@attribute name="durationType" required="true"%><%--
--%><%@attribute name="width" type="java.lang.Integer" required="true"%><%--
--%><%@attribute name="height" type="java.lang.Integer" required="true"%><%--
--%><sst:list var="chartPointList"/><%--
--%><jsp:doBody/><%--
--%><mango:chart duration="${duration}" durationType="${durationType}" width="${width}" height="${height}"><%--
  --%><c:forEach items="${chartPointList}" var="chartPoint"><%--
    --%><mango:chartPoint xid="${chartPoint.xid}" color="${chartPoint.color}"/><%--
  --%></c:forEach><%--
--%></mango:chart><%--
--%><img id="c${componentId}" src="images/hourglass.png" border="0"/><%--
--%><script type="text/javascript">
  mango.view.custom.functions["c${componentId}"] = function(value) { $("c${componentId}").src = value; }
</script>