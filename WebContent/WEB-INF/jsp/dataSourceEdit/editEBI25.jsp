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
<%@page import="com.serotonin.mango.Common"%>

<%@page import="com.serotonin.mango.vo.dataSource.ebro.EBI25PointLocatorVO"%>
<script type="text/javascript">
  function initImpl() {
      hide("editImg"+ <c:out value="<%= Common.NEW_ID %>"/>);
  }
  
  function readInterface() {
      disableButtons(true);
      cancelEditPoint();
      $set("readInterfaceMessage", "<fmt:message key="dsEdit.ebi25.startRead"/>");
      DataSourceEditDwr.ebi25ReadInterface($get("host"), $get("port"), $get("keepAlive"), $get("timeout"),
              $get("retries"), function(reader) {
          disableButtons(false);
          
          if (reader.errorMessage)
              $set("readInterfaceMessage", reader.errorMessage);
          else {
              var info = "System time: "+ reader.systemTime +"<br/>Logger count: "+ reader.loggerCount;
              $set("readInterfaceMessage", info);
              
              $set("serialNumber", reader.serialNumber);
              $set("productionDate", reader.productionDate);
              $set("hardwareVersion", reader.hardwareVersion);
              $set("firmwareVersion", reader.firmwareVersion);
              
              writePointList(reader.points);
          }
      });
  }
  
  function syncTime() {
      disableButtons(true);
      $set("readInterfaceMessage", "<fmt:message key="dsEdit.ebi25.syncing"/>");
      DataSourceEditDwr.ebi25SyncTime($get("host"), $get("port"), $get("timeout"), $get("retries"), function(result) {
          disableButtons(false);
          if (result)
              $set("readInterfaceMessage", result);
          else
              $set("readInterfaceMessage", "<fmt:message key="dsEdit.ebi25.synced"/>");
      });
  }
  
  function disableButtons(disable) {
      setDisabled("readInterfaceBtn", disable);
      setDisabled("syncTimeBtn", disable);
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveEBI25DataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"), $get("timeout"), $get("retries"), $get("host"), $get("port"), $get("keepAlive"),
              saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.ebi25.unit"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.configurationDescription; };
      
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.ebi25.serialNumber"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.serialNumber; };
  }
  
  function editPointCBImpl(locator) {
      $set("index", locator.index);
      $set("type", locator.prettyType);
      $set("serialNumberPoint", locator.serialNumber);
      $set("productionDatePoint", locator.productionDate);
      $set("calibrationDatePoint", locator.calibrationDate);
      $set("hardwareVersionPoint", locator.hardwareVersion);
      $set("firmwareVersionPoint", locator.firmwareVersion);
      $set("unit", locator.prettyUnit);
      $set("sampleRate", locator.sampleRate);
      $set("lowLimit", locator.lowLimit);
      $set("highLimit", locator.highLimit);
      
      if (locator.type == <c:out value="<%= EBI25PointLocatorVO.TYPE_VALUE %>"/>) {
          show("valuePointDetails");
      }
      else
          hide("valuePointDetails");
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.suffix;
      delete locator.prettyUnit;
      delete locator.dataTypeId;
      delete locator.prettyType;
      
      locator.sampleRate = $get("sampleRate");
      locator.lowLimit = $get("lowLimit");
      locator.highLimit = $get("highLimit");
      
      DataSourceEditDwr.saveEBI25PointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>


<c:set var="dsDesc"><fmt:message key="dsEdit.ebi25.desc"/></c:set>
<c:set var="dsHelpId" value="ebi25DS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.host"/></td>
          <td class="formField"><input id="host" type="text" value="${dataSource.host}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.port"/></td>
          <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.keepAlive"/></td>
          <td class="formField"><sst:checkbox id="keepAlive" value="${dataSource.keepAlive}"/></td>
        </tr>
        
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
          <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.timeout"/></td>
          <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.retries"/></td>
          <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
        </tr>
        
        <tr><td class="horzSeparator" colspan="2"></td></tr>
        
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.ebi25.serialNumber"/></td>
          <td class="formField" id="serialNumber">${dataSource.serialNumber}</td>
        </tr>
        
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.ebi25.productionDate"/></td>
          <td class="formField" id="productionDate">${dataSource.productionDate}</td>
        </tr>
        
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.ebi25.hardwareVersion"/></td>
          <td class="formField" id="hardwareVersion">${dataSource.hardwareVersion}</td>
        </tr>
        
        <tr>
          <td class="formLabel"><fmt:message key="dsEdit.ebi25.firmwareVersion"/></td>
          <td class="formField" id="firmwareVersion">${dataSource.firmwareVersion}</td>
        </tr>
        
        <tr><td class="horzSeparator" colspan="2"></td></tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.ebi25.ebi25Interface"/></td></tr>
        
        <tr>
          <td colspan="2" align="center">
            <input id="readInterfaceBtn" type="button" value="<fmt:message key="dsEdit.ebi25.readInterface"/>" onclick="readInterface();"/>
            <input id="syncTimeBtn" type="button" value="<fmt:message key="dsEdit.ebi25.syncTime"/>" onclick="syncTime();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="readInterfaceMessage" class="formError"></td></tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="ebi25PP">
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.index"/></td>
    <td class="formField" id="index"></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.type"/></td>
    <td class="formField" id="type"></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.serialNumber"/></td>
    <td class="formField" id="serialNumberPoint"></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.productionDate"/></td>
    <td class="formField" id="productionDatePoint"></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.calibrationDate"/></td>
    <td class="formField" id="calibrationDatePoint"></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.hardwareVersion"/></td>
    <td class="formField" id="hardwareVersionPoint"></td>
  </tr>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.ebi25.firmwareVersion"/></td>
    <td class="formField" id="firmwareVersionPoint"></td>
  </tr>
  
  <tbody id="valuePointDetails">
    <tr>
      <td class="formLabel"><fmt:message key="dsEdit.ebi25.unit"/></td>
      <td class="formField" id="unit"></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.sampleRate"/></td>
      <td class="formField"><input type="text" id="sampleRate"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.lowLimit"/></td>
      <td class="formField"><input type="text" id="lowLimit"/></td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.ebi25.highLimit"/></td>
      <td class="formField"><input type="text" id="highLimit"/></td>
    </tr>
  </tbody>
</tag:pointList>