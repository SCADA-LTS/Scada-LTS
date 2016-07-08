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

<%@page import="com.serotonin.mango.vo.dataSource.vmstat.VMStatDataSourceVO"%>
<%@page import="com.serotonin.mango.vo.dataSource.vmstat.VMStatPointLocatorVO"%>
<script type="text/javascript">
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveVMStatDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("pollSeconds"),
              $get("outputScale"), saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.vmstat.attribute"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
              function(p) { return p.pointLocator.configurationDescription; };
  }
  
  function editPointCBImpl(locator) {
      $set("attributeId", locator.attributeId);
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.dataTypeId;
      
      locator.attributeId = $get("attributeId");
      
      DataSourceEditDwr.saveVMStatPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.vmstat.desc"/></c:set>
<c:set var="dsHelpId" value="vmstatDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.vmstat.pollSeconds"/></td>
    <td class="formField"><input id="pollSeconds" type="text" value="${dataSource.pollSeconds}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.vmstat.outputScale"/></td>
    <td class="formField">
      <sst:select id="outputScale" value="${dataSource.outputScale}">
        <tag:exportCodesOptions sst="true" optionList="<%= VMStatDataSourceVO.OUTPUT_SCALE_CODES.getIdKeys() %>"/>
      </sst:select>
    </td>
  </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="vmstatPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.vmstat.attribute"/></td>
    <td class="formField">
      <select id="attributeId">
        <tag:exportCodesOptions optionList="<%= VMStatPointLocatorVO.ATTRIBUTE_CODES.getIdKeys() %>"/>
      </select>
    </td>
  </tr>
</tag:pointList>