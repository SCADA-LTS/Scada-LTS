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
<%@page import="com.serotonin.mango.vo.UserComment"%>
<%@page import="com.serotonin.mango.rt.event.type.EventType"%>
<%@page import="com.serotonin.mango.web.dwr.EventsDwr"%>
<tag:page dwr="EventsDwr">
  <%@ include file="/WEB-INF/jsp/include/userComment.jsp" %>
  <link rel="stylesheet" href="resources/jquery.ui.all.css">
  <link rel="stylesheet" href="resources/jQuery/plugins/jquery-ui/development-bundle/themes/base/jquery-ui.css">
  <link rel="stylesheet" href="resources/demos.css">
  <script type="text/javascript" src="resources/jquery.js"></script>
  <script type="text/javascript" src="resources/jquery_ui.js"></script>
  <script type="text/javascript" src="resources/jquery.ui.datepicker.js"></script>
  <script type="text/javascript" src="resources/jQuery/plugins/jquery-ui/development-bundle/ui/i18n/jquery.ui.datepicker-en-GB.js"></script>
  <style>
    .incrementControl { width: 2em; }
  </style>
  <script type="text/javascript">
    // Tell the log poll that we're interested in monitoring pending alarms.
    mango.longPoll.pollRequest.pendingAlarms = true;
//     dojo.requireLocalization("dojo.i18n.calendar", "gregorian", null, "de,en,es,fi,fr,ROOT,hu,it,ja,ko,nl,pt,pt-br,sv,zh,zh-cn,zh-hk,zh-tw");
//     dojo.requireLocalization("dojo.i18n.calendar", "gregorianExtras", null, "ROOT,ja,zh");
  
  	jQuery.noConflict();

    jQuery.datepicker.setDefaults(jQuery.datepicker.regional['en-GB']);

  	jQuery(function() {
      jQuery("#startDate, #endDate").datepicker();

      // Initialize the datepickers on each input
      jQuery("#startDate").datepicker({ dateFormat: 'dd-mm-yy' });
      jQuery("#endDate").datepicker({ dateFormat: 'dd-mm-yy' });

      // Clicking the icon opens the respective datepicker
      jQuery("#startDateIcon").click(function() {
        jQuery("#startDate").datepicker("show");
      });

      jQuery("#endDateIcon").click(function() {
        jQuery("#endDate").datepicker("show");
      });
	});
  	
    function updatePendingAlarmsContent(content) {
        hide("hourglass");
        
        $set("pendingAlarms", content);
        if (content) {
            show("ackAllDiv");
            hide("noAlarms");
        } else {
            $set("pendingAlarms", "");
            hide("ackAllDiv");
            show("noAlarms");
        }
    }
    
    function doSearchOld() {
    	setDisabled("searchBtn", true);
    	$set("searchMessage", "<spring:message code="events.search.searching"/>");
    	var eventId = parseInt($get("eventId"));
    	var maxResults = parseInt($get("maxResults"));
        EventsDwr.searchOld(eventId, $get("eventSourceType"), $get("eventStatus"), $get("alarmLevel"),
                $get("keywords"), maxResults, function(results) {
            $set("searchResults", results.data.content);
            setDisabled("searchBtn", false);
            $set("searchMessage", results.data.resultCount);
        });
    }

    function doSearch(page, date) {
        setDisabled("searchBtn", true);
        $set("searchMessage", "<spring:message code="events.search.searching"/>");
    	var eventId = parseInt($get("eventId"));
        EventsDwr.search(eventId, $get("eventSourceType"), $get("eventStatus"), $get("alarmLevel"),
                $get("keywords"), page.value, date.value, function(results) {
            $set("searchResults", results.data.content);
            setDisabled("searchBtn", false);
            $set("searchMessage", results.data.resultCount);
        });
    }

    function jumpToDate(parent) {
        var div = $("datePickerDiv");
        var bounds = getAbsoluteNodeBounds(parent);
        div.style.top = bounds.y +"px";
        div.style.left = bounds.x +"px";
        var x = dojo.widget.byId("datePicker");
        x.show();
    }

    var dptimeout = null;
    
    function expireDatePicker() {
        dptimeout = setTimeout(function() { dojo.widget.byId("datePicker").hide(); }, 500);
    }

    function cancelDatePickerExpiry() {
        if (dptimeout) {
            clearTimeout(dptimeout);
            dptimeout = null;
        }
    }

    function jumpToDateClicked(date) {
        var x = dojo.widget.byId("datePicker");
        if (x.isShowing()) {
            x.hide();
            doSearch(0, date);
        }
    }

    function newSearch() {
      // Collect the selected Event Source Types
      var eventSourceTypes = [];
      jQuery("input[name='eventSourceType']:checked").each(function() {
        eventSourceTypes.push(this.value);
      });

      // Collect the selected Alarm Levels
      var alarmLevels = [];
      jQuery("input[name='alarmLevel']:checked").each(function() {
        alarmLevels.push(this.value);
      });

      // Collect the selected Statuses
      var statuses = [];
      jQuery("input[name='status']:checked").each(function() {
        statuses.push(this.value);
      });

      // Retrieve date fields and keywords
      var startDate = jQuery('#startDate').val();
      var endDate   = jQuery('#endDate').val();
      var keywords  = jQuery('#keywords').val();
      var eventIdStr = jQuery('#eventId').val();
      var eventId = eventIdStr ? parseInt(eventIdStr, 10) : 0;

      jQuery('#searchBtn').prop('disabled', true);
      jQuery('#searchMessage').text("Searching...");

      // Call our new DWR method
      EventsDwr.searchNew(
              eventSourceTypes,
              statuses,
              alarmLevels,
              startDate,
              endDate,
              keywords,
              eventId,
              function(response) {
                jQuery('#searchBtn').prop('disabled', false);

                if (response.hasMessages) {

                  jQuery('#searchMessage').text("Error or messages found");
                }
                else {
                  // Update the search results div
                  if (response.data && response.data.content) {
                    $set("searchResults", response.data.content);
                  }
                  // Show a result count
                  if (response.data && response.data.resultCount) {
                    jQuery('#searchMessage').text(response.data.resultCount);
                  }
                  else {
                    jQuery('#searchMessage').text("");
                  }
                }
              }
      );
    }

    function createSearchConfigTemp(){
        let searchConfig = {}
        searchConfig.eventId = $get("eventId");
        searchConfig.maxResults = $get("maxResults");
        return searchConfig;
    }

    function validateSearchParameters(parametersToCheck){
        let messages = [];

        if (!isValid(parametersToCheck.eventId)) {
            let message = createValidationMessage("eventId", "<spring:message code='badIntegerFormat'/>");
            messages.push(message);
        }
        if(!isValid(parametersToCheck.maxResults)) {
            let message = createValidationMessage("maxResults", "<spring:message code='badIntegerFormat'/>");
            messages.push(message)
        }
        return messages;
    }

    function silenceAll() {
    	MiscDwr.silenceAll(function(result) {
    		var silenced = result.data.silenced;
    		for (var i=0; i<silenced.length; i++)
    			setSilenced(silenced[i], true);
    	});
    }

    function parseInt(value) {
        return value == "" ? 0 : Number.parseInt(value);
    }

