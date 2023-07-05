package org.scada_lts.web.ws.config;

import org.scada_lts.mango.service.SystemSettingsService;
import org.springframework.security.web.header.HeaderWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HeadersFromSystemSettingsWriter implements HeaderWriter {

    private final SystemSettingsService systemSettingsService;

    public HeadersFromSystemSettingsWriter(SystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
    }

    @Override
    public void writeHeaders(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Map<String, String> staticHeaders = systemSettingsService.getHttpResponseHeaders();
        staticHeaders.forEach(httpServletResponse::addHeader);
    }
}
