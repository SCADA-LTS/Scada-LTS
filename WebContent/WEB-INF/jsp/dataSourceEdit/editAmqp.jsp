<%--
    SoftQ - http://www.softq.pl/
    Copyright (C) 2019 Softq
    @author Radek Jajko
--%>

<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="org.scada_lts.ds.messaging.amqp.AmqpPointLocatorVO"%>
<%@page import="org.scada_lts.ds.messaging.amqp.ExchangeType"%>
<%@page import="org.scada_lts.ds.messaging.amqp.MessageAckType"%>
<%@page import="org.scada_lts.ds.messaging.amqp.DurabilityType"%>
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
        dataSourceToSave.serverIpAddress=$get("serverIpAddress");
        dataSourceToSave.serverPortNumber=$get("serverPortNumber");
        dataSourceToSave.serverUsername=$get("serverUsername");
        dataSourceToSave.serverPassword=$get("serverPassword");
        dataSourceToSave.serverVirtualHost=$get("serverVirtualHost");
        dataSourceToSave.connectionTimeout=$get("connectionTimeout");
        dataSourceToSave.networkRecoveryInterval=$get("networkRecoveryInterval");
        dataSourceToSave.channelRpcTimeout=$get("channelRpcTimeout");
        dataSourceToSave.automaticRecoveryEnabled=$get("automaticRecoveryEnabled");

        DataSourceEditDwr.saveAmqpDataSource(dataSourceToSave, saveDataSourceCB);
    }
  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("writable", locator.writable);
        $set("dataTypeId", locator.dataTypeId);
        $set("exchangeType", locator.exchangeType);
        $set("exchangeName", locator.exchangeName);
        $set("queueName", locator.queueName);
        $set("queueDurability", locator.queueDurability);
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
    locator.queueDurability = $get("queueDurability");
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
        <tag:timePeriodOptions sst="false" ms="false" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.updateAttempts"/></td>
    <td class="formField"><input type="text" id="updateAttempts" value="${dataSource.updateAttempts}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.address"/></td>
    <td class="formField"><input type="text" id="serverIpAddress" value="${dataSource.serverIpAddress}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.port"/></td>
    <td class="formField"><input type="number" id="serverPortNumber" value="${dataSource.serverPortNumber}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.username"/></td>
    <td class="formField"><input type="text" id="serverUsername" value="${dataSource.serverUsername}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.password"/></td>
    <td class="formField"><input type="text" id="serverPassword" value="${dataSource.serverPassword}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.virtualhost"/></td>
    <td class="formField"><input type="text" id="serverVirtualHost" value="${dataSource.serverVirtualHost}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.connectionTimeout"/></td>
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
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.queueName"/></td>
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
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqp.qos"/></td>
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
        <select id="queueDurability">
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