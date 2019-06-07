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
<%@page import="com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO"%>

<script type="text/javascript">
	function saveDataSourceImpl() {
	    DataSourceEditDwr.saveIEC101EthernetDataSource(
	  	      $get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"), $get("updatePeriodType"),
	  	      $get("giRelativePeriod"),$get("clockSynchRelativePeriod"), 
	  	      $get("linkLayerAddressSize"),$get("linkLayerAddress"),
	  	      $get("asduAddressSize"),$get("asduAddress"), 
	  	      $get("cotSize"),$get("objectAddressSize"),
	            $get("timeout"), $get("retries"),$get("host"),$get("port"), $get("quantize"),
	            saveDataSourceCB);
	}
</script>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Ip.host"/></td>
  <td class="formField"><input id="host" type="text" value="${dataSource.host}"/></td>
</tr>
<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.dnp3Ip.port"/></td>
  <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
</tr>
