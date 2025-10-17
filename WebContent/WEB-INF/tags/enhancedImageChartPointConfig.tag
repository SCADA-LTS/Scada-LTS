<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@ tag body-content="empty"%>
<%@attribute name="componentId" required="true" %>
<div id="c${componentId}PointConfig" class="enhancedImageChartConfig">
	<div>
		<h2 id="c${componentId}PointConfigTitle" style="float: left;"></h2>
		<tag:img png="cross" onclick="jQuery('#c${componentId}PointConfig').hide()" title="common.close" style="display: inline; float: right; margin-top: 13px;"/>
	</div>
	<div>
		<form>
			<div>
				<label for="c${componentId}_showPoints"><b><spring:message code="graphic.enhancedImageChart.pointConfig.showPoints"/></b></label><input id="c${componentId}_showPoints" type="checkbox"/>
			</div>
			<div class="margin1">
				<b><spring:message code="graphic.enhancedImageChart.pointConfig.renderMode"/></b>
				<input id="c${componentId}_renderModeLine" name="c${componentId}_renderMode" type="radio"/><label for="c${componentId}_renderModeLine"><spring:message code="graphic.enhancedImageChart.pointConfig.renderMode.line"/></label>
				<input id="c${componentId}_renderModeSpline" name="c${componentId}_renderMode" type="radio"/><label for="c${componentId}_renderModeSpline"><spring:message code="graphic.enhancedImageChart.pointConfig.renderMode.spline"/></label>
			</div>
		</form>
	</div>
</div>