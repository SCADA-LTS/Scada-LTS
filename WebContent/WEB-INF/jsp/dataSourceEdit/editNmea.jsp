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
  function initImpl() {
      listenButtons(false);
  }
  
  function saveDataSourceImpl() {
      listenCancel();
      DataSourceEditDwr.saveNmeaDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("commPortId"),
              $get("baudRate"), $get("resetTimeout"), saveDataSourceCB);
  }
  
  function listen() {
      $set("listenMessage", "<fmt:message key="dsEdit.nmea.listening"/>");
      dwr.util.removeAllRows("messagesList");
      listenButtons(true);
      DataSourceEditDwr.nmeaListenForMessages($get("commPortId"), $get("baudRate"), listenCB);
  }
  
  function listenCB() {
      setTimeout(listenUpdate, 2000);
  }
  
  function listenButtons(listening) {
      setDisabled("listenBtn", listening);
      setDisabled("listenCancelBtn", !listening);
  }
  
  function listenUpdate() {
      DataSourceEditDwr.nmeaListenerUpdate(function(result) {
          if (result) {
              $set("listenMessage", result.message);
              dwr.util.addRows("messagesList", result.messages, [function(msg) { return msg }]);
              listenCB();
          }
      });
  }
  
  function listenCancel() {
      DataSourceEditDwr.cancelTestingUtility(function() {
          listenButtons(false);
          $("listenMessage").innerHTML = "<fmt:message key="common.cancelled"/>";
      });
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.nmea.messageName"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
          function(p) { return encodeHtml(p.pointLocator.messageName); };
      
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.nmea.fieldIndex"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
          function(p) { return p.pointLocator.fieldIndex; };
  }
  
  function editPointCBImpl(locator) {
      $set("messageName", locator.messageName);
      $set("fieldIndex", locator.fieldIndex);
      $set("dataTypeId", locator.dataTypeId);
      $set("binary0Value", locator.binary0Value);
      dataTypeChanged();
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      
      locator.messageName = $get("messageName");
      locator.fieldIndex = $get("fieldIndex");
      locator.dataTypeId = $get("dataTypeId");
      locator.binary0Value = $get("binary0Value");
      
      DataSourceEditDwr.saveNmeaPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function dataTypeChanged() {
      var dataTypeId = $get("dataTypeId");
      if (dataTypeId == <%= DataTypes.BINARY %>)
          setDisabled("binary0Value", false);
      else
          setDisabled("binary0Value", true);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.nmea.props"/></c:set>
<c:set var="dsHelpId" value="nmeaListenerDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.nmea.port"/></td>
          <td class="formField">
            <c:choose>
              <c:when test="${!empty commPortError}">
                <input id="commPortId" type="hidden" value=""/>
                <span class="formError">${commPortError}</span>
              </c:when>
              <c:otherwise>
                <sst:select id="commPortId" value="${dataSource.commPortId}">
                  <c:forEach items="${commPorts}" var="port">
                    <sst:option value="${port.name}">${port.name}</sst:option>
                  </c:forEach>
                </sst:select>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.nmea.baud"/></td>
          <td class="formField">
            <sst:select id="baudRate" value="${dataSource.baudRate}">
              <sst:option>1200</sst:option>
              <sst:option>2400</sst:option>
              <sst:option>4800</sst:option>
              <sst:option>9600</sst:option>
            </sst:select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.nmea.resetTimeout"/></td>
          <td class="formField"><input type="text" id="resetTimeout" value="${dataSource.resetTimeout}"/></td>
        </tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.nmea.listener"/></td></tr>
        
        <tr>
          <td colspan="2">
            <input id="listenBtn" type="button" value="<fmt:message key="dsEdit.nmea.listen"/>" onclick="listen();"/>
            <input id="listenCancelBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="listenCancel();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="listenMessage" class="formError"></td></tr>
        
        <tr>
          <td colspan="2"><table><tbody id="messagesList"></tbody></table></td>
        </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="nmeaListenerPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId" onchange="dataTypeChanged()">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.nmea.binary0Value"/></td>
    <td class="formField"><input type="text" id="binary0Value"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.nmea.messageName"/></td>
    <td class="formField"><input type="text" id="messageName"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.nmea.fieldIndex"/></td>
    <td class="formField"><input type="text" id="fieldIndex"/></td>
  </tr>
</tag:pointList>