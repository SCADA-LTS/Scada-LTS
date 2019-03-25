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
<%@page import="com.serotonin.mango.vo.event.PointEventDetectorVO"%>

    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_NO_CHANGE %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.noChange"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Xid" type="text" class="formLong"/></td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="pointEdit.detectors.alias"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Alias" type="text" class="formLong"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="common.alarmLevel"/></td>
        <td class="formField">
          <select id="eventDetector_TEMPLATE_AlarmLevel"
                  onchange="pointEventDetectorEditor.updateAlarmLevelImage(this.value, getPedId(this))">
            <tag:alarmLevelOptions/>
          </select>
          <tag:img id="eventDetector_TEMPLATE_AlarmLevelImg" png="flag_green" title="common.alarmLevel.none" style="display:none;"/>
        </td>
      </tr>
      <tr>
        <td class="formLabel"><fmt:message key="pointEdit.detectors.duration"/></td>
        <td class="formField">
          <input id="eventDetector_TEMPLATE_Duration" type="text" class="formShort"/>
          <select id="eventDetector_TEMPLATE_DurationType"><tag:timePeriodOptions s="true" min="true" h="true"/></select>
        </td>
      </tr>
      <tr><td class="formError" id="eventDetector_TEMPLATE_ErrorMessage" colspan="2"></td></tr>
    </tbody>