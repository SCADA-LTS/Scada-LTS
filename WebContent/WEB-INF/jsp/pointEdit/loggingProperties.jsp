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
<%@page import="com.serotonin.mango.vo.DataPointVO"%>
<%@page import="com.serotonin.mango.DataTypes"%>

<script type="text/javascript">
  function changeLoggingType() {
      var loggingType = $get("loggingType");
      var tolerance = $("tolerance");
      var purgePeriod = $("purgePeriod");
      var purgeType = $("purgeType");
      
      if ($("toleranceSection") && loggingType == <%= DataPointVO.LoggingTypes.ON_CHANGE %>)
          // On change logging for a numeric requires a tolerance setting.
          tolerance.disabled = false;
      else
          tolerance.disabled = true;
      
      if (loggingType == <%= DataPointVO.LoggingTypes.NONE %>) {
          purgePeriod.disabled = true;
          purgeType.disabled = true;
      }
      else {
          purgePeriod.disabled = false;
          purgeType.disabled = false;
      }
      
      if (loggingType == <%= DataPointVO.LoggingTypes.INTERVAL %>)
          show("intervalLoggingSection");
      else
          hide("intervalLoggingSection");
  }
  
  function changeDiscard() {
      var discard = $get("discardExtremeValues");
      if (discard) {
          $("discardLowLimit").disabled = false;
          $("discardHighLimit").disabled = false;
      }
      else {
          $("discardLowLimit").disabled = true;
          $("discardHighLimit").disabled = true;
      }
  }
  
  function clearPointCache() {
      setDisabled("clearCacheBtn", true);
      DataPointEditDwr.clearPointCache(function() {
          setDisabled("clearCacheBtn", false);
      });
  }
  
  dojo.addOnLoad(function() {
      if (dataTypeId == <%= DataTypes.NUMERIC %>) {
          show("toleranceSection");
          show("discardSection");
      }
      else {
          $("intervalLoggingType").disabled = true;
          $set("intervalLoggingType", <%= DataPointVO.IntervalLoggingTypes.INSTANT %>);
      }
      changeLoggingType();
      changeDiscard();
  });
</script>