//     dojo.addOnLoad(function() {
//         var x = dojo.widget.byId("datePicker");
//         x.hide();
//         x.setDate(x.today);
//         dojo.event.connect(x,'onValueChanged','jumpToDateClicked');
//     });
  </script>

  <div class="borderDiv marB" style="float:left;">
    <div class="smallTitle titlePadding" style="float:left;">
      <tag:img png="flag_white" title="events.alarms"/>
      <spring:message code="events.pending"/>
    </div>
    <div id="ackAllDiv" class="titlePadding" style="display:none;float:right;">
      <spring:message code="events.acknowledgeAll"/>
      <tag:img png="tick" onclick="MiscDwr.acknowledgeAllPendingEvents()" title="events.acknowledgeAll"/>&nbsp;
      <spring:message code="events.silenceAll"/>
      <tag:img png="sound_mute" onclick="silenceAll()" title="events.silenceAll"/><br/>
    </div>
    <div id="pendingAlarms" style="clear:both;"></div>
    <div id="noAlarms" style="display:none;padding:6px;text-align:center;">
      <b><spring:message code="events.emptyList"/></b>
    </div>
    <div id="hourglass" style="padding:6px;text-align:center;"><tag:img png="hourglass"/></div>
  </div>

  <div class="borderDiv" style="clear:left; float:left;" id="eventSearchForm">
    <h3><spring:message code="events.search"/></h3>

    <!-- Keywords -->
    <div style="display: flex; margin-bottom: 1em; align-items: center;">
      <label for="keywords" style="display:inline-block; font-weight:bold;">
        <spring:message code="events.search.keywords"/>:
      </label>
      <input type="text" id="keywords" name="keywords" style="flex: 1;"/>
    </div>

    <!-- Date range fields -->
    <div style="margin-bottom: 1em; white-space: nowrap;">
      <label for="startDate" style="display:inline-block; font-weight:bold;">
        <spring:message code="events.search.startDate"/>:
      </label>
      <input type="text" id="startDate" name="startDate" class="datepicker" />
      <!-- Calendar icon next to the field -->
      <img
              src="images/calendar.gif"
              alt="Calendar"
              style="cursor:pointer; vertical-align: middle;"
              id="startDateIcon"
      />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <label for="endDate" style="display:inline-block; font-weight:bold;">
        <spring:message code="events.search.endDate"/>:
      </label>
      <input type="text" id="endDate" name="endDate" class="datepicker" />
      <!-- Calendar icon next to the field -->
      <img
              src="images/calendar.gif"
              alt="Calendar"
              style="cursor:pointer; vertical-align: middle;"
              id="endDateIcon"
      />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
      <!-- eventID search -->
      <label for="eventId" style="display:inline-block; font-weight:bold;">
        <spring:message code="events.search.id"/>:
      </label>
      <input type="text" id="eventId" name="eventId" style="width:80px;"/>
    </div>

    <!-- Checkbox groups in 3 columns -->
    <div style="display: flex; gap: 2rem;">

      <!-- Column 1: Event source type -->
      <div style="flex: none; white-space: nowrap;">
        <strong><spring:message code="events.search.type"/>:</strong>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.DATA_POINT %>"/>">
            <spring:message code="eventHandlers.pointEventDetector"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.SCHEDULED %>" />"/>
            <spring:message code="scheduledEvents.ses"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.COMPOUND %>" />"/>
            <spring:message code="compoundDetectors.compoundEventDetectors"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.DATA_SOURCE %>" />"/>
            <spring:message code="eventHandlers.dataSourceEvents"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.PUBLISHER %>" />"/>
            <spring:message code="eventHandlers.publisherEvents"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.MAINTENANCE %>" />"/>
            <spring:message code="eventHandlers.maintenanceEvents"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.SYSTEM %>" />"/>
            <spring:message code="eventHandlers.systemEvents"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="eventSourceType" value="<c:out value="<%= EventType.EventSources.AUDIT %>" />"/>
            <spring:message code="eventHandlers.auditEvents"/>
          </label>
        </div>
      </div>

      <!-- Column 2: Alarm level -->
      <div style="flex: none; white-space: nowrap;">
        <strong><spring:message code="common.alarmLevel"/>:</strong>
        <div>
          <label>
            <input type="checkbox" name="alarmLevel" value="0" />
            <spring:message code="common.alarmLevel.none"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="alarmLevel" value="1" />
            <spring:message code="common.alarmLevel.info"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="alarmLevel" value="2" />
            <spring:message code="common.alarmLevel.urgent"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="alarmLevel" value="3" />
            <spring:message code="common.alarmLevel.critical"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="alarmLevel" value="4" />
            <spring:message code="common.alarmLevel.lifeSafety"/>
          </label>
        </div>
      </div>

      <!-- Column 3: Status -->
      <div style="flex: none; white-space: nowrap;">
        <strong><spring:message code="common.status"/>:</strong>
        <div>
          <label>
            <input type="checkbox" name="status" value="<c:out value="<%= EventsDwr.STATUS_ACTIVE %>" />"/>
            <spring:message code="common.active"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="status" value="<c:out value="<%= EventsDwr.STATUS_RTN %>" />"/>
            <spring:message code="event.rtn.rtn"/>
          </label>
        </div>
        <div>
          <label>
            <input type="checkbox" name="status" value="<c:out value="<%= EventsDwr.STATUS_NORTN %>" />"/>
            <spring:message code="common.nortn"/>
          </label>
        </div>
      </div>
    </div><!-- end flex container -->

    <!-- Search button -->
    <div style="margin-top: 1em;">
      <input id="searchBtn"
             type="button"
             value="<spring:message code='events.search.search'/>"
             onclick="newSearch()"/>
      <span id="searchMessage" class="formError"></span>
    </div>

    <!-- Search results placeholder -->
    <div id="searchResults" style="margin-top: 1em;"></div>
  </div>

   <tag:newPageNotification href="./app.shtm#/alarms/scada" ref="alarmListNotification"/>
  
<!--   <div id="datePickerDiv" style="position:relative; top:0px; left:0px;" onmouseover="cancelDatePickerExpiry()" onmouseout="expireDatePicker()"> -->
<%--     <div widgetId="datePicker" dojoType="datepicker" dayWidth="narrow" lang="${lang}"></div> --%>
<!--   </div> -->
  
</tag:page>