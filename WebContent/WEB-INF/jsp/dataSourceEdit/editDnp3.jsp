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
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>
<%@page import="br.org.scadabr.vo.dataSource.dnp3.Dnp3PointLocatorVO"%>

<script type="text/javascript">
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.dnp3.index"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.index; };
  }
  
  function changeDataType() {
      if(isSettablePointSelected()) {
    	  var typeSelected = document.getElementById("datatype").selectedIndex;
    	  show("divWrittable");
    	  hide("divBinWrittable");
    	  if(typeSelected == 1) {
			show("divBinWrittable");
          }
      } else {
    	  hide("divWrittable");
    	  hide("divBinWrittable");
      }
  }
  
  function isSettablePointSelected() {
	  var typeSelected = document.getElementById("datatype").selectedIndex;
	  if(typeSelected == 1 || typeSelected == 3) {
		  return true;
      } else {
          return false;
      }
  }
  
  function initImpl() {
  }
  
  function editPointCBImpl(locator) {
	  $set("index", locator.index);
	  $set("datatype", locator.dnp3DataType);
	  if(currentPoint.id != -1) {
		  document.getElementById("addMany").checked = false;
		  document.getElementById("minIndex").disabled = true;
	      document.getElementById("maxIndex").disabled = true;
	      document.getElementById("index").disabled = false;
	      document.getElementById("name").disabled = false;
	      document.getElementById("xid").disabled = false;
		  document.getElementById("addMany").disabled = true;
	  } else {
		  document.getElementById("addMany").disabled = false;
	  }
	  changeDataType();
	  if(isSettablePointSelected()) {
		  $set("operateMode", locator.operateMode);
		  $set("controlCommand", locator.controlCommand);
		  $set("timeOn", locator.timeOn);
		  $set("timeOff", locator.timeOff);
	  }
  }

  function savePointImpl(locator) {
	  var checkValue = dwr.util.getValue("addMany");
	  var name = "";
	  locator.settable = false;
	  locator.dnp3DataType = $get("datatype");
	  if(isSettablePointSelected()) {
			locator.settable = true;
			locator.operateMode = $get("operateMode");
			locator.controlCommand = $get("controlCommand");
			locator.timeOn = $get("timeOn");
			locator.timeOff = $get("timeOff");
	  } 
	  if(checkValue == false) {
		  locator.index = $get("index");
		  
		  DataSourceEditDwr.saveDnp3PointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
	  } else {
		  var names = new Array($get("maxIndex") - $get("minIndex"));
		  var index = new Array($get("maxIndex") - $get("minIndex"));
		  var locators = new Array($get("maxIndex") - $get("minIndex"));
		  qntPoints = $get("maxIndex") - $get("minIndex");
		  for(var i = 0; i <= qntPoints; i++) {
			  index[i] = parseInt($get("minIndex")) + i;
			  locators[i] = locator;
			  if ($get("datatype") == 00) {
					names[i] = ("<fmt:message key="dsEdit.dnp3.binaryInput"/>" + " " + (parseInt($get("minIndex")) + i));
			 }  else if ($get("datatype") == 0x10) {
					names[i] = ("<fmt:message key="dsEdit.dnp3.binaryOutput"/>" + " " + (parseInt($get("minIndex")) + i));
			 }  else if ($get("datatype") == 0x30) {
					names[i] = ("<fmt:message key="dsEdit.dnp3.analogInput"/>" + " " + (parseInt($get("minIndex")) + i));
			 }  else if ($get("datatype") == 0x50) {
					names[i] = ("<fmt:message key="dsEdit.dnp3.analogOutput"/>" + " " + (parseInt($get("minIndex")) + i));
			 }  else if ($get("datatype") == 0x20) {
					names[i] = ("<fmt:message key="dsEdit.dnp3.runningCounter"/>" + " " + (parseInt($get("minIndex")) + i));
			 }
		  }
		  DataSourceEditDwr.saveMultipleDnp3PointLocator(names, index, locators, savePointCB);
	  }
  }

  function changeAddMany() {
      var checkValue = dwr.util.getValue("addMany");
      if(checkValue == false) {
	      document.getElementById("minIndex").disabled = true;
	      document.getElementById("maxIndex").disabled = true;
	      document.getElementById("index").disabled = false;
	      document.getElementById("name").disabled = false;
	      document.getElementById("xid").disabled = false;
      } else {
    	  document.getElementById("minIndex").disabled = false;
	      document.getElementById("maxIndex").disabled = false;
	      document.getElementById("index").disabled = true;
	      document.getElementById("name").disabled = true;
	      document.getElementById("xid").disabled = true;
      }
  }
  
  //
  // List manipulation.
  function addListValue(prefix) {
      var theValue = $get(prefix);
      var theNumber = parseInt(theValue);
      if (isNaN(theNumber)) {
          alert("<fmt:message key="dsEdit.virtual.errorParsingValue"/>");
          return false;
      }
      var arr = currentPoint.pointLocator[prefix +"Change"].values;
      for (var i=arr.length-1; i>=0; i--) {
          if (arr[i] == theNumber) {
              alert("<fmt:message key="dsEdit.virtual.invalidValue"/> "+ theNumber);
              return false;
          }
      }
      arr[arr.length] = theNumber;
      arr.sort( function(a,b) { return a-b; } );
      refreshValueList(prefix, arr);
      $set(prefix, theNumber + 1);
      return false;
  }
  
  function removeListValue(theValue, prefix) {
      var arr = currentPoint.pointLocator[prefix +"Change"].values;
      for (var i=arr.length-1; i>=0; i--) {
          if (arr[i] == theValue)
              arr.splice(i, 1);
      }
      refreshValueList(prefix, arr);
      return false;
  }
  
  function refreshValueList(prefix, arr) {
      dwr.util.removeAllRows(prefix +"Values");
      dwr.util.addRows(prefix +"Values", arr, [
              function(data) { return data; },
              function(data) {
                  return writeImage(null, null, "bullet_delete", "<fmt:message key="common.delete"/>",
                          "removeListValue("+ data +", '"+ prefix +"');");
              }
              ]);
      var startDD = $(prefix +"Change.startValue");
      var currentStart = startDD.value;
      dwr.util.removeAllOptions(startDD);
      dwr.util.addOptions(startDD, arr);
      startDD.value = currentStart;
  }
