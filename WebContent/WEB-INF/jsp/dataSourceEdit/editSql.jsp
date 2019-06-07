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

<script type="text/javascript">
  function initImpl() {
      sqlTestButton(false);
      rowBasedQueryChange();
  }
  
  function sqlTest() {
      $set("sqlTestError", "<fmt:message key="dsEdit.sql.testing"/>");
      show("sqlTestError");
      sqlTestButton(true);
      hide("sqlTestResults");
      dwr.util.removeAllRows("sqlTestResults");
      DataSourceEditDwr.sqlTestStatement($get("driverClassname"), $get("connectionUrl"), $get("username"), 
              $get("password"), $get("selectStatement"), $get("rowBasedQuery"), sqlTestCB);
  }
  
  function sqlTestCB() {
      setTimeout(sqlTestUpdate, 1000);
  }
  
  function sqlTestUpdate() {
      DataSourceEditDwr.sqlTestStatementUpdate(sqlTestUpdateCB);
  }
  
  function sqlTestUpdateCB(result) {
      if (result) {
          if (result.error)
              $set("sqlTestError", result.error);
          else {
              hide("sqlTestError");
              var tbody, td, tr, r, c;
              tbody = $("sqlTestResults");
              tr = document.createElement("tr");
              tr.className = "smRowHeader";
              tbody.appendChild(tr);
              for (c=0; c<result.resultTable[0].length; c++) {
                  td = document.createElement("td");
                  $set(td, result.resultTable[0][c]);
                  tr.appendChild(td);
              }
              
              for (r=1; r<result.resultTable.length; r++) {
                  tr = document.createElement("tr");
                  tr.className = "smRow"+ (r % 2 == 0 ? "" : "Alt");
                  tbody.appendChild(tr);
                  for (c=0; c<result.resultTable[r].length; c++) {
                      td = document.createElement("td");
                      $set(td, result.resultTable[r][c]);
                      tr.appendChild(td);
                  }
              }
              
              show(tbody);
          }
          sqlTestButton(false);
      }
      else
          sqlTestCB();
  }
  
  function sqlTestButton(testing) {
      setDisabled($("sqlTestBtn"), testing);
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveSqlDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"), $get("driverClassname"), $get("connectionUrl"), $get("username"),
              $get("password"), $get("selectStatement"), $get("rowBasedQuery"), saveDataSourceCB);
  }
  
  function writePointListImpl(points) {
      var rowBasedQuery = $("rowBasedQuery");
      if (!points || points.length == 0) {
          setDisabled(rowBasedQuery, false);
          hide("rowBasedQueryWarning");
      }
      else {
          setDisabled(rowBasedQuery, true);
          show("rowBasedQueryWarning");
      }
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = function(td) { td.id = "fieldNameTitle"; };
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.fieldName; };
  }
  
  function editPointCBImpl(locator) {
      $set("fieldName", locator.fieldName);
      $set("timeOverrideName", locator.timeOverrideName);
      $set("updateStatement", locator.updateStatement);
      $set("dataTypeId", locator.dataTypeId);
  }
  
  function savePointImpl(locator) {
      delete locator.settable;
      
      locator.fieldName = $get("fieldName");
      locator.timeOverrideName = $get("timeOverrideName");
      locator.updateStatement = $get("updateStatement");
      locator.dataTypeId = $get("dataTypeId");
      
      DataSourceEditDwr.saveSqlPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function rowBasedQueryChange() {
      if ($get("rowBasedQuery")) {
          $set("fieldNameLabel", "<fmt:message key="dsEdit.sql.rowId"/>");
          $set("fieldNameTitle", "<fmt:message key="dsEdit.sql.rowId"/>");
          hide("columnBasedProperties");
      }
      else {
          $set("fieldNameLabel", "<fmt:message key="dsEdit.sql.columnName"/>");
          $set("fieldNameTitle", "<fmt:message key="dsEdit.sql.columnName"/>");
          show("columnBasedProperties");
      }
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.sql.desc"/></c:set>
<c:set var="dsHelpId" value="sqlDS"/>
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
          <td class="formLabelRequired"><fmt:message key="dsEdit.sql.driverClassName"/></td>
          <td class="formField"><input id="driverClassname" type="text" value="${dataSource.driverClassname}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.sql.connectionString"/></td>
          <td class="formField"><input id="connectionUrl" type="text" value="${dataSource.connectionUrl}"
                  class="formLong"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.sql.username"/></td>
          <td class="formField"><input id="username" type="text" value="${dataSource.username}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.sql.password"/></td>
          <td class="formField"><input id="password" type="text" value="${dataSource.password}"/></td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.sql.select"/></td>
          <td class="formField">
            <textarea id="selectStatement" rows="10" cols="45">${dataSource.selectStatement}</textarea>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.sql.rowQuery"/></td>
          <td class="formField">
            <sst:checkbox id="rowBasedQuery" selectedValue="${dataSource.rowBasedQuery}"
                    onclick="rowBasedQueryChange()"/>
            <span id="rowBasedQueryWarning" style="display:none"><tag:img png="warn" title="dsEdit.sql.deleteWarn"/></span>
          </td>
        </tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table cellspacing="1">
        <tr><td class="smallTitle"><fmt:message key="dsEdit.sql.test"/></td></tr>
        
        <tr>
          <td align="center">
            <input id="sqlTestBtn" type="button" value="<fmt:message key="dsEdit.sql.execute"/>" onclick="sqlTest();"/>
          </td>
        </tr>
        
        <tr><td id="sqlTestError"></td></tr>
        <tbody id="sqlTestResults"></tbody>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="sqlPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr>
    <td id="fieldNameLabel" class="formLabelRequired"></td>
    <td class="formField"><input type="text" id="fieldName"/></td>
  </tr>
  
  <tbody id="columnBasedProperties">
    <tr>
      <td class="formLabel"><fmt:message key="dsEdit.sql.timeColumn"/></td>
      <td class="formField"><input type="text" id="timeOverrideName"/></td>
    </tr>
  </tbody>
  
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.sql.update"/></td>
    <td class="formField"><textarea cols="35" rows="4" name="updateStatement"></textarea></td>
  </tr>
</tag:pointList>