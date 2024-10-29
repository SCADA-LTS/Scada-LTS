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
--%><%@include file="/WEB-INF/tags/decl.tagf" %><%--
--%><%@tag body-content="empty" %><%--
--%><%@attribute name="event" type="com.serotonin.mango.rt.event.EventInstance" required="true" rtexprvalue="true" %><%--
--%><c:if test="${event.userNotified}">
  <c:choose>
    <c:when test="${event.acknowledged}"><tag:img png="tick_off" title="events.acknowledged" style="display:inline;"/></c:when>
    <c:otherwise>
      <c:if test="${not event.active}"><tag:img png="tick" id="ackImg${event.id}" onclick="ackEvent(${event.id})" title="events.acknowledge" style="display:inline;"/></c:if>
      <c:if test="${isEventAssignEnabled and event.active and not event.assignee}"><tag:img png="user_add" id="assigneeImg${event.id}" onclick="assignEvent(${event.id})" title="events.assign" style="display:inline;"/></c:if>
      <c:if test="${isEventAssignEnabled and event.active and event.assignee}"><tag:img png="user_delete" id="unassigneeImg${event.id}" onclick="unassignEvent(${event.id})" title="events.unassign" style="display:inline;"/></c:if>
      <c:choose>
        <c:when test="${event.silenced}">
          <tag:img png="sound_mute" id="silenceImg${event.id}" onclick="toggleSilence(${event.id})" title="events.unsilence" style="display:inline;"/>
        </c:when>
        <c:otherwise>
          <tag:img png="sound_none" id="silenceImg${event.id}" onclick="toggleSilence(${event.id})" title="events.silence" style="display:inline;"/>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
  <c:if test="${sessionUser.admin}" >
      <c:choose>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.DATA_POINT']}">
            <a href="data_point_details.shtm?dpid=${event.eventType.dataPointId}"><tag:img png="icon_comp" title="events.pointDetails"/></a>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.DATA_SOURCE']}">
            <a href="data_source_edit.shtm?dsid=${event.eventType.dataSourceId}"><tag:img png="icon_ds_edit" title="events.editDataSource"/></a>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.DATA_SOURCE_POINT']}">
            <a href="data_source_edit.shtm?dsid=${event.eventType.dataSourceId}&pid=${event.eventType.dataPointId}"><tag:img png="icon_ds_edit" title="events.editDataSource"/></a>
            <a href="data_point_details.shtm?dpid=${event.eventType.dataPointId}"><tag:img png="icon_comp" title="events.pointDetails"/></a>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.SYSTEM']}">
            <c:choose>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_VERSION_CHECK']}">
                <a href="http://mango.serotoninsoftware.com/download.jsp" target="_blank"><tag:img png="bullet_down" title="events.downloadMango"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_COMPOUND_DETECTOR_FAILURE']}">
                <a href="compound_events.shtm?cedid=${event.eventType.referenceId2}"><tag:img png="multi_bell" title="events.editCompound"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_SET_POINT_HANDLER_FAILURE']}">
                <a href="event_handlers.shtm?ehid=${event.eventType.referenceId2}"><tag:img png="cog" title="events.editEventHandler"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_POINT_LINK_FAILURE']}">
                <a href="point_links.shtm?plid=${event.eventType.referenceId2}"><tag:img png="link" title="events.editPointLink"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_EMAIL_SEND_FAILURE']}">
                <a href="event_handlers.shtm?ehid=${event.eventType.referenceId2}"><tag:img png="cog" title="events.editEventHandler"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_PROCESS_FAILURE']}">
                <a href="event_handlers.shtm?ehid=${event.eventType.referenceId2}"><tag:img png="cog" title="events.editEventHandler"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_SMS_SEND_FAILURE']}">
                <a href="event_handlers.shtm?ehid=${event.eventType.referenceId2}"><tag:img png="cog" title="events.editEventHandler"/></a>
              </c:when>
              <c:when test="${event.eventType.systemEventTypeId == applicationScope['constants.SystemEventType.TYPE_SCRIPT_HANDLER_FAILURE']}">
                <a href="event_handlers.shtm?ehid=${event.eventType.referenceId2}"><tag:img png="cog" title="events.editEventHandler"/></a>
              </c:when>
            </c:choose>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.COMPOUND']}">
            <a href="compound_events.shtm?cedid=${event.eventType.compoundEventDetectorId}"><tag:img png="multi_bell" title="events.editCompound"/></a>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.SCHEDULED']}">
            <a href="scheduled_events.shtm?seid=${event.eventType.scheduleId}"><tag:img png="clock" title="events.editScheduledEvent"/></a>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.PUBLISHER']}">
            <a href="publisher_edit.shtm?pid=${event.eventType.publisherId}"><tag:img png="transmit_edit" title="events.editPublisher"/></a>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.AUDIT']}">
            <c:choose>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_DATA_SOURCE']}">
                <a href="data_source_edit.shtm?dsid=${event.eventType.referenceId2}"><tag:img png="icon_ds_edit" title="events.editDataSource"/></a>
              </c:when>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_DATA_POINT']}">
                <a href="data_point_edit.shtm?dpid=${event.eventType.referenceId2}"><tag:img png="icon_comp_edit" title="events.pointEdit"/></a>
                <a href="data_source_edit.shtm?pid=${event.eventType.referenceId2}"><tag:img png="icon_ds_edit" title="events.editDataSource"/></a>
              </c:when>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_POINT_EVENT_DETECTOR']}">
                <a href="data_point_edit.shtm?pedid=${event.eventType.referenceId2}"><tag:img png="icon_comp_edit" title="events.pointEdit"/></a>
              </c:when>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_COMPOUND_EVENT_DETECTOR']}">
                <a href="compound_events.shtm?cedid=${event.eventType.referenceId2}"><tag:img png="multi_bell" title="events.editCompound"/></a>
              </c:when>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_SCHEDULED_EVENT']}">
                <a href="scheduled_events.shtm?seid=${event.eventType.referenceId2}"><tag:img png="clock" title="events.editScheduledEvent"/></a>
              </c:when>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_EVENT_HANDLER']}">
                <a href="event_handlers.shtm?ehid=${event.eventType.referenceId2}"><tag:img png="cog" title="events.editEventHandler"/></a>
              </c:when>
              <c:when test="${event.eventType.auditEventTypeId == applicationScope['constants.AuditEventType.TYPE_POINT_LINK']}">
                <a href="point_links.shtm?plid=${event.eventType.referenceId2}"><tag:img png="link" title="events.editPointLink"/></a>
              </c:when>
            </c:choose>
          </c:when>
          <c:when test="${event.eventType.eventSourceId == applicationScope['constants.EventType.EventSources.MAINTENANCE']}">
            <a href="maintenance_events.shtm?meid=${event.eventType.maintenanceId}"><tag:img png="hammer" title="events.editMaintenanceEvent"/></a>
          </c:when>
          <c:otherwise>(unknown event source id ${event.eventType.eventSourceId})</c:otherwise>
      </c:choose>
  </c:if>
</c:if>