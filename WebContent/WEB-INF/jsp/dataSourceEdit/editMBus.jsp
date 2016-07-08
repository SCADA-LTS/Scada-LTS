<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2010 Arne Pl�se
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
        searchButtons(false);
        updateConnectionType();
        updateAddressing();
        hide("editImg"+"<c:out value='<%= Common.NEW_ID %>'/>");
    }

    /**
     * enabele/disable search buttons
     */
    function searchButtons(searching) {
        setDisabled("searchBtn", searching);
        setDisabled("cancelSearchBtn", !searching);
    }

    /*
             SerialDirectConnectionData connData = new SerialDirectConnectionData();
        connData.setBaudRate(baudRate);
        connData.setResponseTimeoutOffset(responseTimeoutOffset);
        connData.setFlowControlIn(flowControlIn);
        connData.setFlowControlOut(flowControlOut);
        connData.setDataBits(dataBits);
        connData.setStopBits(stopBits);
        connData.setParity(parity);
        connData.setCommPortId(commPortId);


        MBusDataSourceVO.SerialAtModemConnectionData connData = new MBusDataSourceVO.SerialAtModemConnectionData();
        connData.setBaudRate(baudRate);
        connData.setResponseTimeoutOffset(responseTimeoutOffset);
        connData.setFlowControlIn(flowControlIn);
        connData.setFlowControlOut(flowControlOut);
        connData.setDataBits(dataBits);
        connData.setStopBits(stopBits);
        connData.setParity(parity);
        connData.setCommPortId(commPortId);
        connData.setPhonenumber(phonenumber);

        MBusDataSourceVO.TcpIpConnectionData connData = new MBusDataSourceVO.TcpIpConnectionData();
        connData.setBaudRate(baudRate);
        connData.setResponseTimeoutOffset(responseTimeoutOffset);
        connData.setIpAddressOrName(ipAddressOrName);
        connData.setTcpIpPort(tcpIpPort);

    */

    function getConnection() {
        conn = null;
        if ($get("connectionType") == "TCP_IP") {
            conn = new TcpIpConnection();
            conn.host = $get("ipAddressOrHostname");
            conn.port = $get("tcpPort");
            conn.bitPerSecond = $get("tcpIpBitPerSecond");
        }
        conn.responseTimeOutOffset = $get("responseTimeOutOffset");
        return conn;
    }

    function getSearchAddressing() {
        addressing = null;
        if ($get("addressingType") == "PRIMARY") {
            addressing = new PrimaryAddressingSearch();
            addressing.firstPrimaryAddress = $get("firstPrimaryAddress");
            addressing.lastPrimaryAddress = $get("lastPrimaryAddress");
        } else if ($get("addressingType") == "SECONDARY") {
            addressing = new SecondaryAddressingSearch();
            addressing.id = $get("secAddrId");
            addressing.manufacturer = $get("secAddrMan");
            addressing.medium = $get("secAddrMedium");
            addressing.version = $get("secAddrVersion");
        }
        return addressing;
    }


   function search() {
        searchButtons(true);
        conn = getConnection();
        addressing = getSearchAddressing();
        DataSourceEditDwr.searchMBus(conn, addressing, searchCB);
    }

    function searchCB() {
        searchButtons(true);
        $set("searchMessage", "Callback searchCB");
        setTimeout(searchUpdate, 1000);
    }

    function searchUpdate() {
        DataSourceEditDwr.mBusSearchUpdate(searchUpdateCB);
    }

    function searchUpdateCB(result) {
        if (result) {
            $set("searchMessage", result.message);
            dwr.util.removeAllRows("mbusDevices");
            dwr.util.addRows("mbusDevices", result.devices, [
                function(device) { return device.addressHex; },
                function(device) { return device.identNumber; },
                function(device) { return device.medium; },
                function(device) { return device.manufacturer; },
                function(device) { return device.versionHex; },
                function(device) {
                    return writeImage("responseFramesImg"+ device.index, null, "control_play_blue",
                    "<fmt:message key='dsEditMbus.getDetails'/>", "getResponseFrames(" + device.index + ")");
                }

            ],
            {
                rowCreator: function(options) {
                    var tr = document.createElement("tr");
                    tr.id = "deviceIndex"+ options.rowData.id;
                    tr.className = "row"+ (options.rowIndex % 2 == 0 ? "" : "Alt");
                    return tr;
                }
            });

            hide("responseFrames");

            if (result.finished) {
                $set("searchMessage", "Search done!");
                searchButtons(false);
            } else {
                searchCB();
            }
        }
    }

    function getResponseFrames(index) {
        startImageFader("responseFramesImg"+ index, true);
        DataSourceEditDwr.getMBusResponseFrames(index, getResponseFramesCB);
    }

    function getResponseFramesCB(result) {
        if (result) {
            stopImageFader("responseFramesImg"+ result.deviceIndex);

            show("responseFrames");
            var tree = dojo.widget.manager.getWidgetById("responseFramesTree");

            // Remove all of the old results.
            while (tree.children.length > 0)
                tree.removeNode(tree.children[0]);

            // Add the new stuff.
            var deviceNode = dojo.widget.createWidget("TreeNode", {
                title: "<b>" + result.deviceName  + "</b>", isFolder: "true" });
            tree.addChild(deviceNode);

            for (var rsIndex = 0; rsIndex < result.responseFrames.length; rsIndex++) {
                var responseFrame = result.responseFrames[rsIndex];
                var responseFrameNode = dojo.widget.createWidget("TreeNode", {
                    title: responseFrame.name, isFolder: "true"});
                deviceNode.addChild(responseFrameNode);

                for (var dbIndex = 0; dbIndex < responseFrame.dataBlocks.length; dbIndex++) {
                    var dataBlock = responseFrame.dataBlocks[dbIndex];
                    var dataBlockNode = dojo.widget.createWidget("TreeNode", {
                        title: dataBlock.name + "(" + dataBlock.params  + ")" + writeImageSQuote(null, null,
                        "icon_comp_add", "<fmt:message key='dsEdit.mbus.addPoint'/>", "addPoint( { 'addressing': '" + result.addressing + "', 'deviceIndex': "+ result.deviceIndex + ", 'rsIndex': " + rsIndex + ", 'dbIndex': " + dbIndex + "})"),
                        isFolder: "true"});
                    responseFrameNode.addChild(dataBlockNode);

                    dataBlockNode.addChild(dojo.widget.createWidget("TreeNode",
                    { title: "<fmt:message key='dsEdit.mbus.presentValue'/>: "+ dataBlock.value}));
                }

            }

            deviceNode.expand();
        }
    }


    function cancelSearch() {
        DataSourceEditDwr.cancelTestingUtility(cancelSearchCB);
    }

    function cancelSearchCB() {
        $set("searchMessage", "<fmt:message key='dsEdit.mbus.seachStopped'/>");
        searchButtons(false);
    }

    function saveDataSourceImpl() {
            DataSourceEditDwr.saveMBusDataSource($get("dataSourceName"), $get("dataSourceXid"),
            getConnection(),
            $get("updatePeriodType"), $get("updatePeriods"), saveDataSourceCB);
    }


    function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.correctionFactor'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.correctionFactor; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.addressing'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.addressing; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.addressHex'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.addressHex; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.identNumber'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.identNumber; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.medium'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.medium; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.manufacturer'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.manufacturer; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.versionHex'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.versionHex; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.responseFrame'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.responseFrame; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.difCode'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.difCode; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.functionField'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.functionField; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.deviceUnit'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.deviceUnit; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.tariff'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.tariff; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.storageNumber'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.storageNumber; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.vifType'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.vifType; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.vifLabel'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.vifLabel; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.unitOfMeasurement'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.unitOfMeasurement; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.siPrefix'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.siPrefix; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.exponent'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.exponent; };

        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.vifeTypes'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.vifeTypes; };
        
        pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key='dsEdit.mbus.vifeLabels'/>";
        pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.vifeLabels; };

    }

    function addPointImpl(indicies) {
        DataSourceEditDwr.addMBusPoint(indicies.addressing, indicies.deviceIndex, indicies.rsIndex, indicies.dbIndex, editPointCB);
    }

    function editPointCBImpl(locator) {
        $set("correctionFactor", locator.correctionFactor);
        $set("addressing", locator.addressing);
        $set("addressHex", locator.addressHex);
        $set("identNumber", locator.identNumber);
        $set("medium", locator.medium);
        $set("manufacturer", locator.manufacturer);
        $set("versionHex", locator.versionHex);
        $set("responseFrame", locator.responseFrame);
        $set("difCode", locator.difCode);
        $set("functionField", locator.functionField);
        $set("deviceUnit", locator.deviceUnit);
        $set("tariff", locator.tariff);
        $set("storageNumber", locator.storageNumber);
        $set("vifType", locator.vifType);
        $set("vifLabel", locator.vifLabel);
        $set("unitOfMeasurement", locator.unitOfMeasurement);
        $set("siPrefix", locator.siPrefix);
        $set("exponent", locator.exponent);
        $set("vifeTypes", locator.vifeTypes);
        $set("vifeLabels", locator.vifeLabels);
        if (true) {
            show("pointSaveImg");
        } else {
            // Didn't find the device.
            hide("pointSaveImg");
        }
    }

    function savePointImpl(locator) {
        locator.correctionFactor = $get("correctionFactor");
        locator.addressing = $get("addressing");
        locator.addressHex = $get("addressHex");
        locator.identNumber = $get("identNumber");
        locator.medium = $get("medium");
        locator.manufacturer = $get("manufacturer");
        locator.versionHex = $get("versionHex");
        locator.responseFrame = $get("responseFrame");
        locator.difCode = $get("difCode");
        locator.functionField = $get("functionField");
        locator.deviceUnit = $get("deviceUnit");
        locator.tariff = $get("tariff");
        locator.storageNumber = $get("storageNumber");
        locator.vifType = $get("vifType");
        locator.vifLabel = $get("vifLabel");
        locator.unitOfMeasurement = $get("unitOfMeasurement");
        locator.siPrefix = $get("siPrefix");
        locator.exponent = $get("exponent");
        locator.vifeTypes = $get("vifeTypes");
        locator.vifeLabels = $get("vifeLabels");

        DataSourceEditDwr.saveMBusPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
    }

    function addressChanged() {
        deviceInfo = getElement(networkInfo, $get("addressHex"), "addressString");
        dwr.util.addOptions("id", "description");
    }

    //Apl neu
    function updateConnectionType() {
        if($get("connectionType") == "SERIAL_AT_MODEM") {
            document.getElementById("phoneNumber").disabled=false;
            document.getElementById("phoneNumber").checked=false;
        } else {
            document.getElementById("phoneNumber").disabled=true;
            document.getElementById("phoneNumber").checked=false;
        }
        updatePhoneNumber();
    }

    function updateAddressing() {
       //Primary Addressing
       setDisabled("firstPrimaryAddress", $get("addressingType") != "PRIMARY");
       setDisabled("lastPrimaryAddress", $get("addressingType") != "PRIMARY");
       //Secondary Addressing
       setDisabled("secAddrId", $get("addressingType") != "SECONDARY");
       setDisabled("secAddrMan", $get("addressingType") != "SECONDARY");
       setDisabled("secAddrMedium", $get("addressingType") != "SECONDARY");
       setDisabled("secAddrVersion", $get("addressingType") != "SECONDARY");
    }

    function updatePhoneNumber() {
       setDisabled("phoneNumberText", !$get("phoneNumber"));
    }

