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
<div class="borderDiv">
  <table id="eventDetectorTable">
    <tr><td colspan="2">
      <span class="smallTitle"><fmt:message key="pointEdit.detectors.eventDetectors"/></span>
      <tag:help id="eventDetectors"/>
    </td></tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.type"/></td>
      <td class="formField">
        <select id="eventDetectorSelect">
          <c:forEach items="${eventDetectors}" var="eddef">
            <option value="${eddef.id}"><fmt:message key="${eddef.nameKey}"/></option>
          </c:forEach>
        </select>
        <tag:img png="bell_add" title="common.add" onclick="pointEventDetectorEditor.addEventDetector();"/>
      </td>
    </tr>
    
    <tr><td colspan="2">
      <div id="emptyListMessage" style="color:#888888;padding:10px;text-align:center;">
        <fmt:message key="pointEdit.detectors.empty"/>
      </div>
    </td></tr>
  </table>
  
  <table style="display:none;">
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_ANALOG_HIGH_LIMIT %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.highLimitDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.highLimit"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Limit" type="text" class="formShort"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_ANALOG_LOW_LIMIT %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.lowLimitDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.lowLimit"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Limit" type="text" class="formShort"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_BINARY_STATE %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.stateDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.state"/></td>
        <td class="formField">
          <select id="eventDetector_TEMPLATE_State">
            <option value="false"><fmt:message key="pointEdit.detectors.zero"/></option>
            <option value="true"><fmt:message key="pointEdit.detectors.one"/></option>
          </select>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_MULTISTATE_STATE %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.stateDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.state"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_State" type="text" class="formShort"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_POINT_CHANGE %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.changeDet"/></td>
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
      <tr><td class="formError" id="eventDetector_TEMPLATE_ErrorMessage" colspan="2"></td></tr>
    </tbody>
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_STATE_CHANGE_COUNT %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.changeCounter"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.changeCount"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_ChangeCount" type="text" class="formShort"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_NO_UPDATE %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.noUpdate"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_ALPHANUMERIC_STATE %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.stateDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.state"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_State" type="text" class="formLong"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_POSITIVE_CUSUM %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.posCusumDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.posLimit"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Limit" type="text" class="formShort"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.weight"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Weight" type="text" class="formShort"/></td>
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
    
    <tbody id="detectorType<%= PointEventDetectorVO.TYPE_NEGATIVE_CUSUM %>">
      <tr><td class="horzSeparator" colspan="2"></td></tr>
      <tr>
        <td class="formLabelRequired">
          <tag:img png="bell_delete" title="common.delete" onclick="pointEventDetectorEditor.deleteDetector(getPedId(this))"/>
          <fmt:message key="pointEdit.detectors.type"/>
        </td>
        <td class="formField"><fmt:message key="pointEdit.detectors.negCusumDet"/></td>
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
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.negLimit"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Limit" type="text" class="formShort"/></td>
      </tr>
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.detectors.weight"/></td>
        <td class="formField"><input id="eventDetector_TEMPLATE_Weight" type="text" class="formShort"/></td>
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
  </table>
</div>

