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
--%><%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@tag body-content="empty"%><%--
--%><%@attribute name="view" type="com.serotonin.mango.view.View" required="true" rtexprvalue="true"%><%--
--%><%@attribute name="emptyMessageKey" required="true"%>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        const viewBackground = document.getElementById("viewBackground");
        if(!viewBackground.src.includes("spacer.gif")) {
            loadDefaultSizeContainer('${view.backgroundFilename}','viewBackground');
        }
    })
</script>
<div id="viewContainer">
<div id="viewContent" width="${view.width}" height="${view.height}">
  <c:choose>
    <c:when test="${empty view}"><fmt:message key="${emptyMessageKey}"/> <a href="view_edit.shtm"><fmt:message key="views.noViews.prefix"/></a></c:when>
    <c:when test="${empty view.backgroundFilename}">
      <img id="viewBackground" src="images/spacer.gif" alt="" width="${view.width}" height="${view.height}"/>
    </c:when>
    <c:otherwise>
      <img id="viewBackground" src="${view.backgroundFilename}" alt=""/>
    </c:otherwise>
  </c:choose>
  
  <c:forEach items="${view.viewComponents}" var="vc">
    <!-- vc ${vc.id} -->
    <c:choose>
      <c:when test="${!vc.visible}"><!-- vc ${vc.id} not visible --></c:when>
      
      <c:when test="${vc.defName == 'simpleCompound'}">
        <div id="c${vc.id}" class="viewComponent" style="position:absolute;left:${vc.x}px;top:${vc.y}px;z-index:${vc.z}"
                  onmouseover="vcOver('c${vc.id}', ${vc.z+1});" onmouseout="vcOut('c${vc.id}', ${vc.z});">
          <tag:pointComponent vc="${vc.leadComponent}"/>
          <c:choose>
            <c:when test="${empty vc.backgroundColour}"><c:set var="bkgd"></c:set></c:when>
            <c:otherwise><c:set var="bkgd">background:${vc.backgroundColour};</c:set></c:otherwise>
          </c:choose>
          <div id="c${vc.id}Controls" class="controlContent" style="left:5px;top:5px;${bkgd}">
            <b>${vc.name}</b><br/>
            <c:forEach items="${vc.childComponents}" var="child">
              <c:if test="${child.viewComponent.visible && child.viewComponent.id != vc.leadComponent.id}">
                <tag:pointComponent vc="${child.viewComponent}"/>
              </c:if>
            </c:forEach>
          </div>
        </div>
      </c:when>
      
      <c:when test="${vc.defName == 'imageChart'}">
        <div id="c${vc.id}" class="viewComponent" style="position:absolute;left:${vc.x}px;top:${vc.y}px;z-index:${vc.z};"
                  onmouseover="vcOver('c${vc.id}', ${vc.z+1});" onmouseout="vcOut('c${vc.id}', ${vc.z});">
          <div id="c${vc.id}Content"><img src="images/icon_comp.png" alt=""/></div>
          <div id="c${vc.id}Controls" class="controlContent">
            <div id="c${vc.id}Info">
              <tag:img png="hourglass" title="common.gettingData"/>
            </div>
          </div>
        </div>
      </c:when>
      
       <c:when test="${vc.defName == 'enhancedImageChart'}">
		<div id="c${vc.id}" class="viewComponent"
			style="position:absolute;left:${vc.x}px;top:${vc.y}px;background-color:white;">
			<tag:enhancedImageChartConfig componentId="${vc.id}"/>
			<tag:enhancedImageChartPointConfig componentId="${vc.id}"/>
			<tag:enhancedImageChartSeriesConfig vc="${vc}"/>
			
			<div id="c${vc.id}Graph" class="enhancedImageChart" onclick="jQuery('#c${vc.id}ConfigButtonDiv').toggle();"></div>
			<div id="c${vc.id}LegendBox" class="enhancedImageChartLegend" onclick="jQuery('#c${vc.id}SeriesConfig').show()">
				<div id="c${vc.id}Legend"></div>
			</div>
			<img id="c${vc.id}ExportImage" style="display: none;"/>
			
			
			
		</div>
		<script type="text/javascript">
			var pointProps = new Array();
			<c:forEach var="childComponent" items="${vc.childComponents}">
				<c:if test="${not empty childComponent.viewComponent.extendedName}">
					pointProps.push({
						pointName: "${childComponent.viewComponent.extendedName}",
	        			alias: "${childComponent.viewComponent.alias}",
	        			color: "${childComponent.viewComponent.color}",
	        			strokeWidth: ${childComponent.viewComponent.strokeWidth},
	        			lineType: "${childComponent.viewComponent.lineType}",
	        			showPoints: ${childComponent.viewComponent.showPoints}
					});
				</c:if>
			</c:forEach>
			dygraphsCharts[${vc.id}] = new DygraphsChart(${view.id}, ${vc.id}, true, false);
			dygraphsCharts[${vc.id}].updateOptions(${vc.width}, ${vc.height}, ${vc.durationType}, ${vc.durationPeriods},
					"${vc.enhancedImageChartType}", pointProps);
			dygraphsCharts[${vc.id}].initConfigControls();
			
			jQuery('#c${vc.id}ConfigButtonDiv').hide().css("position", "absolute");
		</script>
      </c:when>
      
      <c:when test="${vc.compoundComponent}">
        <div id="c${vc.id}" class="viewComponent" style="position:absolute;left:${vc.x}px;top:${vc.y}px;z-index:${vc.z}"
                  onmouseover="vcOver('c${vc.id}', ${vc.z+1});" onmouseout="vcOut('c${vc.id}', ${vc.z});">
          ${vc.staticContent}
          <div id="c${vc.id}Controls" class="controlsDiv">
            <table cellpadding="0" cellspacing="1">
              <tr onmouseover="showMenu('c${vc.id}Info', 16, 0);" onmouseout="hideLayer('c${vc.id}Info');"><td>
                <tag:img png="information"/>
                <div id="c${vc.id}Info" onmouseout="hideLayer(this);">
                  <tag:img png="hourglass" title="common.gettingData"/>
                </div>
              </td></tr>
              <c:if test="${vc.displayImageChart}">
                <tr onmouseover="mango.view.showChart('${vc.id}', event, this);" 
                        onmouseout="mango.view.hideChart('${vc.id}', event, this);"><td>
                  <img src="images/icon_chart.png" alt=""/>
                  <div id="c${vc.id}ChartLayer"></div>
                  <textarea style="display:none;" id="c${vc.id}Chart"><tag:img png="hourglass" title="common.gettingData"/></textarea>
                </td></tr>
              </c:if>
            </table>
          </div>
          <c:forEach items="${vc.childComponents}" var="child">
            <c:if test="${child.viewComponent.visible}"><tag:pointComponent vc="${child.viewComponent}"/></c:if>
          </c:forEach>
        </div>
      </c:when>
      
      <c:otherwise><tag:pointComponent vc="${vc}"/></c:otherwise>
    </c:choose>
  </c:forEach>
</div>
</div>