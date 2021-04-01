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
<link href="resources/js-ui/app/css/chunk-vendors.css" rel="stylesheet" type="text/css">
<link href="resources/js-ui/app/css/app.css" rel="stylesheet" type="text/css">

<style>
  table {
     border-collapse: separate !important;
     border-spacing: 2px !important;
  }
</style>

<script type="text/javascript">

  // when end of load get data from model and set in editHttpRetriver

  // share data old ui and new ui in vuejs

      var editDSNewUI = {
        id: ${dataSource.id},
        stop: ${dataSource.stop},
        reactivation: {
          sleep: ${dataSource.reactivation.sleep},
          type: ${dataSource.reactivation.type}, // 0-"Minute" 1-"Hour", 2-"Day"
          value: ${dataSource.reactivation.value}
        }
      }


  function testValueParams() {
      startImageFader("valueTestImg", true);
      hide("valueTestRow");
      DataSourceEditDwr.testHttpRetrieverValueParams($get("url"), $get("username"), $get("password"), $get("timeoutSeconds"), $get("retries"),
              $get("valueRegex"), $get("dataTypeId"), $get("valueFormat"), testValueParamsCB);
  }
  
  function testValueParamsCB(result) {
      stopImageFader("valueTestImg");
      show("valueTestRow");
      $set("valueTestResult", encodeHtml(result));
  }

  function testTimeParams() {
      startImageFader("timeTestImg", true);
      hide("timeTestRow");
      DataSourceEditDwr.testHttpRetrieverTimeParams($get("url"), $get("username"), $get("password"), $get("timeoutSeconds"), $get("retries"),
              $get("timeRegex"), $get("timeFormat"), testTimeParamsCB);
  }
  
  function testTimeParamsCB(result) {
      stopImageFader("timeTestImg");
      show("timeTestRow");
      $set("timeTestResult", result);
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveHttpRetrieverDataSourceWithReactivationOptions(
                  $get("dataSourceName"),
                  $get("dataSourceXid"),
                  $get("updatePeriods"),
                  $get("updatePeriodType"),
                  $get("url"),
                  $get("username"),
                  $get("password"),
                  $get("timeoutSeconds"),
                  $get("retries"),
                  editDSNewUI.stop,
                  editDSNewUI.reactivation.sleep,
                  editDSNewUI.reactivation.type,
                  editDSNewUI.reactivation.value,
                  saveDataSourceCB
      );
  }
  
  function openURL() {
  	var url = $get('url');
  	if ($get("username") && $get("password")) {
  		var urlParts = url.split("://", 2);
  		if (urlParts.length === 2) {
  			url = urlParts[0] + "://" + $get("username") + ":" + $get("password") + "@" + urlParts[1];
  		}
  	}
  	window.open(url, 'httpRetrieverTarget');
  }

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.httpRetriever.regex"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
          function(p) { return encodeHtml(p.pointLocator.valueRegex); };
  }
  
  function editPointCBImpl(locator) {
      $set("valueRegex", locator.valueRegex);
      $set("ignoreIfMissing", locator.ignoreIfMissing);
      $set("dataTypeId", locator.dataTypeId);
      $set("valueFormat", locator.valueFormat);
      $set("timeRegex", locator.timeRegex);
      $set("timeFormat", locator.timeFormat);
      
      dataTypeChanged();
      timeRegexKU();
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      
      locator.valueRegex = $get("valueRegex");
      locator.ignoreIfMissing = $get("ignoreIfMissing");
      locator.dataTypeId = $get("dataTypeId");
      locator.valueFormat = $get("valueFormat");
      locator.timeRegex = $get("timeRegex");
      locator.timeFormat = $get("timeFormat");
      
      DataSourceEditDwr.saveHttpRetrieverPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function dataTypeChanged() {
      var dataTypeId = $get("dataTypeId");
      if (dataTypeId == <%= DataTypes.BINARY %>) {
          show("valueFormatRow");
          hide("numberFormatHelp");
          $set("valueFormatLabel", "<fmt:message key="dsEdit.httpRetriever.binaryZeroValue"/>");
      }
      else if (dataTypeId == <%= DataTypes.NUMERIC %>) {
          show("valueFormatRow");
          show("numberFormatHelp");
          $set("valueFormatLabel", "<fmt:message key="dsEdit.httpRetriever.numberFormat"/>");
      }
      else {
          hide("numberFormatHelp");
          hide("valueFormatRow");
      }
  }
  
  function timeRegexKU() {
      var timeRegexLen = $get("timeRegex").trim().length;
      display("timeFormatRow", timeRegexLen > 0);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.httpRetriever.desc"/></c:set>
<c:set var="dsHelpId" value="httpRetrieverDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="number" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.httpRetriever.url"/></td>
          <td class="formField">
            <input id="url" type="text" value="${dataSource.url}" class="formLong"/>
            <tag:img png="bullet_go" onclick="openURL()" title="dsEdit.httpRetriever.openUrl"/>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.httpRetriever.credentials"/></td>
          <td class="formField">
            <fmt:message key="dsEdit.httpRetriever.username"/> <input type="text" id="username" class="formShort"/>
            <fmt:message key="dsEdit.httpRetriever.password"/> <input type="password" id="password" class="formShort"/>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.httpRetriever.timeout"/></td>
          <td class="formField"><input id="timeoutSeconds" type="text" value="${dataSource.timeoutSeconds}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.httpRetriever.retries"/></td>
          <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>
        <tr>

          <td COLSPAN=2>
              <div id="sleep-reactivation-ds"/>
          </td>
        </tr>


<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="httpRetrieverPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId" onchange="dataTypeChanged()">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired">
      <fmt:message key="dsEdit.httpRetriever.valueRegex"/>
      <tag:img id="valueTestImg" png="accept" title="dsEdit.httpRetriever.testValue" onclick="testValueParams()"/>
    </td>
    <td class="formField">
      <input type="text" id="valueRegex" class="formLong"/>
      <tag:help id="regex"/>
    </td>
  </tr>
  
  <tbody id="valueTestRow" style="display:none">
    <tr>
      <td></td>
      <td id="valueTestResult"></td>
    </tr>
  </tbody>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.httpRetriever.ignoreIfMissing"/></td>
    <td class="formField"><input type="checkbox" id="ignoreIfMissing"/></td>
  </tr>
  
  <tbody id="valueFormatRow">
    <tr>
      <td id="valueFormatLabel" class="formLabel"></td>
      <td class="formField">
        <input type="text" id="valueFormat"/>
        <span id="numberFormatHelp"><tag:help id="numberFormats"/></span>
      </td>
    </tr>
  </tbody>
  
  <tr>
    <td class="formLabel">
      <fmt:message key="dsEdit.httpRetriever.timeRegex"/>
      <tag:img id="timeTestImg" png="accept" title="dsEdit.httpRetriever.testTime" onclick="testTimeParams()"/>
    </td>
    <td class="formField">
      <input type="text" id="timeRegex" onkeyup="timeRegexKU()" class="formLong"/>
      <tag:help id="regex"/>
    </td>
  </tr>
  
  <tbody id="timeTestRow" style="display:none">
    <tr>
      <td></td>
      <td id="timeTestResult"></td>
    </tr>
  </tbody>
  
  <tbody id="timeFormatRow">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.httpRetriever.timeFormat"/></td>
      <td class="formField">
        <input type="text" id="timeFormat"/>
        <tag:help id="datetimeFormats"/>
      </td>
    </tr>
  </tbody>
</tag:pointList>
<%@ include file="/WEB-INF/jsp/include/vue/vue-app.js.jsp"%>
<%@ include file="/WEB-INF/jsp/include/vue/vue-view.js.jsp"%>
