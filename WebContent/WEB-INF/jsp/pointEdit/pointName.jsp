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
	  jQuery("#allPointsList").chosen({
	      	allow_single_deselect: true,
			placeholder_text_single: " ",
			search_contains: true,
			width: "400px"
	  });
  }
</script>

<table width="100%">
  <tr>
    <td valign="top">
      <table width="100%" cellspacing="0" cellpadding="0" border="0">
        <spring:bind path="form">
        <c:if test="${error.status != null}">
            <tr><td colspan="2" class="formError"><fmt:message key="${error.status}"/></td></tr>
        </c:if>
        </spring:bind>
      </table>
    </td>
    <td valign="top" align="right">
      <fmt:message key="pointEdit.name.goto"/>:&nbsp;
      <sst:select id="allPointsList" value="${form.id}" onchange="window.location='data_point_edit.shtm?dpid='+ this.value;">
        <c:forEach items="${userPoints}" var="point">
          <sst:option value="${point.id}"><c:out value="${point.extendedName}"/></sst:option>
        </c:forEach>
      </sst:select>
      
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
