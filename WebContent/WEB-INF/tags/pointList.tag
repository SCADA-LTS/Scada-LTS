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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@tag import="com.serotonin.mango.Common"%>
<%@attribute name="pointHelpId" required="true"%>

<table cellpadding="0" cellspacing="0" id="pointProperties" style="display:none;">
  <tr>
    <td valign="top">
      <div class="borderDiv marR marB">
        <table width="100%">
          <tr>
            <td class="smallTitle"><fmt:message key="dsEdit.points.points"/></td>
            <td align="right">
            <tag:img id="enableAllImg" png="icon_ds_go"
                      onclick="enableAllPoints()" title="common.enableAll"/>
              <tag:img id="editImg${applicationScope['constants.Common.NEW_ID']}" png="icon_comp_add"
                      onclick="editPoint(${applicationScope['constants.Common.NEW_ID']})" />
            </td>
          </tr>
        </table>
        <table cellspacing="1">
          <tr class="rowHeader" id="pointListHeaders"></tr>
          <tbody id="pointsList"></tbody>
        </table>
      </div>
    </td>

    <td valign="top">
      <div id="pointDetails" class="borderDiv marB" style="display: none;">
        <table width="100%">
          <tr>
            <td>
              <span class="smallTitle"><fmt:message key="dsEdit.points.details"/></span>
              <tag:help id="${pointHelpId}"/>
            </td>
            <td align="right">
              <tag:img id="pointSaveImg" png="save" onclick="savePoint()" title="common.save"/>
              <tag:img id="pointDeleteImg" png="delete" onclick="deletePoint()" title="common.delete" />
            </td>
          </tr>
        </table>
        <div id="pointMessage" class="ctxmsg formError"></div>
        
        <table>
          <tr>
            <td class="formLabelRequired"><fmt:message key="dsEdit.points.name"/></td>
            <td class="formField"><input type="text" id="name"/></td>
          </tr>
          <tr>
            <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
            <td class="formField"><input type="text" id="xid"/></td>
          </tr>
          
          <jsp:doBody/>
          
        </table>
      </div>
    </td>
  </tr>
</table>