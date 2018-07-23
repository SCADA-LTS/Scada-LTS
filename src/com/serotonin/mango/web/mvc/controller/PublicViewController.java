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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ViewDao;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;

/**
 * @author Matthew Lohbihler
 */
public class PublicViewController extends ParameterizableViewController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        ViewDao viewDao = new ViewDao();

        // Get the view by id.
        String vid = request.getParameter("viewId");
        View view = null;
        if (vid != null) {
            try {
                view = viewDao.getView(Integer.parseInt(vid));
            }
            catch (NumberFormatException e) { /* no op */
            }
        }
        else {
            String name = request.getParameter("viewName");
            if (name != null)
                view = viewDao.getView(name);
            else {
                String xid = request.getParameter("viewXid");
                if (xid != null)
                    view = viewDao.getViewByXid(xid);
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();

        // Ensure the view has anonymously accessible.
        if (view != null && view.getAnonymousAccess() == ShareUser.ACCESS_NONE)
            view = null;

        if (view != null) {
            model.put("view", view);
            view.validateViewComponents(view.getAnonymousAccess() == ShareUser.ACCESS_READ);
            Common.addAnonymousView(request, view);
        }

        return new ModelAndView(getViewName(), model);
    }
}
