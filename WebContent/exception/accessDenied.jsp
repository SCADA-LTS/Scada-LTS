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
<%@ include file="/WEB-INF/jsp/include/log.jsp" %>

<tag:page>
<div style="text-align: center;">
<br/>
<span class="bigTitle">Permission denied!</span><br/>
<br/>
You do not have sufficient authority to access the resource you requested. Sadly, this exception must be logged
for review by a system administrator.<br/>
<br/>

<log:error message="${stackTrace}"/>
</div>
</tag:page>