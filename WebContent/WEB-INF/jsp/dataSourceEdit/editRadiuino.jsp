<%--

	Radiuino

--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp"%>
<%@page import="cc.radiuino.scadabr.vo.datasource.radiuino.RadiuinoDataType"%>

<script type="text/javascript">

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveRadiuinoDataSource($get("dataSourceName"), $get("dataSourceXid"), $get("updatePeriods"),
              $get("updatePeriodType"),	$get("commPortId"), $get("baudRate"), $get("dataBits"),
				$get("stopBits"), $get("parity"), $get("pollingMode"), $get("timeout"), $get("retries"), saveDataSourceCB);
  }
  
  function initImpl() {
  }
  
  function editPointCBImpl(locator) {
	  $set("enderecoSensor", locator.enderecoSensor);
	  $set("test_radiuinoDataType", locator.radiuinoDataType);
	  $set("indiceByte", locator.indiceByte);
      $set("multiplicador", locator.multiplicador);
      $set("offset", locator.offset);
      $set("mapaBytesRequisicao", locator.mapaBytesRequisicao);
      for(i=0;i<52;i++) {
    	  $set("mbr_"+i,locator.mapaBytesRequisicao.substr(i*3,3));
      }
      $set("mapaBytesEnvio", locator.mapaBytesEnvio);
      for(i=0;i<52;i++) {
    	  $set("mbe_"+i,locator.mapaBytesEnvio.substr(i*3,3));
      }
	  $set("settableOverride", locator.settableOverride);
  }
  
  function savePointImpl(locator) {
	  locator.enderecoSensor = $get("enderecoSensor");
      locator.radiuinoDataType = $get("test_radiuinoDataType");
      locator.indiceByte = $get("indiceByte");
      locator.multiplicador = $get("multiplicador");
      locator.offset = $get("offset");
      locator.mapaBytesRequisicao = $get("mapaBytesRequisicao");
      locator.mapaBytesEnvio = $get("mapaBytesEnvio");
      locator.settableOverride = $get("settableOverride");
      
      DataSourceEditDwr.saveRadiuinoPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }

  //
  // List manipulation.
  function addListValue(prefix) {
  }
  
  function removeListValue(theValue, prefix) {
  }
  
  function refreshValueList(prefix, arr) {
  }
  
  function changeAvancado() {
	  if (tableAvancado.style.display == 'none') {
		  show("tableAvancado");
		  $set("spanAvancado","- ");
	  } else {
		  hide("tableAvancado");
		  $set("spanAvancado","+ ");
	  }
  }
  
  function changeMBR(select, id) {
	  mbr = $get("mapaBytesRequisicao");
	  mbr_new = mbr.substr(0,id*3) + select.value + mbr.substr(id*3+3);
	  $set("mapaBytesRequisicao",mbr_new);
  }

  function changeMBE(select, id) {
	  mbe = $get("mapaBytesEnvio");
	  mbe_new = mbe.substr(0,id*3) + select.value + mbe.substr(id*3+3);
	  $set("mapaBytesEnvio",mbe_new);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.radiuino.desc"/></c:set>
<c:set var="dsHelpId" value="radiuinoDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>

<tr>
	<td class="formLabelRequired"><fmt:message
		key="dsEdit.radiuino.commPortId" /></td>
	<td class="formField"><c:choose>
		<c:when test="${!empty commPortError}">
			<input id="commPortId" type="hidden" value="" />
			<span class="formError">${commPortError}</span>
		</c:when>
		<c:otherwise>
			<sst:select id="commPortId" value="${dataSource.commPortId}">
				<c:forEach items="${commPorts}" var="port">
					<sst:option value="${port.name}">${port.name}</sst:option>
				</c:forEach>
			</sst:select>
		</c:otherwise>
	</c:choose></td>
</tr>

<tr>
	<td class="formLabelRequired"><fmt:message
		key="dsEdit.radiuino.baud" /></td>
	<td class="formField"><sst:select id="baudRate"
		value="${dataSource.baudRate}">
		<sst:option>110</sst:option>
		<sst:option>300</sst:option>
		<sst:option>1200</sst:option>
		<sst:option>2400</sst:option>
		<sst:option>4800</sst:option>
		<sst:option>9600</sst:option>
		<sst:option>19200</sst:option>
		<sst:option>38400</sst:option>
		<sst:option>57600</sst:option>
		<sst:option>115200</sst:option>
		<sst:option>230400</sst:option>
		<sst:option>460800</sst:option>
		<sst:option>921600</sst:option>
	</sst:select></td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.dataBits"/></td>
  <td class="formField">
    <sst:select id="dataBits" value="${dataSource.dataBits}">
      <sst:option value="5">5</sst:option>
      <sst:option value="6">6</sst:option>
      <sst:option value="7">7</sst:option>
      <sst:option value="8">8</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.stopBits"/></td>
  <td class="formField">
    <sst:select id="stopBits" value="${dataSource.stopBits}">
      <sst:option value="1">1</sst:option>
      <sst:option value="3">1.5</sst:option>
      <sst:option value="2">2</sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.parity"/></td>
  <td class="formField">
    <sst:select id="parity" value="${dataSource.parity}">
      <sst:option value="0"><fmt:message key="dsEdit.modbusSerial.parity.none"/></sst:option>
      <sst:option value="1"><fmt:message key="dsEdit.modbusSerial.parity.odd"/></sst:option>
      <sst:option value="2"><fmt:message key="dsEdit.modbusSerial.parity.even"/></sst:option>
      <sst:option value="3"><fmt:message key="dsEdit.modbusSerial.parity.mark"/></sst:option>
      <sst:option value="4"><fmt:message key="dsEdit.modbusSerial.parity.space"/></sst:option>
    </sst:select>
  </td>
</tr>

<tr>
  <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.pollingMode"/></td>
  <td class="formField">
    <sst:select id="pollingMode" value="${dataSource.pollingMode}">
      <sst:option value="true"><fmt:message key="dsEdit.radiuino.pollingMode.true"/></sst:option>
      <sst:option value="false"><fmt:message key="dsEdit.radiuino.pollingMode.false"/></sst:option>
    </sst:select>
  </td>
</tr>

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
    <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.retries"/></td>
    <td class="formField"><input type="text" id="retries" value="${dataSource.retries}"/></td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.timeout"/></td>
    <td class="formField"><input type="text" id="timeout" value="${dataSource.timeout}"/></td>
  </tr>  
  
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsEventsFoot.jspf" %>

<tag:pointList pointHelpId="radiuinoPP">
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.radiuino.endereco" /></td>
		<td class="formField"><input id="enderecoSensor" type="text"
			value="" /></td>
	</tr>
	<tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.radiuino.radiuinoDataType"/></td>
          <td class="formField">
            <select id="test_radiuinoDataType">
              <option value="<c:out value="<%= RadiuinoDataType.BINARY %>"/>"><fmt:message key="dsEdit.radiuino.radiuinoDataType.binary"/></option>
              <option value="<c:out value="<%= RadiuinoDataType.ONE_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.radiuino.radiuinoDataType.1bUnsigned"/></option>
              <option value="<c:out value="<%= RadiuinoDataType.TWO_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.radiuino.radiuinoDataType.2bUnsigned"/></option>
              <option value="<c:out value="<%= RadiuinoDataType.FOUR_BYTE_INT_UNSIGNED %>"/>"><fmt:message key="dsEdit.radiuino.radiuinoDataType.4bUnsigned"/></option>
              <option value="<c:out value="<%= RadiuinoDataType.FOUR_BYTE_FLOAT %>"/>"><fmt:message key="dsEdit.radiuino.radiuinoDataType.4bFloat"/></option>
              <option value="<c:out value="<%= RadiuinoDataType.RSSI %>"/>"><fmt:message key="dsEdit.radiuino.radiuinoDataType.RSSI"/></option>
            </select>
          </td>
        </tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.radiuino.indiceByte" /></td>
		<td class="formField">
		    <select id="indiceByte">
		      <option value="0">0</option>
		      <option value="1">1</option>
		      <option value="2">2</option>
		      <option value="3">3</option>
		      <option value="4">4</option>
		      <option value="5">5</option>
		      <option value="6">6</option>
		      <option value="7">7</option>
		      <option value="8">8</option>
		      <option value="9">9</option>
		      <option value="10">10</option>
		      <option value="11">11</option>
		      <option value="12">12</option>
		      <option value="13">13</option>
		      <option value="14">14</option>
		      <option value="15">15</option>
		      <option value="16">16</option>
		      <option value="17">17</option>
		      <option value="18">18</option>
		      <option value="19">19</option>
		      <option value="20">20</option>
		      <option value="21">21</option>
		      <option value="22">22</option>
		      <option value="23">23</option>
		      <option value="24">24</option>
		      <option value="25">25</option>
		      <option value="26">26</option>
		      <option value="27">27</option>
		      <option value="28">28</option>
		      <option value="29">29</option>
		      <option value="30">30</option>
		      <option value="31">31</option>
		      <option value="32">32</option>
		      <option value="33">33</option>
		      <option value="34">34</option>
		      <option value="35">35</option>
		      <option value="36">36</option>
		      <option value="37">37</option>
		      <option value="38">38</option>
		      <option value="39">39</option>
		      <option value="40">40</option>
		      <option value="41">41</option>
		      <option value="42">42</option>
		      <option value="43">43</option>
		      <option value="44">44</option>
		      <option value="45">45</option>
		      <option value="46">46</option>
		      <option value="47">47</option>
		      <option value="48">48</option>
		      <option value="49">49</option>
		      <option value="50">50</option>
		      <option value="51">51</option>
		    </select>
		</td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.radiuino.multiplicador" /></td>
		<td class="formField"><input id="multiplicador" type="text" /></td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.radiuino.offset" /></td>
		<td class="formField"><input id="offset" type="text" /></td>
	</tr>
	<tr>
		<td colspan=2>
		<fieldset style="border-top: 1px solid green;border-left: none;border-bottom: 1px solid green;border-right: none;">
		<legend><span id="spanAvancado" onclick="changeAvancado();" style="cursor:hand;">+ </span><fmt:message key="dsEdit.radiuino.avancado"/></legend>
			<table id="tableAvancado" style="display:none;">
			<tr>
				<td class="formLabelRequired" style="text-align: center;"><fmt:message
						key="dsEdit.radiuino.mapaBytesRequisicao" /></td>
				<td class="formField"><input id="mapaBytesRequisicao" type="hidden" /></td>
			</tr>
			<tr>
				<td colspan="2" style="max-width: 630px;">
				<c:forEach var="i" begin="0" end="51" step="1">
					<div style="display: inline-block;"><c:out value="${i}" /></br>
					<select id="mbr_<c:out value="${i}" />" onchange="changeMBR(this, <c:out value="${i}" />);">
						<option>   </option>
						<c:forEach var="j" begin="0" end="255" step="1">
							<option><fmt:formatNumber pattern="000" value="${j}" /></option>
						</c:forEach>
					</select></div>
				</c:forEach>
				</td>
			</tr>
			<tr>
				<td class="formLabelRequired" style="text-align: center;"><fmt:message
						key="dsEdit.radiuino.mapaBytesEnvio" /></td>
				<td class="formField"><input id="mapaBytesEnvio" type="hidden"/></td>
			</tr>
			<tr>
				<td colspan="2" style="max-width: 630px;">
				<c:forEach var="i" begin="0" end="51" step="1">
					<div style="display: inline-block;"><c:out value="${i}" /></br>
					<select id="mbe_<c:out value="${i}" />" onchange="changeMBE(this, <c:out value="${i}" />);">
						<option>   </option>
						<c:forEach var="j" begin="0" end="255" step="1">
							<option><fmt:formatNumber pattern="000" value="${j}" /></option>
						</c:forEach>
					</select></div>
				</c:forEach>
				</td>
			</tr>
			</table>
		</fieldset>
		</td>
	</tr>
	<tr>
		<td class="formLabelRequired"><fmt:message
				key="dsEdit.radiuino.settableOverride" /></td>
		<td class="formField"><input id="settableOverride"
			type="checkbox" /></td>
	</tr>
</tag:pointList>