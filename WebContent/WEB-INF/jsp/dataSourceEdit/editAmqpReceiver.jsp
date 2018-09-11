<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">

  function initImpl() {

    }


  function saveDataSourceImpl() {
        DataSourceEditDwr.saveAmqpReceiverDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
                      $get("updatePeriodType"),$get("serverIpAddress"), $get("serverPortNumber"),$get("serverUsername"), $get("serverPassword"), saveDataSourceCB);
    }

  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("dataTypeId", locator.dataTypeId);
        $set("exchangeType", locator.exchangeType);
        $set("exchangeName", locator.exchangeName);
        $set("queueName", locator.queueName);
        $set("routingKey", locator.routingKey);
    }

  function savePointImpl(locator) {

    delete locator.settable;


                locator.dataTypeId = $get("dataTypeId");
                locator.exchangeType = $get("exchangeType");
                locator.exchangeName = $get("exchangeName");
                locator.queueName = $get("queueName");
                locator.routingKey = $get("routingKey");

                DataSourceEditDwr.saveAmqpReceiverPointLocator(
                currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions)  {
      pointListColumnHeaders[pointListColumnHeaders.length] = function(td) { td.id = "fieldNameTitle"; };
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.fieldName; };
  }
</script>


<c:set var="dsDesc"><fmt:message key="dsEdit.sql.desc"/></c:set>
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
    <td class="formLabelRequired">Server IP address</td>
    <td class="formField"><input type="text" id="serverIpAddress" value="${dataSource.serverIpAddress}"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired">Server Port</td>
    <td class="formField"><input type="text" id="serverPortNumber" value="${dataSource.serverPortNumber}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired">RabbitMQ username</td>
    <td class="formField"><input type="text" id="serverUsername" value="${dataSource.serverUsername}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired">RabbitMQ user password</td>
    <td class="formField"><input type="text" id="serverPassword" value="${dataSource.serverPassword}"/></td>
  </tr>

<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="amqpReceiverPP">

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>

  <tr>
    <td class="formLabelRequired">Exchange Type</td>
    <td class="formField"><input type="text" id="exchangeType"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired">Exchange Name</td>
    <td class="formField"><input type="text" id="exchangeName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired">Queue Name</td>
    <td class="formField"><input type="text" id="queueName"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired">Routing Key</td>
    <td class="formField"><input type="text" id="routingKey"/></td>
  </tr>

</tag:pointList>