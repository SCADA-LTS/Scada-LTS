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

<script type="text/javascript">
  function purgeNowAllChanged() {
      var all = $get("purgeNowAll");
      setDisabled("purgeNowPeriod", all);
      setDisabled("purgeNowType", all);
  }
  
  function purgeNow() {
      var all = $get("purgeNowAll");
      if (all && !confirm("<fmt:message key="pointEdit.purge.confirm"/>"))
          return;
  
      setDisabled("purgeNowBtn", true);
      show("purgeNowWarn");
      startImageFader("purgeNowImg");
      DataPointEditDwr.purgeNow($get("purgeNowType"), $get("purgeNowPeriod"), all, purgeNowCB);
  }
  
  function purgeNowCB(result) {
      setDisabled("purgeNowBtn", false);
      stopImageFader("purgeNowImg");
      hide("purgeNowWarn");
      alert(""+ result +" <fmt:message key="pointEdit.purge.result"/>");
  }
</script>

<div class="borderDiv marB marR">
  <table>
    <tr><td colspan="3">
      <span class="smallTitle"><fmt:message key="pointEdit.purge.purgeNow"/></span>
      <tag:help id="pointValueLogPurging"/>
    </td></tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.purge.olderThan"/></td>
      <td class="formField">
        <input id="purgeNowPeriod" type="text" value="${form.purgePeriod}" class="formShort"/>
        <sst:select id="purgeNowType" value="${form.purgeType}">
          <tag:timePeriodOptions sst="true" min="true" h="true" d="true" w="true" mon="true" y="true"/>
        </sst:select>
      </td>
    </tr>
    
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.purge.all"/></td>
      <td class="formField">
        <input type="checkbox" id="purgeNowAll" onclick="purgeNowAllChanged()">
        <label for="purgeNowAll"><fmt:message key="pointEdit.purge.allData"/></label>
      </td>
    </tr>
    
    <tr>
      <td colspan="2" align="center">
        <input id="purgeNowBtn" type="button" value="<fmt:message key="pointEdit.purge.purgeNow"/>" onclick="purgeNow();"/>
      </td>
    </tr>
    
    <tbody id="purgeNowWarn" style="display:none">
      <tr>
        <td colspan="2" align="center" class="formError">
          <img id="purgeNowImg" src="images/warn.png"/>
          <fmt:message key="pointEdit.purge.warn"/>
        </td>
      </tr>
    </tbody>
  </table>
</div>