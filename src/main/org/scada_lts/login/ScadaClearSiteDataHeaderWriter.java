package org.scada_lts.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ScadaClearSiteDataHeaderWriter implements HeaderWriter {

    private static final String CLEAR_SITE_DATA_HEADER = "Clear-Site-Data";

    private static final Log LOG = LogFactory.getLog(ScadaClearSiteDataHeaderWriter.class);

    private final RequestMatcher requestMatcher;

    private String headerValue;

    public ScadaClearSiteDataHeaderWriter(Directive... directives) {
        Assert.notEmpty(directives, "directives cannot be empty or null");
        this.requestMatcher = new SecureRequestMatcher();
        this.headerValue = transformToHeaderValue(directives);
    }

    @Override
    public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (this.requestMatcher.matches(request)) {
            if (!response.containsHeader(CLEAR_SITE_DATA_HEADER)) {
                response.setHeader(CLEAR_SITE_DATA_HEADER, this.headerValue);
            }
        }
    }

    private String transformToHeaderValue(Directive... directives) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < directives.length - 1; i++) {
            sb.append(directives[i].headerValue).append(", ");
        }
        sb.append(directives[directives.length - 1].headerValue);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [headerValue=" + this.headerValue + "]";
    }

    public enum Directive {

        CACHE("cache"),

        COOKIES("cookies"),

        STORAGE("storage"),

        EXECUTION_CONTEXTS("executionContexts"),

        ALL("*");

        private final String headerValue;

        Directive(String headerValue) {
            this.headerValue = "\"" + headerValue + "\"";
        }

        public String getHeaderValue() {
            return this.headerValue;
        }

    }

    private static final class SecureRequestMatcher implements RequestMatcher {

        @Override
        public boolean matches(HttpServletRequest request) {
            return request.isSecure();
        }

        @Override
        public String toString() {
            return "Is Secure";
        }

    }

}