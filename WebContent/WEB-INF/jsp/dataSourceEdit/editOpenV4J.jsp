<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2009 Arne Pl�se.
    @author Arne Pl�se

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
<%@include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
    var deviceInfo;

    /**
     * called from init()
     */
    function initImpl() {
        checkButtons(false);
    }

    /**
     * enabele/disable refresh/detectDevice buttons
     */
    function checkButtons(busy) {
        setDisabled("detectDeviceBtn", busy);
        setDisabled("refreshBtn", busy);
        setDisabled("cancelRefreshBtn", !busy);
    }

    function refresh() {
        checkButtons(true);
        $set("refreshMessage", "<fmt:message key='dsEdit.openv4j.refreshing' />");
        dwr.util.removeAllRows("openv4jValues");
        DataSourceEditDwr.searchOpenV4J($get("commPortId"), refreshCB);
    }

    function refreshCB() {
        checkButtons(true);
        $set("refreshMessage", "Callback refreshCB");
        setTimeout(refreshUpdate, 1000);
    }

    function refreshUpdate() {
        DataSourceEditDwr.openV4JSearchUpdate(refreshUpdateCB);
    }

    function refreshUpdateCB(result) {
        if (result) {
            $set("refreshMessage", result.message);
            dwr.util.removeAllRows("openv4jValues");
            dwr.util.addRows("openv4jValues", result.valuesByGroup, [
                function(value) { return value.groupLabel; },
                function(value) { return value.label; },
                function(value) { return value.value; },
                function(value) {
                    return writeImage("scanDeviceImg" + value.name, null, "icon_comp_add",
                    "<fmt:message key="common.add"/>", "addPoint('"+ value.name + "')");
                }
            ],
            {
                rowCreator: function(options) {
                    var tr = document.createElement("tr");
                    tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                    return tr;
                }
            });

            if (result.finished) {
                $set("refreshMessage", "refreshUpdateCB");
                checkButtons(false);
            } else {
                refreshCB();
            }
        }
    }

    function cancelRefresh() {
        DataSourceEditDwr.cancelTestingUtility(cancelRefreshCB);
    }

    function cancelRefreshCB() {
        $set("refreshMessage", "<fmt:message key='dsEdit.openv4j.refreshStopped'/>");
        checkButtons(false);
    }

    function saveDataSourceImpl() {
        DataSourceEditDwr.saveOpenV4JDataSource($get("dataSourceName"),
        $get("dataSourceXid"), $get("commPortId"),
        $get("updatePeriodType"), $get("updatePeriods"),
        $get("device"), $get("protocol"), saveDataSourceCB);
    }

    function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.openv4j.group'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.groupLabel; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.openv4j.label'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.label; };

    }

    function addPointImpl(enumName) {
        DataSourceEditDwr.addOpenV4JPoint(enumName, editPointCB);
    }

    var editingDataPoint;

    function editPointCBImpl(locator) {
        $set("editGroup", locator.groupName);
        editGroupChanged();
        editingDataPoint = locator.dataPointName;
        $set("editDataPoint", locator.dataPointName);
        $set("dataPointSettable", locator.settable);
        //        hide("pointSaveImg");
    }

    function savePointImpl(locator) {
        locator.dataPointName = $get("editDataPoint");
        DataSourceEditDwr.saveOpenV4JPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }

    function deviceChanged() {
        dwr.util.removeAllOptions("protocol");
        DataSourceEditDwr.getOpenV4jProtocolsOfDevice($get("device"), getOpenV4jProtocolsOfDeviceCB);
    }

    function getOpenV4jProtocolsOfDeviceCB(protocols) {
        dwr.util.addOptions("protocol", protocols, "name", "label");
    }

    function editGroupChanged() {
        dwr.util.removeAllOptions("editDataPoint");
        DataSourceEditDwr.getOpenV4jDataPointsOfGroup($get("editGroup"), getOpenV4jDataPointsOfGroupCB);
    }

    function getOpenV4jDataPointsOfGroupCB(dataPoints) {
        dwr.util.addOptions("editDataPoint", dataPoints, "name", "label");
        $set("editDataPoint", editingDataPoint);
    }

    function editDataPointChanged() {
        editingDataPoint = $get("editDataPoint");
        dataPointEdited();
    }

    function dataPointEdited() {
        if ($get("name") == "") {
            $set("name", dwr.util.getText("editDataPoint"))
        }
        //        show("pointSaveImg");
    }

    function detectDevice() {
        checkButtons(true);
        DataSourceEditDwr.detectOpenV4JDevice($get("commPortId"), detectDeviceCB);
    }

    function detectDeviceCB() {
        checkButtons(true);
        setTimeout(detectDeviceUpdate, 1000);
    }

    function detectDeviceUpdate() {
        DataSourceEditDwr.openV4JDetectDeviceUpdate(detectDeviceUpdateCB);
    }

    function detectDeviceUpdateCB(result) {
        if (result) {
            $set("device", result.deviceName);

            if (result.finished) {
                checkButtons(false);
            } else {
                detectDeviceCB();
            }
        }
    }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.openv4j.desc"/></c:set>
