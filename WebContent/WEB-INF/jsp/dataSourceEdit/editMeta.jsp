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
<%@page import="com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO"%>
<%@page import="com.serotonin.mango.Common"%>

<script type="text/javascript">
  var pointsArray = new Array();
  var contextArray = new Array();
  
  function initImpl() {
      <c:forEach items="${userPoints}" var="dp">
        pointsArray[pointsArray.length] = {
            id : ${dp.id}, 
            name : '<c:out value="${dp.extendedName}"/>',
            xid : '${dp.xid}',
            type : '<sst:i18n message="${dp.dataTypeMessage}"/>'
        };
      </c:forEach>
      
      createContextualMessageNode("contextContainer", "context");
      
	  jQuery("#allPointsList").chosen({
      	allow_single_deselect: true,
		placeholder_text_single: " ",
		search_contains: true,
		width: "400px"
	  });       
      
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      //pointListColumnHeaders.push("");
     // pointListColumnFunctions.push(function(p) {
     //     var id = "generateImg"+ p.id;
     //     var onclick = "generateHistory("+ p.id +")";
     //     return writeImage(id, null, "clock-history", "<spring:message code="dsEdit.meta.generate"/>", onclick);
     // });
     return null;
  }
  
  function generateHistory(pointId) {
      startImageFader("generateImg"+ pointId, true);
	  DataSourceEditDwr.generateMetaPointHistory(pointId, function(result) {
	      stopImageFader("generateImg"+ pointId);
	      alert(result);
	  });
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveMetaDataSource($get("dataSourceName"), $get("dataSourceXid"), saveDataSourceCB);
  }
  
  function editPointCBImpl(locator) {
      contextArray.length = 0;
      for (var i=0; i<locator.context.length; i++)
          addToContextArray(locator.context[i].key, locator.context[i].value);
      writeContextArray();
      
      $set("script", locator.script);
      $set("dataTypeId", locator.dataTypeId);
      $set("settable", locator.settable);
      $set("updateEvent", locator.updateEvent);
      $set("updateCronPattern", locator.updateCronPattern);
      $set("executionDelaySeconds", locator.executionDelaySeconds);
      $set("executionDelayPeriodTypeCode", locator.executionDelayPeriodTypeCode);

      updateEventChanged();
	        
  }
  
  function savePointImpl(locator) {
      locator.context = createContextArray();
      locator.script = $get("script");
      locator.dataTypeId = $get("dataTypeId");
      locator.settable = $get("settable");
      locator.updateEvent = $get("updateEvent");
      locator.updateCronPattern = $get("updateCronPattern");
      locator.executionDelaySeconds = $get("executionDelaySeconds");
      locator.executionDelayPeriodTypeCode = $get("executionDelayPeriodTypeCode");
      
      DataSourceEditDwr.saveMetaPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function addPointToContext() {
      var pointId = $get("allPointsList");
      addToContextArray(pointId, "p"+ pointId);
      writeContextArray();
  }
  
  function addToContextArray(pointId, scriptVarName) {
      var data = getElement(pointsArray, pointId);
      if (data) {
          // Missing names imply that the point was deleted, so ignore.
          contextArray[contextArray.length] = {
              pointId : pointId,
              pointName : data.name,
              xid : data.xid,
              pointType : data.type,
              scriptVarName : scriptVarName
          };
      }
  }
  
  function removeFromContextArray(pointId) {
      for (var i=contextArray.length-1; i>=0; i--) {
          if (contextArray[i].pointId == pointId)
              contextArray.splice(i, 1);
      }
      writeContextArray();
  }
  
  function writeContextArray() {
      dwr.util.removeAllRows("contextTable");
      if (contextArray.length == 0) {
          show($("contextTableEmpty"));
          hide($("contextTableHeaders"));
      }
      else {
          hide($("contextTableEmpty"));
          show($("contextTableHeaders"));
          dwr.util.addRows("contextTable", contextArray,
              [
                  function(data) { return "<span>" + data.pointName + "</span>" },
                  function(data) { return "<span>" + data.xid + "</span>"; },
                  function(data) { return data.pointType; },
                  function(data) {
                          return "<input type='text' value='"+ escapeHtml(data.scriptVarName) +"' class='formShort' "+
                                  "onblur='updateScriptVarName("+ data.pointId +", this.value)'/>";
                  },
                  function(data) { 
                          return "<img src='images/bullet_delete.png' class='ptr' "+
                                  "onclick='removeFromContextArray("+ data.pointId +")'/>";
                  }
              ],
              {
                  rowCreator:function(options) {
                      var tr = document.createElement("tr");
                      tr.className = "smRow"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                      return tr;
                  }
              });
      }
      updatePointsList();
  }
  
  function updatePointsList() {
      dwr.util.removeAllOptions("allPointsList");
      var availPoints = new Array();
      for (var i=0; i<pointsArray.length; i++) {
          var found = false;
          for (var j=0; j<contextArray.length; j++) {
              if (contextArray[j].pointId == pointsArray[i].id) {
                  found = true;
                  break;
              }
          }
          if (!found) {
              pointsArray[i].name = unescapeHtml(pointsArray[i].name);
              availPoints[availPoints.length] = pointsArray[i];
          }
      }
      dwr.util.addOptions("allPointsList", availPoints, "id", "name");
      jQuery("#allPointsList").trigger('chosen:updated');
  }
  
  function updateScriptVarName(pointId, scriptVarName) {
      for (var i=contextArray.length-1; i>=0; i--) {
          if (contextArray[i].pointId == pointId)
              contextArray[i].scriptVarName = scriptVarName;
      }
  }
  
  function validateScript() {
      hideContextualMessages("pointProperties");
      DataSourceEditDwr.validateScript($get("script"), createContextArray(), $get("dataTypeId"), validateScriptCB);
  }
  
  function createContextArray() {
      var context = new Array();
      for (var i=0; i<contextArray.length; i++) {
          context[context.length] = {
              key : contextArray[i].pointId,
              value : contextArray[i].scriptVarName
          };
      }
      return context;
  }
  
  function validateScriptCB(response) {
      showDwrMessages(response.messages);
  }
  
  function updateEventChanged() {
      display("updateCronPatternRow", $get("updateEvent") == <%= MetaPointLocatorVO.UPDATE_EVENT_CRON %>);
  }
  
  jQuery(document).ready(function(){    
  	(function($) {
		loadjscssfile("resources/jQuery/plugins/chosen/chosen.min.css","css"); 	
		loadjscssfile("resources/jQuery/plugins/chosen/chosen.jquery.min.js","js");
  	})(jQuery);
  });
</script>

<c:set var="dsDesc"><spring:message code="dsEdit.meta.desc"/></c:set>
<c:set var="dsHelpId" value="metaDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="metaPP">
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.meta.scriptContext"/></td>
    <td class="formField">
      <select id="allPointsList"></select>
      <tag:img png="add" onclick="addPointToContext();" title="common.add"/>
      
      <table cellspacing="1" id="contextContainer">
        <tbody id="contextTableEmpty" style="display:none;">
          <tr><th colspan="4"><spring:message code="dsEdit.meta.noPoints"/></th></tr>
        </tbody>
        <tbody id="contextTableHeaders" style="display:none;">
          <tr class="smRowHeader">
            <td><spring:message code="dsEdit.meta.pointName"/></td>
            <td><spring:message code="pointHierarchySLTS.xid"/></td>
            <td><spring:message code="dsEdit.pointDataType"/></td>
            <td><spring:message code="dsEdit.meta.var"/></td>
            <td></td>
          </tr>
        </tbody>
        <tbody id="contextTable"></tbody>
      </table>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired">
      <spring:message code="dsEdit.meta.script"/> <tag:img png="accept" onclick="validateScript();" title="dsEdit.meta.validate"/>
    </td>
    <td class="formField"><textarea id="script" rows="10" cols="50"/></textarea></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.meta.event"/></td>
    <td class="formField">
      <select id="updateEvent" onchange="updateEventChanged()">
        <option value="<c:out value="<%= MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_UPDATE %>"/>"><spring:message code="dsEdit.meta.event.context"/></option>
        <option value="<c:out value="<%= MetaPointLocatorVO.UPDATE_EVENT_CONTEXT_CHANGE %>"/>"><spring:message code="dsEdit.meta.event.context.change"/></option>
        <option value="<c:out value="<%= Common.TimePeriods.MINUTES %>"/>"><spring:message code="dsEdit.meta.event.minute"/></option>
        <option value="<c:out value="<%= Common.TimePeriods.HOURS %>"/>"><spring:message code="dsEdit.meta.event.hour"/></option>
        <option value="<c:out value="<%= Common.TimePeriods.DAYS %>"/>"><spring:message code="dsEdit.meta.event.day"/></option>
        <option value="<c:out value="<%= Common.TimePeriods.WEEKS %>"/>"><spring:message code="dsEdit.meta.event.week"/></option>
        <option value="<c:out value="<%= Common.TimePeriods.MONTHS %>"/>"><spring:message code="dsEdit.meta.event.month"/></option>
        <option value="<c:out value="<%= Common.TimePeriods.YEARS %>"/>"><spring:message code="dsEdit.meta.event.year"/></option>
        <option value="<c:out value="<%= MetaPointLocatorVO.UPDATE_EVENT_CRON %>"/>"><spring:message code="dsEdit.meta.event.cron"/></option>
      </select>
    </td>
  </tr>
  
  <tr id="updateCronPatternRow">
    <td class="formLabelRequired"><spring:message code="dsEdit.meta.event.cron"/></td>
    <td class="formField"><input id="updateCronPattern" type="text"/> <tag:help id="cronPatterns"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.meta.delay"/></td>
    <td class="formField">
        <input id="executionDelaySeconds" type="text" class="formShort"/>
        <sst:select id="executionDelayPeriodTypeCode">
          <tag:timePeriodOptions sst="true" ms="true" s="true" min="true"/>
        </sst:select>
    </td>
  </tr>
</tag:pointList>