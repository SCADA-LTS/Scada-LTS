<%--
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2009 Serotonin Software Technologies Inc.
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
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>

<script type="text/javascript">
	var currentChangeType;

	function saveDataSourceImpl() {
		DataSourceEditDwr.saveSocketCommDataSource($get("dataSourceName"),
				$get("dataSourceXid"), $get("updatePeriods"),
				$get("updatePeriodType"), $get("host"), $get("port"),
				$get("timeout"), $get("retries"), $get("commandFormat"),
				$get("stopMode"), $get("nChar"), $get("charStopMode"),
				$get("charX"), $get("hexValue"), $get("stopTimeout"),
				$get("initString"), $get("bufferSize"), $get("quantize"),
				$get("sameFormat"), saveDataSourceCB);
	}

	function initImpl() {
		hide("tsDiv1");
		hide("tsDiv2");
		changeStopMode();
		checkResponseFormat();
		
	}

	function editPointCBImpl(locator) {
		$set("valueRegex", locator.valueRegex);
		$set("dataTypeId", locator.dataType);
		$set("commandNumber", locator.commandNumber);
		$set("firstCommand", locator.firstCommand);
		$set("firstCommandHexASCII", locator.firstCommandHexASCII);
		$set("firstExpectedResponse", locator.firstExpectedResponse);
		$set("firstExpectedResponseHexASCII", locator.firstExpectedResponseHexASCII);
		$set("secondCommand", locator.secondCommand);
		$set("secondCommandHexASCII", locator.secondCommandHexASCII);
		$set("secondExpectedResponse", locator.secondExpectedResponse);
		$set("secondExpectedResponseHexASCII", locator.secondExpectedResponseHexASCII);
		$set("thirdCommand", locator.thirdCommand);
		$set("thirdCommandHexASCII", locator.thirdCommandHexASCII);
		$set("customTimestamp", locator.customTimestamp);
		checkTimestampChanged();
		showCommands();

		if (locator.customTimestamp) {
			$set("timestampRegex", locator.timestampRegex);
			$set("timestampFormat", locator.timestampFormat);
		}
	}

	function savePointImpl(locator) {
		locator.dataType = $get("dataTypeId");
		locator.commandNumber = $get("commandNumber");
		locator.firstCommand = $get("firstCommand");
		locator.firstCommandHexASCII = $get("firstCommandHexASCII");
		locator.firstExpectedResponse = $get("firstExpectedResponse");
		locator.firstExpectedResponseHexASCII = $get("firstExpectedResponseHexASCII");
		locator.secondCommand = $get("secondCommand");
		locator.secondCommandHexASCII = $get("secondCommandHexASCII");
		locator.secondExpectedResponse = $get("secondExpectedResponse");
		locator.secondExpectedResponseHexASCII = $get("secondExpectedResponseHexASCII");
		locator.thirdCommand = $get("thirdCommand");
		locator.thirdCommandHexASCII = $get("thirdCommandHexASCII");
		locator.valueRegex = $get("valueRegex");
		locator.customTimestamp = $get("customTimestamp");
		locator.timestampFormat = $get("timestampFormat");
		locator.timestampRegex = $get("timestampRegex");

		DataSourceEditDwr.saveSocketCommPointLocator(currentPoint.id,
				$get("xid"), $get("name"), locator, savePointCB);
	}

	function changeDataType() {
		DataSourceEditDwr.getChangeTypes($get("dataTypeId"), changeDataTypeCB);
	}

	function changeDataTypeCB(changeTypes) {
		var changeTypeDD = $("changeTypeId");
		var savedType = changeTypeDD.value;
		dwr.util.removeAllOptions(changeTypeDD);
		dwr.util.addOptions(changeTypeDD, changeTypes, "key", "message");
		changeTypeDD.value = savedType;
		changeChangeType();
	}

	function valueSuggestChanged() {
		$set("valueRegex", $get("valueSuggestions"));
	}

	function timestampSuggestChanged() {
		suggest = document.getElementById("timestampSuggestions");
		index = suggest.selectedIndex;
		dataFormat = suggest.options[index].title;
		$set("timestampFormat", dataFormat);
		$set("timestampRegex", $get("timestampSuggestions"));
	}

	function checkTimestampChanged() {
		if ($get("customTimestamp")) {
			show("tsDiv1");
			show("tsDiv2");
		} else {
			hide("tsDiv1");
			hide("tsDiv2");
		}
	}

	function changeStopMode() {
		var sm = $get("stopMode");
		if (sm == 0) {
			setDisabled("nChar", false);
			setDisabled("charStopMode", true);
			setDisabled("charX", true);
			setDisabled("hexValue", true);
			setDisabled("stopTimeout", true);
		} else if (sm == 1) {
			setDisabled("nChar", true);
			setDisabled("charStopMode", false);
			changeCharStopMode();
			setDisabled("stopTimeout", true);
		} else {
			setDisabled("nChar", true);
			setDisabled("charStopMode", true);
			setDisabled("charX", true);
			setDisabled("hexValue", true);
			setDisabled("stopTimeout", false);
		}
	}

	function changeCharStopMode() {
		var sm = $get("charStopMode");
		if (sm == 0) {
			setDisabled("charX", false);
			setDisabled("hexValue", true);
		} else {
			setDisabled("charX", true);
			setDisabled("hexValue", false);
		}
	}
	//
	// List manipulation.
	function addListValue(prefix) {
	}

	function removeListValue(theValue, prefix) {
	}

	function refreshValueList(prefix, arr) {
	}

	function checkResponseFormat() {
		if ($get("sameFormat")) {
			setDisabled("stopMode", true);
			setDisabled("nChar", true);
			setDisabled("charStopMode", true);
			setDisabled("charX", true);
			setDisabled("hexValue", true);
			setDisabled("stopTimeout", true);
		} else {
			setDisabled("stopMode", false);
			setDisabled("charStopMode", false);
			changeStopMode();
			changeCharStopMode();
		}
	}

	function showCommands() {
		var cmd_number = $get("commandNumber");		
		if (cmd_number == 0) {
			document.getElementById("firstCommandLbl").style.display = "none";
			document.getElementById("firstCommand").style.display = "none";
			document.getElementById("firstExpectedResponseLbl").style.display = "none";
			document.getElementById("firstExpectedResponse").style.display = "none";
			document.getElementById("secondCommandLbl").style.display = "none";
			document.getElementById("secondCommand").style.display = "none";
			document.getElementById("secondExpectedResponseLbl").style.display = "none";
			document.getElementById("secondExpectedResponse").style.display = "none";
			document.getElementById("thirdCommandLbl").style.display = "none";
			document.getElementById("thirdCommand").style.display = "none";			
		} else if (cmd_number == 1){
			document.getElementById("firstCommandLbl").style.display = "inline";
			document.getElementById("firstCommand").style.display = "inline";		
			document.getElementById("firstExpectedResponseLbl").style.display = "none";
			document.getElementById("firstExpectedResponse").style.display = "none";
			document.getElementById("secondCommandLbl").style.display = "none";
			document.getElementById("secondCommand").style.display = "none";
			document.getElementById("secondExpectedResponseLbl").style.display = "none";
			document.getElementById("secondExpectedResponse").style.display = "none";
			document.getElementById("thirdCommandLbl").style.display = "none";
			document.getElementById("thirdCommand").style.display = "none";
		} else if (cmd_number == 2){
			document.getElementById("firstCommandLbl").style.display = "inline";
			document.getElementById("firstCommand").style.display = "inline";
			document.getElementById("firstExpectedResponseLbl").style.display = "inline";
			document.getElementById("firstExpectedResponse").style.display = "inline";
			document.getElementById("secondCommandLbl").style.display = "inline";
			document.getElementById("secondCommand").style.display = "inline";
			document.getElementById("secondExpectedResponseLbl").style.display = "none";
			document.getElementById("secondExpectedResponse").style.display = "none";
			document.getElementById("thirdCommandLbl").style.display = "none";
			document.getElementById("thirdCommand").style.display = "none";
		} else if (cmd_number == 3) {
			document.getElementById("firstCommandLbl").style.display = "inline";
			document.getElementById("firstCommand").style.display = "inline";
			document.getElementById("firstExpectedResponseLbl").style.display = "inline";
			document.getElementById("firstExpectedResponse").style.display = "inline";
			document.getElementById("secondCommandLbl").style.display = "inline";
			document.getElementById("secondCommand").style.display = "inline";
			document.getElementById("secondExpectedResponseLbl").style.display = "inline";
			document.getElementById("secondExpectedResponse").style.display = "inline";
			document.getElementById("thirdCommandLbl").style.display = "inline";
			document.getElementById("thirdCommand").style.display = "inline";
		}
		
		showHexASCIIBox(document.getElementById("firstCommand"));
		showHexASCIIBox(document.getElementById("firstExpectedResponse"));
		showHexASCIIBox(document.getElementById("secondCommand"));
		showHexASCIIBox(document.getElementById("secondExpectedResponse"));
		showHexASCIIBox(document.getElementById("thirdCommand"));
	}
	
	function showHexASCIIBox(box) {
		if (((box.value == "HEX" ) || (box.value == "ASCII")) && document.getElementById(box.id).style.display != "none") {
			document.getElementById(box.id + "HexASCIILbl").style.display = "inline";
			document.getElementById(box.id + "HexASCII").style.display = "inline";
		} else {
			document.getElementById(box.id + "HexASCIILbl").style.display = "none";
			document.getElementById(box.id + "HexASCII").style.display = "none";
		}
	}