<script type="text/javascript">
  function getPedId(node) {
      while (!(node.pedId))
          node = node.parentNode;
      return node.pedId;
  }

  function PointEventDetectorEditor() {
      var detectorCount = 0;
  
      this.init = function() {
          DataPointEditDwr.getEventDetectors(this.initCB);
      }
      
      this.initCB = function(detectorList) {
          for (var i=0; i<detectorList.length; i++)
              pointEventDetectorEditor.addEventDetectorCB(detectorList[i]);
      }
      
      this.addEventDetector = function() {
          DataPointEditDwr.addEventDetector($get("eventDetectorSelect"), this.addEventDetectorCB);
      }
  
      this.addEventDetectorCB = function(detector) {
          detectorCount++;
          hide("emptyListMessage");
          
          // Create the appropriate tbody.
          var content = $("detectorType"+ detector.detectorType).cloneNode(true);
          updateTemplateNode(content, detector.id);
          content.id = "eventDetector"+ detector.id;
          content.pedId = detector.id;
          content.pedType = detector.detectorType;
          $("eventDetectorTable").appendChild(content);
          
          // Set the values in the content controls.
          if (detector.detectorType == <%= PointEventDetectorVO.TYPE_ANALOG_HIGH_LIMIT %>) {
              $set("eventDetector"+ detector.id +"Limit", detector.limit);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_ANALOG_LOW_LIMIT %>) {
              $set("eventDetector"+ detector.id +"Limit", detector.limit);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_BINARY_STATE %>) {
              $set("eventDetector"+ detector.id +"State", detector.binaryState ? "true" : "false");
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_MULTISTATE_STATE %>) {
              $set("eventDetector"+ detector.id +"State", detector.multistateState);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_POINT_CHANGE %>) {}
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_STATE_CHANGE_COUNT %>) {
              $set("eventDetector"+ detector.id +"ChangeCount", detector.changeCount);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_NO_CHANGE %>) {
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_NO_UPDATE %>) {
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_ALPHANUMERIC_STATE %>) {
              $set("eventDetector"+ detector.id +"State", detector.alphanumericState);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_POSITIVE_CUSUM %>) {
              $set("eventDetector"+ detector.id +"Limit", detector.limit);
              $set("eventDetector"+ detector.id +"Weight", detector.weight);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          else if (detector.detectorType == <%= PointEventDetectorVO.TYPE_NEGATIVE_CUSUM %>) {
              $set("eventDetector"+ detector.id +"Limit", detector.limit);
              $set("eventDetector"+ detector.id +"Weight", detector.weight);
              $set("eventDetector"+ detector.id +"Duration", detector.duration);
              $set("eventDetector"+ detector.id +"DurationType", detector.durationType);
          }
          
          $set("eventDetector"+ detector.id +"Xid", detector.xid);
          $set("eventDetector"+ detector.id +"Alias", detector.alias);
          $set("eventDetector"+ detector.id +"AlarmLevel", detector.alarmLevel);
          pointEventDetectorEditor.updateAlarmLevelImage(detector.alarmLevel, detector.id);
      }
      
      this.updateAlarmLevelImage = function(alarmLevel, pedId) {
          setAlarmLevelImg(alarmLevel, $("eventDetector"+ pedId +"AlarmLevelImg"));
      }
      
      this.deleteDetector = function(pedId) {
          DataPointEditDwr.deleteEventDetector(pedId);
          
          detectorCount--;
          if (detectorCount == 0)
              show("emptyListMessage");
          
          var content = $("eventDetector"+ pedId);
          $("eventDetectorTable").removeChild(content);
      }
      
      var saveCBCount;
      var saveCallback;
      var runSaveCallback;
      this.save = function(callback) {
          var edTableNodes = $("eventDetectorTable").childNodes;
          saveCBCount = 0;
          saveCallback = callback;
          runSaveCallback = true;
          
          dwr.engine.beginBatch();
          for (var i=0; i<edTableNodes.length; i++) {
              if (!edTableNodes[i].pedId)
                  continue;
              
              // Found a detector row.
              var pedId = edTableNodes[i].pedId;
              var pedType = edTableNodes[i].pedType;
              var errorMessage = null;
              var xid = $get("eventDetector"+ pedId +"Xid");
              var alias = $get("eventDetector"+ pedId +"Alias");
              var alarmLevel = parseInt($get("eventDetector"+ pedId +"AlarmLevel"));
              
              if (pedType == <%= PointEventDetectorVO.TYPE_ANALOG_HIGH_LIMIT %>) {
                  var limit = parseFloat($get("eventDetector"+ pedId +"Limit"));
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(limit))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingLimit"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateHighLimitDetector(pedId, xid, alias, limit, duration, durationType,
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_ANALOG_LOW_LIMIT %>) {
                  var limit = parseFloat($get("eventDetector"+ pedId +"Limit"));
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(limit))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingLimit"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateLowLimitDetector(pedId, xid, alias, limit, duration, durationType,
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_BINARY_STATE %>) {
                  var state = $get("eventDetector"+ pedId +"State");
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateBinaryStateDetector(pedId, xid, alias, state, duration, durationType,
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_MULTISTATE_STATE %>) {
                  var state = parseInt($get("eventDetector"+ pedId +"State"));
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(state))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingState"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateMultistateStateDetector(pedId, xid, alias, state, duration, durationType,
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_POINT_CHANGE %>) {
                  saveCBCount++;
                  DataPointEditDwr.updatePointChangeDetector(pedId, xid, alias, alarmLevel, saveCB);
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_STATE_CHANGE_COUNT %>) {
                  var count = parseInt($get("eventDetector"+ pedId +"ChangeCount"));
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(count))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingChangeCount"/>";
                  else if (count < 2)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidChangeCount"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 1)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateStateChangeCountDetector(pedId, xid, alias, count, duration, durationType, 
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_NO_CHANGE %>) {
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 1)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateNoChangeDetector(pedId, xid, alias, duration, durationType, alarmLevel,
                              saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_NO_UPDATE %>) {
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 1)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateNoUpdateDetector(pedId, xid, alias, duration, durationType, alarmLevel,
                              saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_ALPHANUMERIC_STATE %>) {
                  var state = $get("eventDetector"+ pedId +"State");
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (state && state.length > 128)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidState"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateAlphanumericStateDetector(pedId, xid, alias, state, duration, durationType, 
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_POSITIVE_CUSUM %>) {
                  var limit = parseFloat($get("eventDetector"+ pedId +"Limit"));
                  var weight = parseFloat($get("eventDetector"+ pedId +"Weight"));
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(limit))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingLimit"/>";
                  else if (isNaN(weight))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingWeight"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updatePositiveCusumDetector(pedId, xid, alias, limit, weight, duration,
                              durationType, alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_NEGATIVE_CUSUM %>) {
                  var limit = parseFloat($get("eventDetector"+ pedId +"Limit"));
                  var weight = parseFloat($get("eventDetector"+ pedId +"Weight"));
                  var duration = parseInt($get("eventDetector"+ pedId +"Duration"));
                  var durationType = parseInt($get("eventDetector"+ pedId +"DurationType"));
                  
                  if (isNaN(limit))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingLimit"/>";
                  else if (isNaN(weight))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingWeight"/>";
                  else if (isNaN(duration))
                      errorMessage = "<fmt:message key="pointEdit.detectors.errorParsingDuration"/>";
                  else if (duration < 0)
                      errorMessage = "<fmt:message key="pointEdit.detectors.invalidDuration"/>";
                  else {
                      saveCBCount++;
                      DataPointEditDwr.updateNegativeCusumDetector(pedId, xid, alias, limit, weight, duration,
                              durationType, alarmLevel, saveCB);
                  }
              }
              
              if (errorMessage != null) {
                  runSaveCallback = false;
                  $("eventDetector"+ pedId +"ErrorMessage").innerHTML = errorMessage;
              }
              else
                  $("eventDetector"+ pedId +"ErrorMessage").innerHTML = "";
          }
          dwr.engine.endBatch();
      
          // If no save calls were made, continue by calling the callback.
          if (runSaveCallback && saveCBCount == 0)
              callback();
      };
      
      function saveCB() {
          if (--saveCBCount == 0) {
              // We're done with the callbacks. If there were no errors, call the callback.
              if (runSaveCallback)
                  saveCallback();
          }
      }
  }
  var pointEventDetectorEditor = new PointEventDetectorEditor();
  dojo.addOnLoad(pointEventDetectorEditor, "init");
</script>