</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.mbus.desc"/></c:set>
<c:set var="dsHelpId" value="mbusDS"/>
<%@include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
<!-- Disable modem for now-->
<tr>
    <td colspan="2">
        <input type="radio" name="connectionType" id="useTcpIpConnection" value="TCP_IP" <c:if test="${dataSource.tcpIp}">checked="checked"</c:if> onclick="updateConnetionType()" disabled="disabled">
        <label class="formLabelRequired" for="useDirectConnection"><fmt:message key="dsEdit.mbus.useTcpIpConnection"/></label>
    </td>
</tr>
<tr>
    <td colspan="2">
        <table>
            <tr>
                <td width="30" />
                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.tcpAddr"/></td>
                <td class="formField"><input type="text" id="ipAddressOrHostname" value="${dataSource.tcpIp ? dataSource.connection.host : ""}" /></td>
            </tr>
            <tr>
                <td width="30" />
                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.tcpPort"/>
                <td class="formField"><input type="text" id="tcpPort" value="${dataSource.tcpIp ? dataSource.connection.port: ""}" /></td>
            </tr>
            <tr>
                <td width="30" />
                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.tcpIpBitPerSecond"/></td>
                <td class="formField"><input type="text" id="tcpIpBitPerSecond" value="${dataSource.tcpIp ? dataSource.connection.bitPerSecond: ""}" /></td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td colspan="2">
        <input type="radio" name="connectionType" id="useDirectConnection" value="SERIAL_DIRECT" <c:if test="${dataSource.serialDirect}">checked="checked"</c:if> onclick="updateConnectionType()" disabled="disabled">
        <label class="formLabelRequired" for="useDirectConnection"><fmt:message key="dsEdit.mbus.useDirectConnection"/></label>
    </td>
    <td/>
