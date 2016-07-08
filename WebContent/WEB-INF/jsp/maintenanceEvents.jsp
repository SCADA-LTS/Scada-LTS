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
<%@page import="com.serotonin.mango.vo.event.MaintenanceEventVO"%>
<%@page import="org.joda.time.DateTimeConstants"%>
<c:set var="NEW_ID"><%= Common.NEW_ID %></c:set>

<tag:page dwr="MaintenanceEventsDwr" onload="init">
  <script type="text/javascript">
    var weekdays = new Array();
    var monthdays = new Array();
    var oncedays = new Array();
    var editingMaintenanceEvent;
    
    this.OptionData = function(_text, _value) {
        this.text = _text;
        this.value = _value;
    }
    
    function init() {
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.mon"/>", <c:out value="<%= DateTimeConstants.MONDAY %>"/>);
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.tue"/>", <c:out value="<%= DateTimeConstants.TUESDAY %>"/>);
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.wed"/>", <c:out value="<%= DateTimeConstants.WEDNESDAY %>"/>);
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.thu"/>", <c:out value="<%= DateTimeConstants.THURSDAY %>"/>);
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.fri"/>", <c:out value="<%= DateTimeConstants.FRIDAY %>"/>);
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.sat"/>", <c:out value="<%= DateTimeConstants.SATURDAY %>"/>);
        weekdays[weekdays.length] = new OptionData("<fmt:message key="common.day.sun"/>", <c:out value="<%= DateTimeConstants.SUNDAY %>"/>);
        
        for (var i=1; i<29; i++)
            monthdays[monthdays.length] = new OptionData(i, i);
        monthdays[monthdays.length] = new OptionData("<fmt:message key="common.day.thirdLast"/>", -3);
        monthdays[monthdays.length] = new OptionData("<fmt:message key="common.day.secondLast"/>", -2);
        monthdays[monthdays.length] = new OptionData("<fmt:message key="common.day.last"/>", -1);
        
        for (var i=1; i<32; i++)
            oncedays[oncedays.length] = new OptionData(i, i);
        
        MaintenanceEventsDwr.getMaintenanceEvents(function(response) {
            dwr.util.addOptions("dataSourceId", response.data.dataSources, "key", "value");
        	
        	var events = response.data.events;
            for (var i=0; i<events.length; i++) {
                appendMaintenanceEvent(events[i].id);
                updateMaintenanceEvent(events[i]);
            }
            
            <c:if test="${!empty param.meid}">showMaintenanceEvent(${param.meid});</c:if>
        });
    }
    
    function showMaintenanceEvent(meId) {
        if (editingMaintenanceEvent)
            stopImageFader($("me"+ editingMaintenanceEvent.id +"Img"));
        hideContextualMessages("maintenanceEventDetails")
        MaintenanceEventsDwr.getMaintenanceEvent(meId, function(response) {
        	var me = response.data.me;
            if (!editingMaintenanceEvent)
                show($("maintenanceEventDetails"));
            editingMaintenanceEvent = me;
            
            updateToggle(response.data.activated);
            
            $set("xid", me.xid);
            $set("dataSourceId", me.dataSourceId);
            $set("alias", me.alias);
            $set("alarmLevel", me.alarmLevel);
            updateAlarmLevelImage();
            $set("scheduleType", me.scheduleType);
            $set("disabled", me.disabled);
            
            refreshDateTimeFields();
            $set("activeYear", me.activeYear);
            $set("activeMonth", me.activeMonth);
            $set("activeDay", me.activeDay);
            $set("activeHour", me.activeHour);
            $set("activeMinute", me.activeMinute);
            $set("activeSecond", me.activeSecond);
            $set("activeCron", me.activeCron);
            $set("inactiveYear", me.inactiveYear);
            $set("inactiveMonth", me.inactiveMonth);
            $set("inactiveDay", me.inactiveDay);
            $set("inactiveHour", me.inactiveHour);
            $set("inactiveMinute", me.inactiveMinute);
            $set("inactiveSecond", me.inactiveSecond);
            $set("inactiveCron", me.inactiveCron);
            
            setUserMessage();
        });
        startImageFader($("me"+ meId +"Img"));
        
        if (meId == ${NEW_ID})
            hide($("deleteMaintenanceEventImg"));
        else
            show($("deleteMaintenanceEventImg"));
    }
    
    function saveMaintenanceEvent() {
        setUserMessage();
        hideContextualMessages("maintenanceEventDetails")
        MaintenanceEventsDwr.saveMaintenanceEvent(editingMaintenanceEvent.id, $get("xid"), $get("dataSourceId"), 
        		$get("alias"), $get("alarmLevel"), $get("scheduleType"), $get("disabled"), $get("activeYear"),
        		$get("activeMonth"), $get("activeDay"), $get("activeHour"), $get("activeMinute"), $get("activeSecond"),
        		$get("activeCron"), $get("inactiveYear"), $get("inactiveMonth"), $get("inactiveDay"), 
        		$get("inactiveHour"), $get("inactiveMinute"), $get("inactiveSecond"), $get("inactiveCron"),
        		function(response) {
                    if (response.hasMessages)
                        showDwrMessages(response.messages);
                    else {
                        if (editingMaintenanceEvent.id == ${NEW_ID}) {
                            stopImageFader($("me"+ editingMaintenanceEvent.id +"Img"));
                            editingMaintenanceEvent.id = response.data.meId;
                            appendMaintenanceEvent(editingMaintenanceEvent.id);
                            startImageFader($("me"+ editingMaintenanceEvent.id +"Img"));
                            setUserMessage("<fmt:message key="maintenanceEvents.meAdded"/>");
                            show($("deleteMaintenanceEventImg"));
                        }
                        else
                            setUserMessage("<fmt:message key="maintenanceEvents.meSaved"/>");
                        MaintenanceEventsDwr.getMaintenanceEvent(editingMaintenanceEvent.id, function(response) {
                            updateToggle(response.data.activated);
                        	updateMaintenanceEvent(response.data.me);
                        });
                    }
                }
        );
    }
    
    function setUserMessage(message) {
        if (message) {
            show($("userMessage"));
            $("userMessage").innerHTML = message;
        }
        else
            hide($("userMessage"));
    }
    
    function appendMaintenanceEvent(meId) {
        createFromTemplate("me_TEMPLATE_", meId, "maintenanceEventsTable");
    }
    
    function updateMaintenanceEvent(me) {
        $("me"+ me.id +"Name").innerHTML = me.description;
        if (me.disabled)
            updateImg("me"+ me.id +"Img", "images/hammer_disabled.png", "<sst:i18n key="maintenanceEvents.meDisabled" escapeDQuotes="true"/>", true);
        else
            updateImg("me"+ me.id +"Img", "images/hammer.png", "<sst:i18n key="maintenanceEvents.me" escapeDQuotes="true"/>", true);
    }
    
    function deleteMaintenanceEvent() {
    	MaintenanceEventsDwr.deleteMaintenanceEvent(editingMaintenanceEvent.id, function() {
            stopImageFader($("me"+ editingMaintenanceEvent.id +"Img"));
            $("maintenanceEventsTable").removeChild($("me"+ editingMaintenanceEvent.id));
            hide($("maintenanceEventDetails"));
            editingMaintenanceEvent = null;
        });
    }
    
    function updateAlarmLevelImage() {
        setAlarmLevelImg($get("alarmLevel"), $("alarmLevelImg"));
    }
    
    function refreshDateTimeFields() {
        var type = $get("scheduleType");
        
        if (type == <c:out value="<%= MaintenanceEventVO.TYPE_MANUAL %>"/>) {
            display($("activeDateTimeRow"), false);
            display($("inactiveDateTimeRow"), false);
        }
        else {
            display($("activeDateTimeRow"), true);
            display($("inactiveDateTimeRow"), true);
            if (type == <c:out value="<%= MaintenanceEventVO.TYPE_HOURLY %>"/>) {
                displayTables(false, true, false);
                displayHour(false);
            }
            else if (type == <c:out value="<%= MaintenanceEventVO.TYPE_DAILY %>"/>) {
                displayTables(false, true, false);
                displayHour(true);
            }
            else if (type == <c:out value="<%= MaintenanceEventVO.TYPE_WEEKLY %>"/>) {
                displayTables(true, true, false);
                displayYear(false);
                displayMonth(false);
                setDayOptions(weekdays);
                displayHour(true);
            }
            else if (type == <c:out value="<%= MaintenanceEventVO.TYPE_MONTHLY %>"/>) {
                displayTables(true, true, false);
                displayYear(false);
                displayMonth(false);
                setDayOptions(monthdays);
                displayHour(true);
            }
            else if (type == <c:out value="<%= MaintenanceEventVO.TYPE_YEARLY %>"/>) {
                displayTables(true, true, false);
                displayYear(false);
                displayMonth(true);
                setDayOptions(monthdays);
                displayHour(true);
            }
            else if (type == <c:out value="<%= MaintenanceEventVO.TYPE_ONCE %>"/>) {
                displayTables(true, true, false);
                displayYear(true);
                displayMonth(true);
                setDayOptions(oncedays);
                displayHour(true);
            }
            else if (type == <c:out value="<%= MaintenanceEventVO.TYPE_CRON %>"/>)
                displayTables(false, false, true);
        }
    }
    
    function displayTables(showDate, showTime, showCron) {
        display($("activeDateTable"), showDate);
        display($("activeTimeTable"), showTime);
        display($("activeCronTable"), showCron);
        display($("inactiveDateTable"), showDate);
        display($("inactiveTimeTable"), showTime);
        display($("inactiveCronTable"), showCron);
    }
    
    function displayHour(showHour) {
        if (showHour) {
            show($("activeHourLabel"));
            show($("activeHourSpacer"));
            show($("activeHourData"));
            show($("activeHourColon"));
            show($("inactiveHourLabel"));
            show($("inactiveHourSpacer"));
            show($("inactiveHourData"));
            show($("inactiveHourColon"));
        }
        else {
            hide($("activeHourLabel"));
            hide($("activeHourSpacer"));
            hide($("activeHourData"));
            hide($("activeHourColon"));
            hide($("inactiveHourLabel"));
            hide($("inactiveHourSpacer"));
            hide($("inactiveHourData"));
            hide($("inactiveHourColon"));
        }
    }
    
    function displayYear(showYear) {
        if (showYear) {
            show($("activeYearLabel"));
            show($("activeYearSpacer"));
            show($("activeYearData"));
            show($("activeYearSlash"));
            show($("inactiveYearLabel"));
            show($("inactiveYearSpacer"));
            show($("inactiveYearData"));
            show($("inactiveYearSlash"));
        }
        else {
            hide($("activeYearLabel"));
            hide($("activeYearSpacer"));
            hide($("activeYearData"));
            hide($("activeYearSlash"));
            hide($("inactiveYearLabel"));
            hide($("inactiveYearSpacer"));
            hide($("inactiveYearData"));
            hide($("inactiveYearSlash"));
        }
    }
    
    function displayMonth(showMonth) {
        if (showMonth) {
            show($("activeMonthLabel"));
            show($("activeMonthSpacer"));
            show($("activeMonthData"));
            show($("activeMonthSlash"));
            show($("inactiveMonthLabel"));
            show($("inactiveMonthSpacer"));
            show($("inactiveMonthData"));
            show($("inactiveMonthSlash"));
        }
        else {
            hide($("activeMonthLabel"));
            hide($("activeMonthSpacer"));
            hide($("activeMonthData"));
            hide($("activeMonthSlash"));
            hide($("inactiveMonthLabel"));
            hide($("inactiveMonthSpacer"));
            hide($("inactiveMonthData"));
            hide($("inactiveMonthSlash"));
        }
    }
    
    function setDayOptions(options, node) {
        if (!node) {
            setDayOptions(options, "activeDay");
            setDayOptions(options, "inactiveDay");
        }
        else {
            dwr.util.removeAllOptions(node);
            dwr.util.addOptions(node, options, "value", "text");
        }
    }
    
    function updateToggle(active) {
        var display = editingMaintenanceEvent.id != ${NEW_ID};
    	
    	if (active)
            updateImg("toggleImg", "images/hammer_activated.png", "<sst:i18n key="maintenanceEvents.activated" escapeDQuotes="true"/>", display);
    	else
            updateImg("toggleImg", "images/hammer_deactivated.png", "<sst:i18n key="maintenanceEvents.deactivated" escapeDQuotes="true"/>", display);
    }
    
    function toggleMaintenanceEvent() {
    	MaintenanceEventsDwr.toggleMaintenanceEvent(editingMaintenanceEvent.id, function(response) {
            if (response.hasMessages)
            	setUserMessage(response.messages[0].genericMessage);
            else
            	updateToggle(response.data.activated);
    	});
    }
  </script>
  
  <table>
    <tr>
      <td valign="top">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="maintenanceEvents.mes"/></span>
                <tag:help id="maintenanceEvents"/>
              </td>
              <td align="right"><tag:img png="add" title="maintenanceEvents.addMe"
                      onclick="showMaintenanceEvent(${NEW_ID})" id="me${NEW_ID}Img"/></td>
            </tr>
          </table>
          <table id="maintenanceEventsTable">
            <tbody id="me_TEMPLATE_" onclick="showMaintenanceEvent(getMangoId(this))" class="ptr" style="display:none">
              <tr>
                <td><tag:img id="me_TEMPLATE_Img" png="hammer" title="maintenanceEvents.me"/></td>
                <td class="link" id="me_TEMPLATE_Name"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
      
      <td valign="top" style="display:none;" id="maintenanceEventDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td><span class="smallTitle"><fmt:message key="maintenanceEvents.meDetails"/></span></td>
              <td align="right">
                <tag:img png="save" onclick="saveMaintenanceEvent();" title="common.save"/>
                <tag:img id="deleteMaintenanceEventImg" png="delete" onclick="deleteMaintenanceEvent();" title="common.delete"/>
                <tag:img id="toggleImg" onclick="toggleMaintenanceEvent();"/>
              </td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
              <td class="formField"><input type="text" id="xid"/></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="maintenanceEvents.dataSource"/></td>
              <td class="formField"><select id="dataSourceId"></select></td>
            </tr>
            
            <tr>
              <td class="formLabel"><fmt:message key="maintenanceEvents.alias"/></td>
              <td class="formField"><input type="text" id="alias"/></td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.alarmLevel"/></td>
              <td class="formField">
                <select id="alarmLevel" onchange="updateAlarmLevelImage()">
                  <tag:alarmLevelOptions/>
                </select>
                <tag:img id="alarmLevelImg" png="flag_green" title="common.alarmLevel.none" style="display:none;"/>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="maintenanceEvents.type"/></td>
              <td class="formField">
                <select id="scheduleType" onchange="refreshDateTimeFields()">
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_MANUAL %>"/>"><fmt:message key="maintenanceEvents.type.manual"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_HOURLY %>"/>"><fmt:message key="maintenanceEvents.type.hour"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_DAILY %>"/>"><fmt:message key="maintenanceEvents.type.day"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_WEEKLY %>"/>"><fmt:message key="maintenanceEvents.type.week"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_MONTHLY %>"/>"><fmt:message key="maintenanceEvents.type.month"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_YEARLY %>"/>"><fmt:message key="maintenanceEvents.type.year"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_ONCE %>"/>"><fmt:message key="maintenanceEvents.type.once"/></option>
                  <option value="<c:out value="<%= MaintenanceEventVO.TYPE_CRON %>"/>"><fmt:message key="maintenanceEvents.type.cron"/></option>
                </select>
              </td>
            </tr>
            
            <tr id="activeDateTimeRow">
              <td class="formLabelRequired"><fmt:message key="common.activeTime"/></td>
              <td>
                <table id="activeDateTable">
                  <tr>
                    <td align="center" id="activeYearLabel"><fmt:message key="common.tp.year"/></td>
                    <td id="activeYearSpacer"></td>
                    <td align="center" id="activeMonthLabel"><fmt:message key="common.tp.month"/></td>
                    <td id="activeMonthSpacer"></td>
                    <td align="center"><fmt:message key="common.tp.day"/></td>
                  </tr>
                  
                  <tr>
                    <td id="activeYearData"><input id="activeYear" type="text" class="formVeryShort"/></td>
                    <td id="activeYearSlash">/</td>
                    <td id="activeMonthData">
                      <select id="activeMonth">
                        <tag:monthOptions/>
                      </select>
                    </td>
                    <td id="activeMonthSlash">/</td>
                    <td><select id="activeDay"></select></td>
                  </tr>
                </table>
                <table id="activeTimeTable">
                  <tr>
                    <td align="center" id="activeHourLabel"><fmt:message key="common.tp.hour"/></td>
                    <td id="activeHourSpacer"></td>
                    <td align="center"><fmt:message key="common.tp.minute"/></td>
                    <td></td>
                    <td align="center"><fmt:message key="common.tp.second"/></td>
                  </tr>
                  <tr>
                    <td id="activeHourData">
                      <select id="activeHour">
                        <c:forEach begin="0" end="23" var="i">
                          <option>${mango:padZeros(i, 2)}</option>
                        </c:forEach>
                      </select>
                    </td>
                    <td id="activeHourColon">:</td>
                    <td>
                      <select id="activeMinute">
                        <c:forEach begin="0" end="59" var="i">
                          <option>${mango:padZeros(i, 2)}</option>
                        </c:forEach>
                      </select>
                    </td>
                    <td>:</td>
                    <td>
                      <select id="activeSecond">
                        <c:forEach begin="0" end="59" var="i">
                          <option>${mango:padZeros(i, 2)}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                </table>
                <table id="activeCronTable">
                  <tr><td align="center"><fmt:message key="common.cronPattern"/></td></tr>
                  <tr><td><input type="text" id="activeCron"/> <tag:help id="cronPatterns"/></td></tr>
                </table>
              </td>
            </tr>
            
            <tr id="inactiveDateTimeRow">
              <td class="formLabelRequired"><fmt:message key="common.inactiveTime"/></td>
              <td>
                <table id="inactiveDateTable">
                  <tr>
                    <td align="center" id="inactiveYearLabel"><fmt:message key="common.tp.year"/></td>
                    <td id="inactiveYearSpacer"></td>
                    <td align="center" id="inactiveMonthLabel"><fmt:message key="common.tp.month"/></td>
                    <td id="inactiveMonthSpacer"></td>
                    <td align="center"><fmt:message key="common.tp.day"/></td>
                  </tr>
                  <tr>
                    <td id="inactiveYearData"><input id="inactiveYear" type="text" class="formVeryShort"/></td>
                    <td id="inactiveYearSlash">/</td>
                    <td id="inactiveMonthData">
                      <select id="inactiveMonth">
                        <tag:monthOptions/>
                      </select>
                    </td>
                    <td id="inactiveMonthSlash">/</td>
                    <td><select id="inactiveDay"></select></td>
                  </tr>
                </table>
                <table id="inactiveTimeTable">
                  <tr>
                    <td align="center" id="inactiveHourLabel"><fmt:message key="common.tp.hour"/></td>
                    <td id="inactiveHourSpacer"></td>
                    <td align="center"><fmt:message key="common.tp.minute"/></td>
                    <td></td>
                    <td align="center"><fmt:message key="common.tp.second"/></td>
                  </tr>
                  <tr>
                    <td id="inactiveHourData">
                      <select id="inactiveHour">
                        <c:forEach begin="0" end="23" var="i">
                          <option>${mango:padZeros(i, 2)}</option>
                        </c:forEach>
                      </select>
                    </td>
                    <td id="inactiveHourColon">:</td>
                    <td>
                      <select id="inactiveMinute">
                        <c:forEach begin="0" end="59" var="i">
                          <option>${mango:padZeros(i, 2)}</option>
                        </c:forEach>
                      </select>
                    </td>
                    <td>:</td>
                    <td>
                      <select id="inactiveSecond">
                        <c:forEach begin="0" end="59" var="i">
                          <option>${mango:padZeros(i, 2)}</option>
                        </c:forEach>
                      </select>
                    </td>
                  </tr>
                </table>
                <table id="inactiveCronTable">
                  <tr><td align="center"><fmt:message key="common.cronPattern"/></td></tr>
                  <tr><td><input type="text" id="inactiveCron"/> <tag:help id="cronPatterns"/></td></tr>
                </table>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.disabled"/></td>
              <td class="formField"><input type="checkbox" id="disabled"/></td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td colspan="2" id="userMessage" class="formError" style="display:none;"></td>
            </tr>
          </table>
        </div>
      </td>
    </tr>
  </table>
</tag:page>