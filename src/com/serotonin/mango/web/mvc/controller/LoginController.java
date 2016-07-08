/*
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
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.mvc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.integration.CrowdUtils;
import com.serotonin.mango.web.mvc.form.LoginForm;
import com.serotonin.util.StringUtils;
import com.serotonin.util.ValidationUtils;

public class LoginController extends SimpleFormController {
    private static final Log logger = LogFactory.getLog(LoginController.class);

    private boolean mobile;
    private String successUrl;
    private String newUserUrl;

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public void setSuccessUrl(String url) {
        successUrl = url;
    }

    public void setNewUserUrl(String newUserUrl) {
        this.newUserUrl = newUserUrl;
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors,
            @SuppressWarnings("rawtypes") Map controlModel) throws Exception {
        // Check if Crowd is enabled
        if (CrowdUtils.isCrowdEnabled()) {
            String username = CrowdUtils.getCrowdUsername(request);

            if (username != null) {
                ((LoginForm) errors.getTarget()).setUsername(username);

                // The user is logged into Crowd. Make sure the username is valid in this instance.
                User user = new UserDao().getUser(username);
                if (user == null)
                    ValidationUtils.rejectValue(errors, "username", "login.validation.noSuchUser");
                else {
                    // Validate some stuff about the user.
                    if (user.isDisabled())
                        ValidationUtils.reject(errors, "login.validation.accountDisabled");
                    else {
                        if (CrowdUtils.isAuthenticated(request, response)) {
                            ModelAndView mav = performLogin(request, username);
                            CrowdUtils.setCrowdAuthenticated(Common.getUser(request));
                            return mav;
                        }
                    }
                }
            }
        }
        return super.showForm(request, response, errors, controlModel);
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) {
        LoginForm login = (LoginForm) command;

        // Make sure there is a username
        if (StringUtils.isEmpty(login.getUsername()))
            ValidationUtils.rejectValue(errors, "username", "login.validation.noUsername");

        // Make sure there is a password
        if (StringUtils.isEmpty(login.getPassword()))
            ValidationUtils.rejectValue(errors, "password", "login.validation.noPassword");
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        LoginForm login = (LoginForm) command;

        boolean crowdAuthenticated = false;

        // Check if the user exists
        User user = new UserDao().getUser(login.getUsername());
        if (user == null)
            ValidationUtils.rejectValue(errors, "username", "login.validation.noSuchUser");
        else if (user.isDisabled())
            ValidationUtils.reject(errors, "login.validation.accountDisabled");
        else {
            if (CrowdUtils.isCrowdEnabled())
                // First attempt authentication with Crowd.
                crowdAuthenticated = CrowdUtils.authenticate(request, response, login.getUsername(),
                        login.getPassword());

            if (!crowdAuthenticated) {
                String passwordHash = Common.encrypt(login.getPassword());

                // Validating the password against the database.
                if (!passwordHash.equals(user.getPassword()))
                    ValidationUtils.reject(errors, "login.validation.invalidLogin");
            }
        }

        if (errors.hasErrors())
            return showForm(request, response, errors);

        ModelAndView mav = performLogin(request, login.getUsername());
        if (crowdAuthenticated)
            CrowdUtils.setCrowdAuthenticated(Common.getUser(request));
        return mav;
    }

    private ModelAndView performLogin(HttpServletRequest request, String username) {
        // Check if the user is already logged in.
        User user = Common.getUser(request);
        if (user != null && user.getUsername().equals(username)) {
            // The user is already logged in. Nothing to do.
            if (logger.isDebugEnabled())
                logger.debug("User is already logged in, not relogging in");
        }
        else {
            UserDao userDao = new UserDao();
            // Get the user data from the app server.
            user = new UserDao().getUser(username);

            // Update the last login time.
            userDao.recordLogin(user.getId());

            // Add the user object to the session. This indicates to the rest
            // of the application whether the user is logged in or not.
            Common.setUser(request, user);
            if (logger.isDebugEnabled())
                logger.debug("User object added to session");
        }

        if (!mobile) {
            if (user.isFirstLogin())
                return new ModelAndView(new RedirectView(newUserUrl));
            if (!StringUtils.isEmpty(user.getHomeUrl()))
                return new ModelAndView(new RedirectView(user.getHomeUrl()));
        }

        return new ModelAndView(new RedirectView(successUrl));
    }
}
