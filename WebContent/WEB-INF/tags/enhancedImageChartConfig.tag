<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@ tag body-content="empty"%>
<%@attribute name="componentId" required="true" %>
<div id="c${componentId}ConfigButtonDiv">
	<button onclick="dygraphsCharts['${componentId}'].showConfiguration()"><fmt:message key="graphic.enhancedImageChart.config"></fmt:message></button>
</div>
<div id="c${componentId}Config" class="enhancedImageChartConfig">
	<div>
		<h3 style="float: left;"><fmt:message key="graphic.enhancedImageChart.config.title"/></h3>
		<tag:img png="cross" onclick="jQuery('#c${componentId}Config').hide()" title="common.close" style="display: inline; float: right; margin-top: 13px;"/>
	</div>
	<div>
		<form>
			<div>
				<b><fmt:message key="graphic.enhancedImageChart.config.axes.ranges"/></b>
				<div class="margin2">
					<label for="c${componentId}_yRangeMin"><fmt:message key="graphic.enhancedImageChart.config.y.min"/></label><input id="c${componentId}_yRangeMin" type="text" size="8"/><br/>
					<label for="c${componentId}_yRangeMax"><fmt:message key="graphic.enhancedImageChart.config.y.max"/></label><input id="c${componentId}_yRangeMax" type="text" size="8"/>
				</div>
				<button id="c${componentId}_yRangeSet" type="button" onclick="dygraphsCharts['${componentId}'].setYRange()"><fmt:message key="graphic.enhancedImageChart.config.set"/></button><br/>
				<button id="c${componentId}_yZoomIn" type="button" onclick="dygraphsCharts['${componentId}'].zoomInGraphY()"><fmt:message key="graphic.enhancedImageChart.config.zoomIn"/></button>
				<button id="c${componentId}_yZoomOut" type="button" onclick="dygraphsCharts['${componentId}'].zoomOutGraphY()"><fmt:message key="graphic.enhancedImageChart.config.zoomOut"/></button><br/>
				<div class="margin2">
					<label for="c${componentId}_xRangeMin"><fmt:message key="graphic.enhancedImageChart.config.x.min"/></label><input id="c${componentId}_xRangeMin" type="text" size="18"/><br/>
					<label for="c${componentId}_xRangeMax"><fmt:message key="graphic.enhancedImageChart.config.x.max"/></label><input id="c${componentId}_xRangeMax" type="text" size="18"/>
				</div>
				<button id="c${componentId}_xRangeSet" type="button" onclick="dygraphsCharts['${componentId}'].setXRange()"><fmt:message key="graphic.enhancedImageChart.config.set"/></button><br/>
				<button id="c${componentId}_xZoomIn" type="button" onclick="dygraphsCharts['${componentId}'].zoomInGraphX()"><fmt:message key="graphic.enhancedImageChart.config.zoomIn"/></button>
				<button id="c${componentId}_xZoomOut" type="button" onclick="dygraphsCharts['${componentId}'].zoomOutGraphX()"><fmt:message key="graphic.enhancedImageChart.config.zoomOut"/></button><br/>
			</div>
			<div class="margin1">
				<b><fmt:message key="graphic.enhancedImageChart.config.export"/></b>
				<div class="margin2">
					<button id="c${componentId}_PNGExport" type="button" onclick="dygraphsCharts['${componentId}'].exportAsImage()"><fmt:message key="graphic.enhancedImageChart.config.export.png"/></button>
					<button id="c${componentId}_CSVExport" type="button" onclick="dygraphsCharts['${componentId}'].exportAsCSV()"><fmt:message key="graphic.enhancedImageChart.config.export.csv"/></button>
				</div>
			</div>
		</form>
	</div>
</div>