</tr>
<tr>
    <td colspan="2">
        <table>
            <tr>
                <td width="30" />
                <td colspan=""2">
                    <input type="checkbox" name="phoneNumber" id="phoneNumber" <c:if test="${dataSource.serialAtModem}"> checked="checked"</c:if> onclick="updatePhoneNumber()" />
                    <label class="formLabelRequired" for="phoneNumber"><fmt:message key="dsEdit.mbus.phoneNumber"/></label>
                    <input type="text" id="phoneNumberText" value="${dataSource.serialAtModem ? dataSource.connection.phoneNumber : ""}" />
                </td>
            </tr>
        </table>

    </td>
    <td/>
</tr>

<c:set var="serialPortSettings" value="${dataSource.serialAtModem || dataSource.serialDirect ? dataSource.connection : null}" />
<c:set var="rxtxDefaultBaudrate" value="2400" />
<c:set var="rxtxDefaultFlowControl" value="NONE" />
<c:set var="rxtxDefaultInputBufferSize" value="512" />
<c:set var="rxtxDefaultOutputBufferSize" value="512" />
<c:set var="rxtxDefaultDataBits" value="8" />
<c:set var="rxtxDefaultStopBits" value="1" />
<c:set var="rxtxDefaultParity" value="NONE"/>

<%@ include file="/WEB-INF/jsp/dataSourceEdit/editSerialSettings.jsp" %>

