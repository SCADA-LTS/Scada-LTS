<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@ tag body-content="empty"%>
<%@attribute name="vc" type="com.serotonin.mango.view.component.EnhancedImageChartComponent" required="true" rtexprvalue="true"%>
<div id="c${vc.id}SeriesConfig" class="enhancedImageChartConfig">
	<div>
		<h3 style="float: left;"><spring:message code="graphic.enhancedImageChart.seriesConfig"/></h3>
		<tag:img png="cross" onclick="jQuery('#c${vc.id}SeriesConfig').hide()" title="common.close" style="display: inline; float: right; margin-top: 13px;"/>
	</div>
	<div>
		<form>
			<div>
				<span><spring:message code="graphic.enhancedImageChart.seriesConfig.visibility"/></span><br/>
				<c:forEach items="${vc.childComponents}" var="point">
					<c:if test="${!empty point.viewComponent.extendedName}">
						<c:set var="pointName">
							<c:choose>
								<c:when test="${!empty point.viewComponent.alias}">
									<c:out value="${point.viewComponent.alias}"/>
								</c:when>
								<c:otherwise>
									<c:out value="${point.viewComponent.extendedName}"/>
								</c:otherwise>
							</c:choose>
						</c:set>
						<input id="c${vc.id}${point.id}_visibility" type="checkbox" checked="checked" onchange="dygraphsCharts['${vc.id}'].changeVisibility('${pointName}', this.checked)"/>
						<label for="c${vc.id}${point.id}_visibility"><c:out value="${pointName}"/></label><br/>
					</c:if>
				</c:forEach>
			</div>
			<c:forEach items="${vc.childComponents}" var="point" varStatus="status">
				<c:if test="${!empty point.viewComponent.extendedName}">
					<c:set var="pointName">
						<c:choose>
							<c:when test="${!empty point.viewComponent.alias}">
								<c:out value="${point.viewComponent.alias}"/>
							</c:when>
							<c:otherwise>
								<c:out value="${point.viewComponent.extendedName}"/>
							</c:otherwise>
						</c:choose>
					</c:set>
					<div>
						<span>
							<spring:message code="graphic.enhancedImageChart.seriesConfig.seriesOptions" arguments="${pointName}">
					        </spring:message>
						</span><br/>
						<label for="c${vc.id}${point.id}_color" class="margin"><spring:message code="graphic.enhancedImageChart.seriesConfig.seriesOptions.color"/></label>
						<input id="c${vc.id}${point.id}_color" type="hidden" value="${point.viewComponent.color}"/><br/>
						<label for="c${vc.id}${point.id}_strokeWidth" class="margin"><spring:message code="graphic.enhancedImageChart.seriesConfig.seriesOptions.strokeWidth"/></label>
						<input id="c${vc.id}${point.id}_strokeWidth" value="${point.viewComponent.strokeWidth}"/>
						<br/>
					</div>
				</c:if>
			</c:forEach>
		</form>
	</div>
</div>