</script>

<c:choose>
  <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.DNP3_SERIAL']}">
    <c:set var="dsDesc"><fmt:message key="dsEdit.dnp3.descSerial"/></c:set>
    <c:set var="dsHelpId" value="dnp3DS"/>
  </c:when>
  <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.DNP3_IP']}">
    <c:set var="dsDesc"><fmt:message key="dsEdit.dnp3.descIp"/></c:set>
    <c:set var="dsHelpId" value="dnp3DS"/>
  </c:when>
</c:choose>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>

  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.sourceAddress"/></td>
    <td class="formField"><input id="sourceAddress" type="text" value="${dataSource.sourceAddress}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.slaveAddress"/></td>
    <td class="formField"><input id="slaveAddress" type="text" value="${dataSource.slaveAddress}"/></td>
  </tr>
  <c:choose>
    <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.DNP3_SERIAL']}">
      <%@ include file="/WEB-INF/jsp/dataSourceEdit/editDnp3Serial.jsp" %>
    </c:when>
    <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.DNP3_IP']}">
      <%@ include file="/WEB-INF/jsp/dataSourceEdit/editDnp3Ip.jsp" %>
    </c:when>
  </c:choose>
  <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
          <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.retries"/></td>
    <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.timeout"/></td>
    <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
  </tr>
  
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.rbePeriod"/></td>
    <td class="formField">
      <input type="text" id="rbePollPeriods" value="${dataSource.rbePollPeriods}" class="formShort" />
      <sst:select id="rbePeriodType" value="${dataSource.rbePeriodType}">
        <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
      </sst:select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.staticPeriod"/></td>
    <td class="formField">
      <input type="text" id="staticPollPeriods" value="${dataSource.staticPollPeriods}" class="formShort" />
    </td>
  </tr>
  
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="dnp3PP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.addMany"/></td>
    <td class="formField"><input type="checkbox" id="addMany" onchange="changeAddMany();"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.minIndex"/></td>
    <td class="formField"><input id="minIndex" disabled="disabled" type="text" value=""/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.maxIndex"/></td>
    <td class="formField"><input id="maxIndex" disabled="disabled" type="text" value=""/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.index"/></td>
    <td class="formField"><input id="index" type="text" value="0"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.dataType"/></td>
    <td class="formField">
      <select id="datatype" onchange="changeDataType();">
        <option value="<c:out value="<%= Dnp3PointLocatorVO.BINARY_INPUT %>"/>">BINARY INPUT</option>
        <option value="<c:out value="<%= Dnp3PointLocatorVO.BINARY_OUTPUT %>"/>">BINARY OUTPUT</option>
        <option value="<c:out value="<%= Dnp3PointLocatorVO.ANALOG_INPUT %>"/>">ANALOG INPUT</option>
        <option value="<c:out value="<%= Dnp3PointLocatorVO.ANALOG_OUTPUT %>"/>">ANALOG OUTPUT</option>
        <!--<option value="<c:out value="<%= Dnp3PointLocatorVO.RUNNING_COUNTER %>"/>">RUNNING COUNTER</option>-->
      </select>
    </td>
  </tr>
  <tbody id="divWrittable" style="display: none;">
   <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.operateMode"/></td>
    <td class="formField">
      <select id="operateMode">
        <!--<option value="<c:out value="<%= Dnp3PointLocatorVO.SBO %>"/>">Select Before Operate</option>-->
        <option value="<c:out value="<%= Dnp3PointLocatorVO.DIRECT %>"/>">Direct Operate</option>
        <!--<option value="<c:out value="<%= Dnp3PointLocatorVO.DIRECT_NO_ACK %>"/>">Direct Operate No Ack</option>-->
      </select>
    </td>
  </tr>
  </tbody>
  <tbody id="divBinWrittable" style="display: none;">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.controlCommand"/></td>
    <td class="formField">
      <select id="controlCommand">
        <option value="<c:out value="<%= Dnp3PointLocatorVO.CLOSE_TRIP %>"/>">Close/Trip</option>
        <option value="<c:out value="<%= Dnp3PointLocatorVO.PULSE %>"/>">Pulse ON/OFF</option>
        <option value="<c:out value="<%= Dnp3PointLocatorVO.LATCH %>"/>">Latch ON/OFF</option>
      </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.timeOn"/></td>
    <td class="formField"><input id="timeOn" type="text" value="0"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3.timeOff"/></td>
    <td class="formField"><input id="timeOff" type="text" value="0"/></td>
  </tr>
  </tbody>
</tag:pointList>