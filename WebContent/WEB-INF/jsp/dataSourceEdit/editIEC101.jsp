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
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="br.org.scadabr.rt.dataSource.iec101.IEC101Master"%>
<%@page import="com.serotonin.modbus4j.code.DataType"%>

<script type="text/javascript">
  function initImpl() {
	  hide('commandFields');
	  hide('singleCommandQualifier');
  }
  
  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
	  pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.iec101.iec101DataType"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.iec101DataType; };

      pointListColumnHeaders[pointListColumnHeaders.length] = "<fmt:message key="dsEdit.iec101.objectAddress"/>";
      pointListColumnFunctions[pointListColumnFunctions.length] = function(p) { return p.pointLocator.objectAddress; };
  }
  
  function editPointCBImpl(locator) {
      $set("objectAddress", locator.objectAddress);
      $set("iec101DataType", locator.iec101DataType);
      $set("settable",locator.settable);
      $set("controlMode",locator.controlMode);
      $set("offset",locator.offset);
      $set("qualifier",locator.qualifier);
      settableChanged();
      
  }
  
  function savePointImpl(locator) {
	  delete locator.settable;
      delete locator.objectAddress;
      delete locator.iec101DataType;
      
      locator.objectAddress = $get("objectAddress");
      locator.iec101DataType = $get("iec101DataType");
      locator.settable = $get("settable");
      locator.controlMode = $get("controlMode");
      locator.offset = $get("offset");
      locator.qualifier= $get("qualifier");
      
      DataSourceEditDwr.saveIEC101PointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  var singlePoint = "<%= IEC101Master.SINGLE_POINT_INFORMATION%>";
  var doublePoint = "<%= IEC101Master.DOUBLE_POINT_INFORMATION%>";
  
  function settableChanged() {
	  if($get('settable')) {
		  selectedType = $get("iec101DataType");
		  if(selectedType == singlePoint || selectedType == doublePoint) {
			  show('commandFields');
			  show('singleCommandQualifier');
		  } else {
			  show('commandFields');
			  hide('singleCommandQualifier');
		  }
	  } else {
		  hide('commandFields');
		  hide('singleCommandQualifier');
	  }
  }

  
</script>

<c:choose>
  <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.IEC101_SERIAL']}">
    <c:set var="dsDesc"><fmt:message key="dsEdit.iec101.descSerial"/></c:set>
    <c:set var="dsHelpId" value="iec101DS"/>
  </c:when>
  <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.IEC101_ETHERNET']}">
    <c:set var="dsDesc"><fmt:message key="dsEdit.iec101.descEthernet"/></c:set>
    <c:set var="dsHelpId" value="iec101DS"/>
  </c:when>
</c:choose>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
		<tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.linkLayerAddressSize"/></td>
          <td class="formField">
	          <sst:select id="linkLayerAddressSize" value="${dataSource.linkLayerAddressSize}">
			      <sst:option value="0">None</sst:option>
			      <sst:option value="1">1 byte</sst:option>
			      <sst:option value="2">2 bytes</sst:option>
    		  </sst:select>
          </td>
          
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.linkLayerAddress"/></td>
          <td class="formField"><input type="text" id="linkLayerAddress" value="${dataSource.linkLayerAddress}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.asduAddressSize"/></td>
          <td class="formField">
          	<sst:select id="asduAddressSize" value="${dataSource.asduAddressSize}">
			      <sst:option value="1">1 byte</sst:option>
			      <sst:option value="2">2 bytes</sst:option>
    		  </sst:select>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.asduAddress"/></td>
          <td class="formField"><input type="text" id="asduAddress" value="${dataSource.asduAddress}"/></td>
        </tr>
         <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.cotSize"/></td>
          <td class="formField">
          	<sst:select id="cotSize" value="${dataSource.cotSize}">
			      <sst:option value="1">1 byte</sst:option>
			      <sst:option value="2">2 bytes</sst:option>
    		  </sst:select>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.objectAddressSize"/></td>
          <td class="formField">
          	<sst:select id="objectAddressSize" value="${dataSource.objectAddressSize}">
			      <sst:option value="1">1 byte</sst:option>
			      <sst:option value="2">2 bytes</sst:option>
			      <sst:option value="3">3 bytes</sst:option>
    		  </sst:select>
          </td>
        </tr>
        
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.updatePeriod"/></td>
          <td class="formField">
            <input type="text" id="updatePeriods" value="${dataSource.updatePeriods}" class="formShort"/>
            <sst:select id="updatePeriodType" value="${dataSource.updatePeriodType}">
              <tag:timePeriodOptions sst="true" ms="true" s="true" min="true" h="true"/>
            </sst:select>
          </td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.giRelativePeriod"/></td>
          <td class="formField"><input type="text" id="giRelativePeriod" value="${dataSource.giRelativePeriod}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.clockSynchRelativePeriod"/></td>
          <td class="formField"><input type="text" id="clockSynchRelativePeriod" value="${dataSource.clockSynchRelativePeriod}"/></td>
        </tr>
             	 
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.timeout"/></td>
          <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
        </tr>
              
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.modbus.retries"/></td>
          <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.quantize"/></td>
          <td class="formField"><sst:checkbox id="quantize" selectedValue="${dataSource.quantize}"/></td>
  		</tr>
              
        <c:choose>
          <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.IEC101_SERIAL']}">
            <%@ include file="/WEB-INF/jsp/dataSourceEdit/editIEC101Serial.jsp" %>
          </c:when>
           <c:when test="${dataSource.type.id == applicationScope['constants.DataSourceVO.Types.IEC101_ETHERNET']}">
            <%@ include file="/WEB-INF/jsp/dataSourceEdit/editIEC101Ethernet.jsp" %>
          </c:when>
        </c:choose>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
  
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="iec101PP">
	<tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.objectAddress"/></td>
	    <td class="formField"><input type="text" id="objectAddress"/></td>
	</tr>
	<tr>
	    <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.iec101DataType"/></td>
	    <td class="formField">
	      <select id="iec101DataType" onchange="settableChanged()">
	        <option value="<c:out value="<%= IEC101Master.SINGLE_POINT_INFORMATION%>"/>"><fmt:message key="dsEdit.iec101.iec101DataType.singlePoint"/></option>
	        <option value="<c:out value="<%= IEC101Master.DOUBLE_POINT_INFORMATION %>"/>"><fmt:message key="dsEdit.iec101.iec101DataType.doublePoint"/></option>
	        <option value="<c:out value="<%= IEC101Master.NORMALIZED_MEASURE %>"/>"><fmt:message key="dsEdit.iec101.iec101DataType.normalizedMeasure"/></option>
	      </select>
	    </td>
  	</tr>
  	<tr>
    	<td class="formLabelRequired"><fmt:message key="dsEdit.settable"/></td>
    	<td class="formField"><input type="checkbox" id="settable" onchange="settableChanged()"/></td>
  	</tr>
  	<tbody id="commandFields">
  		<tr>
		    <td class="formLabelRequired">Control Mode</td>
		    <td class="formField">
		      <select id="controlMode">
		        <option value="<c:out value="<%= IEC101Master.SELECT_AND_EXECUTE%>"/>"><fmt:message key="dsEdit.iec101.selectExecute"/></option>
		        <option value="<c:out value="<%= IEC101Master.EXECUTE_ONLY%>"/>"><fmt:message key="dsEdit.iec101.execute"/></option>
		      </select>
		    </td>
  		</tr>
  		<tr>
		    <td class="formLabelRequired"><fmt:message key="dsEdit.iec101.offset"/></td>
		    <td class="formField"><input type="text" id="offset"/></td>
		</tr>
  	</tbody>
  	<tbody id="singleCommandQualifier">
		<tr>
		    <td class="formLabelRequired">Qualifier</td>
		    <td class="formField">
		      <select id="qualifier">
		        <option value="<c:out value="<%= IEC101Master.DEFAULT%>"/>"><fmt:message key="dsEdit.iec101.default"/></option>
		        <option value="<c:out value="<%= IEC101Master.SHORT_PULSE%>"/>"><fmt:message key="dsEdit.iec101.shortPulse"/></option>
		        <option value="<c:out value="<%= IEC101Master.LONG_PULSE%>"/>"><fmt:message key="dsEdit.iec101.longPulse"/></option>
		        <option value="<c:out value="<%= IEC101Master.PERSISTENT%>"/>"><fmt:message key="dsEdit.iec101.persistent"/></option>
		      </select>
		    </td>
  		</tr>
  	</tbody>
  
</tag:pointList>