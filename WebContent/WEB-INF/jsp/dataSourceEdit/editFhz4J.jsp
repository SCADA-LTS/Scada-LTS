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
    }

    function assembleDevice() {
        dwr.util.removeAllRows("fhz4jValues");
        DataSourceEditDwr.getFhz4JProperties($get("deviceType"), assembleDeviceCB);
    }

    function assembleDeviceCB(propertyLabels) {
            dwr.util.addRows("fhz4jValues", propertyLabels, [
                function(propertyLabel) { return $get(deviceHousecode); },
                function(propertyLabel) { return $get(deviceLocation); },
                function(propertyLabel) { return $get(deviceType); },
                function(propertyLabel) { return propertyLabel; },
                function(propertyLabel) {
                    return writeImage("scanDeviceImg_"+ $get(deviceHousecode) + "_" +  $get(deviceType) + "_" + propertyLabel, null, "icon_comp_add",
                         "<fmt:message key="common.add"/>", "addPoint({'deviceHousecode': '" + $get(deviceHousecode) + "', 'deviceLocation': '" + $get(deviceLocation) + "', 'deviceType': '" + $get(deviceType) + "', 'propertyLabel': '"+ propertyLabel + "'})");
           }
            ],
            {
                rowCreator: function(options) {
                    var tr = document.createElement("tr");
                    tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                    return tr;
                }
            });
    }

    function saveDataSourceImpl() {
        DataSourceEditDwr.saveFhz4JDataSource($get("dataSourceName"),
        $get("dataSourceXid"), $get("commPortId"),
        $get("fhzHousecode"), $get("fhzMaster"), saveDataSourceCB);
    }

    function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.fhz4j.deviceHousecode'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.deviceHousecodeStr; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.fhz4j.deviceType'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.fhzDeviceTypeLabel; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.fhz4j.deviceProperty'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.fhzPropertyLabel; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.fhz4j.PropertySettable'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.settable; };
    }


    /*
     * indicies passed from addPoint(indicies)
     */
    function addPointImpl(indicies) {
        DataSourceEditDwr.addFhz4JPoint(indicies.deviceHousecode, indicies.deviceLocation, indicies.deviceType, indicies.propertyLabel, editPointCB);
    }

 
    function editPointCBImpl(locator) {
        $set("editDeviceHousecode", locator.deviceHousecodeStr);
        $set("editDeviceTypeLabel", locator.fhzDeviceTypeLabel);
        $set("editFhzPropertyLabel", locator.fhzPropertyLabel);
        $set("editDataPointSettable", locator.settable);
    }

    function savePointImpl(locator) {
        //TODO avoid double settings use annotations of dwr 3.0+ ?
        delete locator.deviceHousecode;
        delete locator.fhzProperty;
        delete locator.fhzDeviceType;
        locator.fhzPropertyLabel = $get("editFhzPropertyLabel");
        locator.fhzDeviceTypeLabel = $get("editDeviceTypeLabel");
        locator.deviceHousecodeStr = $get("editDeviceHousecode");
        locator.settable = $get("editDataPointSettable");

        DataSourceEditDwr.saveFhz4JPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }

</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.fhz4j.desc"/></c:set>
<c:set var="dsHelpId" value="fhz4jDS"/>
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
                    <sst:option value="/dev/ttyACM0">/dev/ttyACM0</sst:option>
                    <sst:option value="/dev/ttyACM1">/dev/ttyACM1</sst:option>
                    <sst:option value="/dev/ttyACM2">/dev/ttyACM2</sst:option>
                    <sst:option value="/dev/ttyACM3">/dev/ttyACM3</sst:option>
                </sst:select>
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.fhz4j.fhzHouseCode"/></td>
    <td class="formField">
        <input type="text" id="fhzHousecode" value="${dataSource.fhzHousecodeStr}" class="formShort"/>
    </td>
</tr>

<tr>
    <td colspan="2" />
<input type="checkbox" id="fhzMaster" <c:if test="${dataSource.fhzMaster}"> checked="checked"</c:if> class="formShort" ><fmt:message key="dsEdit.fhz4j.fhzMaster"/></input>
</td>
</tr>

</table>
<tag:dsEvents/>
</div>
</td>

<td valign="top">
    <div class="borderDiv marB">
        <table>
            <tr><td colspan="3" class="smallTitle"><fmt:message key="dsEdit.fhz4j.init"/></td></tr>
            <tr>
                <td class="formLabelRequired"><fmt:message key="dsEdit.fhz4j.deviceHousecode"/></td>
                <td class="formField">
                    <input type="text" id="deviceHousecode" class="formShort"/>
                </td>
                <td class="formLabelRequired"><fmt:message key="dsEdit.fhz4j.deviceLocation"/></td>
                <td class="formField">
                    <input type="text" id="deviceLocation" class="formShort"/>
                </td>
                <td class="formField">
                    <sst:select id="deviceType" value="UNKNOWN">
                        <c:forEach items="${dataSource.deviceTypes}" var="deviceType">
                            <sst:option value="${deviceType.label}">${deviceType.label}</sst:option>
                        </c:forEach>
                    </sst:select>
                </td>
                <td>
                    <input id="showDevicePropsBtn" type="button" value="<fmt:message key="dsEdit.fhz4j.showDevicePropsBtn"/>" onclick="assembleDevice()"/>
                </td>
            </tr>

            <tr><td colspan="2" id="refreshMessage" class="formError"></td></tr>

            <tr>
                <td colspan="2">
                    <table cellspacing="1">
                        <tr class="rowHeader">
                            <td><fmt:message key="dsEdit.fhz4j.deviceHousecode"/></td>
                            <td><fmt:message key="dsEdit.fhz4j.deviceLocation"/></td>
                            <td><fmt:message key="dsEdit.fhz4j.deviceType"/></td>
                            <td><fmt:message key="dsEdit.fhz4j.property"/></td>
                            <td><fmt:message key="dsEdit.fhz4j.add"/></td>
                        </tr>
                        <tbody id="fhz4jValues"></tbody>
                    </table>
                </td>
            </tr>

            <%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

            <tag:pointList pointHelpId="fhz4jPP">
                <tbody id="editableAttributes">
                </tbody>

                <tbody id="readonlyAttributes">
                    <tr>
                        <td class="formLabelRequired"><fmt:message key="dsEdit.fhz4j.deviceHousecode"/></td>
                        <td class="formField"><input id="editDeviceHousecode" disabled="disabled"/></td>
                    </tr>
                    <tr>
                        <td class="formLabelRequired"><fmt:message key="dsEdit.fhz4j.deviceType"/></td>
                        <td class="formField"><input id="editDeviceTypeLabel" disabled="disabled"/></td>
                    </tr>
                    <tr>
                        <td class="formLabelRequired"><fmt:message key="dsEdit.fhz4j.property"/></td>
                        <td class="formField"><input id="editFhzPropertyLabel" disabled="disabled"/></td>
                    </tr>
                    <tr>
                        <td class="formLabelRequired"><fmt:message key="dsEdit.openv4j.dataPointSettable"/></td>
                        <td class="formField"><input type="checkbox" id="editDataPointSettable" disabled="disabled"/></td>
                    </tr>
                </tbody>
            </tag:pointList>
