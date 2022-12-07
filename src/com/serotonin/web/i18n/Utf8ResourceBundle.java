package com.serotonin.web.i18n;

import java.util.*;

public abstract class Utf8ResourceBundle {

    public Utf8ResourceBundle() {
    }

    public static final void clearCache() {
        ResourceBundle.clearCache();
    }

    public static final ResourceBundle getBundle(String baseName) {
        return ResourceBundle.getBundle(baseName);
    }

    public static final ResourceBundle getBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }

    public static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
        return ResourceBundle.getBundle(baseName, locale, loader);
    }
}