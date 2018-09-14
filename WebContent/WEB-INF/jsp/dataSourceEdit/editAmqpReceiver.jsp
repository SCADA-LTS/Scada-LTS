<%--
    SoftQ - http://www.softq.pl/
    Copyright (C) 2018 Softq
    @author Radek Jajko
--%>

<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="org.scada_lts.workdomain.datasource.amqp.AmqpReceiverPointLocatorVO"%>
<script type="text/javascript">

    function exchangeTypeChange() {
        var exType = $get("exchangeType");
        if (exType === "<%= AmqpReceiverPointLocatorVO.ExchangeType.A_NONE %>" ) {
            hide("exchangeFields");
            hide("routingFields");
            show("queueFields");
        } else if ( exType === "<%= AmqpReceiverPointLocatorVO.ExchangeType.A_DIRECT %>" ) {
            hide("queueFields");
            show("exchangeFields");
            show("routingFields");
        } else if ( exType === "<%= AmqpReceiverPointLocatorVO.ExchangeType.A_TOPIC %>" ) {
            hide("queueFields");
            show("exchangeFields");
            show("routingFields");
        } else if ( exType === "<%= AmqpReceiverPointLocatorVO.ExchangeType.A_FANOUT %>" ) {
            hide("queueFields");
            show("exchangeFields");
            hide("routingFields");
        } else if ( exType === "<%= AmqpReceiverPointLocatorVO.ExchangeType.A_HEADERS %>" ) {
            show("queueFields");
            show("exchangeFields");
            show("routingFields");
        }
    }

  function initImpl() {
    exchangeTypeChange()

  }


  function saveDataSourceImpl() {
        DataSourceEditDwr.saveAmqpReceiverDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
                      $get("updatePeriodType"),$get("serverIpAddress"), $get("serverPortNumber"),$get("serverUsername"), $get("serverPassword"), $get("serverVirtualHost"), saveDataSourceCB);
    }

  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("dataTypeId", locator.dataTypeId);
        $set("exchangeType", locator.exchangeType);
        $set("exchangeName", locator.exchangeName);
        $set("queueName", locator.queueName);
        $set("queueDurability", locator.queueDurability);
        $set("routingKey", locator.routingKey);
    }

  function savePointImpl(locator) {

    delete locator.settable;
    delete locator.relinquishable;
    locator.dataTypeId = $get("dataTypeId");
    locator.exchangeType = $get("exchangeType");
    locator.exchangeName = $get("exchangeName");
    locator.queueName = $get("queueName");
    locator.queueDurability = $get("queueDurability");
    locator.routingKey = $get("routingKey");

    DataSourceEditDwr.saveAmqpReceiverPointLocator(
    currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions)  {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.amqpReceiver.queueName"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.queueName; };
  }
</script>


<c:set var="dsDesc"><fmt:message key="dsEdit.amqpReceiver.desc"/></c:set>
<c:set var="dsHelpId" value="amqpReceiverDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
    <td class="formField">
      <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort" />
      <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
        <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.address"/></td>
    <td class="formField"><input type="text" id="serverIpAddress" value="${dataSource.serverIpAddress}"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.port"/></td>
    <td class="formField"><input type="text" id="serverPortNumber" value="${dataSource.serverPortNumber}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.username"/></td>
    <td class="formField"><input type="text" id="serverUsername" value="${dataSource.serverUsername}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.password"/></td>
    <td class="formField"><input type="text" id="serverPassword" value="${dataSource.serverPassword}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.virtualhost"/></td>
    <td class="formField"><input type="text" id="serverVirtualHost" value="${dataSource.serverVirtualHost}"/></td>
  </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="amqpReceiverPP">

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable" disabled /></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeBinary="true" excludeMultistate="true" excludeImage="true" />
      </select>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.exchangeType"/></td>
    <td class="formField">
        <select id="exchangeType" onchange="exchangeTypeChange()">
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.ExchangeType.A_NONE %>"/>">Empty</option>
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.ExchangeType.A_DIRECT %>"/>">Direct</option>
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.ExchangeType.A_TOPIC %>"/>">Topic</option>
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.ExchangeType.A_HEADERS %>"/>">Headers</option>
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.ExchangeType.A_FANOUT %>"/>">Fanout</option>
        </select>
    </td>
  </tr>
  <tbody id="queueFields" style="display:none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.queueName"/></td>
      <td class="formField"><input type="text" id="queueName"/></td>
    </tr>
  </tbody>
  <tr>
    <td class="formLabelRequired">Queue Durability</td>
    <td class="formField">
        <select id="queueDurability">
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.DurabilityType.DURABLE %>"/>">Durable</option>
            <option value="<c:out value="<%= AmqpReceiverPointLocatorVO.DurabilityType.TRANSIENT %>"/>">Transient</option>
        </select>
    </td>
  </tr>
  <tbody id="exchangeFields" style="display:none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.exchangeName"/></td>
      <td class="formField"><input type="text" id="exchangeName"/></td>
    </tr>
  </tbody>
  <tbody id="routingFields" style="display:none;">
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.amqpReceiver.routingKey"/></td>
      <td class="formField"><input type="text" id="routingKey"/></td>
    </tr>
  </tbody>

</tag:pointList>