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
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<%@page import="com.serotonin.mango.Common"%>
<script type="text/javascript">
  var dataTypeId = ${form.pointLocator.dataTypeId};
</script>
<script type="text/javascript">

  var datPointDetailsPointSelect;
  function doSave(taskName) {
      $("taskName").name = taskName;
      textRendererEditor.save(doSaveEventTextRenderer);
      return false;
  }
  function doSaveEventTextRenderer() {
    eventTextRendererEditor.save(doSaveChartRenderer)
  }
  function doSaveChartRenderer() {
      chartRendererEditor.save(doSavePointEventDetectors);
  }
  function doSavePointEventDetectors() {
      pointEventDetectorEditor.save(doSaveForm);
  }
  function doSaveForm() {
      document.forms[0].submit();
  }
  jQuery(document).ready(function(){
	  (function($) {
			loadjscssfile("resources/jQuery/plugins/chosen/chosen.min.css","css"); 	
			loadjscssfile("resources/jQuery/plugins/chosen/chosen.jquery.min.js","js");
	  	})(jQuery);	  
  });
  window.onload = function() {

    if(this.datPointDetailsPointSelect) {
        this.datPointDetailsPointSelect.clear();
    }

    let point = {
        id : "<c:out value="${form.id}"/>",
        name : "<c:out value="${form.extendedName}"/>",
        xid : "<c:out value="${form.xid}"/>",
        dataType : "<sst:i18n message="${form.dataTypeMessage}"/>"
    }

    let ref = {}
    ref.excludePointsArray = [];
    ref.limit = 500;
    ref.selectHtmlId = "allPointsList";
    ref.placeholderTextSingle = "<spring:message code='chosen.selector.selectPoint'/>";
    ref.pointsArray = [point];
    ref.dataTypes = [];
    ref.altKey = "id";
    ref.altValue = "name";
    ref.widthPx = "400px";
    ref.invisibleEmptyOption = true;

    this.datPointDetailsPointSelect = new DataPointsSelect(ref);
  }
</script>

<table width="100%">
  <tr>
    <td valign="top">
      <table width="100%" cellspacing="0" cellpadding="0" border="0">
        <spring:bind path="form">
        <c:if test="${error.status != null}">
            <tr><td colspan="2" class="formError"><spring:message code="${error.status}"/></td></tr>
        </c:if>
        </spring:bind>
      </table>
    </td>
    <td valign="top" align="right">
      <spring:message code="pointEdit.name.goto"/>:&nbsp;
      <select id="allPointsList" value="${form.id}" onchange="window.location='data_point_details.shtm?dpid='+ this.value;" >
      </select>

      <c:if test="${!empty prevId}">
        <tag:img png="bullet_go_left" title="pagination.previous"
                onclick="window.location='data_point_edit.shtm?dpid=${prevId}'"/>
      </c:if>
      
      <c:if test="${!empty nextId}">
        <tag:img png="bullet_go" title="pagination.next"
                onclick="window.location='data_point_edit.shtm?dpid=${nextId}'"/>
      </c:if>
    </td>
  </tr>
</table>
