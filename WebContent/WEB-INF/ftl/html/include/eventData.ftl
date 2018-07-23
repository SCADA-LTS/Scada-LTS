<#--
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
-->
<tr>
  <td><#include "alarmLevel.ftl"></td>
  <td colspan="2">${evt.prettyActiveTimestamp} - <b><@fmt message=evt.message/></b></td>
</tr>
<#if evt.eventComments??>
  <#list evt.eventComments as comment>
    <tr>
      <td width="10"></td>
      <td valign="top" width="16"><img src="cid:<@img src="comment.png"/>" title="<@fmt key="notes.note"/>" alt="<@fmt key="notes.note"/>"/></td>
      <td valign="top">
        <span class="copyTitle">
          ${comment.prettyTime} <@fmt key="notes.by"/>
          <#if comment.username??>
            ${comment.username}
          <#else>
            <@fmt key="common.deleted"/>
          </#if>
        </span><br/>
        ${comment.comment}
      </td>
    </tr>
  </#list>
</#if>
<tr><td colspan="3"></td></tr>