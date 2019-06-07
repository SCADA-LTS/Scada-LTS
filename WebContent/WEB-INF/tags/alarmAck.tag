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
      <tag:img png="tick" id="ackImg${event.id}" onclick="ackEvent(${event.id})" title="events.acknowledge" style="display:inline;"/>
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
</c:if>