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
<#if evt.alarmLevel==1>
  <img src="cid:<@img src="flag_blue.png"/>" alt="<@fmt key="common.alarmLevel.info"/>" title="<@fmt key="common.alarmLevel.info"/>"/>
<#elseif evt.alarmLevel==2>
  <img src="cid:<@img src="flag_yellow.png"/>" alt="<@fmt key="common.alarmLevel.urgent"/>" title="<@fmt key="common.alarmLevel.urgent"/>"/>
<#elseif evt.alarmLevel==3>
  <img src="cid:<@img src="flag_orange.png"/>" alt="<@fmt key="common.alarmLevel.critical"/>" title="<@fmt key="common.alarmLevel.critical"/>"/>
<#elseif evt.alarmLevel==4>
  <img src="cid:<@img src="flag_red.png"/>" alt="<@fmt key="common.alarmLevel.lifeSafety"/>" title="<@fmt key="common.alarmLevel.lifeSafety"/>"/>
</#if>