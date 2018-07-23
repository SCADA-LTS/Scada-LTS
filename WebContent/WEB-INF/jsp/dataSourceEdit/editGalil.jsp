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
<%@page import="com.serotonin.mango.vo.dataSource.galil.PointTypeVO"%>
<%@page import="com.serotonin.mango.DataTypes"%>

<script type="text/javascript">
  var currentPointType;
  
  function initImpl() {
      galilTestButton(false);
  }

  function galilTestButton(executing) {
      setDisabled("galilTestBtn", executing);
  }
  
  function galilTest() {
      DataSourceEditDwr.galilTestCommand($get("host"), $get("port"), $get("timeout"), $get("galilTestCommand"),
              galilTestCB);
      galilTestButton(true);
      $set("galilTestMessage", "");
  }
  
  function galilTestCB(result) {
      $set("galilTestMessage", result);
      galilTestButton(false);
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveGalilDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("host"), $get("port"),
              $get("timeout"), $get("retries"), $get("updatePeriods"), $get("updatePeriodType"), saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.galil.type"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
              function(p) { return p.pointLocator.configurationDescription; };
  }
  
  function editPointCBImpl(locator) {
      $("pointTypeId").value = locator.pointTypeId;
      changePointType();
  }
  
  function changePointType() {
      var pointTypeId = "divPH"+ $get("pointTypeId");
      
      // Close the current point type div.
      if (currentPointType)
          hide(currentPointType);
      
      // Open the selected type.
      show(pointTypeId);
      currentPointType = pointTypeId;
      
      // Update the values.
      var locator = currentPoint.pointLocator;
      $set("inputPointType.dataTypeId", locator.inputPointType.dataTypeId);
      $set("inputPointType.inputId", locator.inputPointType.inputId);
      $set("inputPointType.scaleRawLow", locator.inputPointType.scaleRawLow);
      $set("inputPointType.scaleRawHigh", locator.inputPointType.scaleRawHigh);
      $set("inputPointType.scaleEngLow", locator.inputPointType.scaleEngLow);
      $set("inputPointType.scaleEngHigh", locator.inputPointType.scaleEngHigh);
      $set("outputPointType.outputId", locator.outputPointType.outputId);
      $set("tellPositionPointType.axis", locator.tellPositionPointType.axis);
      $set("tellPositionPointType.scaleRawLow", locator.tellPositionPointType.scaleRawLow);
      $set("tellPositionPointType.scaleRawHigh", locator.tellPositionPointType.scaleRawHigh);
      $set("tellPositionPointType.scaleEngLow", locator.tellPositionPointType.scaleEngLow);
      $set("tellPositionPointType.scaleEngHigh", locator.tellPositionPointType.scaleEngHigh);
      $set("tellPositionPointType.roundToInteger", locator.tellPositionPointType.roundToInteger);
      $set("variablePointType.variableName", locator.variablePointType.variableName);
      $set("variablePointType.dataTypeId", locator.variablePointType.dataTypeId);
      
      changeInputDataType();
  }
  
  function changeInputDataType() {
      var dataType = $get("inputPointType.dataTypeId");
      var bin = dataType == <c:out value="<%= DataTypes.BINARY %>"/>;
      
      display("inputScaleRawLow", !bin);
      display("inputScaleRawHigh", !bin);
      display("inputScaleEngLow", !bin);
      display("inputScaleEngHigh", !bin);
  }
  
  function savePointImpl(locator) {
      delete locator.dataTypeId;
      delete locator.settable;
      delete locator.commandPointType.dataTypeId;
      delete locator.commandPointType.description;
      delete locator.commandPointType.settable;
      delete locator.inputPointType.description;
      delete locator.inputPointType.settable;
      delete locator.outputPointType.dataTypeId;
      delete locator.outputPointType.description;
      delete locator.outputPointType.settable;
      delete locator.tellPositionPointType.dataTypeId;
      delete locator.tellPositionPointType.description;
      delete locator.tellPositionPointType.settable;
      delete locator.variablePointType.description;
      delete locator.variablePointType.settable;
      
      locator.pointTypeId = $get("pointTypeId");
      locator.inputPointType.dataTypeId = $get("inputPointType.dataTypeId");
      locator.inputPointType.inputId = $get("inputPointType.inputId");
      locator.inputPointType.scaleRawLow = $get("inputPointType.scaleRawLow");
      locator.inputPointType.scaleRawHigh = $get("inputPointType.scaleRawHigh");
      locator.inputPointType.scaleEngLow = $get("inputPointType.scaleEngLow");
      locator.inputPointType.scaleEngHigh = $get("inputPointType.scaleEngHigh");
      locator.outputPointType.outputId = $get("outputPointType.outputId");
      locator.tellPositionPointType.axis = $get("tellPositionPointType.axis");
      locator.tellPositionPointType.scaleRawLow = $get("tellPositionPointType.scaleRawLow");
      locator.tellPositionPointType.scaleRawHigh = $get("tellPositionPointType.scaleRawHigh");
      locator.tellPositionPointType.scaleEngLow = $get("tellPositionPointType.scaleEngLow");
      locator.tellPositionPointType.scaleEngHigh = $get("tellPositionPointType.scaleEngHigh");
      locator.tellPositionPointType.roundToInteger = $get("tellPositionPointType.roundToInteger");
      locator.variablePointType.variableName = $get("variablePointType.variableName");
      locator.variablePointType.dataTypeId = $get("variablePointType.dataTypeId");
      
      DataSourceEditDwr.saveGalilPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.galil.desc"/></c:set>
<c:set var="dsHelpId" value="galilDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.galil.host"/></td>
          <td class="formField"><input id="host" type="text" value="${dataSource.host}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.galil.port"/></td>
          <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.galil.timeout"/></td>
          <td class="formField"><input id="timeout" type="text" value="${dataSource.timeout}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.galil.retries"/></td>
          <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.galil.commandTesting"/></td></tr>
        
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.galil.command"/></td>
          <td class="formField"><input type="text" id="galilTestCommand"/></td>
        </tr>
        
        <tr>
          <td colspan="2" align="center">
            <input id="galilTestBtn" type="button" value="<fmt:message key="dsEdit.galil.execute"/>" onclick="galilTest();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="galilTestMessage" class="formError"></td></tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="galilPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.galil.pointType"/></td>
    <td class="formField">
      <select id="pointTypeId" onchange="changePointType();">
        <option value="<c:out value="<%= PointTypeVO.Types.COMMAND %>"/>"><fmt:message key="dsEdit.galil.pointType.command"/></option>
        <option value="<c:out value="<%= PointTypeVO.Types.INPUT %>"/>"><fmt:message key="dsEdit.galil.pointType.input"/></option>
        <option value="<c:out value="<%= PointTypeVO.Types.OUTPUT %>"/>"><fmt:message key="dsEdit.galil.pointType.output"/></option>
        <option value="<c:out value="<%= PointTypeVO.Types.TELL_POSITION %>"/>"><fmt:message key="dsEdit.galil.pointType.tellPosition"/></option>
        <option value="<c:out value="<%= PointTypeVO.Types.VARIABLE %>"/>"><fmt:message key="dsEdit.galil.pointType.variable"/></option>
      </select>
    </td>
  </tr>
  
  <%--
      Command, point type 1
  --%>
  <tbody id="divPH1" style="display: none;">
  </tbody>

  <%--
      Input, point type 2
  --%>
  <tbody id="divPH2" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
      <td class="formField">
        <select id="inputPointType.dataTypeId" onchange="changeInputDataType();">
          <tag:dataTypeOptions excludeAlphanumeric="true" excludeImage="true" excludeMultistate="true"/>
        </select>
      </td>
    </tr>
  
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.inputNumber"/></td>
      <td class="formField"><input type="text" id="inputPointType.inputId" /></td>
    </tr>

    <tr id="inputScaleRawLow">
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.scaleLow"/></td>
      <td class="formField"><input type="text" id="inputPointType.scaleRawLow" /></td>
    </tr>

    <tr id="inputScaleRawHigh">
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.scaleHigh"/></td>
      <td class="formField"><input type="text" id="inputPointType.scaleRawHigh" /></td>
    </tr>

    <tr id="inputScaleEngLow">
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.engLow"/></td>
      <td class="formField"><input type="text" id="inputPointType.scaleEngLow" /></td>
    </tr>

    <tr id="inputScaleEngHigh">
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.engHigh"/></td>
      <td class="formField"><input type="text" id="inputPointType.scaleEngHigh" /></td>
    </tr>
  </tbody>

  <%--
      Output, point type 3
  --%>
  <tbody id="divPH3" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.outputNumber"/></td>
      <td class="formField"><input type="text" id="outputPointType.outputId" /></td>
    </tr>
  </tbody>

  <%--
      Tell position, point type 4
  --%>
  <tbody id="divPH4" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.axis"/></td>
      <td class="formField">
        <select id="tellPositionPointType.axis">
          <option>A</option>
          <option>B</option>
          <option>C</option>
          <option>D</option>
          <option>E</option>
          <option>F</option>
          <option>G</option>
          <option>H</option>
        </select>
      </td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.scaleLow"/></td>
      <td class="formField"><input type="text" id="tellPositionPointType.scaleRawLow" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.scaleHigh"/></td>
      <td class="formField"><input type="text" id="tellPositionPointType.scaleRawHigh" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.engLow"/></td>
      <td class="formField"><input type="text" id="tellPositionPointType.scaleEngLow" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.engHigh"/></td>
      <td class="formField"><input type="text" id="tellPositionPointType.scaleEngHigh" /></td>
    </tr>

    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.round"/></td>
      <td class="formField"><input type="checkbox" id="tellPositionPointType.roundToInteger"></td>
    </tr>
  </tbody>

  <%--
      Variable, point type 5
  --%>
  <tbody id="divPH5" style="display: none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.galil.varName"/></td>
      <td class="formField"><input type="text" id="variablePointType.variableName" /></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
      <td class="formField">
        <select id="variablePointType.dataTypeId">
          <tag:dataTypeOptions excludeImage="true"/>
        </select>
      </td>
    </tr>
  </tbody>
</tag:pointList>