<c:set var="dsHelpId" value="openv4jDS"/>
<%@include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>

      <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.serial.port"/></td>
        <td class="formField">
          <c:choose>
            <c:when test="${!empty commPortError}">
              <input id="commPortId" type="hidden" value=""/>
              <span class="formError">${commPortError}</span>
            </c:when>
            <c:otherwise>
              <sst:select id="commPortId" value="${dataSource.commPortId}">
                <c:forEach items="${commPorts}" var="port">
                  <sst:option value="${port.name}">${port.name}</sst:option>
                </c:forEach>
              </sst:select>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      
      <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.openv4j.device"/></td>
        <td class="formField">
          <sst:select id="device" value="${dataSource.device}" onchange="deviceChanged();">
            <c:forEach items="${dataSource.devices}" var="device">
              <sst:option value="${device}">${device.label}</sst:option>
            </c:forEach>
          </sst:select>
          <input id="detectDeviceBtn" type="button" value="<fmt:message key="dsEdit.openv4j.detectDevice"/>" onclick="detectDevice();"/>
        </td>
      </tr>
      
      <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.openv4j.protocol"/></td>
        <td class="formField">
          <sst:select id="protocol" value="${dataSource.protocol}">
            <c:forEach items="${dataSource.protocols}" var="protocol">
              <sst:option value="${protocol.name}">${protocol.label}</sst:option>
            </c:forEach>
          </sst:select>
        </td>
      </tr>
      
      <tr>
        <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
        <td class="formField">
          <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
          <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
            <tag:timePeriodOptions sst="true" s="true" min="true" h="true" d="true" w="true" mon="true"/>
          </sst:select>
      </td>
      </tr>
    </table>
    <tag:dsEvents/>
  </div>
</td>

<td valign="top">
  <div class="borderDiv marB">
    <table>
      <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.openv4j.refresh"/></td></tr>
        <tr>
          <td colspan="2" align="center">
            <input id="refreshBtn" type="button" value="<fmt:message key="dsEdit.openv4j.refresh"/>" onclick="refresh();"/>
            <input id="cancelRefreshBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="cancelRefresh();"/>
          </td>
        </tr>

        <tr><td colspan="2" id="refreshMessage" class="formError"></td></tr>

        <tr>
          <td colspan="2">
            <table cellspacing="1">
              <tr class="rowHeader">
                <td><fmt:message key="dsEdit.openv4j.group"/></td>
                <td><fmt:message key="dsEdit.openv4j.label"/></td>
                <td><fmt:message key="dsEdit.openv4j.value"/></td>
                <td><fmt:message key="dsEdit.openv4j.add"/></td>
              </tr>
              <tbody id="openv4jValues"></tbody>
            </table>
          </td>
        </tr>

        <%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

        <tag:pointList pointHelpId="openv4jPP">
          <tbody id="editableAttributes">
            <tr>
              <td class="formLabelRequired"><fmt:message key="dsEdit.openv4j.group"/></td>
              <td class="formField">
                <sst:select id="editGroup" onchange="editGroupChanged()">
                  <c:forEach items="${dataSource.groups}" var="group">
                    <sst:option value="${group.name}">${group.label}</sst:option>
                  </c:forEach>
                </sst:select>
              </td>
            </tr>

            <tr>
              <td class="formLabelRequired"><fmt:message key="dsEdit.openv4j.label"/></td>
              <td class="formField"><select id="editDataPoint" onchange="editDataPointChanged()"/></td>
            </tr>
          </tbody>

          <tbody id="readonlyAttributes">
            <tr>
              <td class="formLabelRequired"><fmt:message key="dsEdit.openv4j.dataPointSettable"/></td>
              <td class="formField"><input type="checkbox" id="dataPointSettable" disabled="disabled"/></td>
            </tr>
          </tbody>
        </tag:pointList>
