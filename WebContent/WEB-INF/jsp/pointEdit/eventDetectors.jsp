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

    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/analog_high_limit.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/analog_low_limit.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/binary_state.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/multistate_state.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/point_change.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/state_change_count.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/no_change.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/no_update.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/alphanumeric_state.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/positive_cusum.jsp" %>
    <%@ include file="/WEB-INF/jsp/pointEdit/eventDetectorsTypes/negative_cusum.jsp" %>

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
          DataPointEditDwr.getEventDetectors(document.getElementById('dwrScriptSessionid').value,this.initCB);
      }
      
      this.initCB = function(detectorList) {
          for (var i=0; i<detectorList.length; i++)
              pointEventDetectorEditor.addEventDetectorCB(detectorList[i]);
      }
      
      this.addEventDetector = function() {
          DataPointEditDwr.addEventDetector(document.getElementById('dwrScriptSessionid').value,$get("eventDetectorSelect"), this.addEventDetectorCB);
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
          DataPointEditDwr.deleteEventDetector(document.getElementById('dwrScriptSessionid').value,pedId);
          
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
                      DataPointEditDwr.updateHighLimitDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, limit, duration, durationType,
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
                      DataPointEditDwr.updateLowLimitDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, limit, duration, durationType,
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
                      DataPointEditDwr.updateBinaryStateDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, state, duration, durationType,
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
                      DataPointEditDwr.updateMultistateStateDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, state, duration, durationType,
                              alarmLevel, saveCB);
                  }
              }
              else if (pedType == <%= PointEventDetectorVO.TYPE_POINT_CHANGE %>) {
                  saveCBCount++;
                  DataPointEditDwr.updatePointChangeDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, alarmLevel, saveCB);
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
                      DataPointEditDwr.updateStateChangeCountDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, count, duration, durationType,
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
                      DataPointEditDwr.updateNoChangeDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, duration, durationType, alarmLevel,
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
                      DataPointEditDwr.updateNoUpdateDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, duration, durationType, alarmLevel,
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
                      DataPointEditDwr.updateAlphanumericStateDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, state, duration, durationType,
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
                      DataPointEditDwr.updatePositiveCusumDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, limit, weight, duration,
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
                      DataPointEditDwr.updateNegativeCusumDetector(document.getElementById('dwrScriptSessionid').value,pedId, xid, alias, limit, weight, duration,
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