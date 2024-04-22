<%--
    SoftQ - http://www.softq.pl/
    Copyright (C) 2019 Softq
    @author Radek Jajko
--%>

<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="org.scada_lts.ds.messaging.protocol.amqp.AmqpPointLocatorVO"%>
<%@page import="org.scada_lts.ds.messaging.protocol.amqp.ExchangeType"%>
<%@page import="org.scada_lts.ds.messaging.protocol.amqp.MessageAckType"%>
<%@page import="org.scada_lts.ds.messaging.protocol.amqp.DurabilityType"%>
<%@page import="org.scada_lts.ds.messaging.protocol.amqp.AmqpVersion"%>
<%@page import="org.scada_lts.ds.messaging.BrokerMode"%>

<script type="text/javascript">

  function initImpl() {

  }
  function saveDataSourceImpl() {
        let dataSourceToSave = new Object();
        dataSourceToSave.id=${dataSource.id};
        dataSourceToSave.enabled=${dataSource.enabled};
        dataSourceToSave.name=$get("dataSourceName");
        dataSourceToSave.xid=$get("dataSourceXid");
        dataSourceToSave.updatePeriods=$get("updatePeriods");
        dataSourceToSave.updatePeriodType=$get("updatePeriodType");
        dataSourceToSave.updateAttempts=$get("updateAttempts");
        dataSourceToSave.serverHost=$get("serverHost");
        dataSourceToSave.serverPortNumber=$get("serverPortNumber");
        dataSourceToSave.serverUsername=$get("serverUsername");
        dataSourceToSave.serverPassword=$get("serverPassword");
        dataSourceToSave.serverVirtualHost=$get("serverVirtualHost");
        dataSourceToSave.connectionTimeout=$get("connectionTimeout");
        dataSourceToSave.networkRecoveryInterval=$get("networkRecoveryInterval");
        dataSourceToSave.channelRpcTimeout=$get("channelRpcTimeout");
        dataSourceToSave.automaticRecoveryEnabled=$get("automaticRecoveryEnabled");
        dataSourceToSave.protocolVersion=$get("protocolVersion");
        dataSourceToSave.brokerMode=$get("brokerMode");

        if(isValid(dataSourceToSave.updateAttempts)) {
            DataSourceEditDwr.saveAmqpDataSource(dataSourceToSave, saveDataSourceCB);
        } else {
            let message = createValidationMessage("updateAttempts","<fmt:message key="badIntegerFormat"/>");
            showDwrMessages([message]);
            stopImageFader("dsSaveImg");
        }
    }
  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("writable", locator.writable);
        $set("dataTypeId", locator.dataTypeId);
        $set("exchangeType", locator.exchangeType);
        $set("exchangeName", locator.exchangeName);
        $set("queueName", locator.queueName);
        $set("durability", locator.durability);
        $set("routingKey", locator.routingKey);
        $set("messageAck", locator.messageAck);
        $set("qos", locator.qos);
        $set("autoDelete", locator.autoDelete);
        $set("internal", locator.internal);
    }
  function savePointImpl(locator) {
    delete locator.relinquishable;
    locator.settable = $get("settable");
    locator.writable = $get("writable");
    locator.dataTypeId = $get("dataTypeId");
    locator.exchangeType = $get("exchangeType");
    locator.exchangeName = $get("exchangeName");
    locator.queueName = $get("queueName");
    locator.durability = $get("durability");
    locator.routingKey = $get("routingKey");
    locator.messageAck = $get("messageAck");
    locator.qos = $get("qos");
    locator.autoDelete = $get("autoDelete");
    locator.internal = $get("internal");

    DataSourceEditDwr.saveAmqpPointLocator(
    currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions)  {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.amqp.exchangeType"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.exchangeType; };
  }
</script>


<c:set var="dsDesc"><fmt:message key="dsEdit.amqp.desc"/></c:set>
<c:set var="dsHelpId" value="amqpDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort" />
      <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
        <tag:timePeriodOptions sst="true" ms="false" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.updateAttempts"/></td>
    <td class="formField"><input type="text" id="updateAttempts" value="${dataSource.updateAttempts}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.serverHost"/></td>
    <td class="formField"><input type="text" id="serverHost" value="${dataSource.serverHost}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.serverPortNumber"/></td>
    <td class="formField"><input type="number" id="serverPortNumber" value="${dataSource.serverPortNumber}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.serverUsername"/></td>
    <td class="formField"><input type="text" id="serverUsername" value="${dataSource.serverUsername}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.serverPassword"/></td>
    <td class="formField"><input type="text" id="serverPassword" value="${dataSource.serverPassword}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.protocolVersion"/></td>
    <td class="formField">
        <select id="protocolVersion">
            <option value="<c:out value="<%= AmqpVersion.V0_9_1_EXT_AMQP %>"/>" ${dataSource.protocolVersion.name == 'V0_9_1_EXT_AMQP' ? 'selected' : ''} ><c:out value='<%= AmqpVersion.V0_9_1_EXT_AMQP.getVersion() %>'/></option>
        </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.brokerMode"/></td>
    <td class="formField">
        <select id="brokerMode">
            <option value="<c:out value="<%= BrokerMode.NATIVE %>"/>" ${dataSource.brokerMode == 'NATIVE' ? 'selected' : ''} >Native</option>
        </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.serverVirtualhost"/></td>
    <td class="formField"><input type="text" id="serverVirtualHost" value="${dataSource.serverVirtualHost}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.connectionTimeout"/></td>
    <td class="formField"><input type="number" id="connectionTimeout" value="${dataSource.connectionTimeout}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.channelRpcTimeout"/></td>
    <td class="formField"><input type="number" id="channelRpcTimeout" value="${dataSource.channelRpcTimeout}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.automaticRecoveryEnabled"/></td>
    <td class="formField"><input type="checkbox" id="automaticRecoveryEnabled" ${dataSource.automaticRecoveryEnabled ? 'checked' : 'unchecked'} /></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.networkRecoveryInterval"/></td>
    <td class="formField"><input type="number" id="networkRecoveryInterval" value="${dataSource.networkRecoveryInterval}"/></td>
  </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="amqpPP">

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.writable"/></td>
    <td class="formField"><input type="checkbox" id="writable"/></td>
   </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeMultistate="true" excludeImage="true" />
      </select>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.exchangeType"/></td>
    <td class="formField">
        <select id="exchangeType" onchange="exchangeTypeChange()">
            <option value="<c:out value="<%= ExchangeType.NONE %>"/>">Empty</option>
            <option value="<c:out value="<%= ExchangeType.DIRECT %>"/>">Direct</option>
            <option value="<c:out value="<%= ExchangeType.TOPIC %>"/>">Topic</option>
            <option value="<c:out value="<%= ExchangeType.FANOUT %>"/>">Fanout</option>
        </select>
    </td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.queueName"/></td>
      <td class="formField"><input type="text" id="queueName"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.exchangeName"/></td>
      <td class="formField"><input type="text" id="exchangeName"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.routingKey"/></td>
      <td class="formField"><input type="text" id="routingKey"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.qos"/></td>
      <td class="formField"><input type="number" id="qos"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.autoDelete"/></td>
      <td class="formField"><input type="checkbox" id="autoDelete"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.internal"/></td>
      <td class="formField"><input type="checkbox" id="internal"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.durability"/></td>
    <td class="formField">
        <select id="durability">
            <option value="<c:out value="<%= DurabilityType.DURABLE %>"/>">Durable</option>
            <option value="<c:out value="<%= DurabilityType.TRANSIENT %>"/>">Transient</option>
        </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.messageAck"/></td>
    <td class="formField">
        <select id="messageAck">
            <option value="<c:out value="<%= MessageAckType.NO_ACK %>"/>">No ACK</option>
            <option value="<c:out value="<%= MessageAckType.ACK %>"/>">ACK</option>
        </select>
    </td>
  </tr>
</tag:pointList>