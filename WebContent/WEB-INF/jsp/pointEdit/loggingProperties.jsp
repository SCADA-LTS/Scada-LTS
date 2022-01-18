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
      var purgeStrategy = $("purgeStrategy");
      var purgeValuesLimit = $("purgeValuesLimit");
      
      if ($("toleranceSection") && loggingType == <%= DataPointVO.LoggingTypes.ON_CHANGE %>)
          // On change logging for a numeric requires a tolerance setting.
          tolerance.disabled = false;
      else
          tolerance.disabled = true;
      
      if (loggingType == <%= DataPointVO.LoggingTypes.NONE %>) {
          purgePeriod.disabled = true;
          purgeType.disabled = true;
          purgeStrategy.disabled = true;
          purgeValuesLimit.disabled = true;
      }
      else {
          purgePeriod.disabled = false;
          purgeType.disabled = false;
          purgeStrategy.disabled = false;
          purgeValuesLimit.disabled = false;
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

  function changePurgeWithLimit() {
    var purgeWithLimit = $get("purgeWithLimit");
    if (purgeWithLimit) {
        $("purgePeriod").disabled = true;
        $("purgeType").disabled = true;
    }
    else {
        $("purgePeriod").disabled = false;
        $("purgeType").disabled = false;
    }
  }
  
  function clearPointCache() {
      setDisabled("clearCacheBtn", true);
      DataPointEditDwr.clearPointCache(function() {
          setDisabled("clearCacheBtn", false);
      });
  }

  function changePurgeStrategy() {
        var purgeStrategy = $get("purgeStrategy");

        if (purgeStrategy == <%= DataPointVO.PurgeStrategy.PERIOD %>)
            show("purgePeriodSection");
        else
            hide("purgePeriodSection");

        if (purgeStrategy == <%= DataPointVO.PurgeStrategy.LIMIT %>)
            show("purgeLimitSection");
        else
            hide("purgeLimitSection");
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
      changePurgeStrategy();
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
        <c:if test="${error.loggingType != null}"><td class="formError"><fmt:message key="${status.loggingType}"/></td></c:if>
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
          <c:if test="${error.intervalLoggingPeriodType != null}"><fmt:message key="${error.intervalLoggingPeriodType}"/><br/></c:if>
          <c:if test="${error.intervalLoggingPeriod != null}"><fmt:message key="${error.intervalLoggingPeriod}"/></c:if>
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
          <c:if test="${error.intervalLoggingType != null}"><td class="formError"><fmt:message key="${error.intervalLoggingType}"/></td></c:if>
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
          <c:if test="${error.tolerance != null}"><td class="formError"><fmt:message key="${error.tolerance}"/></td></c:if>
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
          <c:if test="${error.discardExtremeValues != null}"><td class="formError"><fmt:message key="${error.discardExtremeValues}"/></td></c:if>
        </tr>
      </spring:bind>
      <spring:bind path="form.discardLowLimit">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.discardLow"/></td>
          <td class="formField">
            <input id="discardLowLimit" type="text" name="discardLowLimit" value="${status.value}" class="formShort"/>
          </td>
          <c:if test="${error.discardLowLimit != null}"><td class="formError"><fmt:message key="${error.discardLowLimit}"/></td></c:if>
        </tr>
      </spring:bind>
      <spring:bind path="form.discardHighLimit">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.discardHigh"/></td>
          <td class="formField">
            <input id="discardHighLimit" type="text" name="discardHighLimit" value="${status.value}" class="formShort"/>
          </td>
          <c:if test="${error.discardHighLimit != null}"><td class="formError"><fmt:message key="${error.discardHighLimit}"/></td></c:if>
        </tr>
      </spring:bind>
    </tbody>

    <spring:bind path="form.purgeStrategy">
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.logging.purgeStrategy"/></td>
        <td class="formField">
          <sst:select id="purgeStrategy" name="purgeStrategy" onchange="changePurgeStrategy();" value="${status.value}">
            <sst:option value="<%= Integer.toString(DataPointVO.PurgeStrategy.PERIOD) %>"><fmt:message key="pointEdit.purge.type.period"/></sst:option>
            <sst:option value="<%= Integer.toString(DataPointVO.PurgeStrategy.LIMIT) %>"><fmt:message key="pointEdit.purge.type.limit"/></sst:option>
          </sst:select>
        </td>
        <c:if test="${error.purgeStrategy != null}"><td class="formError"><fmt:message key="${status.purgeStrategy}"/></td></c:if>
      </tr>
    </spring:bind>

    <tbody id="purgePeriodSection" style="display:none;">
        <tr>
          <td class="formLabelRequired"><fmt:message key="pointEdit.logging.purge"/></td>
          <td class="formField">
            <fmt:message key="pointEdit.logging.after"/> <input id="purgePeriod" type="text" name="purgePeriod" value="${form.purgePeriod}" class="formShort"/>
            <sst:select id="purgeType" name="purgeType" value="${form.purgeType}">
              <tag:timePeriodOptions sst="true" d="true" w="true" mon="true" y="true"/>
            </sst:select>
          </td>
          <c:if test="${error.purgePeriod != null}"><td class="formError"><fmt:message key="${error.purgePeriod}"/></td></c:if>
        </tr>
    </tbody>

    <tbody id="purgeLimitSection" style="display:none;">
        <spring:bind path="form.purgeValuesLimit">
            <tr>
              <td class="formLabelRequired"><fmt:message key="pointEdit.purge.type.limit"/></td>
              <td class="formField">
                <input id="purgeValuesLimit" type="text" name="purgeValuesLimit" value="${status.value}" class="formShort"/>
              </td>
              <c:if test="${error.purgeValuesLimit != null}"><td class="formError"><fmt:message key="${error.purgeValuesLimit}"/></td></c:if>
            </tr>
        </spring:bind>
    </tbody>
      
    <spring:bind path="form.defaultCacheSize">
      <tr>
        <td class="formLabelRequired"><fmt:message key="pointEdit.logging.defaultCache"/></td>
        <td class="formField">
          <input id="defaultCacheSize" type="text" name="defaultCacheSize" value="${status.value}" class="formShort"/>
          <input id="clearCacheBtn" type="button" value="<fmt:message key="pointEdit.logging.clearCache"/>" onclick="clearPointCache();"/>
        </td>
         <c:if test="${error.defaultCacheSize != null}"><td class="formError"><fmt:message key="${error.defaultCacheSize}"/></td></c:if>
      </tr>
    </spring:bind>
  </table>
</div>