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
  var staticHeaderList;

  var editDSNewUI = {
    id: ${dataSource.id},
    stop: ${dataSource.stop},
    reactivation: {
      sleep: ${dataSource.reactivation.sleep},
      type: ${dataSource.reactivation.type}, // 0-"Minute" 1-"Hour", 2-"Day"
      value: ${dataSource.reactivation.value}
    }
  }

  function initImpl() {
    DataSourceEditDwr.initHttpRetriever(initRetriever);
  }

  function initRetriever(response) {
    staticHeaderList = new Array();
    var i;
    var list = response.data.staticHeaders;
    for (i=0; i<list.length; i++)
      staticHeaderList[staticHeaderList.length] = {key: list[i].key, value: list[i].value};
    refreshStaticHeaderList();
    DataSourceEditDwr.getBasicCredentials(list, setCredentials);
  }

  function addStaticHeader() {
    var key = $get("sheaderKey");
    var value = $get("sheaderValue");

    if (!key || key.trim().length == 0) {
      alert("<spring:message code="publisherEdit.httpSender.keyRequired"/>");
      return;
    }

    for (var i=0; i<staticHeaderList.length; i++) {
      if (staticHeaderList[i].key == key) {
        alert("<spring:message code="publisherEdit.httpSender.keyExists"/>: '"+ key +"'");
        return;
      }
    }

    staticHeaderList[staticHeaderList.length] = {key: key, value: value};
    staticHeaderList.sort();
    refreshStaticHeaderList();
  }

  function removeStaticHeader(index) {
    if (staticHeaderList[index].key == "Authorization") {
      $set("username", "");
      $set("password", "");
    }
    staticHeaderList.splice(index, 1);
    refreshStaticHeaderList();
  }

  function refreshStaticHeaderList() {
    dwr.util.removeAllRows("staticHeaderList");
    if (staticHeaderList.length == 0)
      show("noStaticHeadersMsg");
    else {
      hide("noStaticHeadersMsg");
      dwr.util.addRows("staticHeaderList", staticHeaderList, [
        function(data) { return data.key +"="+ data.value; },
        function(data, options) {
          return "<img src='images/bullet_delete.png' class='ptr' title='<spring:message code="dsEdit.httpRetriever.removeHeader"/>' "+
                  "onclick='removeStaticHeader("+ options.rowIndex + ");'/>";
        }
      ], null);
    }
  }

  function setCredentials(credentials) {
    $set("username", credentials[0]);
    $set("password", credentials[1]);
  }

  function testValueParams() {
      startImageFader("valueTestImg", true);
      hide("valueTestRow");
      DataSourceEditDwr.testHttpRetrieverValueParams($get("url"), $get("timeoutSeconds"), $get("retries"),
              $get("valueRegex"), $get("dataTypeId"), $get("valueFormat"), staticHeaderList, testValueParamsCB);
  }
  
  function testValueParamsCB(result) {
      stopImageFader("valueTestImg");
      show("valueTestRow");
      $set("valueTestResult", encodeHtml(result));
  }

  function testTimeParams() {
      startImageFader("timeTestImg", true);
      hide("timeTestRow");
      DataSourceEditDwr.testHttpRetrieverTimeParams($get("url"), $get("timeoutSeconds"), $get("retries"),
              $get("timeRegex"), $get("timeFormat"), staticHeaderList, testTimeParamsCB);
  }
  
  function testTimeParamsCB(result) {
      stopImageFader("timeTestImg");
      show("timeTestRow");
      $set("timeTestResult", result);
  }

  function saveDataSourceImplOld(){
    updateStaticHeadersList();
    DataSourceEditDwr.saveHttpRetrieverDataSource($get("dataSourceName"), $get("dataSourceXid"),
                $get("updatePeriods"), $get("updatePeriodType"), $get("url"), $get("timeoutSeconds"), $get("retries"),
                $get("stop"), $get("username"), $get("password"), staticHeaderList,
                saveHttpRetrieverDataSourceCB);
  }

  function saveDataSourceImpl() {
      updateStaticHeadersList();
      DataSourceEditDwr.saveHttpRetrieverDataSourceWithReactivationOptions(
                  $get("dataSourceName"),
                  $get("dataSourceXid"),
                  $get("updatePeriods"),
                  $get("updatePeriodType"),
                  $get("url"),
                  $get("timeoutSeconds"),
                  $get("retries"),
                  editDSNewUI.stop,
                  editDSNewUI.reactivation.sleep,
                  editDSNewUI.reactivation.type,
                  editDSNewUI.reactivation.value,
                  $get("username"),
                  $get("password"),
                  staticHeaderList,
                  saveHttpRetrieverDataSourceCB
      );
  }

  function saveHttpRetrieverDataSourceCB(response) {
    saveDataSourceCB(response);
    DataSourceEditDwr.initHttpRetriever(initRetriever);
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
      pointListColumnHeaders[pointListColumnHeaders.length] = "<spring:message code="dsEdit.httpRetriever.regex"/>";
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
          $set("valueFormatLabel", "<spring:message code="dsEdit.httpRetriever.binaryZeroValue"/>");
      }
      else if (dataTypeId == <%= DataTypes.NUMERIC %>) {
          show("valueFormatRow");
          show("numberFormatHelp");
          $set("valueFormatLabel", "<spring:message code="dsEdit.httpRetriever.numberFormat"/>");
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

  function removeAuthFromStaticHeaders() {
    staticHeaderList = staticHeaderList.filter(header => header.key != 'Authorization');
  }

  function updateStaticHeadersList() {
    if (!$get("username") || !$get("password")) {
      removeAuthFromStaticHeaders();
      refreshStaticHeaderList();
    }
  }
</script>

<c:set var="dsDesc"><spring:message code="dsEdit.httpRetriever.desc"/></c:set>
<c:set var="dsHelpId" value="httpRetrieverDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.httpRetriever.url"/></td>
          <td class="formField">
            <input id="url" type="text" value="${dataSource.url}" class="formLong"/>
            <tag:img png="bullet_go" onclick="openURL()" title="dsEdit.httpRetriever.openUrl"/>
          </td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.httpRetriever.credentials"/></td>
          <td class="formField">
            <spring:message code="dsEdit.httpRetriever.username"/> <input type="text" id="username" class="formShort"/>
            <spring:message code="dsEdit.httpRetriever.password"/> <input type="password" id="password" class="formShort"/>
          </td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="publisherEdit.httpSender.staticHeaders"/></td>
          <td class="formField">
            <spring:message code="publisherEdit.httpSender.headerKey"/> <input type="text" id="sheaderKey" class="formShort"/>
            <spring:message code="publisherEdit.httpSender.headerValue"/> <input type="text" id="sheaderValue" class="formShort"/>
            <tag:img png="add" title="publisherEdit.httpSender.addStaticHeader" onclick="addStaticHeader()"/>
            <table>
              <tr id="noStaticHeadersMsg" style="display:none"><td><spring:message code="publisherEdit.httpSender.noStaticHeaders"/></td></tr>
              <tbody id="staticHeaderList"></tbody>
            </table>
          </td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.httpRetriever.timeout"/></td>
          <td class="formField"><input id="timeoutSeconds" type="text" value="${dataSource.timeoutSeconds}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.httpRetriever.retries"/></td>
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
    <td class="formLabelRequired"><spring:message code="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId" onchange="dataTypeChanged()">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired">
      <spring:message code="dsEdit.httpRetriever.valueRegex"/>
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
    <td class="formLabelRequired"><spring:message code="dsEdit.httpRetriever.ignoreIfMissing"/></td>
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
      <spring:message code="dsEdit.httpRetriever.timeRegex"/>
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
      <td class="formLabelRequired"><spring:message code="dsEdit.httpRetriever.timeFormat"/></td>
      <td class="formField">
        <input type="text" id="timeFormat"/>
        <tag:help id="datetimeFormats"/>
      </td>
    </tr>
  </tbody>
</tag:pointList>
<%@ include file="/WEB-INF/jsp/include/vue/vue-app.js.jsp"%>
<%@ include file="/WEB-INF/jsp/include/vue/vue-view.js.jsp"%>
