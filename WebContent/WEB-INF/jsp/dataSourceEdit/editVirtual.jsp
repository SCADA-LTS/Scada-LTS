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
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>

<script type="text/javascript">
  var currentChangeType;
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveVirtualDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"), saveDataSourceCB);
  }
  
  function initImpl() {
      createContextualMessageNode($("incrementMultistateTable"), "incrementMultistateChange.values");
      createContextualMessageNode($("randomMultistateTable"), "randomMultistateChange.values");
  }
  
  function editPointCBImpl(locator) {
      $set("settable", locator.settable);
      $set("dataTypeId", locator.dataTypeId);
      DataSourceEditDwr.getChangeTypes(locator.dataTypeId, function(data) {
          changeDataTypeCB(data);
          $set("changeTypeId", locator.changeTypeId);
          changeDataType();
      });
  }
  
  function savePointImpl(locator) {
      delete locator.alternateBooleanChange.description;
      delete locator.brownianChange.description;
      delete locator.incrementAnalogChange.description;
      delete locator.incrementMultistateChange.description;
      delete locator.noChange.description;
      delete locator.randomAnalogChange.description;
      delete locator.randomBooleanChange.description;
      delete locator.randomMultistateChange.description;
      delete locator.analogAttractorChange.description;
      
      locator.settable = $get("settable");
      locator.dataTypeId = $get("dataTypeId");
      locator.changeTypeId = $get("changeTypeId");
      locator.alternateBooleanChange.startValue = $get("alternateBooleanChange.startValue");
      locator.brownianChange.min = $get("brownianChange.min");
      locator.brownianChange.max = $get("brownianChange.max");
      locator.brownianChange.maxChange = $get("brownianChange.maxChange");
      locator.brownianChange.startValue = $get("brownianChange.startValue");
      locator.incrementAnalogChange.min = $get("incrementAnalogChange.min");
      locator.incrementAnalogChange.max = $get("incrementAnalogChange.max");
      locator.incrementAnalogChange.change = $get("incrementAnalogChange.change");
      locator.incrementAnalogChange.roll = $get("incrementAnalogChange.roll") ? "true" : "false";
      locator.incrementAnalogChange.startValue = $get("incrementAnalogChange.startValue");
      locator.incrementMultistateChange.roll = $get("incrementMultistateChange.roll") ? "true" : "false";
      locator.incrementMultistateChange.startValue = $get("incrementMultistateChange.startValue");
      locator.noChange.startValue = $get("noChange.startValue");
      locator.randomAnalogChange.min = $get("randomAnalogChange.min");
      locator.randomAnalogChange.max = $get("randomAnalogChange.max");
      locator.randomAnalogChange.startValue = $get("randomAnalogChange.startValue");
      locator.randomBooleanChange.startValue = $get("randomBooleanChange.startValue");
      locator.randomMultistateChange.startValue = $get("randomMultistateChange.startValue");
      locator.analogAttractorChange.maxChange = $get("analogAttractorChange.maxChange");
      locator.analogAttractorChange.volatility = $get("analogAttractorChange.volatility");
      locator.analogAttractorChange.attractionPointId = $get("analogAttractorChange.attractionPointId");
      locator.analogAttractorChange.startValue = $get("analogAttractorChange.startValue");
      
      DataSourceEditDwr.saveVirtualPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function changeDataType() {
      DataSourceEditDwr.getChangeTypes($get("dataTypeId"), changeDataTypeCB);
  }
  
  function changeDataTypeCB(changeTypes) {
      var changeTypeDD = $("changeTypeId");
      var savedType = changeTypeDD.value;
      dwr.util.removeAllOptions(changeTypeDD);
      dwr.util.addOptions(changeTypeDD, changeTypes, "key", "message");
      changeTypeDD.value = savedType;
      changeChangeType();
  }
  
  function changeChangeType() {
      var changeTypeId = "divCH"+ $get("changeTypeId");
      
      // Close the current change type div.
      if (currentChangeType)
          hide(currentChangeType);
      
      // Open the selected type.
      show(changeTypeId);
      currentChangeType = changeTypeId;
      
      // Update the values.
      var locator = currentPoint.pointLocator;
      $set("alternateBooleanChange.startValue", locator.alternateBooleanChange.startValue);
      $set("brownianChange.min", locator.brownianChange.min);
      $set("brownianChange.max", locator.brownianChange.max);
      $set("brownianChange.maxChange", locator.brownianChange.maxChange);
      $set("brownianChange.startValue", locator.brownianChange.startValue);
      $set("incrementAnalogChange.min", locator.incrementAnalogChange.min);
      $set("incrementAnalogChange.max", locator.incrementAnalogChange.max);
      $set("incrementAnalogChange.change", locator.incrementAnalogChange.change);
      $set("incrementAnalogChange.roll", locator.incrementAnalogChange.roll == "true");
      $set("incrementAnalogChange.startValue", locator.incrementAnalogChange.startValue);
      refreshValueList("incrementMultistate", locator.incrementMultistateChange.values);
      $set("incrementMultistateChange.roll", locator.incrementMultistateChange.roll == "true");
      $set("incrementMultistateChange.startValue", locator.incrementMultistateChange.startValue);
      $set("noChange.startValue", locator.noChange.startValue);
      $set("randomAnalogChange.min", locator.randomAnalogChange.min);
      $set("randomAnalogChange.max", locator.randomAnalogChange.max);
      $set("randomAnalogChange.startValue", locator.randomAnalogChange.startValue);
      $set("randomBooleanChange.startValue", locator.randomBooleanChange.startValue);
      refreshValueList("randomMultistate", locator.randomMultistateChange.values);
      $set("randomMultistateChange.startValue", locator.randomMultistateChange.startValue);
      $set("analogAttractorChange.maxChange", locator.analogAttractorChange.maxChange);
      $set("analogAttractorChange.volatility", locator.analogAttractorChange.volatility);
      $set("analogAttractorChange.attractionPointId", locator.analogAttractorChange.attractionPointId);
      $set("analogAttractorChange.startValue", locator.analogAttractorChange.startValue);
  }
  
  //
  // List manipulation.
  function addListValue(prefix) {
      var theValue = $get(prefix);
      var theNumber = parseInt(theValue);
      if (isNaN(theNumber)) {
          alert("<fmt:message key="dsEdit.virtual.errorParsingValue"/>");
          return false;
      }
      var arr = currentPoint.pointLocator[prefix +"Change"].values;
      for (var i=arr.length-1; i>=0; i--) {
          if (arr[i] == theNumber) {
              alert("<fmt:message key="dsEdit.virtual.invalidValue"/> "+ theNumber);
              return false;
          }
      }
      arr[arr.length] = theNumber;
      arr.sort( function(a,b) { return a-b; } );
      refreshValueList(prefix, arr);
      $set(prefix, theNumber + 1);
      return false;
  }
  
  function removeListValue(theValue, prefix) {
      var arr = currentPoint.pointLocator[prefix +"Change"].values;
      for (var i=arr.length-1; i>=0; i--) {
          if (arr[i] == theValue)
              arr.splice(i, 1);
      }
      refreshValueList(prefix, arr);
      return false;
  }
  
  function refreshValueList(prefix, arr) {
      dwr.util.removeAllRows(prefix +"Values");
      dwr.util.addRows(prefix +"Values", arr, [
              function(data) { return data; },
              function(data) {
                  return writeImage(null, null, "bullet_delete", "<fmt:message key="common.delete"/>",
                          "removeListValue("+ data +", '"+ prefix +"');");
              }
              ]);
      var startDD = $(prefix +"Change.startValue");
      var currentStart = startDD.value;
      dwr.util.removeAllOptions(startDD);
      dwr.util.addOptions(startDD, arr);
      startDD.value = currentStart;
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.virtual.desc"/></c:set>
<c:set var="dsHelpId" value="virtualDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort" />
      <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
        <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="virtualPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId" onchange="changeDataType();">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.changeType"/></td>
    <td class="formField"><select id="changeTypeId" onchange="changeChangeType();"></select></td>
  </tr>
  
  <%--
      Alternate boolean, change type 1
  --%>
  <tbody id="divCH1" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField">
        <select id="alternateBooleanChange.startValue">
          <option value="true"><fmt:message key="common.true"/></option>
          <option value="false"><fmt:message key="common.false"/></option>
        </select>
      </td>
    </tr>
  </tbody>

  <%--
      Brownian, change type 2
  --%>
  <tbody id="divCH2" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.min"/></td>
      <td class="formField"><input type="text" id="brownianChange.min" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.max"/></td>
      <td class="formField"><input type="text" id="brownianChange.max" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.maxChange"/></td>
      <td class="formField"><input type="text" id="brownianChange.maxChange" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><input type="text" id="brownianChange.startValue" /></td>
    </tr>
  </tbody>

  <%--
      Increment analog, change type 3
  --%>
  <tbody id="divCH3" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.min"/></td>
      <td class="formField"><input type="text" id="incrementAnalogChange.min" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.max"/></td>
      <td class="formField"><input type="text" id="incrementAnalogChange.max" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.change"/></td>
      <td class="formField"><input type="text" id="incrementAnalogChange.change" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.roll"/></td>
      <td class="formField"><input type="checkbox" id="incrementAnalogChange.roll" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><input type="text" id="incrementAnalogChange.startValue" /></td>
    </tr>
  </tbody>

  <%--
      Increment multistate, change type 4
  --%>
  <tbody id="divCH4" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.values"/></td>
      <td class="formField">
        <input id="incrementMultistate" type="text" />
        <tag:img png="add" onclick="addListValue('incrementMultistate');" title="common.add" />
        <table id="incrementMultistateTable"><tbody id="incrementMultistateValues"></tbody></table>
      </td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.roll"/></td>
      <td class="formField"><input type="checkbox" id="incrementMultistateChange.roll"></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><select id="incrementMultistateChange.startValue"></select></td>
    </tr>
  </tbody>

  <%--
      No change, change type 5
  --%>
  <tbody id="divCH5" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><input type="text" id="noChange.startValue" /></td>
    </tr>
  </tbody>

  <%--
      Random analog, change type 6
  --%>
  <tbody id="divCH6" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.min"/></td>
      <td class="formField"><input type="text" id="randomAnalogChange.min" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.max"/></td>
      <td class="formField"><input type="text" id="randomAnalogChange.max" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><input type="text" id="randomAnalogChange.startValue" /></td>
    </tr>
  </tbody>

  <%--
      Random boolean, change type 7
  --%>
  <tbody id="divCH7" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField">
        <select id="randomBooleanChange.startValue">
          <option value="true"><fmt:message key="common.true"/></option>
          <option value="false"><fmt:message key="common.false"/></option>
        </select>
    </tr>
  </tbody>

  <%--
      Random multistate, change type 8
  --%>
  <tbody id="divCH8" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.values"/></td>
      <td class="formField">
        <input id="randomMultistate" type="text" />
        <tag:img png="add" title="common.add" onclick="addListValue('randomMultistate');" />
        <table id="randomMultistateTable"><tbody id="randomMultistateValues"></tbody></table>
      </td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><select id="randomMultistateChange.startValue"></select></td>
    </tr>
  </tbody>

  <%--
      Attractor, change type 9
  --%>
  <tbody id="divCH9" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.maxChange"/></td>
      <td class="formField"><input type="text" id="analogAttractorChange.maxChange" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.volatility"/></td>
      <td class="formField"><input type="text" id="analogAttractorChange.volatility" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.attractionPoint"/></td>
      <td class="formField"><select id="analogAttractorChange.attractionPointId">
        <c:forEach items="${analogPoints}" var="dataPoint">
          <option value="${dataPoint.id}">${dataPoint.extendedName}</option>
        </c:forEach>
      </select></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.virtual.startValue"/></td>
      <td class="formField"><input type="text" id="analogAttractorChange.startValue" /></td>
    </tr>
  </tbody>
</tag:pointList>