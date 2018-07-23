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
	  setDisabled("objectNamesBtn", false);
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveJmxDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("useLocalServer"),
    		  $get("remoteServerAddr"), $get("updatePeriodType"), $get("updatePeriods"), $get("quantize"),
    		  saveDataSourceCB);
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.jmx.attributeName"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] =
              function(p) { return p.pointLocator.configurationDescription; };
  }
  
  function editPointCBImpl(locator) {
      $set("objectName", locator.objectName);
      $set("attributeName", locator.attributeName);
      $set("compositeItemName", locator.compositeItemName);
      $set("dataTypeId", locator.dataTypeId);
      $set("settable", locator.settable);
  }
  
  function savePointImpl(locator) {
	  delete locator.relinquishable;
      
      locator.objectName = $get("objectName");
      locator.attributeName = $get("attributeName");
      locator.compositeItemName = $get("compositeItemName");
      locator.dataTypeId = $get("dataTypeId");
      locator.settable = $get("settable");
      
      DataSourceEditDwr.saveJmxPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
  
  function useLocalServerChange() {
      setDisabled("remoteServerAddr", $get("useLocalServer"));
  }
  
  function getObjectNames() {
      $set("objectNamesMessage", "<fmt:message key="dsEdit.jmx.gettingObjectNames"/>");
      setDisabled("objectNamesBtn", true);
      
      var tree = dojo.widget.manager.getWidgetById("inspectionTree");
      
      // Remove all of the old results.
      while (tree.children.length > 0)
          tree.removeNode(tree.children[0]);
      
      var localServer = $get("useLocalServer");
      DataSourceEditDwr.getJmxObjectNames(localServer, $get("remoteServerAddr"), function(response) {
          setDisabled("objectNamesBtn", false);
    	  if (response.hasMessages)
              $set("objectNamesMessage", response.messages[0].genericMessage);
    	  else {
              $set("objectNamesMessage");
    		  
              var parentNode = dojo.widget.createWidget("TreeNode", {
            	  title: "<b>"+ (localServer ? "<fmt:message key="dsEdit.jmx.dsconn.local"/>" : $("remoteServerAddr")) +"</b>",
            	  isFolder: true
              });
    		  tree.addChild(parentNode);
    		  
    		  for (var name in response.data.names) {
    			  var obj = response.data.names[name];
                  var objNode = dojo.widget.createWidget("TreeNode", { title: name, isFolder: true});
                  parentNode.addChild(objNode);
                  
                  for (var ai=0; ai<obj.length; ai++) {
                	  var attr = obj[ai];
                      var attrNode; 
                      if (!attr.items) {
                    	  var func = "preAddPoint(\""+ name +"\", \""+ attr.name +"\", \"\")";
                          attrNode = dojo.widget.createWidget("TreeNode", { 
                              title: attr.name +"("+ attr.type +") "+ writeImageSQuote(null, null,
                                      "icon_comp_add", "<fmt:message key="dsEdit.jmx.addPoint"/>", func),
                              isFolder: false
                          });
                          objNode.addChild(attrNode);
                      }
                      else {
                          attrNode = dojo.widget.createWidget("TreeNode", { 
                              title: attr.name +"("+ attr.type +")",
                              isFolder: true
                          });
                          objNode.addChild(attrNode);
                    	  
                          for (var ii=0; ii<attr.items.length; ii++) {
                        	  var item = attr.items[ii];
                              var func = "preAddPoint(\""+ name +"\", \""+ attr.name +"\", \""+ item.name +"\")";
                              var itemNode = dojo.widget.createWidget("TreeNode", { 
                                  title: item.name +"("+ item.type +") "+ writeImageSQuote(null, null,
                                          "icon_comp_add", "<fmt:message key="dsEdit.jmx.addPoint"/>", func),
                                  isFolder: false
                              });
                              
                              attrNode.addChild(itemNode);
                          }
                      }
                  }
    		  }
    		      
   		      parentNode.expand();
    	  }
      });
  }
  
  var addPointData;
  function preAddPoint(objectName, attrName, compositeName) {
      addPointData = {
              objectName: objectName,
              attrName: attrName,
              compositeName: compositeName
      };
      
      addPoint();
  }
  
  function addPointImpl() {
      DataSourceEditDwr.getPoint(-1, function(point) {
          editPointCB(point);
          $set("objectName", addPointData.objectName);
          $set("attributeName", addPointData.attrName);
          $set("compositeItemName", addPointData.compositeName);
      });
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.jmx.desc"/></c:set>
<c:set var="dsHelpId" value="jmxDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.useLocalServer"/></td>
          <td class="formField">
            <sst:checkbox id="useLocalServer" selectedValue="${dataSource.useLocalServer}" onclick="useLocalServerChange()"/>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.remoteServerAddr"/></td>
          <td class="formField"><input id="remoteServerAddr" type="text" value="${dataSource.remoteServerAddr}"/></td>
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
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
          <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
        </tr>
      </table>
      
      <tag:dsEvents/>
    </div>
  </td>
  
  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.jmx.inspect"/></td></tr>
        <tr>
          <td colspan="2" align="center">
            <input id="objectNamesBtn" type="button" value="<fmt:message key="dsEdit.jmx.getObjectNames"/>" onclick="getObjectNames();"/>
          </td>
        </tr>
        
        <tr><td colspan="2" id="objectNamesMessage" class="formError"></td></tr>
        
        <tbody id="inspectionDetails">
          <tr><td colspan="2"><div dojoType="Tree" toggle="wipe" widgetId="inspectionTree"></div></td></tr>
        </tbody>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="jmxPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.objectName"/></td>
    <td class="formField"><input type="text" id="objectName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.attributeName"/></td>
    <td class="formField"><input type="text" id="attributeName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.jmx.compositeItemName"/></td>
    <td class="formField"><input type="text" id="compositeItemName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeImage="true" excludeMultistate="true"/>
      </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
</tag:pointList>