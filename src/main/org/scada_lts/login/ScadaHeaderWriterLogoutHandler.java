package org.scada_lts.login;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ScadaHeaderWriterLogoutHandler implements LogoutSuccessHandler {

    private static final Log LOG = LogFactory.getLog(ScadaHeaderWriterLogoutHandler.class);

    private final HeaderWriter headerWriter;

    public ScadaHeaderWriterLogoutHandler(HeaderWriter headerWriter) {
        Assert.notNull(headerWriter, "headerWriter cannot be null");
        this.headerWriter = headerWriter;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        this.headerWriter.writeHeaders(request, response);
        resetUser(request);
        response.sendRedirect("login.htm");
    }

    private static void resetUser(HttpServletRequest request) {
        try {
            User user = Common.getUser(request);
            SystemEventType.returnToNormal(new SystemEventType(
                    SystemEventType.TYPE_USER_LOGIN, user.getId()), System
                    .currentTimeMillis());
            user.cancelTestingUtility();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
        }
    }
}