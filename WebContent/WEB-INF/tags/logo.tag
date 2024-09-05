<%@include file="/WEB-INF/tags/decl.tagf" %>

<div class="logo-section">
    <div class="logo-section--title">
      <img id="logo" src="assets/logo.png" alt="Logo">

        <div id="top-description-container">
            <span id="top-description-prefix" class="custom-text">
                <c:out value="${topDescriptionPrefix}"/>
            </span>
            <span id="top-description" class="custom-text">
                <c:out value="${topDescription}"/>
            </span>
        </div>

      <c:if test="${(!empty scadaVersion) && scadaVersion.isShowVersionInfo()}">
        <c:if test="${!empty scadaVersion.getCompanyName()}">
        <div id="company-container">
            <span>
                <fmt:message key="logo.for"/>
            </span>
            <span id="company-name">
                ${scadaVersion.getCompanyName()}
            </span>
            </div>
        </c:if>
      </c:if>
    </div>

    <c:if test="${(!empty scadaVersion) && scadaVersion.isShowVersionInfo()}">
      <div class="logo-section--details">
      <c:if test="${!empty scadaVersion.getPoweredBy()}">
        <span id="powered-by">
            <fmt:message key="logo.powered"/>
            ${scadaVersion.getPoweredBy()}
        </span>
      </c:if>
      <span id="scada-details--version">
        ${scadaVersion.getVersionNumber()}
        build ${scadaVersion.getBuildNumber()}
      </span>
      <c:if test="${!scadaVersion.getCommitNumber().contains('N/A')}">
        <span id="scada-details--github">
            (GitHub ref:
            ${scadaVersion.getCommitNumber().length() > 7 ? scadaVersion.getCommitNumber().substring(0,7) : scadaVersion.getCommitNumber()});
        </span>
      </c:if>
      <span id="scada-details--os">
        runs on ${scadaVersion.getRunningOs()}
      </span>
    </div>

    </c:if>
  </div>