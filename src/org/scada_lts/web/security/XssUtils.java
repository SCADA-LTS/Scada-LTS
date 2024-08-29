package org.scada_lts.web.security;

import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.regex.Pattern;

public final class XssUtils {

    private XssUtils() {}

    private static final Pattern SECURITY_HTTP_ACCESS_DENIED_QUERY_REGEX = init(SystemSettingsUtils.getSecurityHttpQueryAccessDeniedRegex());
    private static final Pattern SECURITY_HTTP_ACCESS_GRANTED_QUERY_REGEX = init(SystemSettingsUtils.getSecurityHttpQueryAccessGrantedRegex());
    public static final int SECURITY_HTTP_ACCESS_GRANTED_QUERY_LIMIT = SystemSettingsUtils.getSecurityHttpQueryLimit();
    public static final boolean SECURITY_HTTP_QUERY_PROTECT_ENABLED = SystemSettingsUtils.isSecurityHttpQueryProtectEnabled();

    public static boolean validateHttpQuery(String query) {

        if(!SECURITY_HTTP_QUERY_PROTECT_ENABLED)
            return true;

        if (query == null || query.isEmpty()) {
            return false;
        }

        if(query.length() > SECURITY_HTTP_ACCESS_GRANTED_QUERY_LIMIT) {
            return false;
        }

        if(SECURITY_HTTP_ACCESS_DENIED_QUERY_REGEX != null && SECURITY_HTTP_ACCESS_DENIED_QUERY_REGEX.matcher(query).matches()) {
            return false;
        }

        return SECURITY_HTTP_ACCESS_GRANTED_QUERY_REGEX == null || SECURITY_HTTP_ACCESS_GRANTED_QUERY_REGEX.matcher(query).matches();
    }

    private static Pattern init(String regex) {
        if(StringUtils.isEmpty(regex)) {
            return null;
        }
        return Pattern.compile(regex);
    }
}
