package org.scada_lts.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ScadaHeaderWriterLogoutHandler implements LogoutSuccessHandler {

    private final HeaderWriter headerWriter;

    public ScadaHeaderWriterLogoutHandler(HeaderWriter headerWriter) {
        Assert.notNull(headerWriter, "headerWriter cannot be null");
        this.headerWriter = headerWriter;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        this.headerWriter.writeHeaders(request, response);
        response.sendRedirect("login.htm");
    }
}