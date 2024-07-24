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
--%><%@ include file="/WEB-INF/jsp/include/tech.jsp" %>
<c:if test="${!empty events || !noContentWhenEmpty}">
  <c:if test="${displayPagination}">
    <div style="padding:3px; float:left;">
      <c:choose>
        <c:when test="${page == 0}">1</c:when>
        <c:otherwise><a href="#" onclick="doSearch(0);return false;">1</a></c:otherwise>
      </c:choose>
      <c:if test="${leftEllipsis}">...</c:if>
      <c:forEach begin="${linkFrom}" end="${linkTo}" var="i">
        <c:choose>
          <c:when test="${i-1 == page}">${i}</c:when>
          <c:otherwise><a href="#" onclick="doSearch(${i-1});return false;">${i}</a></c:otherwise>
        </c:choose>
      </c:forEach>
      <c:if test="${rightEllipsis}">...</c:if>
      <c:choose>
        <c:when test="${page == numberOfPages-1}">${numberOfPages}</c:when>
        <c:otherwise><a href="#" onclick="doSearch(${numberOfPages-1});return false;">${numberOfPages}</a></c:otherwise>
      </c:choose>
      
      <c:choose>
        <c:when test="${page <= 0}">&lt;</c:when>
        <c:otherwise><a href="#" onclick="doSearch(${page-1});return false;">&lt;</a></c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${page + 1 >= numberOfPages}">&gt;</c:when>
        <c:otherwise><a href="#" onclick="doSearch(${page+1});return false;">&gt;</a></c:otherwise>
      </c:choose>
    </div>
  </c:if>
  <c:if test="${!empty events && !pendingEvents}">
    <div style="padding:3px; float:left;">
      <c:if test="${displayPagination}">|</c:if>
      <a href="#" onclick="jumpToDate(this);return false;"><sst:i18n key="events.jumpToDate"/></a>
      | <sst:i18n message="${sst:message('events.listed',fn:length(events),'')}" />
    </div>
  </c:if>
  <div style="clear:both;"></div>
  
  <table cellspacing="1" cellpadding="0" border="0">
    <tr class="rowHeader">
      <td><sst:i18n key="events.id"/></td>
      <td><sst:i18n key="common.alarmLevel"/></td>
      <td><sst:i18n key="common.time"/></td>
      <td><sst:i18n key="events.msg"/></td>
      <td><sst:i18n key="common.inactiveTime"/></td>
      <c:if test="${!pendingEvents}"><td><sst:i18n key="events.acknowledged"/></td></c:if>
      <td><sst:i18n key="common.assignee"/></td>
      <td></td>
    </tr>
    <c:if test="${empty events}"><tr><td colspan="6"><b><sst:i18n key="events.emptyList"/></b></td></tr></c:if>
    <c:forEach items="${events}" var="event" varStatus="status">
      <tr class="row<c:if test="${status.index % 2 == 1}">Alt</c:if>">
        <td align="center">${event.id}</td>
        <td align="center"><tag:eventIcon event="${event}"/></td>
        <td>${sst:time(event.activeTimestamp)}</td>
        <td>
          <table cellspacing="0" cellpadding="0" width="100%">
            <tr>
              <td colspan="2"><b><sst:i18n message="${event.message}"/></b></td>
              <td align="right">
                <tag:img png="comment_add" title="notes.addNote"
                        onclick="openCommentDialog(${applicationScope['constants.UserComment.TYPE_EVENT']}, ${event.id})"/>
              </td>
            </tr>
            <tbody id="eventComments${event.id}"><tag:comments comments="${event.eventComments}"/></tbody>
          </table>
        </td>
        <td>
          <c:choose>
            <c:when test="${event.active}">
              <sst:i18n key="common.active"/>
              <a href="events.shtm"><tag:img png="flag_white" title="common.active"/></a>
            </c:when>
            <c:when test="${!event.rtnApplicable}"><sst:i18n key="common.nortn"/></c:when>
            <c:otherwise>
              ${sst:time(event.rtnTimestamp)} - <sst:i18n message="${event.rtnMessage}"/>
            </c:otherwise>
          </c:choose>
        </td>
        <c:if test="${!pendingEvents}">
          <td>
            <c:if test="${event.acknowledged}">
              ${sst:time(event.acknowledgedTimestamp)}
              <sst:i18n message="${event.ackMessage}"/>
            </c:if>
          </td>
        </c:if>
          <td>
            <c:if test="${event.assignee}">
              ${sst:time(event.assigneeTimestamp)}
              <sst:i18n message="${event.assigneeMessage}"/>
            </c:if>
          </td>
        <td style="white-space:nowrap;">
          <tag:alarmAck event="${event}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:if>