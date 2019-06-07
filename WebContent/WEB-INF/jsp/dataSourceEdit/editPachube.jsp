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
<%@page import="com.serotonin.mango.DataTypes"%>

<script type="text/javascript">
  function testValueParams() {
      startImageFader("valueTestImg", true);
      hide("valueTestRow");
      DataSourceEditDwr.testPachubeValueParams($get("apiKey"), $get("timeoutSeconds"), $get("retries"),
              $get("feedId"), $get("dataStreamId"), $get("dataTypeId"), $get("binary0Value"), function (result) {
          stopImageFader("valueTestImg");
          show("valueTestRow");
          $set("valueTestResult", encodeHtml(result));
      });
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.savePachubeDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("apiKey"),
            $get("updatePeriods"), $get("updatePeriodType"), $get("timeoutSeconds"), $get("retries"), saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.pachube.feedId"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.feedId; };
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.pachube.dataStreamId"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
          function(p) { return encodeHtml(p.pointLocator.dataStreamId); };
  }
  
  function editPointCBImpl(locator) {
      $set("feedId", locator.feedId);
      $set("dataStreamId", locator.dataStreamId);
      $set("dataTypeId", locator.dataTypeId);
      $set("binary0Value", locator.binary0Value);
      $set("settable", locator.settable);
      dataTypeChanged();
  }
  
  function savePointImpl(locator) {
      locator.feedId = $get("feedId");
      locator.dataStreamId = $get("dataStreamId");
      locator.dataTypeId = $get("dataTypeId");
      locator.binary0Value = $get("binary0Value");
      locator.settable = $get("settable");
      DataSourceEditDwr.savePachubePointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function dataTypeChanged() {
      var dataTypeId = $get("dataTypeId");
      if (dataTypeId == <%= DataTypes.BINARY %>)
          show("binaryZeroValueRow");
      else
          hide("binaryZeroValueRow");
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.pachube.desc"/></c:set>
<c:set var="dsHelpId" value="pachubeDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.apiKey"/></td>
          <td class="formField"><input id="apiKey" type="text" value="${dataSource.apiKey}" class="formLong"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.timeout"/></td>
          <td class="formField"><input id="timeoutSeconds" type="text" value="${dataSource.timeoutSeconds}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.retries"/></td>
          <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="pachubePP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId" onchange="dataTypeChanged()">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pachube.feedId"/></td>
    <td class="formField"><input type="text" id="feedId"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired">
      <fmt:message key="dsEdit.pachube.dataStreamId"/>
      <tag:img id="valueTestImg" png="accept" title="dsEdit.pachube.testValue" onclick="testValueParams()"/>
    </td>
    <td class="formField"><input type="text" id="dataStreamId"/></td>
  </tr>
  
  <tbody id="valueTestRow" style="display:none">
    <tr>
      <td></td>
      <td id="valueTestResult"></td>
    </tr>
  </tbody>
  
  <tbody id="binaryZeroValueRow">
    <tr>
      <td><fmt:message key="dsEdit.pachube.binaryZeroValue"/></td>
      <td class="formField"><input type="text" id="binary0Value"/></td>
    </tr>
  </tbody>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
</tag:pointList>