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
<%--
  This snippet supports only multistate types. In particular, it only supports
  point views with a MultistateRenderer.
--%>
<%@ include file="/WEB-INF/snippet/common.jsp" %>
<fmt:message key="common.chooseSetPoint"/>:<br/>
<sst:select value="${mango:rawText(point, pointValue)}" onchange="mango.view.setPoint(${point.id}, '${componentId}', this.value)">
  <c:forEach items="${point.textRenderer.multistateValues}" var="valueDef">
    <sst:option value="${valueDef.key}">${valueDef.text}</sst:option>
  </c:forEach>
</sst:select>
<tag:relinquish/>