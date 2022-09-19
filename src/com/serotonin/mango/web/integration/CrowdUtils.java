package com.serotonin.mango.web.integration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.crowd.exception.ApplicationAccessDeniedException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidTokenException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.ClientResourceLocator;
import com.atlassian.crowd.service.client.CrowdClient;
import com.atlassian.crowd.service.factory.CrowdClientFactory;
import com.serotonin.mango.Common;

public class CrowdUtils {
    private static final Log LOG = LogFactory.getLog(CrowdUtils.class);
    private static final String CROWD_AUTHENTICATED_KEY = CrowdUtils.class.getName() + "CROWD_AUTHENTICATED_KEY";

    private volatile static CrowdHttpAuthenticator authenticator;

    public static boolean isCrowdEnabled() {
        return Common.getEnvironmentProfile().getBoolean("auth.crowd.on", false);
    }

    public static boolean authenticate(HttpServletRequest request, HttpServletResponse response, String username,
            String password) {
        long start = System.currentTimeMillis();

        boolean authenticated = false;

        try {
            getAuthenticator().authenticate(request, response, username, password);
            authenticated = true;
        }
        catch (ApplicationPermissionException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (InvalidAuthenticationException e) {
            // ignore
        }
        catch (OperationFailedException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (ExpiredCredentialException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (InactiveAccountException e) {
            // ignore
        }
        catch (ApplicationAccessDeniedException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (InvalidTokenException e) {
            // ignore
        }

        if (LOG.isDebugEnabled())
            LOG.debug("Authentication check took " + (System.currentTimeMillis() - start) + "ms");

        return authenticated;
    }

    public static boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) {

        try {
            return getAuthenticator().isAuthenticated(request, response);
        }
        catch (OperationFailedException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }

        return false;
    }

    public static String getCrowdUsername(HttpServletRequest request) {

        try {
            User user = getAuthenticator().getUser(request);
            if (user != null)
                return user.getName();
        }
        catch (ApplicationPermissionException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (InvalidAuthenticationException e) {
            // ignore
        }
        catch (OperationFailedException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (InvalidTokenException e) {
            // ignore
        }

        return null;
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {

        try {
            getAuthenticator().logout(request, response);
        }
        catch (ApplicationPermissionException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
        catch (InvalidAuthenticationException e) {
            // ignore
        }
        catch (OperationFailedException e) {
            LOG.warn("Exception during Crowd authentication attempt", e);
        }
    }

    public static void setCrowdAuthenticated(com.serotonin.mango.vo.User user) {
        user.setAttribute(CROWD_AUTHENTICATED_KEY, true);
    }

    public static boolean isCrowdAuthenticated(com.serotonin.mango.vo.User user) {
        Boolean b = user.getAttribute(CROWD_AUTHENTICATED_KEY);
        return b == null ? false : b;
    }

    private static CrowdHttpAuthenticator getAuthenticator() {
        CrowdHttpAuthenticator auth = authenticator;
        if (auth == null) {
            synchronized (CrowdUtils.class) {
                auth = authenticator;
                if (auth == null) {
                    ClientResourceLocator clientResourceLocator = new ClientResourceLocator("crowd.properties");
                    ClientProperties props = ClientPropertiesImpl.newInstanceFromResourceLocator(clientResourceLocator);
                    CrowdClientFactory clientFactory = new RestCrowdClientFactory();
                    CrowdClient client = clientFactory.newInstance(props);
                    CrowdHttpTokenHelper tokenHelper = CrowdHttpTokenHelperImpl
                            .getInstance(CrowdHttpValidationFactorExtractorImpl.getInstance());
                    auth = new CrowdHttpAuthenticatorImpl(client, props, tokenHelper);
                    authenticator = auth;
                }
            }
        }
        return auth;
    }
}
