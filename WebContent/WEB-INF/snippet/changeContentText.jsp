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
<%@ include file="/WEB-INF/snippet/common.jsp" %>
<fmt:message key="common.enterSetPoint"/>:<br/>
<input id="txtChange${componentId}" type="text" value="${mango:rawText(point, pointValue)}" 
        onkeypress="if (event.keyCode==13) $('txtSet${componentId}').onclick();"/>
<a id="txtSet${componentId}" class="ptr"
        onclick="mango.view.setPoint(${point.id}, '${componentId}', $('txtChange${componentId}').value);"><fmt:message key="common.set"/></a>
<tag:relinquish/>