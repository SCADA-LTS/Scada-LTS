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
<%@page import="org.scada_lts.web.mvc.controller.DataPointEditController"%>
<table>
  <tr>
    <td colspan="2" align="center">
      <input type="submit" value="<spring:message code="common.save"/>"
              onclick="return doSave('<%= DataPointEditController.SUBMIT_SAVE %>');"/>
      <c:choose>
        <c:when test="${form.enabled}">
          <input type="submit" value="<spring:message code="pointEdit.buttons.disable"/>"
                  onclick="return doSave('<%= DataPointEditController.SUBMIT_DISABLE %>');"/>
          <input type="submit" value="<spring:message code="pointEdit.buttons.restart"/>"
                  onclick="return doSave('<%= DataPointEditController.SUBMIT_RESTART %>');"/>
        </c:when>
        <c:otherwise>
          <input type="submit" value="<spring:message code="pointEdit.buttons.enable"/>"
                  onclick="return doSave('<%= DataPointEditController.SUBMIT_ENABLE %>');"/>
        </c:otherwise>
      </c:choose>
      
      <input type="button" value="<spring:message code="common.cancel"/>"
              onclick="window.location='data_source_edit.shtm?dsid=${dataSource.id}&pid=${form.id}';"/>
    </td>
    <td></td>
  </tr>
  
  <tr>
    <td colspan="2"><span class="smallTitle"><spring:message code="pointEdit.buttons.note.prefix"/></span> <spring:message code="pointEdit.buttons.note"/></td>
  </tr>
</table>