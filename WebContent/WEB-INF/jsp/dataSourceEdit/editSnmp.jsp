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
<%@page import="org.snmp4j.mp.SnmpConstants"%>
<%@page import="com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO"%>
<%@page import="com.serotonin.mango.vo.dataSource.snmp.SnmpPointLocatorVO"%>
<%@page import="com.serotonin.mango.DataTypes"%>

<script type="text/javascript">
  function versionChange() {
      var version = $get("snmpVersion");
      if (version == <c:out value="<%= SnmpConstants.version3 %>"/>) {
          show("version3Fields");
          show("trapSection");
          hide("version12Fields");
      } else if (version == <c:out value="<%= SnmpConstants.version1 %>"/>) {
          hide("version3Fields");
          show("version12Fields");
          hide("trapSection");
      } else {
          hide("version3Fields");
          show("version12Fields");
          show("trapSection");
      }

  }

  function securityLevelChange() {
    var securityLevel = $get("securityLevel");
    if (securityLevel === "1") {
      hide("authFieldsProtocol")
      hide("authFieldsPassphrase")
      hide("privFieldsProtocol")
      hide("privFieldsPassphrase")
    } else if (securityLevel === "2") {
      show("authFieldsProtocol")
      show("authFieldsPassphrase")
      hide("privFieldsProtocol")
      hide("privFieldsPassphrase")
    } else if (securityLevel === "3") {
      show("authFieldsProtocol")
      show("authFieldsPassphrase")
      show("privFieldsProtocol")
      show("privFieldsPassphrase")
    }
  }

  function snmpTest() {
      $set("snmpTestMessage", "<spring:message code="dsEdit.snmp.gettingValue"/>");
      snmpTestButton(true);
      DataSourceEditDwr.snmpGetOid($get("snmpTestOid"), $get("host"), $get("port"), $get("snmpVersion"),
              $get("community"), $get("securityName"), $get("authProtocol"), $get("authPassphrase"),
              $get("privProtocol"), $get("privPassphrase"), $get("securityLevel"),
              $get("contextName"), $get("retries"), $get("timeout"), snmpTestCB);
  }

  function snmpWalk() {
      $set("snmpTestMessage", "<spring:message code="dsEdit.snmp.gettingValue"/>");
      snmpWalkButton(true);
      DataSourceEditDwr.snmpWalkOid($get("snmpWalkOid"), $get("host"), $get("port"), $get("snmpVersion"),
              $get("community"), $get("securityName"), $get("authProtocol"), $get("authPassphrase"),
              $get("privProtocol"), $get("privPassphrase"), $get("securityLevel"),
              $get("contextName"), $get("retries"), $get("timeout"), snmpWalkCB);
  }

  function snmpTestCB() {
      setTimeout(snmpTestUpdate, 1000);
  }

  function snmpWalkCB() {
      setTimeout(snmpWalkUpdate, 1000);
  }

  function snmpTestUpdate() {
      DataSourceEditDwr.snmpGetOidUpdate(snmpTestUpdateCB);
  }

  function snmpWalkUpdate() {
      DataSourceEditDwr.snmpGetWalkUpdate(snmpWalkUpdateCB);
  }

  function snmpTestUpdateCB(result) {
      if (result) {
          $set("snmpTestMessage", result);
          snmpTestButton(false);
      }
      else
          snmpTestCB();
  }

  function snmpWalkUpdateCB(result) {
      if (result) {
          $set("snmpWalkMessage", result);
          snmpWalkButton(false);
      }
      else
          snmpWalkCB();
  }

  function snmpTestButton(testing) {
      setDisabled($("snmpTestBtn"), testing);
  }

  function snmpWalkButton(testing) {
      setDisabled($("snmpWalkBtn"), testing);
  }

  function initImpl() {
      versionChange();
      securityLevelChange();
      snmpTestButton(false);
      toggleTrapSetting();
  }

  function toggleTrapSetting() {
    var checkbox = document.getElementById("trapEnabled");
    if(checkbox.checked) {
      show("trapSectionPort")
      show("trapSectionAddress")
    } else {
      hide("trapSectionPort")
      hide("trapSectionAddress")
    }
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveSnmpDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"), $get("host"), $get("port"), $get("snmpVersion"), $get("community"),
              $get("securityName"), $get("authProtocol"), $get("authPassphrase"), $get("privProtocol"),
              $get("privPassphrase"), $get("securityLevel"), $get("contextName"), $get("retries"),
              $get("timeout"), $get("trapEnabled"), $get("trapPort"), $get("localAddress"), saveDataSourceCB);
  }

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<spring:message code="dsEdit.snmp.oid"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.oid; };
  }

  function editPointCBImpl(locator) {
      $set("oid", locator.oid);
      $set("dataTypeId", locator.dataTypeId);
      $set("binary0Value", locator.binary0Value);
      $set("setType", locator.setType);
      $set("trapOnly", locator.trapOnly ? "true" : "false");
      dataTypeChanged();
  }

  function savePointImpl(locator) {
      delete locator.settable;

      locator.oid = $get("oid");
      locator.dataTypeId = $get("dataTypeId");
      locator.binary0Value = $get("binary0Value");
      locator.setType = $get("setType");
      locator.trapOnly = $get("trapOnly") == "true";

      DataSourceEditDwr.saveSnmpPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  function dataTypeChanged() {
      display("binary0ValueRow", $get("dataTypeId") == <c:out value="<%= DataTypes.BINARY %>"/>);
  }
</script>

<c:set var="dsDesc"><spring:message code="dsEdit.snmp.desc"/></c:set>
<c:set var="dsHelpId" value="snmpDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.snmp.host"/></td>
          <td class="formField"><input id="host" type="text" value="${dataSource.host}"/></td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.snmp.port"/></td>
          <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.snmp.retries"/></td>
          <td class="formField"><input id="retries" type="text" value="${dataSource.retries}"/></td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.snmp.timeout"/></td>
          <td class="formField"><input id="timeout" type="text" value="${dataSource.timeout}"/></td>
        </tr>

        <tr>
          <td class="formLabelRequired"><spring:message code="dsEdit.snmp.version"/></td>
          <td class="formField">
            <sst:select id="snmpVersion" value="${dataSource.snmpVersion}" onchange="versionChange()">
              <sst:option value="<%= Integer.toString(SnmpConstants.version1) %>">1</sst:option>
              <sst:option value="<%= Integer.toString(SnmpConstants.version2c) %>">2c</sst:option>
              <sst:option value="<%= Integer.toString(SnmpConstants.version3) %>">3</sst:option>
            </sst:select>
          </td>
        </tr>

        <tbody id="version12Fields" style="display:none;">
          <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.community"/></td>
            <td class="formField"><input id="community" type="text" value="${dataSource.community}"/></td>
          </tr>
        </tbody>

        <tbody id="version3Fields" style="display:none;">

          <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.securityName"/></td>
            <td class="formField"><input id="securityName" type="text" value="${dataSource.securityName}"/></td>
          </tr>

          <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.sl.label"/></td>
            <td class="formField">
              <sst:select id="securityLevel" value="${dataSource.securityLevel}" onchange="securityLevelChange()">
                <sst:option value="1"><spring:message code="dsEdit.snmp.sl.noauthnopriv"/></sst:option>
                <sst:option value="2"><spring:message code="dsEdit.snmp.sl.authnopriv"/></sst:option>
                <sst:option value="3"><spring:message code="dsEdit.snmp.sl.authpriv"/></sst:option>
              </sst:select>
            </td>
          </tr>

          <tr id="authFieldsProtocol">
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.authProtocol"/></td>
            <td class="formField">
              <sst:select id="authProtocol" value="${dataSource.authProtocol}">
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.NONE %>"><spring:message code="dsEdit.snmp.none"/></sst:option>
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.MD5 %>">MD5</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.SHA %>">SHA</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.HMAC128SHA224 %>">HMAC128 SHA224</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.HMAC192SHA256 %>">HMAC192 SHA256</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.HMAC256SHA384 %>">HMAC256 SHA384</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.AuthProtocols.HMAC384SHA512 %>">HMAC384 SHA512</sst:option>
              </sst:select>
            </td>
          </tr>

          <tr id="authFieldsPassphrase">
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.authPassphrase"/></td>
            <td class="formField"><input id="authPassphrase" type="text" value="${dataSource.authPassphrase}"/></td>
          </tr>

          <tr id="privFieldsProtocol">
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.privProtocol"/></td>
            <td class="formField">
              <sst:select id="privProtocol" value="${dataSource.privProtocol}">
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.NONE %>"><spring:message code="dsEdit.snmp.none"/></sst:option>
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.DES %>">DES</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.AES128 %>">AES128</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.AES192 %>">AES192</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.AES256 %>">AES256</sst:option>

                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.DES3 %>">3DES</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.AES192With3DES %>">AES192With3DES</sst:option>
                <sst:option value="<%= SnmpDataSourceVO.PrivProtocols.AES256With3DES %>">AES256With3DES</sst:option>
              </sst:select>
            </td>
          </tr>

          <tr id="privFieldsPassphrase">
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.privPassphrase"/></td>
            <td class="formField"><input id="privPassphrase" type="text" value="${dataSource.privPassphrase}"/></td>
          </tr>

          <tr>
            <td class="formLabelRequired"><spring:message code="dsEdit.snmp.contextName"/></td>
            <td class="formField"><input id="contextName" type="text" value="${dataSource.contextName}"/></td>
          </tr>

        </tbody>    

        <tr id="trapSection">
          <td class="formLabel"><spring:message code="dsEdit.snmp.trapPortEnabled"/></td>
          <td class="formField"><sst:checkbox id="trapEnabled" selectedValue="${dataSource.trapEnabled}" onclick="toggleTrapSetting()"/></td>
        </tr>

        <tr id="trapSectionPort">
          <td class="formLabel"><spring:message code="dsEdit.snmp.trapPort"/></td>
          <td class="formField"><input id="trapPort" type="text" value="${dataSource.trapPort}"/></td>
        </tr>

        <tr id="trapSectionAddress">
          <td class="formLabel"><spring:message code="dsEdit.snmp.localAddress"/></td>
          <td class="formField"><input id="localAddress" type="text" value="${dataSource.localAddress}"/></td>
        </tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>

  <td valign="top">
    <div class="borderDiv marB">
      <table>
        <tr><td colspan="2" class="smallTitle"><spring:message code="dsEdit.snmp.testing"/></td></tr>

        <tr>
          <td class="formLabel"><spring:message code="dsEdit.snmp.oid"/></td>
          <td class="formField"><input type="text" id="snmpTestOid"/></td>
        </tr>

        <tr>
          <td colspan="2" align="center">
            <input id="snmpTestBtn" type="button" value="<spring:message code="dsEdit.snmp.test"/>" onclick="snmpTest();"/>
          </td>
        </tr>

        <tr><td colspan="2" id="snmpTestMessage" class="formError"></td></tr>

        <!--
        <tr>
          <td colspan="2" class="smallTitle"><spring:message code="dsEdit.snmp.walking"/></td>
        </tr>

        <tr>
          <td class="formLabel"><spring:message code="dsEdit.snmp.oidWalk"/></td>
          <td class="formField"><input type="text" id="snmpWalkOid"/></td>
        </tr>

        <tr>
          <td colspan="2" align="center">
            <input id="snmpWalkBtn" type="button" value="<spring:message code="dsEdit.snmp.walk"/>" onclick="snmpWalk();"/>
          </td>
        </tr>

        <tr>
          <td colspan="2" id="snmpWalkMessage" class="formError"></td>
        </tr> -->

<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="snmpPP">
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.snmp.oid"/></td>
    <td class="formField"><input type="text" id="oid"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId" onchange="dataTypeChanged()">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  
  <tr id="binary0ValueRow">
    <td class="formLabelRequired"><spring:message code="dsEdit.snmp.binary0Value"/></td>
    <td class="formField"><input type="text" id="binary0Value"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.snmp.setType"/></td>
    <td class="formField">
      <select id="setType">
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.NONE %>"/>"><spring:message code="dsEdit.snmp.setType.none"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.INTEGER_32 %>"/>"><spring:message code="dsEdit.snmp.setType.int"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.OCTET_STRING %>"/>"><spring:message code="dsEdit.snmp.setType.string"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.OID %>"/>"><spring:message code="dsEdit.snmp.setType.oid"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.IP_ADDRESS %>"/>"><spring:message code="dsEdit.snmp.setType.ipAddress"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.COUNTER_32 %>"/>"><spring:message code="dsEdit.snmp.setType.counter"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.GAUGE_32 %>"/>"><spring:message code="dsEdit.snmp.setType.gauge"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.TIME_TICKS %>"/>"><spring:message code="dsEdit.snmp.setType.ticks"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.OPAQUE %>"/>"><spring:message code="dsEdit.snmp.setType.opaque"/></option>
        <option value="<c:out value="<%= SnmpPointLocatorVO.SetTypes.COUNTER_64 %>"/>"><spring:message code="dsEdit.snmp.setType.counter64"/></option>
      </select>
    </td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><spring:message code="dsEdit.snmp.polling"/></td>
    <td class="formField">
      <select id="trapOnly">
        <option value="false"><spring:message code="dsEdit.snmp.polling.pollTrap"/></option>
        <option value="true"><spring:message code="dsEdit.snmp.polling.trap"/></option>
      </select>
    </td>
  </tr>
</tag:pointList>