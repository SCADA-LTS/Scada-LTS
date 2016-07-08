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
<%@page import="com.serotonin.mango.vo.event.ScheduledEventVO"%>
<%@page import="org.joda.time.DateTimeConstants"%>
<c:set var="NEW_ID"><%= Common.NEW_ID %></c:set>

<tag:page dwr="ScheduledEventsDwr" onload="init">
  <script type="text/javascript">
    var weekdays = new Array();
    var monthdays = new Array();
    var oncedays = new Array();
    
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
        
        ScheduledEventsDwr.getScheduledEvents(initCB);
    }
    
    var editingScheduledEvent;
    function initCB(scheduledEvents) {
        for (var i=0; i<scheduledEvents.length; i++) {
            appendScheduledEvent(scheduledEvents[i].id);
            updateScheduledEvent(scheduledEvents[i]);
        }
        
        <c:if test="${!empty param.seid}">
          showScheduledEvent(${param.seid});
        </c:if>
    }
    
    function showScheduledEvent(seId) {
        if (editingScheduledEvent)
            stopImageFader($("se"+ editingScheduledEvent.id +"Img"));
        hideContextualMessages("scheduledEventDetails");
        
        ScheduledEventsDwr.getScheduledEvent(seId, function(se) {
            if (!editingScheduledEvent)
                show($("scheduledEventDetails"));
            editingScheduledEvent = se;
            
            $set("xid", se.xid);
            $set("alias", se.alias);
            $set("alarmLevel", se.alarmLevel);
            updateAlarmLevelImage();
            $set("scheduleType", se.scheduleType);
            $set("rtn", se.returnToNormal);
            $set("disabled", se.disabled);
            
            refreshDateTimeFields();
            $set("activeYear", se.activeYear);
            $set("activeMonth", se.activeMonth);
            $set("activeDay", se.activeDay);
            $set("activeHour", se.activeHour);
            $set("activeMinute", se.activeMinute);
            $set("activeSecond", se.activeSecond);
            $set("activeCron", se.activeCron);
            $set("inactiveYear", se.inactiveYear);
            $set("inactiveMonth", se.inactiveMonth);
            $set("inactiveDay", se.inactiveDay);
            $set("inactiveHour", se.inactiveHour);
            $set("inactiveMinute", se.inactiveMinute);
            $set("inactiveSecond", se.inactiveSecond);
            $set("inactiveCron", se.inactiveCron);
            
            setUserMessage();
        });
        startImageFader($("se"+ seId +"Img"));
        
        if (seId == ${NEW_ID})
            hide($("deleteScheduledEventImg"));
        else
            show($("deleteScheduledEventImg"));
    }
    
    function saveScheduledEvent() {
        setUserMessage();
        hideContextualMessages("scheduledEventDetails")
        ScheduledEventsDwr.saveScheduledEvent(editingScheduledEvent.id, $get("xid"), $get("alias"), $get("alarmLevel"), 
                $get("scheduleType"), $get("rtn"), $get("disabled"), $get("activeYear"), $get("activeMonth"),
                $get("activeDay"), $get("activeHour"), $get("activeMinute"), $get("activeSecond"), $get("activeCron"),
                $get("inactiveYear"), $get("inactiveMonth"), $get("inactiveDay"), $get("inactiveHour"),
                $get("inactiveMinute"), $get("inactiveSecond"), $get("inactiveCron"), function(response) {
            if (response.hasMessages)
                showDwrMessages(response.messages);
            else {
                if (editingScheduledEvent.id == ${NEW_ID}) {
                    stopImageFader($("se"+ editingScheduledEvent.id +"Img"));
                    editingScheduledEvent.id = response.data.seId;
                    appendScheduledEvent(editingScheduledEvent.id);
                    startImageFader($("se"+ editingScheduledEvent.id +"Img"));
                    setUserMessage("<fmt:message key="scheduledEvents.seAdded"/>");
                    show($("deleteScheduledEventImg"));
                }
                else
                    setUserMessage("<fmt:message key="scheduledEvents.seSaved"/>");
                ScheduledEventsDwr.getScheduledEvent(editingScheduledEvent.id, updateScheduledEvent)
            }
        });
    }
    
    function setUserMessage(message) {
        if (message) {
            show($("userMessage"));
            $("userMessage").innerHTML = message;
        }
        else
            hide($("userMessage"));
    }
    
    function appendScheduledEvent(seId) {
        createFromTemplate("se_TEMPLATE_", seId, "scheduledEventsTable");
    }
    
    function updateScheduledEvent(se) {
        $("se"+ se.id +"Name").innerHTML = se.description;
        setScheduledEventImg(se.disabled, $("se"+ se.id +"Img"));
    }
    
    function deleteScheduledEvent() {
        ScheduledEventsDwr.deleteScheduledEvent(editingScheduledEvent.id, function() {
            stopImageFader($("se"+ editingScheduledEvent.id +"Img"));
            $("scheduledEventsTable").removeChild($("se"+ editingScheduledEvent.id));
            hide($("scheduledEventDetails"));
            editingScheduledEvent = null;
        });
    }
    
    function updateAlarmLevelImage() {
        setAlarmLevelImg($get("alarmLevel"), $("alarmLevelImg"));
    }
    
    function refreshDateTimeFields() {
        var type = $get("scheduleType");
        var rtn = $get("rtn");
        
        display($("inactiveDateTimeRow"), rtn);
        
        if (type == <c:out value="<%= ScheduledEventVO.TYPE_HOURLY %>"/>) {
            displayTables(false, true, false);
            displayHour(false);
        }
        else if (type == <c:out value="<%= ScheduledEventVO.TYPE_DAILY %>"/>) {
            displayTables(false, true, false);
            displayHour(true);
        }
        else if (type == <c:out value="<%= ScheduledEventVO.TYPE_WEEKLY %>"/>) {
            displayTables(true, true, false);
            displayYear(false);
            displayMonth(false);
            setDayOptions(weekdays);
            displayHour(true);
        }
        else if (type == <c:out value="<%= ScheduledEventVO.TYPE_MONTHLY %>"/>) {
            displayTables(true, true, false);
            displayYear(false);
            displayMonth(false);
            setDayOptions(monthdays);
            displayHour(true);
        }
        else if (type == <c:out value="<%= ScheduledEventVO.TYPE_YEARLY %>"/>) {
            displayTables(true, true, false);
            displayYear(false);
            displayMonth(true);
            setDayOptions(monthdays);
            displayHour(true);
        }
        else if (type == <c:out value="<%= ScheduledEventVO.TYPE_ONCE %>"/>) {
            displayTables(true, true, false);
            displayYear(true);
            displayMonth(true);
            setDayOptions(oncedays);
            displayHour(true);
        }
        else if (type == <c:out value="<%= ScheduledEventVO.TYPE_CRON %>"/>)
            displayTables(false, false, true);
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
  </script>
    
  <table>
    <tr>
      <td valign="top">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td>
                <span class="smallTitle"><fmt:message key="scheduledEvents.ses"/></span>
                <tag:help id="scheduledEvents"/>
              </td>
              <td align="right"><tag:img png="clock_add" title="scheduledEvents.addSe"
                      onclick="showScheduledEvent(${NEW_ID})" id="se${NEW_ID}Img"/></td>
            </tr>
          </table>
          <table id="scheduledEventsTable">
            <tbody id="se_TEMPLATE_" onclick="showScheduledEvent(getMangoId(this))" class="ptr" style="display:none">
              <tr>
                <td><tag:img id="se_TEMPLATE_Img" png="clock" title="scheduledEvents.se"/></td>
                <td class="link" id="se_TEMPLATE_Name"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </td>
      
      <td valign="top" style="display:none;" id="scheduledEventDetails">
        <div class="borderDiv">
          <table width="100%">
            <tr>
              <td><span class="smallTitle"><fmt:message key="scheduledEvents.seDetails"/></span></td>
              <td align="right">
                <tag:img png="save" onclick="saveScheduledEvent();" title="common.save"/>
                <tag:img id="deleteScheduledEventImg" png="delete" onclick="deleteScheduledEvent();" title="common.delete"/>
              </td>
            </tr>
          </table>
          
          <table>
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.xid"/></td>
              <td class="formField"><input type="text" id="xid"/></td>
            </tr>
            
            <tr>
              <td class="formLabel"><fmt:message key="scheduledEvents.alias"/></td>
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
              <td class="formLabelRequired"><fmt:message key="scheduledEvents.type"/></td>
              <td class="formField">
                <select id="scheduleType" onchange="refreshDateTimeFields()">
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_HOURLY %>"/>"><fmt:message key="scheduledEvents.type.hour"/></option>
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_DAILY %>"/>"><fmt:message key="scheduledEvents.type.day"/></option>
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_WEEKLY %>"/>"><fmt:message key="scheduledEvents.type.week"/></option>
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_MONTHLY %>"/>"><fmt:message key="scheduledEvents.type.month"/></option>
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_YEARLY %>"/>"><fmt:message key="scheduledEvents.type.year"/></option>
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_ONCE %>"/>"><fmt:message key="scheduledEvents.type.once"/></option>
                  <option value="<c:out value="<%= ScheduledEventVO.TYPE_CRON %>"/>"><fmt:message key="scheduledEvents.type.cron"/></option>
                </select>
              </td>
            </tr>
            
            <tr>
              <td class="formLabelRequired"><fmt:message key="common.rtn"/></td>
              <td class="formField"><input type="checkbox" id="rtn" onclick="refreshDateTimeFields()"/></td>
            </tr>
            
            <tr>
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