</script>

<c:set var="dsDesc">
	<fmt:message key="dsEdit.socketComm.desc" />
</c:set>
<c:set var="dsHelpId" value="socketCommDS" />
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf"%>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.host" /></td>
	<td class="formField"><input type="text" id="host"
		value="${dataSource.host}" /></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.port" /></td>
	<td class="formField"><input type="text" id="port"
		value="${dataSource.port}" /></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.updatePeriod" /></td>
	<td class="formField"><input type="text" id="updatePeriods"
		value="${dataSource.updatePeriods}" class="formShort" /> <sst:select
			id="updatePeriodType" value="${dataSource.updatePeriodType}">
			<tag:timePeriodOptions sst="true" ms="true" s="true" min="true"
				h="true" />
		</sst:select></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message key="dsEdit.quantize" /></td>
	<td class="formField"><sst:checkbox id="quantize"
			selectedValue="${dataSource.quantize}" /></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.retries" /></td>
	<td class="formField"><input type="text" id="retries"
		value="${dataSource.retries}" /></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.timeout" /></td>
	<td class="formField"><input type="text" id="timeout"
		value="${dataSource.timeout}" /></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.commandFormat" /></td>
	<td class="formField"><sst:select id="commandFormat"
			value="${dataSource.commandFormat}">
			<sst:option value="0">
				<fmt:message key="dsEdit.socketComm.commandFormat.None" />
			</sst:option>
			<sst:option value="1">
				<fmt:message key="dsEdit.socketComm.commandFormat.CR_LF" />
			</sst:option>
			<sst:option value="2">
				<fmt:message key="dsEdit.socketComm.commandFormat.CR" />
			</sst:option>
			<sst:option value="3">
				<fmt:message key="dsEdit.socketComm.commandFormat.LF" />
			</sst:option>
			<sst:option value="4">
				<fmt:message key="dsEdit.socketComm.commandFormat.NULL" />
			</sst:option>
		</sst:select></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.responseSameFormat" /></td>
	<td class="formField"><sst:checkbox id="sameFormat"
			selectedValue="${dataSource.sameFormat}"
			onclick="checkResponseFormat()" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.stopMode" /></td>
	<td class="formField"><sst:select id="stopMode"
			onchange="changeStopMode()" value="${dataSource.stopMode}">
			<sst:option value="0">
				<fmt:message key="dsEdit.socketComm.stopMode.nChar" />
			</sst:option>
			<sst:option value="1">
				<fmt:message key="dsEdit.socketComm.stopMode.charX" />
			</sst:option>
			<sst:option value="2">
				<fmt:message key="dsEdit.socketComm.stopMode.stopTimeout" />
			</sst:option>
		</sst:select></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.nChar" /></td>
	<td class="formField"><input id="nChar" type="text"
		value="${dataSource.nChar}" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.charStopMode" /></td>
	<td class="formField"><sst:select id="charStopMode"
			onchange="changeCharStopMode()" value="${dataSource.charStopMode}">
			<sst:option value="0">
				<fmt:message key="dsEdit.socketComm.charStopMode.charASCII" />
			</sst:option>
			<sst:option value="1">
				<fmt:message key="dsEdit.socketComm.charStopMode.hexValue" />
			</sst:option>
		</sst:select></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.charStopMode.charASCII" /></td>
	<td class="formField"><input id="charX" type="text"
		value="${dataSource.charX}" disabled="disabled" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.charStopMode.hexValue" /></td>
	<td class="formField"><input id="hexValue" type="text"
		value="${dataSource.hexValue}" disabled="disabled" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.stopTimeout" /></td>
	<td class="formField"><input id="stopTimeout" type="text"
		value="${dataSource.stopTimeout}" disabled="disabled" /></td>
