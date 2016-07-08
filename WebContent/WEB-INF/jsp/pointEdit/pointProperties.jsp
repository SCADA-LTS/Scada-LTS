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
<%@page import="com.serotonin.mango.DataTypes"%>

<script type="text/javascript">
  dojo.addOnLoad(function() {
      if (dataTypeId == <%= DataTypes.NUMERIC %>)
          show("engineeringUnitsSection");
  });
</script>

<div class="borderDiv marB marR">
  <table>
    <tr>
      <td colspan="3">
        <img src="images/icon_comp_edit.png"/>
        <span class="smallTitle"><fmt:message key="pointEdit.props.props"/></span>
        <tag:help id="dataPointEditing"/>
        <a href="data_point_details.shtm?dpid=${form.id}"><tag:img png="icon_comp" title="pointEdit.props.details"/></a>
      </td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.props.ds"/></td>
      <td colspan="2" class="formField">
        ${dataSource.name}
        <a href="data_source_edit.shtm?dsid=${dataSource.id}&pid=${form.id}"><tag:img png="icon_ds_edit"
                title="pointEdit.props.editDs"/></a>
      </td>
    </tr>
      
    <spring:bind path="form.name">
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.props.name"/></td>
        <td class="formField"><input type="text" name="name" value="${status.value}"/></td>
        <td class="formError">${status.errorMessage}</td>
      </tr>
    </spring:bind>
    
    <tbody id="engineeringUnitsSection" style="display:none;">
      <spring:bind path="form.engineeringUnits">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.props.engineeringUnits"/></td>
          <td class="formField">
            <sst:select name="engineeringUnits" value="${status.value}"><tag:engineeringUnits/></sst:select>
          </td>
          <td class="formError">${status.errorMessage}</td>
        </tr>
      </spring:bind>
    </tbody>
    
  </table>
</div>