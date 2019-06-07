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
<%@page import="com.serotonin.mango.Common"%>

<script type="text/javascript">
  function initImpl() {
      hide("editImg"+ <c:out value="<%= Common.NEW_ID %>"/>);
  }
  
  function saveDataSourceImpl() {
      DataSourceEditDwr.savePersistentDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("port"),
    		  $get("authorizationKey"), $get("acceptPointUpdates"), saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
  }
  
  function editPointCBImpl(locator) {
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.dataTypeId;
      DataSourceEditDwr.savePersistentPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function getRtStatus() {
      setDisabled("getStatusBtn", true);
      DataSourceEditDwr.getPersistentStatus(function(response) {
          dwr.util.removeAllOptions("statusResults");
          dwr.util.addOptions("statusResults", response.messages, "genericMessage");
          setDisabled("getStatusBtn", false);
      });
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.persistent.desc"/></c:set>
<c:set var="dsHelpId" value="persistentDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.persistent.port"/></td>
          <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.persistent.authorizationKey"/></td>
          <td class="formField"><input id="authorizationKey" type="text" value="${dataSource.authorizationKey}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.persistent.acceptPointUpdates"/></td>
          <td class="formField"><sst:checkbox id="acceptPointUpdates" selectedValue="${dataSource.acceptPointUpdates}"/></td>
        </tr>
        
      </table>
      
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td class="smallTitle"><fmt:message key="dsEdit.persistent.status"/></td></tr>
        <tr>
          <td align="center">
            <input id="getStatusBtn" type="button" value="<fmt:message key="dsEdit.persistent.getStatus"/>" onclick="getRtStatus();"/>
          </td>
        </tr>
        
        <tr><td><ul id="statusResults" style="padding-left: 20px;"></ul></td></tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="persistentPP">
</tag:pointList>