<tr>
    <td colspan="3">
        <label class="formLabelRequired" for="responseTimeOutOffset" ><fmt:message key="dsEdit.mbus.responseTimeOutOffset"/></label>
        <input class="formShort" type="text" id="responseTimeOutOffset" value="${dataSource.connection.responseTimeOutOffset}" />
        <label class="formLabelRequired">ms</label>
    </td>
</tr>
<tr>
    <td colspan="3">
        <label class="formLabelRequired" for="updatePeriods" ><fmt:message key="dsEdit.updatePeriod"/></label>
        <input class="formShort" type="text" id="updatePeriods" value="${dataSource.updatePeriods}" />
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
            <tbody>
                <tr><td colspan="2" class="smallTitle"><fmt:message key="dsEdit.mbus.search"/></td></tr>
                <tr>
                    <td>
                        <input type="radio" name="addressingType" id="usePrimnaryAddressing" value="PRIMARY" onclick="updateAddressing()">
                        <label class="formLabelRequired" for="usePrimnaryAddressing"><fmt:message key="dsEdit.mbus.usePrimaryAddressing"/></label>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table>
                            <tr>
                                <td width="30" />
                                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.firstHexAddress"/></td>
                                <td class="formField"><input type="text" id="firstPrimaryAddress" value="00"/></td>
                            </tr>
                            <tr>
                                <td width="30" />
                                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.lastHexAddress"/></td>
                                <td class="formField"><input type="text" id="lastPrimaryAddress" value="FA"/></td>
                            </tr>
                        </table>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="radio" name="addressingType" id="useSecondaryAddressing" checked="checked" value="SECONDARY" onclick="updateAddressing()">
                        <label class="formLabelRequired" for="useSecondaryAddressing"><fmt:message key="dsEdit.mbus.useSecondaryAddressing"/></label>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table>
                            <tr>
                                <td width="30" />
                                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.id"/></td>
                                <td class="formField"><input type="text" id="secAddrId" value=""/></td>
                            </tr>
                            <tr>
                                <td width="30" />
                                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.man"/></td>
                                <td class="formField"><input type="text" id="secAddrMan" value=""/></td>
                            </tr>
                            <tr>
                                <td width="30" />
                                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.secAddrMedium"/></td>
                                <td class="formField">
                                    <sst:select id="secAddrMedium" value="">
                                        <sst:option></sst:option>
                                        <c:forEach items="${dataSource.labels}" var="mediumLabel">
                                            <sst:option value="${mediumLabel}">${mediumLabel}</sst:option>
                                        </c:forEach>
                                    </sst:select>
                                </td>
                            </tr>
                            <tr>
                                <td width="30" />
                                <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.version"/></td>
                                <td class="formField"><input type="text" id="secAddrVersion" value=""/></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </tbody>
        </table>
        <table>
            <tr>
                <td colspan="2" align="center">
                        <input id="searchBtn" type="button" value="<fmt:message key="dsEdit.mbus.search"/>" onclick="search();"/>
                        <input id="cancelSearchBtn" type="button" value="<fmt:message key="common.cancel"/>" onclick="cancelSearch();"/>
                </td>
            </tr>

            <tr><td colspan="2" id="searchMessage" class="formError"></td></tr>

            <tr>
                <td colspan="2">
                    <table cellspacing="1">
                        <tr class="rowHeader">
                            <td><fmt:message key="dsEdit.mbus.addressHex"/></td>
                            <td><fmt:message key="dsEdit.mbus.identNumber"/></td>
                            <td><fmt:message key="dsEdit.mbus.medium"/></td>
                            <td><fmt:message key="dsEdit.mbus.manufacturer"/></td>
                            <td><fmt:message key="dsEdit.mbus.versionHex"/></td>
                        </tr>
                        <tbody id="mbusDevices"></tbody>
                    </table>
                </td>
            </tr>

            <tbody id="responseFrames">
                <tr><td colspan="2"><div dojoType="Tree" toggle="wipe" widgetId="responseFramesTree"></div></td></tr>
            </tbody>

            <%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>


            <tag:pointList pointHelpId="mbusPP">

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.correctionFactor"/></td>
                    <td class="formField"><input type="text" id="correctionFactor"/></td>
                </tr>
                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.addressing"/></td>
                    <td class="formField"><input type="text" id="addressing" disabled="disabled"/></td>
                </tr>
                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.addressHex"/></td>
                    <td class="formField"><input type="text" id="addressHex" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.identNumber"/></td>
                    <td class="formField"><input type="text" id="identNumber" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.medium"/></td>
                    <td class="formField"><input type="text" id="medium" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.manufacturer"/></td>
                    <td class="formField"><input type="text" id="manufacturer" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.versionHex"/></td>
                    <td class="formField"><input type="text" id="versionHex" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.responseFrame"/></td>
                    <td class="formField"><input type="text" id="responseFrame" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.difCode"/></td>
                    <td class="formField"><input type="text" id="difCode" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.functionField"/></td>
                    <td class="formField"><input type="text" id="functionField" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.deviceUnit"/></td>
                    <td class="formField"><input type="text" id="deviceUnit" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.tariff"/></td>
                    <td class="formField"><input type="text" id="tariff" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.storageNumber"/></td>
                    <td class="formField"><input type="text" id="storageNumber" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.vifType"/></td>
                    <td class="formField"><input type="text" id="vifType" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.vifLabel"/></td>
                    <td class="formField"><input type="text" id="vifLabel" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.unitOfMeasurement"/></td>
                    <td class="formField"><input type="text" id="unitOfMeasurement" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.siPrefix"/></td>
                    <td class="formField"><input type="text" id="siPrefix" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.exponent"/></td>
                    <td class="formField"><input type="text" id="exponent" disabled="disabled"/></td>
                </tr>

                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.vifeTypes"/></td>
                    <td class="formField"><input type="text" id="vifeTypes" disabled="disabled"/></td>
                </tr>
                <tr>
                    <td class="formLabelRequired"><fmt:message key="dsEdit.mbus.vifeLabels"/></td>
                    <td class="formField"><input type="text" id="vifeLabels" disabled="disabled"/></td>
                </tr>

            </tag:pointList>
