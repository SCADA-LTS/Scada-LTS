<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>
<%@page import="com.serotonin.modbus4j.code.RegisterRange"%>
<%@page import="com.serotonin.modbus4j.code.DataType"%>

<script type="text/javascript"><!--
  function initImpl() {
	  hide("console");
	  hide("editImg-1");
	  if (!newDataSource())
		 searchServer();
	  document.getElementById('addDiv').style.display = "none";
}

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
  } 

  function deleteOPCPoint(pointId) {
      alert(pointId);
  }
  
  var cellFuncs = [
          function(data) { return data.tag; },
          function(data) {
              if (data.dataType == ${applicationScope['constants.DataTypes.BINARY']})
                  return "<fmt:message key="common.dataTypes.binary"/>";
              if (data.dataType == ${applicationScope['constants.DataTypes.MULTISTATE']})
                  return "<fmt:message key="common.dataTypes.multistate"/>";
              if (data.dataType == ${applicationScope['constants.DataTypes.NUMERIC']})
                  return "<fmt:message key="common.dataTypes.numeric"/>";
              if (data.dataType == ${applicationScope['constants.DataTypes.ALPHANUMERIC']})
                  return "<fmt:message key="common.dataTypes.alphanumeric"/>";
              if (data.dataType == 7)
                      return "<fmt:message key="common.dataTypes.bad"/>";

              return "<fmt:message key="common.unknown"/> ("+ data.dataType +")";
          },
          function(data) { return data.settable; },
          function(data) { return "<input type='checkbox' name='addTag'/>"; }
  ];

  function editPointCBImpl(locator) {
	  hide('pointSaveImg');
  }

  function searchServer() {
      DataSourceEditDwr.searchOpcServer($get("host"), $get("domain"), $get("user"), $get("password"), function(response) {
    	  if (response.hasMessages)
              $set("console", response.messages[0]);
    	  else {
              $set("console");
              
              dwr.util.removeAllOptions("server");
              dwr.util.addOptions("server", response);
              //dwr.util.addOptions("server", response.data.servers);
              
              if (!newDataSource()) {
                  var server = '${dataSource.server}';
                  serverList = $('server');
                  for (index = 0; index < serverList.length; index++) {
                      if (serverList[index].value == server)
                          serverList.selectedIndex = index;
                  }
              }
    	  }
      });
  }

  function newDataSource() {
	  if (${dataSource.id}!= -1) {
		  return false; 
	  } 
	  return true;
  }

  function saveDataSourceImpl() {
	  DataSourceEditDwr.saveOPCDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("host"), $get("domain"), 
			  $get("user"), $get("password"), $get("server"), $get("updatePeriods"), $get("updatePeriodType"), $get("quantize"),
			  saveDataSourceCB);
  }

  function savePointImpl(locator) {
  }
  
  function browseTags() {
      dwr.util.removeAllRows('tagsTable');
	  if ($get("server") != '') {
		  DataSourceEditDwr.listOPCTags($get("host"), $get("domain"), $get("user"), $get("password"), $get("server"),
				  function(tagList) {
			          dwr.util.addRows('tagsTable', tagList, cellFuncs, { escapeHtml:false });
			      }
		  );
      }
  }

  function addTags() {
	  DataSourceEditDwr.getPoint(-1, addTagsImpl);
  }
  
  function addTagsImpl(point) {
	  list = document.getElementById('tagsTable');
	  var locator = point.pointLocator;
      
      // Prevents DWR warnings
      delete locator.configurationDescription;
      delete locator.dataTypeMessage;
      delete locator.relinquish;
      
	  var tags = new Array();
	  var dataTypes = new Array();
	  var settables = new Array();
	  var locators = new Array();
	  for (var i = 0; i < list.rows.length; i++) {
		  if (list.rows[i].cells[3].firstChild.checked) { 
			  tags.push(list.rows[i].cells[0].innerHTML);
			  
			  // TODO Not good. What is put into innerHTML is not always what comes out.
			  if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.binary"/>") {
	           	  dataTypes.push(1);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.multistate"/>") {
    			  dataTypes.push(2);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.numeric"/>") {
    			  dataTypes.push(3);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.alphanumeric"/>") {
    			  dataTypes.push(4);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.unknown"/>") {
    			  dataTypes.push(0);
    		  }
    		  
    		  if (list.rows[i].cells[2].innerHTML == "false") {
    			  settables.push(false);
    		  } else {
    			  settables.push(true);
    		  }
			  locators.push(locator);
			  
		  }
	  }
	  
	  if (locators.length > 0) {
		  DataSourceEditDwr.saveMultipleOPCPointLocator(tags, dataTypes, settables, locators, "addBtn", savePointCB);
	  } 
  }
  
  function btnAddTag() {
	  DataSourceEditDwr.getPoint(-1, addTagsImplOPC);
  }

  function addTagsImplOPC(point) {
	  list = document.getElementById('addTagsTable');
	  var locator = point.pointLocator;
      
      // Prevents DWR warnings
      delete locator.configurationDescription;
      delete locator.dataTypeMessage;
      delete locator.relinquish;

	  var tags = new Array();
	  var dataTypes = new Array();
	  var settables = new Array();
	  var locators = new Array();

	  for (var i = 0; i < list.rows.length; i++) {
		var check = list.rows[i].getElementsByTagName('input');
		if (check[0] == null){
			continue;
		}
		
		check = check[0].checked;
		
		if (check == true) { 
			  tags.push(list.rows[i].cells[0].innerHTML);
			  
			  // TODO Not good. What is put into innerHTML is not always what comes out.
			  if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.binary"/>") {
	           	  dataTypes.push(1);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.multistate"/>") {
    			  dataTypes.push(2);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.numeric"/>") {
    			  dataTypes.push(3);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.dataTypes.alphanumeric"/>") {
    			  dataTypes.push(4);
    		  } else if (list.rows[i].cells[1].innerHTML == "<fmt:message key="common.unknown"/>") {
    			  dataTypes.push(0);
    		  }
    		  
    		  if (list.rows[i].cells[2].innerHTML == "false") {
    			  settables.push(false);
    		  } else {
    			  settables.push(true);
    		  }
			  locators.push(locator);
			  
		  }
	  }
	  
	  if (locators.length > 0) {
		  DataSourceEditDwr.saveMultipleOPCPointLocator(tags, dataTypes, settables, locators, "btnAddTag", savePointCB);
	  } 
  }
  
  function validateTag() {

	  if($get("tagName") != ''){

		list = document.getElementById('addTagsTable');
		var existTag = false ;
		for (var i = 0; i < list.rows.length; i++) {
			if (list.rows[i].cells[0].innerHTML == $get("tagName")){
				existTag = true;
				break;
			}
		}

		if (existTag == false) {

		DataSourceEditDwr.validateOPCTag($get("tagName"), $get("user"), $get("password"),
	 			$get("host"), $get("domain"), $get("server"), function(response) {
			
	 			var tbody = document.getElementById('addTagsTable');
	 			var row = document.createElement("TR");
	 			var td1 = document.createElement("TD");
	 			td1.setAttribute("align","center");
				text1 = document.createTextNode($get("tagName"));
				td1.appendChild(text1);

				var td2 = document.createElement("TD");
				td2.setAttribute("align","center");
				var text2 = "";
				if(response.dataType == ${applicationScope['constants.DataTypes.BINARY']}){
					text2 = document.createTextNode("<fmt:message key="common.dataTypes.binary"/>");
				}
				if(response.dataType == ${applicationScope['constants.DataTypes.NUMERIC']}){
					text2 = document.createTextNode("<fmt:message key="common.dataTypes.numeric"/>");
				}
				if(response.dataType == ${applicationScope['constants.DataTypes.ALPHANUMERIC']}){
					text2 = document.createTextNode("<fmt:message key="common.dataTypes.alphanumeric"/>");
				}
				if(response.dataType == 6) {
					text2 = document.createTextNode("<fmt:message key="common.unknown"/>");
				}
				td2.appendChild(text2);

	 			var td3 = document.createElement("TD");
	 			td3.setAttribute("align","center");
	 			var text3 = "";

				var td4 = document.createElement("TD");
	 			td4.setAttribute("align","center");
	 			img = document.createElement("IMG");

	 			if (response.validate == true) {
	 				img.setAttribute("src","images/accept.png");
	 				img.setAttribute("title","<fmt:message key="dsEdit.opc.TagValidated"/>")
	 				text3 = document.createTextNode("true");
				}
	 			else {
	 				img.setAttribute("src","images/cancel.png");
	 				img.setAttribute("title","<fmt:message key="dsEdit.opc.TagNotValidated"/>")
	 				text3 = document.createTextNode("false");
		 		}

				td3.appendChild(text3);
	 			td4.appendChild(img);

	 			var td5 = document.createElement("TD");
	 			td5.setAttribute("align","center");
	 			var input = document.createElement("INPUT");
	 			input.type = 'checkbox'
	 	 		input.name = 'addTag'
				td5.appendChild(input)

				row.appendChild(td1);
	 			row.appendChild(td2);
	 			row.appendChild(td3);
	 			row.appendChild(td4);
	 			row.appendChild(td5);
	 			tbody.appendChild(row);
	 	});
		}
	}
 }

