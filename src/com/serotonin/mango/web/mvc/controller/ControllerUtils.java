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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;

import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.web.i18n.Utf8ResourceBundle;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * @author Matthew Lohbihler
 */
public final class ControllerUtils {

    private static final Log LOG = LogFactory.getLog(ControllerUtils.class);

    private ControllerUtils() {}

    public static ResourceBundle getResourceBundle(HttpServletRequest request) {
        return Utf8ResourceBundle.getBundle("messages", getLocale(request));
    }

    public static Locale getLocale(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(request.getSession().getServletContext());
        LocaleResolver localeResolver = (LocaleResolver) webApplicationContext.getBean("localeResolver");
        return localeResolver.resolveLocale(request);
    }

    public static void addPointListDataToModel(User user, int pointId, Map<String, Object> model) {
        List<DataPointVO> allPoints = new DataPointDao().getDataPoints(DataPointExtendedNameComparator.instance, false);
        List<DataPointVO> userPoints = new LinkedList<DataPointVO>();
        int pointIndex = -1;
        for (DataPointVO dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
                if (dp.getId() == pointId)
                    pointIndex = userPoints.size() - 1;
            }
        }
        model.put("userPoints", userPoints);

        // Determine next and previous ids
        if (pointIndex > 0)
            model.put("prevId", userPoints.get(pointIndex - 1).getId());
        if (pointIndex < userPoints.size() - 1)
            model.put("nextId", userPoints.get(pointIndex + 1).getId());
    }
    
    public static void addPointListDataToModel(User user, int pointId, Model model){
        List<DataPointVO> allPoints = new DataPointDao().getDataPoints(DataPointExtendedNameComparator.instance, false);
        List<DataPointVO> userPoints = new LinkedList<DataPointVO>();
        int pointIndex = -1;
        for (DataPointVO dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
                if (dp.getId() == pointId)
                    pointIndex = userPoints.size() - 1;
            }
        }
        model.addAttribute("userPoints", userPoints);

        // Determine next and previous ids
        if (pointIndex > 0)
        	model.addAttribute("prevId", userPoints.get(pointIndex - 1).getId());
        if (pointIndex < userPoints.size() - 1)
            model.addAttribute("nextId", userPoints.get(pointIndex + 1).getId());
    }

    public static void setLocale(String locale) {

        Permissions.ensureValidUser();
        Common.setSystemLanguage(locale);

        WebContext webContext = WebContextFactory.get();
        if(webContext != null) {
            HttpServletRequest request = webContext.getHttpServletRequest();
            HttpServletResponse response = webContext.getHttpServletResponse();
            setLocale(request, response, locale);
            Common.setSystemLanguage(locale);
        } else {
            LOG.warn("webContext is null");
        }
    }

    public static void setLocale(HttpServletRequest request, HttpServletResponse response, String locale) {
        User user = Common.getUser(request);
        if(user != null) {
            LocaleResolver localeResolver = new SessionLocaleResolver();
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setAsText(locale);
            localeResolver.setLocale(request, response, (Locale) localeEditor.getValue());
            Common.setSystemLanguage(locale);
        } else {
            LOG.warn("User is logged!");
        }
    }
}
