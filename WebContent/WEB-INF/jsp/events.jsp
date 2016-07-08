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
  <link rel="stylesheet" href="resources/demos.css">
  <script type="text/javascript" src="resources/jquery.js"></script>
  <script type="text/javascript" src="resources/jquery_ui.js"></script>
  <script type="text/javascript" src="resources/jquery.ui.datepicker.js"></script>
  <script type="text/javascript" src="resources/jquery.ui.datepicker-pt-BR.js"></script>
  <style>
    .incrementControl { width: 2em; }
  </style>
  <script type="text/javascript">
    // Tell the log poll that we're interested in monitoring pending alarms.
    mango.longPoll.pollRequest.pendingAlarms = true;
//     dojo.requireLocalization("dojo.i18n.calendar", "gregorian", null, "de,en,es,fi,fr,ROOT,hu,it,ja,ko,nl,pt,pt-br,sv,zh,zh-cn,zh-hk,zh-tw");
//     dojo.requireLocalization("dojo.i18n.calendar", "gregorianExtras", null, "ROOT,ja,zh");
  
  	jQuery.noConflict();
  	
  	jQuery.datepicker.setDefaults(jQuery.datepicker.regional['pt-BR']);
  	
  	jQuery(function() {
		jQuery('#datePicker').datepicker();
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
    	   $set("searchMessage", "<fmt:message key="events.search.searching"/>");
    		        EventsDwr.searchOld($get("eventId"), $get("eventSourceType"), $get("eventStatus"), $get("alarmLevel"),
    		                $get("keywords"), $get("maxResults"), function(results) {
    		            $set("searchResults", results.data.content);
    		            setDisabled("searchBtn", false);
    		            $set("searchMessage", results.data.resultCount);
    		        });
    		    }
    
    function doSearch(page, date) {
        setDisabled("searchBtn", true);
        $set("searchMessage", "<fmt:message key="events.search.searching"/>");
        EventsDwr.search($get("eventId"), $get("eventSourceType"), $get("eventStatus"), $get("alarmLevel"),
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
//         var x = dojo.widget.byId("datePicker");
//         console.log(x);
//         x.setDate(x.today);
		time = new Date($("datePicker"));
		
		
//         doSearch(0,time.getTime());
		doSearchOld();
    }
    
    function silenceAll() {
    	MiscDwr.silenceAll(function(result) {
    		var silenced = result.data.silenced;
    		for (var i=0; i<silenced.length; i++)
    			setSilenced(silenced[i], true);
    	});
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
      <fmt:message key="events.pending"/>
    </div>
    <div id="ackAllDiv" class="titlePadding" style="display:none;float:right;">
      <fmt:message key="events.acknowledgeAll"/>
      <tag:img png="tick" onclick="MiscDwr.acknowledgeAllPendingEvents()" title="events.acknowledgeAll"/>&nbsp;
      <fmt:message key="events.silenceAll"/>
      <tag:img png="sound_mute" onclick="silenceAll()" title="events.silenceAll"/><br/>
    </div>
    <div id="pendingAlarms" style="clear:both;"></div>
    <div id="noAlarms" style="display:none;padding:6px;text-align:center;">
      <b><fmt:message key="events.emptyList"/></b>
    </div>
    <div id="hourglass" style="padding:6px;text-align:center;"><tag:img png="hourglass"/></div>
  </div>
  
  <div class="borderDiv" style="clear:left;float:left;">
    <div class="smallTitle titlePadding"><fmt:message key="events.search"/></div>
    <div>
      <table>
        <tr>
          <td class="formLabel"><fmt:message key="events.id"/></td>
          <td class="formField"><input id="eventId" type="text"></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="events.search.type"/></td>
          <td class="formField">
            <select id="eventSourceType">
              <option value="-1"><fmt:message key="common.all"/></option>
              <option value="<c:out value="<%= EventType.EventSources.DATA_POINT %>"/>"><fmt:message key="eventHandlers.pointEventDetector"/></option>
              <option value="<c:out value="<%= EventType.EventSources.SCHEDULED %>"/>"><fmt:message key="scheduledEvents.ses"/></option>
              <option value="<c:out value="<%= EventType.EventSources.COMPOUND %>"/>"><fmt:message key="compoundDetectors.compoundEventDetectors"/></option>
              <option value="<c:out value="<%= EventType.EventSources.DATA_SOURCE %>"/>"><fmt:message key="eventHandlers.dataSourceEvents"/></option>
              <option value="<c:out value="<%= EventType.EventSources.PUBLISHER %>"/>"><fmt:message key="eventHandlers.publisherEvents"/></option>
              <option value="<c:out value="<%= EventType.EventSources.MAINTENANCE %>"/>"><fmt:message key="eventHandlers.maintenanceEvents"/></option>
              <option value="<c:out value="<%= EventType.EventSources.SYSTEM %>"/>"><fmt:message key="eventHandlers.systemEvents"/></option>
              <option value="<c:out value="<%= EventType.EventSources.AUDIT %>"/>"><fmt:message key="eventHandlers.auditEvents"/></option>
            </select>
          </td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="common.status"/></td>
          <td class="formField">
            <select id="eventStatus">
              <option value="<c:out value="<%= EventsDwr.STATUS_ALL %>"/>"><fmt:message key="common.all"/></option>
              <option value="<c:out value="<%= EventsDwr.STATUS_ACTIVE %>"/>"><fmt:message key="common.active"/></option>
              <option value="<c:out value="<%= EventsDwr.STATUS_RTN %>"/>"><fmt:message key="event.rtn.rtn"/></option>
              <option value="<c:out value="<%= EventsDwr.STATUS_NORTN %>"/>"><fmt:message key="common.nortn"/></option>
            </select>
          </td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="common.alarmLevel"/></td>
          <td class="formField"><select id="alarmLevel"><tag:alarmLevelOptions allOption="true"/></select></td>
        </tr>
        <tr>
          <td class="formLabel"><fmt:message key="events.search.keywords"/></td>
          <td class="formField"><input id="keywords" type="text"/></td>
        </tr>
        
<!--         <tr> -->
<!--         	<td> -->
<!--         		<div id="datePickerDiv" style="position:relative; top:0px; left:0px;" onmouseover="cancelDatePickerExpiry()" onmouseout="expireDatePicker()"> -->
<%--   				  <div widgetId="datePicker" dojoType="datepicker" dayWidth="narrow" lang="${lang}"></div> --%>
<!--   				</div> -->
<!--         	</td> -->
<!--         </tr> -->

		<tr>
          <td class="formLabel"><fmt:message key="events.search.maxResults"/></td>
          <td class="formField"><input id="maxResults" type="text"/></td>
        </tr>
		
        
        <tr>
          <td colspan="2" align="center">
            <input id="searchBtn" type="button" value="<fmt:message key="events.search.search"/>" onclick="newSearch()"/>
            <span id="searchMessage" class="formError"></span>
          </td>
        </tr>
        
        
        
      </table>
    </div>
    <div id="searchResults"></div>
  </div>
  
<!--   <div id="datePickerDiv" style="position:relative; top:0px; left:0px;" onmouseover="cancelDatePickerExpiry()" onmouseout="expireDatePicker()"> -->
<%--     <div widgetId="datePicker" dojoType="datepicker" dayWidth="narrow" lang="${lang}"></div> --%>
<!--   </div> -->
  
</tag:page>