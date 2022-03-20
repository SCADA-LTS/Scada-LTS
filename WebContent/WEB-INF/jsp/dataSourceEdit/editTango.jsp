<%--
    @author GP Orcullo
--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<script type="text/javascript">
  function initImpl() {
  }

  function saveDataSourceImpl() {
      DataSourceEditDwr.saveTangoDataSource($get("dataSourceName"), $get("dataSourceXid"),
                                            $get("deviceID"), $get("hostName"),
                                            $get("port"), saveDataSourceCB);
  }

  function appendPointListColumnFunctions(pointListColumnHeaders, pointListColumnFunctions) {
      pointListColumnHeaders.push('<fmt:message key="dsEdit.tango.attribute"/>');
      pointListColumnHeaders.push('<fmt:message key="dsEdit.tango.type"/>');
      pointListColumnFunctions.push(function(p) { return p.pointLocator.attribute; });
      pointListColumnFunctions.push(
           function(p) { return p.pointLocator.settable ? '<fmt:message key="dsEdit.tango.writable"/>' :
                                                          '<fmt:message key="dsEdit.tango.readonly"/>'; });
  }

  function editPointCBImpl(locator) {
      $set("attribute", locator.attribute);
      $set("settable", locator.settable);
      $set("dataTypeId", locator.dataTypeId);
  }

  function savePointImpl(locator) {
      delete locator.settable;

      locator.attribute = $get("attribute");
      locator.settable = $get("settable");
      locator.dataTypeId = $get("dataTypeId");

      DataSourceEditDwr.saveTangoPointLocator(currentPoint.id, $get("xid"), $get("name"), locator, savePointCB);
  }
</script>

<c:set var="dsDesc"><fmt:message key="dsEdit.tango.desc"/></c:set>
<c:set var="dsHelpId" value="tangoDS"/>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsHead.jspf" %>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.tango.deviceID"/></td>
          <td class="formField"><input id="deviceID" type="text" value="${dataSource.deviceID}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.tango.hostName"/></td>
          <td class="formField"><input id="hostName" type="text" value="${dataSource.hostName}"/></td>
        </tr>
        <tr>
          <td class="formLabelRequired"><fmt:message key="dsEdit.tango.port"/></td>
          <td class="formField"><input id="port" type="text" value="${dataSource.port}"/></td>
        </tr>
      </table>
      <tag:dsEvents/>
    </div>
  </td>
<%@ include file="/WEB-INF/jsp/dataSourceEdit/dsFoot.jspf" %>

<tag:pointList pointHelpId="tangoPP">
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.pointDataType"/></td>
    <td class="formField">
      <select name="dataTypeId">
        <tag:dataTypeOptions excludeImage="true"/>
      </select>
    </td>
  </tr>
  <tr>
    <td class="formLabelRequired"><fmt:message key="dsEdit.tango.attribute"/></td>
    <td class="formField"><input type="text" id="attribute"/></td>
  </tr>
  <tr>
    <td class="formLabel"><fmt:message key="dsEdit.tango.writable"/></td>
    <td class="formField"><input type="checkbox" id="settable"/></td>
  </tr>
</tag:pointList>