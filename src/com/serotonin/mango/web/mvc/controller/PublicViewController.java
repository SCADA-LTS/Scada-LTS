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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.vo.User;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.mango.service.ViewService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;

import static com.serotonin.mango.web.dwr.util.AnonymousUserUtils.getUser;
import static com.serotonin.mango.web.dwr.util.AnonymousUserUtils.authenticateAnonymousUser;

/**
 * @author Matthew Lohbihler
 */
public class PublicViewController extends ParameterizableViewController {

    public final ViewService viewService;
    public final UserService userService;

    public PublicViewController() {
        this.viewService = new ViewService();
        this.userService = new UserService();
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        return getView(request, viewService).stream()
                .filter(this::hasPermissions)
                .flatMap(view -> getUser(userService, request).stream()
                        .peek(user -> authenticateAnonymousUser(user, request, response))
                        .filter(user -> hasPermissions(view, user))
                        .map(user -> {
                            applyPermissions(view, user);
                            Common.addAnonymousView(request, view);
                            return createModel(view);
                        })
                )
                .map(model -> new ModelAndView(getViewName(), model))
                .findAny()
                .orElse(new ModelAndView(getViewName(), new HashMap<>()));
    }

    private Map<String, Object> createModel(View view) {
        Map<String, Object> model = new HashMap<>();
        model.put("view", view);
        return model;
    }

    private Optional<View> getView(HttpServletRequest request, ViewService viewService) {
        // Get the view by id.
        String vid = request.getParameter("viewId");
        View view = null;
        if (vid != null) {
            try {
                view = viewService.getView(Integer.parseInt(vid));
            }
            catch (NumberFormatException e) { /* no op */
            }
        }
        else {
            String name = request.getParameter("viewName");
            if (name != null)
                view = viewService.getView(name);
            else {
                String xid = request.getParameter("viewXid");
                if (xid != null)
                    view = viewService.getViewByXid(xid);
            }
        }
        return Optional.ofNullable(view);
    }

    private boolean hasPermissions(View view, User user) {
        return view.getUserAccess(user) > ShareUser.ACCESS_NONE;
    }

    private boolean hasPermissions(View view) {
        return view.getAnonymousAccess() > ShareUser.ACCESS_NONE;
    }

    private void applyPermissions(View view, User user) {
        view.validateViewComponentsAnon(user);
        view.getViewUsers().stream()
                .filter(a -> a.getAccessType() > view.getAnonymousAccess())
                .forEach(a -> a.setAccessType(view.getAnonymousAccess()));
    }
}