function toggleDiv(elem) {
	    if (elem.value == 'BrowseTags') {
			document.getElementById('addDiv').style.display = "none";
			document.getElementById('browseDiv').style.display = "block";
		}

		if (elem.value == 'AddTags') {
			document.getElementById('browseDiv').style.display = "none";
			document.getElementById('addDiv').style.display = "block";
		}
}
  
--></script>

<c:set var="dsDesc">
	<fmt:message key="dsEdit.opc.desc" />
</c:set>
<c:set var="dsHelpId" value="opcDS" />
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf"%>
<tr>
	<td class="formLabelRequired"><fmt:message key="dsEdit.opc.host" /></td>
	<td class="formField"><input id="host" type="text"
		value="${dataSource.host}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message key="dsEdit.opc.domain" /></td>
	<td class="formField"><input id="domain" type="text"
		value="${dataSource.domain}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message key="dsEdit.opc.user" /></td>
	<td class="formField"><input id="user" type="text"
		value="${dataSource.user}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
		key="dsEdit.opc.password" /></td>
	<td class="formField"><input id="password" type="password"
		name="password" value="${dataSource.password}" maxlength="20" /></td>
</tr>
<tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
			key="dsEdit.opc.server" /></td>
		<td class="formField"><sst:select id="server" value=""></sst:select>
		<div style="height: 2px;"></div>
		<input id="searchBtn" type="button"
			value="<fmt:message key="dsEdit.opc.refreshServers"/>"
			onclick="searchServer();" /></td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
			key="dsEdit.updatePeriod" /></td>
		<td class="formField"><input type="text" id="updatePeriods"
			value="${dataSource.updatePeriods}" class="formShort" /> <sst:select
			id="updatePeriodType" value="${dataSource.updatePeriodType}">
			<tag:timePeriodOptions sst="true" ms="true" s="true" min="true"
				h="true" />
		</sst:select></td>
	</tr>

	<tr>
		<td class="formLabelRequired"><fmt:message
			key="dsEdit.opc.creationMode" /></td>
		<td class="formField"><sst:select id="selectMethodTag"
			value="${dataSource.creationMode}" onchange="toggleDiv(this)">
			<sst:option value="BrowseTags">
				<fmt:message key="dsEdit.opc.browseTags" />
			</sst:option>
			<sst:option value="AddTags">
				<fmt:message key="dsEdit.opc.addTags" />
			</sst:option>
		</sst:select></td>
	</tr>

	<tr>
		<td class="formLabelRequired"><fmt:message key="dsEdit.quantize" /></td>
		<td class="formField"><sst:checkbox id="quantize"
			selectedValue="${dataSource.quantize}" /></td>
	</tr>
	</table>
	</div>
	</td>

	<td valign="top">
	<div id="browseDiv" class="borderDiv marB marR">
	<table>
		<tr>
			<td colspan="2" class="smallTitle"><fmt:message
				key="dsEdit.opc.tagList" /></td>
		</tr>

		<tr>
			<td colspan="2" align="center"><input id="browseTags"
				type="button" value="<fmt:message key="dsEdit.opc.browseTags"/>"
				onclick="browseTags();" /> <input id="addBtn" type="button"
				value="<fmt:message key="dsEdit.opc.addTags"/>" onclick="addTags();" />
			</td>
		</tr>

		<tr>
			<td colspan="2" id="tagsMessage" class="formError"></td>
		</tr>
		<tr>
			<td>
			<table cellspacing="1" cellpadding="0" border="0">
				<thead class="rowHeader">
					<td align="center"><fmt:message key="dsEdit.opc.tag" /></td>
					<td align="center"><fmt:message key="dsEdit.pointDataType" /></td>
					<td align="center"><fmt:message key="dsEdit.settable" /></td>
					<td align="center"><fmt:message key="common.add" /></td>
				</thead>

				<!-- TODO why is the height being enforced? -->
				<tbody id="tagsTable" style="height: 160px; overflow: auto;"></tbody>
			</table>
			</td>
		</tr>

		<tr>
			<td>
			<div id="console"></div>
			</td>
		</tr>

	</table>
	</div>
	</td>

	<!-- addTagsMethod -->

	<td valign="top">
	<div id="addDiv" class="borderDiv marB marR">
	<table>
		<tr>
			<td colspan="2" class="smallTitle"><fmt:message
				key="dsEdit.opc.addTags" /></td>
		</tr>

		<tr>
			<td colspan="2" align="center"><fmt:message
				key="dsEdit.opc.tagName" /> <input id="tagName" type="text" /> <input
				id="btnAddTag" type="button"
				value="<fmt:message key="dsEdit.opc.validateTag"/>"
				onclick="validateTag();" /></td>
			</td>
		</tr>

		<tr>
			<td colspan="2" id="tagsMessage" class="formError"></td>
		</tr>

		<tr>
			<td>
			<table cellspacing="1" cellpadding="0" border="0">
				<thead class="rowHeader">
					<td align="center"><fmt:message key="dsEdit.opc.tag" /></td>
					<td align="center"><fmt:message key="dsEdit.pointDataType" /></td>
					<td align="center"><fmt:message key="dsEdit.settable" /></td>
					<td align="center"><fmt:message key="dsEdit.opc.validation" /></td>
					<td align="center"><fmt:message key="common.add" /></td>
				</thead>

				<!-- TODO why is the height being enforced? -->
				<tbody id="addTagsTable" style="height: 160px; overflow: auto;"></tbody>
			</table>
			</td>
		</tr>

		<tr>
			<td colspan="2" align="center"><input id="btnAddTag"
				type="button" value="<fmt:message key="dsEdit.opc.addTags"/>"
				onclick="btnAddTag();" /></td>
		</tr>

		<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf"%>

		<tag:pointList pointHelpId="opcPP"></tag:pointList>