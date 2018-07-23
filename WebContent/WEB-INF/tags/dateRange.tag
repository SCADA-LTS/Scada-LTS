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
--%><%@tag body-content="empty"%>
<table>
  <tr>
    <td><fmt:message key="common.dateRangeFrom"/></td>
    <td><input type="text" id="fromYear" class="formVeryShort" value="${fromYear}"/></td>
    <td><sst:select id="fromMonth" value="${fromMonth}"><tag:monthOptions sst="true"/></sst:select></td>
    <td><sst:select id="fromDay" value="${fromDay}"><tag:dayOptions sst="true"/></sst:select></td>
    <td>,</td>
    <td><sst:select id="fromHour" value="${fromHour}"><tag:hourOptions sst="true"/></sst:select></td>
    <td>:</td>
    <td><sst:select id="fromMinute" value="${fromMinute}"><tag:minuteOptions sst="true"/></sst:select></td>
    <td>:</td>
    <td><sst:select id="fromSecond" value="${fromSecond}"><tag:secondOptions sst="true"/></sst:select></td>
    <td><input type="checkbox" name="fromNone" id="fromNone" onclick="updateDateRange()"/><label
            for="fromNone"><fmt:message key="common.inception"/></label></td>
  </tr>
  <tr>
    <td><fmt:message key="common.dateRangeTo"/></td>
    <td><input type="text" id="toYear" class="formVeryShort" value="${toYear}"/></td>
    <td><sst:select id="toMonth" value="${toMonth}"><tag:monthOptions sst="true"/></sst:select></td>
    <td><sst:select id="toDay" value="${toDay}"><tag:dayOptions sst="true"/></sst:select></td>
    <td>,</td>
    <td><sst:select id="toHour" value="${toHour}"><tag:hourOptions sst="true"/></sst:select></td>
    <td>:</td>
    <td><sst:select id="toMinute" value="${toMinute}"><tag:minuteOptions sst="true"/></sst:select></td>
    <td>:</td>
    <td><sst:select id="toSecond" value="${toSecond}"><tag:secondOptions sst="true"/></sst:select></td>
    <td><input type="checkbox" name="toNone" id="toNone" checked="checked" onclick="updateDateRange()"/><label
            for="toNone"><fmt:message key="common.latest"/></label></td>
  </tr>
</table>
