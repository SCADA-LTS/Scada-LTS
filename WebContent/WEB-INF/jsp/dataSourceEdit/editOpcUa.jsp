<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>

<%@page import="br.org.scadabr.KeyStoreType"%>
<%@page import="br.org.scadabr.vo.dataSource.opcua.OpcUaDataType"%>
<%@page import="br.org.scadabr.vo.dataSource.opcua.OpcUaIdentifierType"%>
<%@page import="br.org.scadabr.vo.dataSource.opcua.OpcUaMessageSecurityType"%>
<%@page import="br.org.scadabr.vo.dataSource.opcua.OpcUaSecurityPolicyType"%>

<script type="text/javascript"><!--
  function initImpl() {
	  hide("editImg-1");
	  if (!newDataSource())
		 searchServer();
}

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
  } 

  function deleteOPCPoint(pointId) {
      alert(pointId);
  }
  
  let cellFuncs = [
          function(data) { return data.tag; },
          function(data) {
              if (data.dataType == ${applicationScope['constants.DataTypes.BINARY']})
                  return "<spring:message code="common.dataTypes.binary"/>";
              if (data.dataType == ${applicationScope['constants.DataTypes.MULTISTATE']})
                  return "<spring:message code="common.dataTypes.multistate"/>";
              if (data.dataType == ${applicationScope['constants.DataTypes.NUMERIC']})
                  return "<spring:message code="common.dataTypes.numeric"/>";
              if (data.dataType == ${applicationScope['constants.DataTypes.ALPHANUMERIC']})
                  return "<spring:message code="common.dataTypes.alphanumeric"/>";
              if (data.dataType == 7)
                      return "<spring:message code="common.dataTypes.bad"/>";

              return "<spring:message code="common.unknown"/> ("+ data.dataType +")";
          },
          function(data) { return data.settable; },
          function(data) { return "<input type='checkbox' name='addTag'/>"; }
  ];

  function editPointCBImpl(locator) {
	  hide('pointSaveImg');
  }

  function searchServer() {
      let dataSource = createDataSource();
      DataSourceEditDwr.searchServerOpcUa(dataSource, function(response) {
    	  if (response.hasMessages) {
    	      let messages = response.messages;
              document.getElementById("console").textContent = messages[0].contextualMessage;
    	  } else {
    	      document.getElementById("console").textContent = '';

              let serverList = response.data.serverList;

              dwr.util.removeAllOptions("serverName");
              dwr.util.addOptions("serverName", serverList);
              
              if (!newDataSource()) {
                  let server = '${dataSource.serverName}';
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

  function createDataSource() {
        let dataSourceToSave = new Object();
        dataSourceToSave.id=${dataSource.id};
        dataSourceToSave.enabled=${dataSource.enabled};
        dataSourceToSave.name=$get("dataSourceName");
        dataSourceToSave.xid=$get("dataSourceXid");
        dataSourceToSave.updatePeriods=$get("updatePeriods");
        dataSourceToSave.updatePeriodType=$get("updatePeriodType");
        dataSourceToSave.serverHost=$get("serverHost");
        dataSourceToSave.serverPort=$get("serverPort");
        dataSourceToSave.serverPath=$get("serverPath");
        dataSourceToSave.serverName=$get("serverName");
        dataSourceToSave.user=$get("user");
        dataSourceToSave.password=$get("password");
        dataSourceToSave.quantize=$get("quantize");
        dataSourceToSave.messageSecurity=$get("messageSecurityType");
        dataSourceToSave.securityPolicy=$get("securityPolicyType");
        dataSourceToSave.discovery=$get("discovery");
        dataSourceToSave.keyStoreFile=$get("keyStoreFile");
        dataSourceToSave.keyStoreType=$get("keyStoreType");
        dataSourceToSave.keyStorePassword=$get("keyStorePassword");
        dataSourceToSave.serverCertificateFile=$get("serverCertificateFile");
        dataSourceToSave.trustStoreFile=$get("trustStoreFile");
        dataSourceToSave.trustStoreType=$get("trustStoreType");
        dataSourceToSave.trustStorePassword=$get("trustStorePassword");
        dataSourceToSave.channelLifetime=$get("channelLifetime");
        dataSourceToSave.sessionTimeout=$get("sessionTimeout");
        dataSourceToSave.negotiationTimeout=$get("negotiationTimeout");
        dataSourceToSave.requestTimeout=$get("requestTimeout");
        dataSourceToSave.receiveBufferSize=$get("receiveBufferSize");
        dataSourceToSave.sendBufferSize=$get("sendBufferSize");
        dataSourceToSave.maxMessageSize=$get("maxMessageSize");
        dataSourceToSave.maxChunkCount=$get("maxChunkCount");
        dataSourceToSave.keepAlive=$get("keepAlive");
        dataSourceToSave.noDelay=$get("noDelay");
        dataSourceToSave.defaultTimeout=$get("defaultTimeout");

        return dataSourceToSave;
  }

  function saveDataSourceImpl() {

      let dataSourceToSave = createDataSource();
	  DataSourceEditDwr.saveOpcUaDataSource(dataSourceToSave, saveDataSourceCB);
  }


  function savePointImpl(locator) {
    console.log('locator: ', locator);
    delete locator.relinquishable;
    locator.settable = $get("settable");
    locator.dataType = $get("dataType");
    locator.tag = $get("tag");
    locator.identifierType = $get("identifierType");
    locator.identifier = $get("identifier");
    locator.namespaceIndex = $get("namespaceIndex");
    locator.attributes = $get("attributes");

    DataSourceEditDwr.saveOpcUaPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("tag", locator.tag);
        $set("identifier", locator.identifier);
        $set("namespaceIndex", locator.namespaceIndex);
        $set("attributes", locator.attributes);

        let identifierType = document.getElementById("identifierType");
        for (let i = 0; i < identifierType.options.length; i++) {
            identifierType.options[i].selected = identifierType.options[i].value == locator.identifierType;
        }

        let dataType = document.getElementById("dataType");
        for (let i = 0; i < dataType.options.length; i++) {
            dataType.options[i].selected = dataType.options[i].value == locator.dataType;
        }
  }
  
  function browseTags() {
      dwr.util.removeAllRows('tagsTable');
	  if ($get("serverName") != '') {
		  DataSourceEditDwr.listOpcUaTags($get("serverHost"), $get("serverPort"), $get("user"), $get("password"), $get("serverName"),
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
	  let locator = point.pointLocator;

      // Prevents DWR warnings
      delete locator.configurationDescription;
      delete locator.dataTypeMessage;
      delete locator.relinquish;

	  let locators = new Array();
	  for (let i = 0; i < list.rows.length; i++) {
		let check = list.rows[i].getElementsByTagName('input');
		if (check[0] == null){
			continue;
		}

		check = check[0].checked;

		if (check == true) {
		      let copyLocator = JSON.parse(JSON.stringify(locator));
		      copyLocator.tag = list.rows[i].cells[0].innerHTML;
			  copyLocator.dataType = list.rows[i].cells[1].innerHTML;
			  copyLocator.identifier = list.rows[i].cells[2].innerHTML;
              copyLocator.identifierType = list.rows[i].cells[3].innerHTML;
			  copyLocator.namespaceIndex = list.rows[i].cells[4].innerHTML;
			  copyLocator.settable = list.rows[i].cells[5].innerHTML;
			  locators.push(copyLocator);

		  }
	  }
	  
	  if (locators.length > 0) {
		  DataSourceEditDwr.saveMultipleOpcUaPointLocator(locators, "addBtn", savePointCB);
	  } 
  }
  
  function btnAddTag() {
	  DataSourceEditDwr.getPoint(-1, addTagsImplOpcUa);
  }

  function addTagsImplOpcUa(point) {
	  list = document.getElementById('addTagsTable');
	  let locator = point.pointLocator;
      
      // Prevents DWR warnings
      delete locator.configurationDescription;
      delete locator.dataTypeMessage;
      delete locator.relinquish;

	  let locators = new Array();
	  for (let i = 0; i < list.rows.length; i++) {
		let check = list.rows[i].getElementsByTagName('input');
		if (check[0] == null){
			continue;
		}
		
		check = check[0].checked;
		
		if (check == true) {
		      let copyLocator = JSON.parse(JSON.stringify(locator));
		      copyLocator.tag = list.rows[i].cells[0].innerHTML;
			  copyLocator.dataType = list.rows[i].cells[1].innerHTML;
			  copyLocator.namespaceIndex = list.rows[i].cells[2].innerHTML;
              copyLocator.identifier = list.rows[i].cells[3].innerHTML;
              copyLocator.identifierType = list.rows[i].cells[4].innerHTML;
			  copyLocator.settable = list.rows[i].cells[5].innerHTML;
			  locators.push(copyLocator);

		}
	  }
	  
	  if (locators.length > 0) {
		  DataSourceEditDwr.saveMultipleOpcUaPointLocator(locators, "btnAddTag", savePointCB);
	  } 
  }
  
  function findTag() {

	  if($get("searchTag") != ''){

		list = document.getElementById('addTagsTable');
		let existTag = false ;
		for (let i = 0; i < list.rows.length; i++) {
		    let finds = list.rows[i].cells[0].innerHTML;
			if (finds == $get("searchTag")) {
				existTag = true;
				document.getElementById("tagsMessage").textContent = "<spring:message code="dsEdit.opcua.tagAlreadyExists"/>";
				break;
			}
		}
		if (existTag == false) {
		      let dataSourceToSave = createDataSource();
		      DataSourceEditDwr.findTagOpcUa(dataSourceToSave, $get("searchTag"),
	 			$get("searchIdentifier"), $get("searchIdentifierType"), $get("searchDataType"), function(response) {
	 			let tag = response.data.tag;
	 			if(response.hasMessages) {
	 			    let messages = response.messages;
	 			    document.getElementById("tagsMessage").textContent = messages[0].contextualMessage;
	 			    return;
	 			} else {
	 			    document.getElementById("tagsMessage").textContent = '';
	 			}
	 			let tbody = document.getElementById('addTagsTable');
	 			let row = document.createElement("TR");
	 			let td1 = document.createElement("TD");
	 			td1.setAttribute("align","center");
				text1 = document.createTextNode($get("searchTag"));
				td1.appendChild(text1);

				let td2 = document.createElement("TD");
				let dataType = document.createTextNode(tag.dataType);
				td2.setAttribute("align","center");
				td2.appendChild(dataType);

                let td21 = document.createElement("TD");
                let namespaceIndex = document.createTextNode(tag.namespaceIndex);
				td21.setAttribute("align","center");
                td21.appendChild(namespaceIndex);

                let td22 = document.createElement("TD");
                let identifier = document.createTextNode(tag.identifier);
				td22.setAttribute("align","center");
                td22.appendChild(identifier);

                let td23 = document.createElement("TD");
                let identifierType = document.createTextNode(tag.identifierType);
                td23.setAttribute("align","center");
                td23.appendChild(identifierType);

	 			let td3 = document.createElement("TD");
	 			td3.setAttribute("align","center");
	 			let text3 = "";

				let td4 = document.createElement("TD");
	 			td4.setAttribute("align","center");
	 			img = document.createElement("IMG");

	 			if (tag.validate == true) {
	 				img.setAttribute("src","images/accept.png");
	 				img.setAttribute("title","<spring:message code="dsEdit.opcua.TagValidated"/>")
	 				text3 = document.createTextNode("true");
				}
	 			else {
	 				img.setAttribute("src","images/cancel.png");
	 				img.setAttribute("title","<spring:message code="dsEdit.opcua.TagNotValidated"/>")
	 				text3 = document.createTextNode("false");
		 		}

				td3.appendChild(text3);
	 			td4.appendChild(img);

	 			let td5 = document.createElement("TD");
	 			td5.setAttribute("align","center");
	 			let input = document.createElement("INPUT");
	 			input.type = 'checkbox'
	 	 		input.name = 'addTag'
				td5.appendChild(input)
				row.appendChild(td1);
	 			row.appendChild(td2);
	 			row.appendChild(td21);
	 			row.appendChild(td22);
	 			row.appendChild(td23);
	 			row.appendChild(td3);
	 			row.appendChild(td4);
	 			row.appendChild(td5);
	 			tbody.appendChild(row);
	 	});
		}
	}
 }

function toggleDiv(elem) {

		if (elem.value == 'AddTags') {

			document.getElementById('addDiv').style.display = "block";
		}
}

--></script>

<c:set var="dsDesc">
	<spring:message code="dsEdit.opcua.desc" />
</c:set>
<c:set var="dsHelpId" value="opcUaDS" />
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf"%>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.serverHost" /></td>
	<td class="formField"><input id="serverHost" type="text"
		value="<c:out value="${dataSource.serverHost}"/>" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.serverPort" /></td>
	<td class="formField"><input id="serverPort" type="number"
		value="${dataSource.serverPort}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.serverPath" /></td>
	<td class="formField"><input id="serverPath" type="text"
		value="<c:out value="${dataSource.serverPath}"/>" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.user" /></td>
	<td class="formField"><input id="user" type="text"
		value="<c:out value="${dataSource.user}"/>" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message
		code="dsEdit.opcua.password" /></td>
	<td class="formField"><input id="password" type="password"
		name="password" value="${dataSource.password}" maxlength="20" /></td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.messageSecurityType"/></td>
    <td class="formField">
        <select id="messageSecurityType">
            <option value="<c:out value="<%= OpcUaMessageSecurityType.NONE %>"/>" ${dataSource.messageSecurity.name() == 'NONE' ? 'selected' : ''} ><c:out value='<%= OpcUaMessageSecurityType.NONE.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaMessageSecurityType.SIGN %>"/>" ${dataSource.messageSecurity.name() == 'SIGN' ? 'selected' : ''} ><c:out value='<%= OpcUaMessageSecurityType.SIGN.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaMessageSecurityType.SIGN_ENCRYPT %>"/>" ${dataSource.messageSecurity.name() == 'SIGN_ENCRYPT' ? 'selected' : ''} ><c:out value='<%= OpcUaMessageSecurityType.SIGN_ENCRYPT.getDescription() %>'/></option>
        </select>
    </td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.securityPolicyType"/></td>
    <td class="formField">
        <select id="securityPolicyType">
            <option value="<c:out value="<%= OpcUaSecurityPolicyType.NONE %>"/>" ${dataSource.securityPolicy.name() == 'NONE' ? 'selected' : ''} ><c:out value='<%= OpcUaSecurityPolicyType.NONE.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaSecurityPolicyType.BASIC_128_RSA_15 %>"/>" ${dataSource.securityPolicy.name() == 'BASIC_128_RSA_15' ? 'selected' : ''} ><c:out value='<%= OpcUaSecurityPolicyType.BASIC_128_RSA_15.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaSecurityPolicyType.BASIC_256 %>"/>" ${dataSource.securityPolicy.name() == 'BASIC_256' ? 'selected' : ''} ><c:out value='<%= OpcUaSecurityPolicyType.BASIC_256.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaSecurityPolicyType.BASIC_256_SHA_256 %>"/>" ${dataSource.securityPolicy.name() == 'BASIC_256_SHA_256' ? 'selected' : ''} ><c:out value='<%= OpcUaSecurityPolicyType.BASIC_256_SHA_256.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaSecurityPolicyType.AES_128_SHA_256_RSA_OAEP %>"/>" ${dataSource.securityPolicy.name() == 'AES_128_SHA_256_RSA_OAEP' ? 'selected' : ''} ><c:out value='<%= OpcUaSecurityPolicyType.AES_128_SHA_256_RSA_OAEP.getDescription() %>'/></option>
            <option value="<c:out value="<%= OpcUaSecurityPolicyType.AES_256_SHA_256_RSA_PSS %>"/>" ${dataSource.securityPolicy.name() == 'AES_256_SHA_256_RSA_PSS' ? 'selected' : ''} ><c:out value='<%= OpcUaSecurityPolicyType.AES_256_SHA_256_RSA_PSS.getDescription() %>'/></option>
        </select>
    </td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.serverCertificateFile" /></td>
	<td class="formField"><input id="serverCertificateFile" type="text"
		value="<c:out value="${dataSource.serverCertificateFile}"/>"" /></td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.keyStoreType"/></td>
    <td class="formField">
        <select id="keyStoreType">
            <option value="<c:out value="<%= KeyStoreType.JKS %>"/>" ${dataSource.keyStoreType.name() == 'JKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.JKS.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.PKCS11 %>"/>" ${dataSource.keyStoreType.name() == 'PKCS11' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.PKCS11.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.DKS %>"/>" ${dataSource.keyStoreType.name() == 'DKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.DKS.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.JCEKS %>"/>" ${dataSource.keyStoreType.name() == 'JCEKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.JCEKS.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.PKCS12 %>"/>" ${dataSource.keyStoreType.name() == 'PKCS12' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.PKCS12.getDescription() %>'/></option>
        </select>
    </td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.keyStoreFile" /></td>
	<td class="formField"><input id="keyStoreFile" type="text"
		value="<c:out value="${dataSource.keyStoreFile}"/>" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.keyStorePassword" /></td>
	<td class="formField"><input id="keyStorePassword" type="password"
		value="<c:out value="${dataSource.keyStorePassword}"/>" /></td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.trustStoreType"/></td>
    <td class="formField">
        <select id="trustStoreType">
            <option value="<c:out value="<%= KeyStoreType.JKS %>"/>" ${dataSource.trustStoreType.name() == 'JKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.JKS.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.PKCS11 %>"/>" ${dataSource.trustStoreType.name() == 'PKCS11' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.PKCS11.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.DKS %>"/>" ${dataSource.trustStoreType.name() == 'DKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.DKS.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.JCEKS %>"/>" ${dataSource.trustStoreType.name() == 'JCEKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.JCEKS.getDescription() %>'/></option>
            <option value="<c:out value="<%= KeyStoreType.PKCS12 %>"/>" ${dataSource.trustStoreType.name() == 'PKCS12' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.PKCS12.getDescription() %>'/></option>
        </select>
    </td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.trustStoreFile" /></td>
	<td class="formField"><input id="trustStoreFile" type="text"
		value="<c:out value="${dataSource.trustStoreFile}"/>" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.trustStorePassword" /></td>
	<td class="formField"><input id="trustStorePassword" type="password"
		value="<c:out value="${dataSource.trustStorePassword}"/>" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.channelLifetime" /></td>
	<td class="formField"><input id="channelLifetime" type="number"
		value="${dataSource.channelLifetime}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.sessionTimeout" /></td>
	<td class="formField"><input id="sessionTimeout" type="number"
		value="${dataSource.sessionTimeout}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.negotiationTimeout" /></td>
	<td class="formField"><input id="negotiationTimeout" type="number"
		value="${dataSource.negotiationTimeout}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.requestTimeout" /></td>
	<td class="formField"><input id="requestTimeout" type="number"
		value="${dataSource.requestTimeout}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.receiveBufferSize" /></td>
	<td class="formField"><input id="receiveBufferSize" type="number"
		value="${dataSource.receiveBufferSize}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.sendBufferSize" /></td>
	<td class="formField"><input id="sendBufferSize" type="number"
		value="${dataSource.sendBufferSize}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.maxMessageSize" /></td>
	<td class="formField"><input id="maxMessageSize" type="number"
		value="${dataSource.maxMessageSize}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.maxChunkCount" /></td>
	<td class="formField"><input id="maxChunkCount" type="number"
		value="${dataSource.maxChunkCount}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.defaultTimeout" /></td>
	<td class="formField"><input id="defaultTimeout" type="number"
		value="${dataSource.defaultTimeout}" /></td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.keepAlive" /></td>
    <td class="formField"><sst:checkbox id="keepAlive"
        selectedValue="${dataSource.keepAlive}" /></td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.noDelay" /></td>
    <td class="formField"><sst:checkbox id="noDelay"
        selectedValue="${dataSource.noDelay}" /></td>
</tr>
<tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.discovery" /></td>
    <td class="formField"><sst:checkbox id="discovery"
        selectedValue="${dataSource.discovery}" /></td>
</tr>

<tr>
	<tr>
		<td class="formLabelRequired"><spring:message
			code="dsEdit.opcua.serverName" /></td>
		<td class="formField"><sst:select id="serverName" value=""></sst:select>
		<div style="height: 2px;"></div>
		<input id="searchBtn" type="button"
			value="<spring:message code="dsEdit.opcua.refreshServers"/>"
			onclick="searchServer();" /></td>
	</tr>
    <tr>
        <td colspan="2" id="console" class="formError"></td>
    </tr>
	<tr>
		<td class="formLabelRequired"><spring:message
			code="dsEdit.updatePeriod" /></td>
		<td class="formField"><input type="text" id="updatePeriods"
			value="${dataSource.updatePeriods}" class="formShort" /> <sst:select
			id="updatePeriodType" value="${dataSource.updatePeriodType}">
			<tag:timePeriodOptions sst="true" ms="true" s="true" min="true"
				h="true" />
		</sst:select></td>
	</tr>

	<tr>
		<td class="formLabelRequired"><spring:message
			code="dsEdit.opcua.creationMode" /></td>
		<td class="formField"><sst:select id="selectMethodTag"
			value="${dataSource.creationMode}" onchange="toggleDiv(this)">
			<sst:option value="AddTags">
				<spring:message code="dsEdit.opcua.addTags" />
			</sst:option>
		</sst:select></td>
	</tr>

	<tr>
		<td class="formLabelRequired"><spring:message code="dsEdit.quantize" /></td>
		<td class="formField"><sst:checkbox id="quantize"
			selectedValue="${dataSource.quantize}" /></td>
	</tr>
	</table>
	</div>
	</td>

	<!-- findTagsMethod -->

	<td valign="top">
	<div id="addDiv" class="borderDiv marB marR">
	<table>
		<tr>
			<td colspan="2" class="smallTitle"><spring:message
				code="dsEdit.opcua.addTags" /></td>
		</tr>
		<tr>
		    <td>
		        <spring:message code="dsEdit.opcua.tagName" />
			    <input id="searchTag" type="text" />
			</td>
		</tr>
		<tr>
		    <td>
                <spring:message code="dsEdit.opcua.identifier" />
                <input id="searchIdentifier" type="text" />
            </td>
		</tr>
		<tr>
			<td>
			   <spring:message code="dsEdit.opcua.identifierType" />
               <select id="searchIdentifierType">
                  <option value="<c:out value="<%= OpcUaIdentifierType.STRING %>"/>" ><c:out value='<%= OpcUaIdentifierType.STRING.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaIdentifierType.NUMERIC %>"/>" ><c:out value='<%= OpcUaIdentifierType.NUMERIC.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaIdentifierType.BINARY %>"/>" ><c:out value='<%= OpcUaIdentifierType.BINARY.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaIdentifierType.GUID %>"/>" ><c:out value='<%= OpcUaIdentifierType.GUID.getDescription() %>'/></option>
               </select>
            </td>
		</tr>

		<tr>
		    <td>
		       <spring:message code="dsEdit.opcua.dataType"/>
               <select id="searchDataType">
                  <option value="<c:out value="<%= OpcUaDataType.BOOL %>"/>" ><c:out value='<%= OpcUaDataType.BOOL.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaDataType.SINT %>"/>" ><c:out value='<%= OpcUaDataType.SINT.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaDataType.USINT %>"/>" ><c:out value='<%= OpcUaDataType.USINT.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaDataType.BYTE %>"/>" ><c:out value='<%= OpcUaDataType.BYTE.getDescription() %>'/></option>

                  <option value="<c:out value="<%= OpcUaDataType.INT %>"/>" ><c:out value='<%= OpcUaDataType.INT.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaDataType.UINT %>"/>" ><c:out value='<%= OpcUaDataType.UINT.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaDataType.WORD %>"/>" ><c:out value='<%= OpcUaDataType.WORD.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaDataType.DINT %>"/>" ><c:out value='<%= OpcUaDataType.DINT.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaDataType.UDINT %>"/>" ><c:out value='<%= OpcUaDataType.UDINT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.DWORD %>"/>" ><c:out value='<%= OpcUaDataType.DWORD.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.LINT %>"/>" ><c:out value='<%= OpcUaDataType.LINT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.ULINT %>"/>" ><c:out value='<%= OpcUaDataType.ULINT.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaDataType.LWORD %>"/>" ><c:out value='<%= OpcUaDataType.LWORD.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.REAL %>"/>" ><c:out value='<%= OpcUaDataType.REAL.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.LREAL %>"/>" ><c:out value='<%= OpcUaDataType.LREAL.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.CHAR %>"/>" ><c:out value='<%= OpcUaDataType.CHAR.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaDataType.WCHAR %>"/>" ><c:out value='<%= OpcUaDataType.WCHAR.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.STRING %>"/>" ><c:out value='<%= OpcUaDataType.STRING.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaDataType.WSTRING %>"/>" ><c:out value='<%= OpcUaDataType.WSTRING.getDescription() %>'/></option>
               </select>
            </td>
        </tr>

        <tr>
            <td>
                <input id="btnFindTag" type="button" value="<spring:message code="dsEdit.opcua.findTag"/>" onclick="findTag();"/>
            </td>
        </tr>

		<tr>
			<td colspan="2" id="tagsMessage" class="formError"></td>
		</tr>

		<tr>
			<td>
			<table cellspacing="1" cellpadding="0" border="0">
				<thead class="rowHeader">
					<td align="center"><spring:message code="dsEdit.opcua.tag" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.dataType" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.namespaceIndex" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.identifier" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.identifierType" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.settable" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.validation" /></td>
					<td align="center"><spring:message code="common.add" /></td>
				</thead>

				<!-- TODO why is the height being enforced? -->
				<tbody id="addTagsTable" style="height: 160px; overflow: auto;"></tbody>
			</table>
			</td>

		</tr>
		<tr>
			<td colspan="2" align="center"><input id="btnAddTag"
				type="button" value="<spring:message code="dsEdit.opcua.addTags"/>"
				onclick="btnAddTag();" /></td>
		</tr>

		<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf"%>

		<tag:pointList pointHelpId="opcUaPP">
		  <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.settable"/></td>
            <td class="formField"><input type="checkbox" id="settable"/></td>
          </tr>
          <tr>
              <td class="formLabelRequired"><spring:message code="dsEdit.opcua.dataType"/></td>
              <td class="formField">
                  <select id="dataType">
                    <option value="<c:out value="<%= OpcUaDataType.BOOL %>"/>" ><c:out value='<%= OpcUaDataType.BOOL.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaDataType.SINT %>"/>" ><c:out value='<%= OpcUaDataType.SINT.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaDataType.USINT %>"/>" ><c:out value='<%= OpcUaDataType.USINT.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaDataType.BYTE %>"/>" ><c:out value='<%= OpcUaDataType.BYTE.getDescription() %>'/></option>

                    <option value="<c:out value="<%= OpcUaDataType.INT %>"/>" ><c:out value='<%= OpcUaDataType.INT.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaDataType.UINT %>"/>" ><c:out value='<%= OpcUaDataType.UINT.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaDataType.WORD %>"/>" ><c:out value='<%= OpcUaDataType.WORD.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaDataType.DINT %>"/>" ><c:out value='<%= OpcUaDataType.DINT.getDescription() %>'/></option>

                   <option value="<c:out value="<%= OpcUaDataType.UDINT %>"/>" ><c:out value='<%= OpcUaDataType.UDINT.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.DWORD %>"/>" ><c:out value='<%= OpcUaDataType.DWORD.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.LINT %>"/>" ><c:out value='<%= OpcUaDataType.LINT.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.ULINT %>"/>" ><c:out value='<%= OpcUaDataType.ULINT.getDescription() %>'/></option>

                   <option value="<c:out value="<%= OpcUaDataType.LWORD %>"/>" ><c:out value='<%= OpcUaDataType.LWORD.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.REAL %>"/>" ><c:out value='<%= OpcUaDataType.REAL.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.LREAL %>"/>" ><c:out value='<%= OpcUaDataType.LREAL.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.CHAR %>"/>" ><c:out value='<%= OpcUaDataType.CHAR.getDescription() %>'/></option>

                   <option value="<c:out value="<%= OpcUaDataType.WCHAR %>"/>" ><c:out value='<%= OpcUaDataType.WCHAR.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.STRING %>"/>" ><c:out value='<%= OpcUaDataType.STRING.getDescription() %>'/></option>
                   <option value="<c:out value="<%= OpcUaDataType.WSTRING %>"/>" ><c:out value='<%= OpcUaDataType.WSTRING.getDescription() %>'/></option>
                  </select>
              </td>
          </tr>
		  <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.tagName" /></td>
            <td class="formField"><input id="tag" type="text" /></td>
          </tr>
          <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.namespaceIndex" /></td>
            <td class="formField"><input id="namespaceIndex" type="number" /></td>
          </tr>
		  <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.identifier" /></td>
            <td class="formField"><input id="identifier" type="text" /></td>
          </tr>
		  <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.identifierType" /></td>
            <td class="formField">
                <select id="identifierType">
                    <option value="<c:out value="<%= OpcUaIdentifierType.STRING %>"/>" ><c:out value='<%= OpcUaIdentifierType.STRING.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.NUMERIC %>"/>" ><c:out value='<%= OpcUaIdentifierType.NUMERIC.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.BINARY %>"/>" ><c:out value='<%= OpcUaIdentifierType.BINARY.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.GUID %>"/>" ><c:out value='<%= OpcUaIdentifierType.GUID.getDescription() %>'/></option>
                </select>
           </td>
          </tr>
	      <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.attributes" /></td>
            <td class="formField"><input id="attributes" type="text" /></td>
          </tr>

		</tag:pointList>