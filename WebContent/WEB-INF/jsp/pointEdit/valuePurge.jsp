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

  function purgeNow() {
    setDisabled("purgeNowBtn", true);
    show("purgeNowWarn");
    startImageFader("purgeNowImg");

    var purgeStrategy = $get("purgeNowStrategy");
    if (purgeStrategy == <%= DataPointVO.PurgeStrategy.PERIOD %>)
        DataPointEditDwr.purgeNowPeriod($get("purgeNowType"), $get("purgeNowPeriod"), purgeNowCB);

    if (purgeStrategy == <%= DataPointVO.PurgeStrategy.LIMIT %>)
        DataPointEditDwr.purgeNowValuesLimit($get("purgeNowValuesLimit"), purgeNowCB);

    if (purgeStrategy == <%= DataPointVO.PurgeStrategy.ALL %>) {
        if (!confirm("<spring:message code="pointEdit.purge.confirm"/>"))
                  return;
        DataPointEditDwr.purgeNowAll(purgeNowCB);
    }
  }
  
  function purgeNowCB(result) {
      setDisabled("purgeNowBtn", false);
      stopImageFader("purgeNowImg");
      hide("purgeNowWarn");
      alert(""+ result +" <spring:message code="pointEdit.purge.result"/>");
  }

  function changePurgeNowStrategy() {
      var purgeStrategy = $get("purgeNowStrategy");

      showOrHideSectionByPurgeType(purgeStrategy);

      if (purgeStrategy == <%= DataPointVO.PurgeStrategy.ALL %>) {
          hide("purgeNowLimitSection");
          hide("purgeNowLimitSection");
      }
  }

  function changePurgeNowStrategyOnLoad() {
        var purgeStrategy = $get("purgeStrategy");

        showOrHideSectionByPurgeType(purgeStrategy);
    }

    function showOrHideSectionByPurgeType(purgeStrategy) {
        if (purgeStrategy == <%= DataPointVO.PurgeStrategy.PERIOD %>)
            show("purgeNowPeriodSection");
        else
            hide("purgeNowPeriodSection");

        if (purgeStrategy == <%= DataPointVO.PurgeStrategy.LIMIT %>)
            show("purgeNowLimitSection");
        else
            hide("purgeNowLimitSection");
    }

  dojo.addOnLoad(function() {
        changePurgeNowStrategyOnLoad();
    });
</script>

<div class="borderDiv marB marR" style="margin:20px; padding:10px 10px 10px 10px; border-color:blue; max-width: 800px;">
  <table>
    <tr><td colspan="3">
      <span class="smallTitle"><spring:message code="pointEdit.purge.purgeNow"/></span>
      <tag:help id="pointValueLogPurging"/>
    </td></tr>

    <spring:bind path="form.purgeStrategy">
        <tr>
          <td class="formLabelRequired"><spring:message code="pointEdit.logging.purgeStrategy"/></td>
          <td class="formField">
            <sst:select id="purgeNowStrategy" name="purgeNowStrategy" onchange="changePurgeNowStrategy();" value="${status.value}">
              <sst:option value="<%= Integer.toString(DataPointVO.PurgeStrategy.PERIOD) %>"><spring:message code="pointEdit.purge.type.period"/></sst:option>
              <sst:option value="<%= Integer.toString(DataPointVO.PurgeStrategy.LIMIT) %>"><spring:message code="pointEdit.purge.type.limit"/></sst:option>
              <sst:option value="<%= Integer.toString(DataPointVO.PurgeStrategy.ALL) %>"><spring:message code="pointEdit.purge.type.all"/></sst:option>
            </sst:select>
          </td>
        </tr>
    </spring:bind>

    <tbody id="purgeNowPeriodSection">
        <tr>
          <td class="formLabelRequired"><spring:message code="pointEdit.purge.olderThan"/></td>
          <td class="formField">
            <input id="purgeNowPeriod" type="text" value="${form.purgePeriod}" class="formShort"/>
            <sst:select id="purgeNowType" value="${form.purgeType}">
              <tag:timePeriodOptions sst="true" min="true" h="true" d="true" w="true" mon="true" y="true"/>
            </sst:select>
          </td>
        </tr>
    </tbody>

    <tbody id="purgeNowLimitSection" style="display:none;">
      <tr>
        <td class="formLabelRequired"><spring:message code="pointEdit.purge.type.limit"/></td>
        <td class="formField">
          <input id="purgeNowValuesLimit" type="text" name="purgeNowValuesLimit" value="${form.purgeValuesLimit}" class="formShort"/>
        </td>
      </tr>
    </tbody>
    
    <tr>
      <td colspan="2" align="center">
        <input id="purgeNowBtn" type="button" value="<spring:message code="pointEdit.purge.purgeNow"/>" onclick="purgeNow();"/>
      </td>
    </tr>
    
    <tbody id="purgeNowWarn" style="display:none">
      <tr>
        <td colspan="2" align="center" class="formError">
          <img id="purgeNowImg" src="images/warn.png"/>
          <spring:message code="pointEdit.purge.warn"/>
        </td>
      </tr>
    </tbody>
  </table>
</div>