package org.scada_lts.utils;

import org.apache.commons.validator.routines.UrlValidator;


import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

public final class UrlValidatorUtils {

    private UrlValidatorUtils() {}

    public static boolean isValid(String url) {
        if(url == null)
            return false;
        return new UrlValidator(ALLOW_LOCAL_URLS).isValid(url);
    }
}
