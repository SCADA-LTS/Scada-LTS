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

<%@page import="com.serotonin.mango.vo.dataSource.internal.InternalDataSourceVO"%>
<%@page import="com.serotonin.mango.vo.dataSource.internal.InternalPointLocatorVO"%>
<script type="text/javascript">
  function saveDataSourceImpl() {
      DataSourceEditDwr.saveInternalDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
    		  $get("updatePeriodType"), saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.internal.attribute"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
              function(p) { return p.pointLocator.configurationDescription; };
  }
  
  function editPointCBImpl(locator) {
      $set("attributeId", locator.attributeId);
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      delete locator.dataTypeId;
      delete locator.relinquishable;
      
      locator.attributeId = $get("attributeId");
      
      DataSourceEditDwr.saveInternalPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.internal.desc"/></c:set>
<c:set var="dsHelpId" value="internalDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
      <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
        <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="internalPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.internal.attribute"/></td>
    <td class="formField">
      <select id="attributeId">
        <tag:exportCodesOptions optionList="<%= InternalPointLocatorVO.ATTRIBUTE_CODES.getIdKeys() %>"/>
      </select>
    </td>
  </tr>
</tag:pointList>