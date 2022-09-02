<%--
    SoftQ - http://www.softq.pl/
    Copyright (C) 2022 Softq
    @author Kamil Jarmusik
--%>

<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="org.scada_lts.ds.messaging.mqtt.MqttPointLocatorVO"%>
<%@page import="org.scada_lts.ds.messaging.mqtt.MqttVersion"%>
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
        dataSourceToSave.connectionTimeout=$get("connectionTimeout");
        dataSourceToSave.maxReconnectDelay=$get("maxReconnectDelay");
        dataSourceToSave.executorServiceTimeout=$get("executorServiceTimeout");
        dataSourceToSave.automaticReconnect=$get("automaticReconnect");
        dataSourceToSave.keepAliveInterval=$get("keepAliveInterval");
        dataSourceToSave.protocolVersion=$get("protocolVersion");
        dataSourceToSave.cleanSession=$get("cleanSession");
        dataSourceToSave.brokerMode=$get("brokerMode");

        DataSourceEditDwr.saveMqttDataSource(dataSourceToSave, saveDataSourceCB);
    }
  function editPointCBImpl(locator) {
        $set("settable", locator.settable);
        $set("writable", locator.writable);
        $set("dataTypeId", locator.dataTypeId);
        $set("topicFilter", locator.topicFilter);
        $set("retained", locator.retained);
        $set("qos", locator.qos);
        $set("clientId", locator.clientId);
    }
  function savePointImpl(locator) {
    delete locator.relinquishable;
    locator.settable = $get("settable");
    locator.writable = $get("writable");
    locator.dataTypeId = $get("dataTypeId");
    locator.topicFilter = $get("topicFilter");
    locator.retained = $get("retained");
    locator.qos = $get("qos");
    locator.clientId = $get("clientId");

    DataSourceEditDwr.saveMqttPointLocator(
    currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }
</script>


<c:set var="dsDesc"><fmt:message key="dsEdit.mqtt.desc"/></c:set>
<c:set var="dsHelpId" value="mqttDS"/>
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
            <option value="<c:out value="<%= MqttVersion.V3_1_1 %>"/>" ${dataSource.protocolVersion == 'V3_1_1' ? 'selected' : ''} >v3.1.1</option>
            <option value="<c:out value="<%= MqttVersion.V5_0 %>"/>" ${dataSource.protocolVersion == 'V5_0' ? 'selected' : ''} >v5.0</option>
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
      <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.cleanSession"/></td>
      <td class="formField"><input type="checkbox" id="cleanSession" ${dataSource.cleanSession ? 'checked' : 'unchecked'} /></td>
    </tr>
    <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.keepAliveInterval"/></td>
      <td class="formField"><input type="number" id="keepAliveInterval" value="${dataSource.keepAliveInterval}"/></td>
    </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.connectionTimeout"/></td>
    <td class="formField"><input type="number" id="connectionTimeout" value="${dataSource.connectionTimeout}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.executorServiceTimeout"/></td>
    <td class="formField"><input type="number" id="executorServiceTimeout" value="${dataSource.executorServiceTimeout}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.automaticReconnect"/></td>
    <td class="formField"><input type="checkbox" id="automaticReconnect" ${dataSource.automaticReconnect ? 'checked' : 'unchecked'} /></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.maxReconnectDelay"/></td>
    <td class="formField"><input type="number" id="maxReconnectDelay" value="${dataSource.maxReconnectDelay}"/></td>
  </tr>

<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="mqttPP">

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
    <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.clientId"/></td>
    <td class="formField"><input type="text" id="clientId"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.topicFilter"/></td>
      <td class="formField"><input type="text" id="topicFilter"/></td>
  </tr>
  <tr>
      <td class="formLabelRequired"><fmt:message key="dsEdit.messaging.qos"/></td>
      <td class="formField"><input type="number" id="qos"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.mqtt.retained"/></td>
      <td class="formField"><input type="checkbox" id="retained"/></td>
  </tr>
</tag:pointList>