<div class="borderDiv marB marR">
  <table>
    <tr><td colspan="3">
      <span class="smallTitle"><fmt:message key="pointEdit.logging.props"/></span>
      <tag:help id="pointValueLogging"/>
    </td></tr>
    
    <spring:bind path="form.loggingType">
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.logging.type"/></td>
        <td class="formField">
          <sst:select id="loggingType" name="loggingType" onchange="changeLoggingType();" value="${status.value}">
            <sst:option value="<%= Integer.toString(DataPointVO.LoggingTypes.ON_CHANGE) %>"><fmt:message key="pointEdit.logging.type.change"/></sst:option>
            <sst:option value="<%= Integer.toString(DataPointVO.LoggingTypes.ALL) %>"><fmt:message key="pointEdit.logging.type.all"/></sst:option>
            <sst:option value="<%= Integer.toString(DataPointVO.LoggingTypes.NONE) %>"><fmt:message key="pointEdit.logging.type.never"/></sst:option>
            <sst:option value="<%= Integer.toString(DataPointVO.LoggingTypes.INTERVAL) %>"><fmt:message key="pointEdit.logging.type.interval"/></sst:option>
            <sst:option value="<%= Integer.toString(DataPointVO.LoggingTypes.ON_TS_CHANGE) %>"><fmt:message key="pointEdit.logging.type.tsChange"/></sst:option>
          </sst:select>
        </td>
        <td class="formError">${status.errorMessage}</td>
      </tr>
    </spring:bind>
    
    <tbody id="intervalLoggingSection" style="display:none;">
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.logging.period"/></td>
        <td class="formField">
          <fmt:message key="pointEdit.logging.every"/> <input type="text" name="intervalLoggingPeriod" value="${form.intervalLoggingPeriod}" class="formShort"/>
          <sst:select name="intervalLoggingPeriodType" value="${form.intervalLoggingPeriodType}">
            <tag:timePeriodOptions sst="true" s="true" min="true" h="true" d="true" w="true" mon="true" y="true"/>
          </sst:select>
        </td>
        <td class="formError">
          <spring:bind path="form.intervalLoggingPeriodType">
            <c:if test="${status.error}">${status.errorMessage}<br/></c:if>
          </spring:bind>
          <spring:bind path="form.intervalLoggingPeriod">${status.errorMessage}</spring:bind>
        </td>
      </tr>
      
      <spring:bind path="form.intervalLoggingType">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.valueType"/></td>
          <td class="formField">
            <sst:select id="intervalLoggingType" name="intervalLoggingType" value="${form.intervalLoggingType}">
              <sst:option value="<%= Integer.toString(DataPointVO.IntervalLoggingTypes.INSTANT) %>"><fmt:message key="pointEdit.logging.valueType.instant"/></sst:option>
              <sst:option value="<%= Integer.toString(DataPointVO.IntervalLoggingTypes.MAXIMUM) %>"><fmt:message key="pointEdit.logging.valueType.maximum"/></sst:option>
              <sst:option value="<%= Integer.toString(DataPointVO.IntervalLoggingTypes.MINIMUM) %>"><fmt:message key="pointEdit.logging.valueType.minimum"/></sst:option>
              <sst:option value="<%= Integer.toString(DataPointVO.IntervalLoggingTypes.AVERAGE) %>"><fmt:message key="pointEdit.logging.valueType.average"/></sst:option>
            </sst:select>
          </td>
          <td class="formError">${status.errorMessage}</td>
        </tr>
      </spring:bind>
    </tbody>
    
    <tbody id="toleranceSection" style="display:none;">
      <spring:bind path="form.tolerance">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.tolerance"/></td>
          <td class="formField">
            <input id="tolerance" type="text" name="tolerance" value="${status.value}" class="formShort"/>
          </td>
          <td class="formError">${status.errorMessage}</td>
        </tr>
      </spring:bind>
    </tbody>
      
    <tbody id="discardSection" style="display:none;">
      <spring:bind path="form.discardExtremeValues">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.discard"/></td>
          <td class="formField">
            <sst:checkbox id="discardExtremeValues" name="discardExtremeValues" selectedValue="${status.value}"
                    onclick="changeDiscard()"/>
          </td>
          <td class="formError">${status.errorMessage}</td>
        </tr>
      </spring:bind>
      <spring:bind path="form.discardLowLimit">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.discardLow"/></td>
          <td class="formField">
            <input id="discardLowLimit" type="text" name="discardLowLimit" value="${status.value}" class="formShort"/>
          </td>
          <td class="formError">${status.errorMessage}</td>
        </tr>
      </spring:bind>
      <spring:bind path="form.discardHighLimit">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.discardHigh"/></td>
          <td class="formField">
            <input id="discardHighLimit" type="text" name="discardHighLimit" value="${status.value}" class="formShort"/>
          </td>
          <td class="formError">${status.errorMessage}</td>
        </tr>
      </spring:bind>
    </tbody>
      
    <tr>
      <td class="formLabelRequired"><fmt:message key="pointEdit.logging.purge"/></td>
      <td class="formField">
        <fmt:message key="pointEdit.logging.after"/> <input id="purgePeriod" type="text" name="purgePeriod" value="${form.purgePeriod}" class="formShort"/>
        <sst:select id="purgeType" name="purgeType" value="${form.purgeType}">
          <tag:timePeriodOptions sst="true" d="true" w="true" mon="true" y="true"/>
        </sst:select>
      </td>
      <td class="formError">
        <spring:bind path="form.purgeType">
          <c:if test="${status.error}">${status.errorMessage}<br/></c:if>
        </spring:bind>
        <spring:bind path="form.purgePeriod">${status.errorMessage}</spring:bind>
      </td>
    </tr>
      
    <spring:bind path="form.defaultCacheSize">
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.logging.defaultCache"/></td>
        <td class="formField">
          <input id="defaultCacheSize" type="text" name="defaultCacheSize" value="${status.value}" class="formShort"/>
          <input id="clearCacheBtn" type="button" value="<fmt:message key="pointEdit.logging.clearCache"/>" onclick="clearPointCache();"/>
        </td>
        <td class="formError">${status.errorMessage}</td>
      </tr>
    </spring:bind>
  </table>
</div>