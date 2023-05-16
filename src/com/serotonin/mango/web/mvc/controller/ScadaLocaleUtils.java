package com.serotonin.mango.web.mvc.controller;

import com.serotonin.db.KeyValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

public final class ScadaLocaleUtils {

    private ScadaLocaleUtils() {}

    private static final Log LOG = LogFactory.getLog(ScadaLocaleUtils.class);

    public static boolean setLocale(String locale) {
        return setLocale(locale, request -> response -> lang -> setLocale(request, response, lang));
    }

    public static boolean setLocaleInSession(String locale) {
        return setLocale(locale, request -> response -> lang -> setLocaleInSession(request, response, lang));
    }

    public static boolean setLocale(HttpServletRequest request, HttpServletResponse response, String lang) {
        User user = Common.getUser(request);
        if(user != null && user.isAdmin()) {
            setSystemLanguage(lang);
            return true;
        } else {
            LOG.warn("User is not logged!");
            return false;
        }
    }

    public static boolean setLocaleInSession(HttpServletRequest request, HttpServletResponse response) {
        return setLocaleInSession(request, response, getLocale(request, false).getLanguage());
    }

    public static String getMessage(String key) {
        return LocalizableMessage.getMessage(getBundle(), key);
    }

    public static ResourceBundle getBundle(HttpServletRequest request) {
        if(request == null) {
            return getResourceBundleByLocale(getLocale(null, false));
        }
        return getResourceBundleByLocale(getLocale(request, true));
    }

    public static ResourceBundle getBundle() {
        return getBundle(getRequestFromWebContext());
    }

    public static String getMessage(String key, Object... args) {
        String pattern = getMessage(key);
        return MessageFormat.format(pattern, args);
    }

    public static void setSystemLanguage(String language) {
        Locale locale = findLocale(language);
        if (locale == null)
            throw new IllegalArgumentException(
                    "Locale for given language not found: " + language);
        new SystemSettingsDAO().setValue(SystemSettingsDAO.LANGUAGE, language);
    }

    public static List<KeyValuePair> getLanguages() {
        List<KeyValuePair> languages = new ArrayList<>();
        ResourceBundle i18n = ResourceBundle.getBundle("i18n");
        for (String key : i18n.keySet())
            languages.add(new KeyValuePair(key, i18n.getString(key)));
        return languages;
    }

    public static Locale findLocale(String language) {
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getLanguage().equals(language))
                return locale;
        }
        return null;
    }

    private static boolean setLocaleInSession(HttpServletRequest request, HttpServletResponse response, String localeCode) {
        try {
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setAsText(localeCode);
            LocaleResolver localeResolver = getLocaleResolver(request);
            Locale locale = (Locale) localeEditor.getValue();
            localeResolver.setLocale(request, response, locale);
            return true;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static LocaleResolver getLocaleResolver(HttpServletRequest request) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if(localeResolver == null) {
            WebApplicationContext webApplicationContext = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(request.getSession().getServletContext());
            localeResolver = (LocaleResolver) webApplicationContext.getBean("localeResolver");
        }
        return localeResolver;
    }

    private static boolean setLocale(String locale, Function<HttpServletRequest, Function<HttpServletResponse, Function<String, Boolean>>> setLocale) {
        Permissions.ensureValidUser();
        WebContext webContext = WebContextFactory.get();
        if(webContext != null) {
            HttpServletRequest request = webContext.getHttpServletRequest();
            HttpServletResponse response = webContext.getHttpServletResponse();
            return setLocale.apply(request).apply(response).apply(locale);
        } else {
            LOG.warn("webContext is null!");
            return false;
        }
    }

    private static Locale getLocaleFromSession(HttpServletRequest request) {
        return getLocaleResolver(request).resolveLocale(request);
    }

    private static ResourceBundle getResourceBundleByLocale(Locale locale) {
        return ResourceBundle.getBundle("messages", locale);
    }

    private static Locale getLocale(HttpServletRequest request, boolean localeIsSetInSession) {
        Locale locale = null;
        if(request != null && localeIsSetInSession) {
            locale = getLocaleFromSession(request);
        }
        User user = Common.getUser();
        if(request != null && user == null) {
            user = Common.getUser(request);
        }
        if(locale == null && user != null) {
            locale = findLocale(user.getLang());
        }
        String langFromSystemSettings = SystemSettingsDAO.getValue(SystemSettingsDAO.LANGUAGE);
        if(locale == null) {
            locale = findLocale(langFromSystemSettings);
        }
        if(locale == null)
            throw new IllegalArgumentException(
                    "Locale for given language not found: "
                            + langFromSystemSettings);
        return locale;
    }

    private static HttpServletRequest getRequestFromWebContext() {
        WebContext webContext = WebContextFactory.get();
        if(webContext != null)
            return webContext.getHttpServletRequest();
        return null;
    }
}
