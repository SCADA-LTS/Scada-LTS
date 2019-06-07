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
    
  Used only for non-compound view components.
--%><%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@attribute name="vc" type="com.serotonin.mango.view.component.ViewComponent" required="true" rtexprvalue="true"%>
<c:choose>
  <c:when test="${vc.pointComponent && !vc.visible}"><!-- vc ${vc.id} not a point component or not visible --></c:when>
  <c:when test="${vc.pointComponent}">
    <div id="c${vc.id}" style="${vc.style}"
            <c:if test="${vc.displayControls}">onmouseover="vcOver('c${vc.id}');" onmouseout="vcOut('c${vc.id}');"</c:if>>
      <div id="c${vc.id}Content"><img src="images/icon_comp.png" alt=""/></div>
      <c:if test="${vc.displayControls}">
        <div id="c${vc.id}Controls" class="controlsDiv">
          <table cellpadding="0" cellspacing="1">
            <tr onmouseover="showMenu('c${vc.id}Info', 16, 0);" onmouseout="hideLayer('c${vc.id}Info');"><td>
              <tag:img png="information"/>
              <div id="c${vc.id}Info" onmouseout="hideLayer(this);">
                <tag:img png="hourglass" title="common.gettingData"/>
              </div>
            </td></tr>
            <c:if test="${vc.settable}">
              <tr id="c${vc.id}ChangeMin" ondblclick="mango.view.hideChange('c${vc.id}Change');"
                      onclick="mango.view.showChange('c${vc.id}Change', 16, 0);"><td>
                <img src="images/icon_edit.png" alt=""/>
                <div id="c${vc.id}Change">
                  <tag:img png="hourglass" title="common.gettingData"/>
                </div>
              </td></tr>
            </c:if>
            <c:if test="${vc.chartRenderer}">
              <tr onmouseover="mango.view.showChart('${vc.id}', event, this);"
                      onmouseout="mango.view.hideChart('${vc.id}', event, this);"><td>
                <img src="images/icon_chart.png" alt=""/>
                <div id="c${vc.id}ChartLayer"></div>
                <textarea style="display:none;" id="c${vc.id}Chart"><tag:img png="hourglass" title="common.gettingData"/></textarea>
              </td></tr>
            </c:if>
          </table>
        </div>
      </c:if>
      <div style="position:absolute;left:-16px;top:0px;z-index:1;">
        <div id="c${vc.id}Warning" style="display:none;" onmouseover="showMenu('c${vc.id}Messages', 16, 0);"
                onmouseout="hideLayer('c${vc.id}Messages');">
          	  <tag:img png="warn" title="common.warning"/>
          <div id="c${vc.id}Messages" onmouseout="hideLayer(this);" class="controlContent"></div>
        </div>
        <div id="c${vc.id}Changing" style="display:none;"><tag:img png="icon_edit" title="common.settingValue"/></div>
      </div>
    </div>
  </c:when>
  <c:when test="${!vc.customComponent}">
    <div style="${vc.style}">${vc.staticContent}</div>
  </c:when>
  <c:when test="${vc.customComponent}">
    <div id="c${vc.id}" style="${vc.style}">
      <div id="c${vc.id}Content"><img src="images/icon_comp.png" alt=""/></div>
      <div style="position:absolute;left:-16px;top:0px;z-index:1;">
        <div id="c${vc.id}Warning" style="display:none;" onmouseover="showMenu('c${vc.id}Messages', 16, 0);"
                onmouseout="hideLayer('c${vc.id}Messages');">
          	  <tag:img png="warn" title="common.warning"/>
          <div id="c${vc.id}Messages" onmouseout="hideLayer(this);" class="controlContent"></div>
        </div>
        <div id="c${vc.id}Changing" style="display:none;"><tag:img png="icon_edit" title="common.settingValue"/></div>
      </div>
    </div>
  </c:when>
</c:choose>