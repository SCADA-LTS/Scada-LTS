<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>

<%@page import="org.scada_lts.utils.security.KeyStoreType"%>
<%@page import="org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaBaseDataType"%>
<%@page import="org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaIdentifierType"%>
<%@page import="org.scada_lts.ds.polling.protocol.opcua.security.OpcUaMessageSecurityType"%>
<%@page import="org.scada_lts.ds.polling.protocol.opcua.security.OpcUaSecurityPolicyType"%>

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
          function(data) { return data.nodeName; },
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
          function(data) { return "<input type='checkbox' name='addNode'/>"; }
  ];

  function editPointCBImpl(locator) {
	  hide('pointSaveImg');
  }

  function searchServer() {
      let dataSource = createDataSource();
      DataSourceEditDwr.searchServerOpcUa(dataSource, function(response) {
    	  if (response.hasMessages) {
    	      let messages = response.messages;
    	      console.log('messages: ', messages);
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
      console.log('newDataSource: ', 1);
      let dataSourceId = ${dataSource.id};
	  if (dataSourceId != -1) {
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
        dataSourceToSave.keyStoreFile=$get("keyStoreFile");
        dataSourceToSave.keyStoreType=$get("keyStoreType");
        dataSourceToSave.keyStorePassword=$get("keyStorePassword");
        dataSourceToSave.channelLifetime=$get("channelLifetime");
        dataSourceToSave.sessionTimeout=$get("sessionTimeout");
        dataSourceToSave.negotiationTimeout=$get("negotiationTimeout");
        dataSourceToSave.requestTimeout=$get("requestTimeout");
        dataSourceToSave.maxMessageSize=$get("maxMessageSize");
        dataSourceToSave.defaultTimeout=$get("defaultTimeout");

        return dataSourceToSave;
  }

  function saveDataSourceImpl() {
      let dataSourceToSave = createDataSource();
	  DataSourceEditDwr.saveOpcUaDataSource(dataSourceToSave, saveDataSourceCB);
  }


  function savePointImpl(locator) {
    delete locator.relinquishable;
    locator.settable = $get("settable");
    locator.dataType = $get("dataType");
    locator.nodeName = $get("nodeName");
    locator.identifierType = $get("identifierType");
    locator.identifier = $get("identifier");
    locator.namespaceIndex = $get("namespaceIndex");
    locator.attributes = $get("attributes");

    DataSourceEditDwr.saveOpcUaPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("nodeName", locator.nodeName);
        $set("identifier", locator.identifier);
        $set("namespaceIndex", locator.namespaceIndex);
        $set("attributes", locator.attributes);

        let identifierType = document.getElementById("identifierType");
        for (let i = 0; i < identifierType.options.length; i++) {
            identifierType.options[i].selected = identifierType.options[i].value == locator.identifierType;
        }

        let dataType = document.getElementById("opcDataType");
        for (let i = 0; i < dataType.options.length; i++) {
            dataType.options[i].selected = dataType.options[i].value == locator.opcDataType;
        }
  }
  
  function btnAddNode() {
	  DataSourceEditDwr.getPoint(-1, addNodesImplOpcUa);
  }

  function addNodesImplOpcUa(point) {
	  list = document.getElementById('addNodesTable');
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
		      copyLocator.nodeName = list.rows[i].cells[0].innerHTML;
		      copyLocator.identifier = list.rows[i].cells[1].innerHTML;
              copyLocator.identifierType = list.rows[i].cells[2].innerHTML
			  copyLocator.opcDataType = list.rows[i].cells[3].innerHTML;
			  copyLocator.namespaceIndex = list.rows[i].cells[4].innerHTML;
			  copyLocator.settable = list.rows[i].cells[5].innerHTML;
			  locators.push(copyLocator);

		}
	  }
	  
	  if (locators.length > 0) {
		  DataSourceEditDwr.saveMultipleOpcUaPointLocator(locators, "btnAddNode", savePointCB);
	  } 
  }
  
  function findNode() {
	document.getElementById("nodesMessage").textContent = "<spring:message code="dsEdit.opcua.wait"/>";
    let dataSourceToSave = createDataSource();
    DataSourceEditDwr.findNodesOpcUa(dataSourceToSave, $get("searchDepth"), $get("searchNamespaceIndex"), $get("searchIdentifier"),
        $get("searchIdentifierType"), $get("searchDataType"), function(response) {

            let nodes = response.data.nodes;

            if(response.hasMessages) {
                let messages = response.messages;
                document.getElementById("nodesMessage").textContent = messages[0].contextualMessage;
                if(!nodes || nodes.length == 0)
                    return;
            } else {
                document.getElementById("nodesMessage").textContent = '';
            }

            for(var i=0; i < nodes.length; i++) {
                let node = nodes[i];

                let tbody = document.getElementById('addNodesTable');
                let row = document.createElement("TR");

                let td1 = document.createElement("TD");
                let nodeName = document.createTextNode(node.nodeName);
                td1.setAttribute("align","center");
                td1.appendChild(nodeName);

                let td22 = document.createElement("TD");
                let identifier = document.createTextNode(node.identifierDisplay);
                td22.setAttribute("align","center");
                td22.appendChild(identifier);

                let td23 = document.createElement("TD");
                let identifierType = document.createTextNode(node.identifierType);
                td23.setAttribute("align","center");
                td23.appendChild(identifierType);

                let td2 = document.createElement("TD");
                let opcDataType = document.createTextNode(node.opcDataType);
                td2.setAttribute("align","center");
                td2.appendChild(opcDataType);

                let td21 = document.createElement("TD");
                let namespaceIndex = document.createTextNode(node.namespaceIndex);
                td21.setAttribute("align","center");
                td21.appendChild(namespaceIndex);

                let td3 = document.createElement("TD");
                td3.setAttribute("align","center");
                let text3 = "";

                let td4 = document.createElement("TD");
                td4.setAttribute("align","center");
                img = document.createElement("IMG");

                if (node.validate == true) {

                    img.setAttribute("src","images/accept.png");
                    img.setAttribute("title","<spring:message code="dsEdit.opcua.NodeValidated"/>")

                } else {

                    img.setAttribute("src","images/cancel.png");
                    img.setAttribute("title","<spring:message code="dsEdit.opcua.NodeNotValidated"/>")

                }

                if (node.settable == true) {

                    text3 = document.createTextNode("true");

                } else {

                    text3 = document.createTextNode("false");
                }

                td3.appendChild(text3);
                td4.appendChild(img);

                let td5 = document.createElement("TD");
                td5.setAttribute("align","center");
                let input = document.createElement("INPUT");
                input.type = 'checkbox'
                input.name = 'addNode'
                td5.appendChild(input)

                row.appendChild(td1);
                row.appendChild(td22);
                row.appendChild(td23);
                row.appendChild(td2);
                row.appendChild(td21);
                row.appendChild(td3);
                row.appendChild(td4);
                row.appendChild(td5);
                tbody.appendChild(row);
            }
        });
  }

function toggleDiv(elem) {
    if (elem.value == 'AddNodes') {
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
    <td class="formLabelRequired"><spring:message code="dsEdit.opcua.keyStoreType"/></td>
    <td class="formField">
        <select id="keyStoreType">
            <option value="<c:out value="<%= KeyStoreType.JKS %>"/>" ${dataSource.keyStoreType.name() == 'JKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.JKS.getDescription() %>'/></option>
            <!-- option value="<c:out value="<%= KeyStoreType.PKCS11 %>"/>" ${dataSource.keyStoreType.name() == 'PKCS11' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.PKCS11.getDescription() %>'/></option -->
            <!-- option value="<c:out value="<%= KeyStoreType.DKS %>"/>" ${dataSource.keyStoreType.name() == 'DKS' ? 'selected' : ''} ><c:out value='<%= KeyStoreType.DKS.getDescription() %>'/></option -->
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
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.maxMessageSize" /></td>
	<td class="formField"><input id="maxMessageSize" type="number"
		value="${dataSource.maxMessageSize}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><spring:message code="dsEdit.opcua.defaultTimeout" /></td>
	<td class="formField"><input id="defaultTimeout" type="number"
		value="${dataSource.defaultTimeout}" /></td>
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
		<td class="formField"><sst:select id="selectMethodNode"
			value="${dataSource.creationMode}" onchange="toggleDiv(this)">
			<sst:option value="AddNodes">
				<spring:message code="dsEdit.opcua.addNodes" />
			</sst:option>
		</sst:select></td>
	</tr>

	<tr>
		<td class="formLabelRequired"><spring:message code="dsEdit.quantize" /></td>
		<td class="formField"><sst:checkbox id="quantize"
			selectedValue="${dataSource.quantize}" /></td>
	</tr>
	</table>
	<tag:dsEvents/>
	</div>
	</td>

	<!-- findNodesMethod -->

	<td valign="top">
	<div id="addDiv" class="borderDiv marB marR">
	<table>
		<tr>
			<td colspan="2" class="smallTitle"><spring:message
				code="dsEdit.opcua.addNodes" /></td>
		</tr>
        <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.searchDepth" /></td>
            <td class="formField"><input id="searchDepth" type="number" /></td>
        </tr>
        <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.namespaceIndex" /></td>
            <td class="formField"><input id="searchNamespaceIndex" type="number" value="-1"/></td>
        </tr>
		<tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.identifier" /></td>
            <td class="formField"><input id="searchIdentifier" type="text" /></td>
		</tr>
		<tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.identifierType" /></td>
            <td class="formField">
                <select id="searchIdentifierType">
                    <option value="<c:out value="<%= OpcUaIdentifierType.ALL %>"/>" ><c:out value='<%= OpcUaIdentifierType.ALL.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.STRING %>"/>" ><c:out value='<%= OpcUaIdentifierType.STRING.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.NUMERIC %>"/>" ><c:out value='<%= OpcUaIdentifierType.NUMERIC.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.BINARY %>"/>" ><c:out value='<%= OpcUaIdentifierType.BINARY.getDescription() %>'/></option>
                    <option value="<c:out value="<%= OpcUaIdentifierType.GUID %>"/>" ><c:out value='<%= OpcUaIdentifierType.GUID.getDescription() %>'/></option>
                </select>
            </td>
		</tr>

		<tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.opcDataType"/></td>
            <td class="formField">
                <select id="searchDataType">
                  <option value="<c:out value="<%= OpcUaBaseDataType.ALL %>"/>" ><c:out value='<%= OpcUaBaseDataType.ALL.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.UNKNOWN %>"/>" ><c:out value='<%= OpcUaBaseDataType.UNKNOWN.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.NUMBER %>"/>" ><c:out value='<%= OpcUaBaseDataType.NUMBER.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.UNUMBER %>"/>" ><c:out value='<%= OpcUaBaseDataType.UNUMBER.getDescription() %>'/></option>

                  <option value="<c:out value="<%= OpcUaBaseDataType.BOOLEAN %>"/>" ><c:out value='<%= OpcUaBaseDataType.BOOLEAN.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.SBYTE %>"/>" ><c:out value='<%= OpcUaBaseDataType.SBYTE.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.BYTE %>"/>" ><c:out value='<%= OpcUaBaseDataType.BYTE.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.INT16 %>"/>" ><c:out value='<%= OpcUaBaseDataType.INT16.getDescription() %>'/></option>

                  <option value="<c:out value="<%= OpcUaBaseDataType.UINT16 %>"/>" ><c:out value='<%= OpcUaBaseDataType.UINT16.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.INT32 %>"/>" ><c:out value='<%= OpcUaBaseDataType.INT32.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.UINT32 %>"/>" ><c:out value='<%= OpcUaBaseDataType.UINT32.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.INT64 %>"/>" ><c:out value='<%= OpcUaBaseDataType.INT64.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.UINT64 %>"/>" ><c:out value='<%= OpcUaBaseDataType.UINT64.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.FLOAT %>"/>" ><c:out value='<%= OpcUaBaseDataType.FLOAT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.DOUBLE %>"/>" ><c:out value='<%= OpcUaBaseDataType.DOUBLE.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.STRING %>"/>" ><c:out value='<%= OpcUaBaseDataType.STRING.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.DATE_TIME %>"/>" ><c:out value='<%= OpcUaBaseDataType.DATE_TIME.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.GUID %>"/>" ><c:out value='<%= OpcUaBaseDataType.GUID.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.BYTE_STRING %>"/>" ><c:out value='<%= OpcUaBaseDataType.BYTE_STRING.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.XML_ELEMENT %>"/>" ><c:out value='<%= OpcUaBaseDataType.XML_ELEMENT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.NODE_ID %>"/>" ><c:out value='<%= OpcUaBaseDataType.NODE_ID.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.EXPANDED_NODE_ID %>"/>" ><c:out value='<%= OpcUaBaseDataType.EXPANDED_NODE_ID.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.STATUS_CODE %>"/>" ><c:out value='<%= OpcUaBaseDataType.STATUS_CODE.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.QUALIFIED_NAME %>"/>" ><c:out value='<%= OpcUaBaseDataType.QUALIFIED_NAME.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.LOCALIZED_TEXT %>"/>" ><c:out value='<%= OpcUaBaseDataType.LOCALIZED_TEXT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.EXTENSION_OBJECT %>"/>" ><c:out value='<%= OpcUaBaseDataType.EXTENSION_OBJECT.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.DATA_VALUE %>"/>" ><c:out value='<%= OpcUaBaseDataType.DATA_VALUE.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.VARIANT %>"/>" ><c:out value='<%= OpcUaBaseDataType.VARIANT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.DIAGNOSTIC_INFO %>"/>" ><c:out value='<%= OpcUaBaseDataType.DIAGNOSTIC_INFO.getDescription() %>'/></option>
                </select>
            </td>
        </tr>

        <tr>
            <td class="formLabelRequired"></td>
            <td class="formField"><input id="btnFindNode" type="button" value="<spring:message code="dsEdit.opcua.findNode"/>" onclick="findNode();"/></td>
        </tr>

		<tr>
		    <td class="formLabelRequired"></td>
			<td colspan="2" id="nodesMessage" class="formError"></td>
		</tr>

		<tr>
			<td colspan="2">
			<table cellspacing="2" cellpadding="0" border="0">
				<thead class="rowHeader">
				    <td align="center"><spring:message code="dsEdit.opcua.nodeName" /></td>
                    <td align="center"><spring:message code="dsEdit.opcua.identifier" /></td>
                    <td align="center"><spring:message code="dsEdit.opcua.identifierType" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.opcDataType" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.namespaceIndex" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.settable" /></td>
					<td align="center"><spring:message code="dsEdit.opcua.validation" /></td>
					<td align="center"><spring:message code="common.add" /></td>
				</thead>

				<!-- TODO why is the height being enforced? -->
				<tbody id="addNodesTable" style="height: 160px; overflow: auto;"></tbody>
			</table>
			</td>

		</tr>
		<tr>
			<td colspan="2" align="center"><input id="btnAddNode"
				type="button" value="<spring:message code="dsEdit.opcua.addNodes"/>"
				onclick="btnAddNode();" /></td>
		</tr>

		<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf"%>

		<tag:pointList pointHelpId="opcUaPP">
		  <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.nodeName" /></td>
            <td class="formField"><input id="nodeName" type="text" /></td>
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
              <td class="formLabelRequired"><spring:message code="dsEdit.opcua.opcDataType"/></td>
              <td class="formField">
                  <select id="opcDataType">
                  <option value="<c:out value="<%= OpcUaBaseDataType.UNKNOWN %>"/>" ><c:out value='<%= OpcUaBaseDataType.UNKNOWN.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.NUMBER %>"/>" ><c:out value='<%= OpcUaBaseDataType.NUMBER.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.UNUMBER %>"/>" ><c:out value='<%= OpcUaBaseDataType.UNUMBER.getDescription() %>'/></option>

                  <option value="<c:out value="<%= OpcUaBaseDataType.BOOLEAN %>"/>" ><c:out value='<%= OpcUaBaseDataType.BOOLEAN.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.SBYTE %>"/>" ><c:out value='<%= OpcUaBaseDataType.SBYTE.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.BYTE %>"/>" ><c:out value='<%= OpcUaBaseDataType.BYTE.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.INT16 %>"/>" ><c:out value='<%= OpcUaBaseDataType.INT16.getDescription() %>'/></option>

                  <option value="<c:out value="<%= OpcUaBaseDataType.UINT16 %>"/>" ><c:out value='<%= OpcUaBaseDataType.UINT16.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.INT32 %>"/>" ><c:out value='<%= OpcUaBaseDataType.INT32.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.UINT32 %>"/>" ><c:out value='<%= OpcUaBaseDataType.UINT32.getDescription() %>'/></option>
                  <option value="<c:out value="<%= OpcUaBaseDataType.INT64 %>"/>" ><c:out value='<%= OpcUaBaseDataType.INT64.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.UINT64 %>"/>" ><c:out value='<%= OpcUaBaseDataType.UINT64.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.FLOAT %>"/>" ><c:out value='<%= OpcUaBaseDataType.FLOAT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.DOUBLE %>"/>" ><c:out value='<%= OpcUaBaseDataType.DOUBLE.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.STRING %>"/>" ><c:out value='<%= OpcUaBaseDataType.STRING.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.DATE_TIME %>"/>" ><c:out value='<%= OpcUaBaseDataType.DATE_TIME.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.GUID %>"/>" ><c:out value='<%= OpcUaBaseDataType.GUID.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.BYTE_STRING %>"/>" ><c:out value='<%= OpcUaBaseDataType.BYTE_STRING.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.XML_ELEMENT %>"/>" ><c:out value='<%= OpcUaBaseDataType.XML_ELEMENT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.NODE_ID %>"/>" ><c:out value='<%= OpcUaBaseDataType.NODE_ID.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.EXPANDED_NODE_ID %>"/>" ><c:out value='<%= OpcUaBaseDataType.EXPANDED_NODE_ID.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.STATUS_CODE %>"/>" ><c:out value='<%= OpcUaBaseDataType.STATUS_CODE.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.QUALIFIED_NAME %>"/>" ><c:out value='<%= OpcUaBaseDataType.QUALIFIED_NAME.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.LOCALIZED_TEXT %>"/>" ><c:out value='<%= OpcUaBaseDataType.LOCALIZED_TEXT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.EXTENSION_OBJECT %>"/>" ><c:out value='<%= OpcUaBaseDataType.EXTENSION_OBJECT.getDescription() %>'/></option>

                 <option value="<c:out value="<%= OpcUaBaseDataType.DATA_VALUE %>"/>" ><c:out value='<%= OpcUaBaseDataType.DATA_VALUE.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.VARIANT %>"/>" ><c:out value='<%= OpcUaBaseDataType.VARIANT.getDescription() %>'/></option>
                 <option value="<c:out value="<%= OpcUaBaseDataType.DIAGNOSTIC_INFO %>"/>" ><c:out value='<%= OpcUaBaseDataType.DIAGNOSTIC_INFO.getDescription() %>'/></option>
                 </select>
              </td>
          </tr>
		  <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.settable"/></td>
            <td class="formField"><input type="checkbox" id="settable"/></td>
          </tr>
          <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.namespaceIndex" /></td>
            <td class="formField"><input id="namespaceIndex" type="number" /></td>
          </tr>
	      <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.opcua.attributes" /></td>
            <td class="formField"><input id="attributes" type="text" /></td>
          </tr>

		</tag:pointList>