</tr>
<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.bufferSize" /></td>
	<td class="formField"><sst:select id="bufferSize"
			value="${dataSource.bufferSize}">
			<sst:option value="2">2</sst:option>
			<sst:option value="4">4</sst:option>
			<sst:option value="8">8</sst:option>
			<sst:option value="16">16</sst:option>
			<sst:option value="32">32</sst:option>
			<sst:option value="64">64</sst:option>
			<sst:option value="256">256</sst:option>
			<sst:option value="1024">1024</sst:option>
			<sst:option value="2048">2048</sst:option>
			<sst:option value="4096">4096</sst:option>
		</sst:select></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
			key="dsEdit.socketComm.initString" /></td>
	<td class="formField"><input id="initString" type="text" value="" />
	</td>
</tr>

<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf"%>
<tag:pointList pointHelpId="socketCommPP">

	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.pointDataType" /></td>
		<td class="formField"><select id="dataTypeId">
				<tag:dataTypeOptions excludeImage="true" />
		</select></td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.socketComm.commandNumber" /></td>
		<td class="formField"><sst:select id="commandNumber" value=""
				onchange="showCommands()">
				<sst:option value="0">0</sst:option>
				<sst:option value="1">1</sst:option>
				<sst:option value="2">2</sst:option>
				<sst:option value="3">3</sst:option>
			</sst:select></td>

	</tr>
	<tr>
		<td class="formLabelRequired"><span id="firstCommandLbl"><fmt:message
				key="dsEdit.socketComm.firstCommand" /></span></td>
		<td class="formField"><sst:select id="firstCommand" value=""
				onchange="showHexASCIIBox(this)">
				<sst:option value="STX">STX</sst:option>
				<sst:option value="ETX">ETX</sst:option>
				<sst:option value="ENQ">ENQ</sst:option>
				<sst:option value="ACK">ACK</sst:option>
				<sst:option value="NAK">NAK</sst:option>
				<sst:option value="HEX">HEX</sst:option>
				<sst:option value="ASCII">ASCII</sst:option>
			</sst:select></td>
		<td class="formLabelRequired"><span id="firstCommandHexASCIILbl"><fmt:message
					key="dsEdit.socketComm.hexASCIIValue" /></span></td>
		<td class="formField"><input id="firstCommandHexASCII"
			type="text" value="" /></td>
	</tr>
	<tr>
		<td class="formLabelRequired"><span id="firstExpectedResponseLbl"><fmt:message
					key="dsEdit.socketComm.firstExpectedResponse" /></span></td>
		<td class="formField"><sst:select id="firstExpectedResponse"
				value="" onchange="showHexASCIIBox(this)">
				<sst:option value="STX">STX</sst:option>
				<sst:option value="ETX">ETX</sst:option>
				<sst:option value="ENQ">ENQ</sst:option>
				<sst:option value="ACK">ACK</sst:option>
				<sst:option value="NAK">NAK</sst:option>
				<sst:option value="HEX">HEX</sst:option>
				<sst:option value="ASCII">ASCII</sst:option>
			</sst:select></td>
		<td class="formLabelRequired"><span
			id="firstExpectedResponseHexASCIILbl"><fmt:message
					key="dsEdit.socketComm.hexASCIIValue" /></span></td>
		<td class="formField"><input id="firstExpectedResponseHexASCII"
			type="text" value="" /></td>

	</tr>
	<tr>
		<td class="formLabelRequired"><span id="secondCommandLbl"><fmt:message
					key="dsEdit.socketComm.secondCommand" /></span></td>
		<td class="formField"><sst:select id="secondCommand" value=""
				onchange="showHexASCIIBox(this)">
				<sst:option value="STX">STX</sst:option>
				<sst:option value="ETX">ETX</sst:option>
				<sst:option value="ENQ">ENQ</sst:option>
				<sst:option value="ACK">ACK</sst:option>
				<sst:option value="NAK">NAK</sst:option>
				<sst:option value="HEX">HEX</sst:option>
				<sst:option value="ASCII">ASCII</sst:option>
			</sst:select></td>
		<td class="formLabelRequired"><span id="secondCommandHexASCIILbl"><fmt:message
					key="dsEdit.socketComm.hexASCIIValue" /></span></td>
		<td class="formField"><input id="secondCommandHexASCII"
			type="text" value="" /></td>
	</tr>
	<tr>
		<td class="formLabelRequired"><span
			id="secondExpectedResponseLbl"><fmt:message
					key="dsEdit.socketComm.secondExpectedResponse" /></span></td>
		<td class="formField"><sst:select id="secondExpectedResponse"
				value="" onchange="showHexASCIIBox(this)">
				<sst:option value="STX">STX</sst:option>
				<sst:option value="ETX">ETX</sst:option>
				<sst:option value="ENQ">ENQ</sst:option>
				<sst:option value="ACK">ACK</sst:option>
				<sst:option value="NAK">NAK</sst:option>
				<sst:option value="HEX">HEX</sst:option>
				<sst:option value="ASCII">ASCII</sst:option>
			</sst:select></td>
		<td class="formLabelRequired"><span
			id="secondExpectedResponseHexASCIILbl"><fmt:message
					key="dsEdit.socketComm.hexASCIIValue" /></span></td>
		<td class="formField"><input id="secondExpectedResponseHexASCII"
			type="text" value="" /></td>

	</tr>
	<tr>
		<td class="formLabelRequired"><span id="thirdCommandLbl"><fmt:message
					key="dsEdit.socketComm.thirdCommand" /></span></td>
		<td class="formField"><sst:select id="thirdCommand" value=""
				onchange="showHexASCIIBox(this)">
				<sst:option value="STX">STX</sst:option>
				<sst:option value="ETX">ETX</sst:option>
				<sst:option value="ENQ">ENQ</sst:option>
				<sst:option value="ACK">ACK</sst:option>
				<sst:option value="HEX">HEX</sst:option>
				<sst:option value="ASCII">ASCII</sst:option>
			</sst:select></td>
		<td class="formLabelRequired"><span id="thirdCommandHexASCIILbl"><fmt:message
					key="dsEdit.socketComm.hexASCIIValue" /></span></td>
		<td class="formField"><input id="thirdCommandHexASCII"
			type="text" value="" /></td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.asciiFile.valueRegex" /></td>
		<td class="formField"><input id="valueRegex" type="text" value="" />
		</td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.asciiFile.timestampCheck" /></td>
		<td class="formField"><input id="customTimestamp" type="checkbox"
			onchange="checkTimestampChanged();" /></td>
	</tr>
	<tr id="tsDiv1">
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.asciiFile.timestampFormat" /></td>
		<td class="formField"><input id="timestampFormat" type="text"
			value="" /> <select id="timestampSuggestions"
			onchange="timestampSuggestChanged();">
				<option value="">&nbsp;</option>
				<option
					value="20\d{2}\/((0[1-9])|(1[0-2]))\/((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])"
					title="yyyy/MM/dd KK:mm:ss">YYYY/MM/DD HH:MM:SS
					(2010/12/25 18:30:00)</option>
				<option
					value="20\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])"
					title="yyyy-MM-dd KK:mm:ss">YYYY-MM-DD HH:MM:SS
					(2010-12-25 18:30:00)</option>
				<option
					value="\d{2}\/((0[1-9])|(1[0-2]))\/((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])"
					title="yy/MM/dd KK:mm:ss">YY/MM/DD HH:MM:SS (10/12/25
					18:30:00)</option>
				<option
					value="\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])"
					title="yy-MM-dd KK:mm:ss">YY-MM-DD HH:MM:SS (10-12-25
					18:30:00)</option>
		</select></td>
	</tr>
	<tr id="tsDiv2">
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.asciiFile.timestampRegex" /></td>
		<td class="formField"><input id="timestampRegex" type="text"
			value="" /></td>
	</tr>

</tag:pointList>