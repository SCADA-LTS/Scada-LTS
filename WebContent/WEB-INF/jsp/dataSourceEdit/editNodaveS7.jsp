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
      DataSourceEditDwr.saveNodaveS7DataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"),$get("filePath"), $get("quantize"), $get("nodaveWriteBaseCmd"), saveDataSourceCB);
  }
  
  function initImpl() {
	  hide("tsDiv1");
	  hide("tsDiv2");
  }
  
  function editPointCBImpl(locator) {
	  $set("valueRegex", locator.valueRegex);
	  $set("dataTypeId", locator.dataType);
	  $set("settable", locator.settable);
	  $set("customTimestamp",locator.customTimestamp);
	  
	  checkTimestampChanged();

	  if(locator.customTimestamp) {
		  $set("timestampRegex", locator.timestampRegex);
		  $set("timestampFormat",locator.timestampFormat);
	  }
	  $set("s7writeMemoryArea", locator.s7writeMemoryArea);
	  $set("s7writeDBNUM", locator.s7writeDBNUM);
	  $set("s7writeStarts", locator.s7writeStarts);
	  $set("s7writeBytesQty", locator.s7writeBytesQty);
	  $set("s7writeBitOffset", locator.s7writeBitOffset);
	  
  }
  
  function savePointImpl(locator) {
      delete locator.dataTypeId;
      delete locator.relinquishable;
	  locator.dataType = $get("dataTypeId");
      locator.valueRegex = $get("valueRegex");
	  locator.customTimestamp = $get("customTimestamp");
	  locator.timestampFormat = $get("timestampFormat");
      locator.timestampRegex = $get("timestampRegex");
      locator.settable = $get("settable");
      locator.s7writeMemoryArea = $get("s7writeMemoryArea");
      locator.s7writeDBNUM = $get("s7writeDBNUM");
      locator.s7writeStarts = $get("s7writeStarts")
      locator.s7writeBytesQty = $get("s7writeBytesQty");
      locator.s7writeBitOffset = $get("s7writeBitOffset");
      DataSourceEditDwr.saveNodaveS7PointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  function checkFile() {
	  DataSourceEditDwr.checkFile($get("filePath"), checkFileCB);
  }

  function checkFileCB(exists) {
	  if(exists) {
		  alert('Arquivo OK!');
	  } else {
		  alert('Arquivo não encontrado!');
	  }
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
	  $set("valueRegex",$get("valueSuggestions"));
  }

  function timestampSuggestChanged() {
	  suggest = document.getElementById("timestampSuggestions");
	  index = suggest.selectedIndex;
	  dataFormat = suggest.options[index].title;
	  $set("timestampFormat",dataFormat);
	  $set("timestampRegex",$get("timestampSuggestions"));
  }
  
  function checkTimestampChanged() {
	  if($get("customTimestamp")) {
		  show("tsDiv1");
		  show("tsDiv2");
	  } else {
		  hide("tsDiv1");
		  hide("tsDiv2");
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
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.nodaves7.desc"/></c:set>
<c:set var="dsHelpId" value="asciiFileReaderDS"/>
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
          <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
          <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
  </tr>
  <tr>
	<td class="formLabelRequired"><fmt:message key="dsEdit.asciiFile.filePath"/></td>
  	<td class="formField"><input id="filePath" type="text" value="${dataSource.filePath}"/></td>

  	<td>
		<input id="checkBtn" type="button" value="<fmt:message key="dsEdit.asciiFile.checkFile"/>" onclick="checkFile();" />
	</td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.nodaves7.writeBaseCmd"/></td>
   	<td class="formField"><input id="nodaveWriteBaseCmd" type="text" value="${dataSource.nodaveWriteBaseCmd}"/></td>
  </tr>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="asciiFileReaderPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select id="dataTypeId">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiFile.valueRegex"/></td>
    <td class="formField">
    	<input id="valueRegex" type="text" value=""/>
    	<select id="valueSuggestions" onchange="valueSuggestChanged();">
    		<option value=""> &nbsp; </option>
        	<option value="((\b[0-9]+)?\.)?[0-9]+\b"> <fmt:message key="dsEdit.asciiFile.regex.number"/>  </option>
      	</select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiFile.timestampCheck"/></td>
    <td class="formField"><input id="customTimestamp" type="checkbox" onchange="checkTimestampChanged();"/></td>
  </tr>
   	<tr>
    	<td class="formLabelRequired"><fmt:message key="dsEdit.settable" /></td>
    	<td class="formField"><input id="settable" type="checkbox" ></td>
  	</tr>
  
  	  <tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.nodaves7.s7writeMemoryArea"/></td>
	    <td class="formField">
	    	<input id="s7writeMemoryArea" type="text" value=""/>
	    </td> 
	  <tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.nodaves7.s7writeDBNUM"/></td>
	    <td class="formField">
	    	<input id="s7writeDBNUM" type="text" value=""/>
	    </td> 
	  <tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.nodaves7.s7writeStarts"/></td>
	    <td class="formField">
	    	<input id="s7writeStarts" type="text" value=""/>
	    </td> 
	  <tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.nodaves7.s7writeBytesQty"/></td>
	    <td class="formField">
	    	<input id="s7writeBytesQty" type="text" value=""/>
	    </td> 
	  <tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.nodaves7.s7writeBitOffset"/></td>
	    <td class="formField">
	    	<input id="s7writeBitOffset" type="text" value=""/>
	    </td> 

	  <tr id="tsDiv1">
	    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiFile.timestampFormat"/></td>
	    <td class="formField">
	    	<input id="timestampFormat" type="text" value=""/>
	    	<select id="timestampSuggestions" onchange="timestampSuggestChanged();">
	    		<option value=""> &nbsp; </option>
	        	<option value="20\d{2}\/((0[1-9])|(1[0-2]))\/((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yyyy/MM/dd KK:mm:ss"> YYYY/MM/DD HH:MM:SS (2010/12/25 18:30:00) </option>
	        	<option value="20\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yyyy-MM-dd KK:mm:ss"> YYYY-MM-DD HH:MM:SS (2010-12-25 18:30:00)  </option>
	        	<option value="\d{2}\/((0[1-9])|(1[0-2]))\/((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yy/MM/dd KK:mm:ss"> YY/MM/DD HH:MM:SS (10/12/25 18:30:00) </option>
	        	<option value="\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))\s(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])" title="yy-MM-dd KK:mm:ss"> YY-MM-DD HH:MM:SS (10-12-25 18:30:00) </option>
	      	</select>
	    </td>
	  </tr>
	  <tr id="tsDiv2">
	    <td class="formLabelRequired"><fmt:message key="dsEdit.asciiFile.timestampRegex"/></td>
	    <td class="formField">
	    	<input id="timestampRegex" type="text" value=""/>
	    </td> 
	  </tr>
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
</tag:pointList>