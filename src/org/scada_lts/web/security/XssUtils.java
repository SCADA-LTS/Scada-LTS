package org.scada_lts.web.security;

import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class XssUtils {

    private XssUtils() {}

    private static final Pattern SECURITY_HTTP_ACCESS_DENIED_QUERY_REGEX = init(SystemSettingsUtils.getSecurityHttpQueryAccessDeniedRegex());
    private static final Pattern SECURITY_HTTP_ACCESS_GRANTED_QUERY_REGEX = init(SystemSettingsUtils.getSecurityHttpQueryAccessGrantedRegex());
    public static final int SECURITY_HTTP_ACCESS_GRANTED_QUERY_LIMIT = SystemSettingsUtils.getSecurityHttpQueryLimit();
    public static final boolean SECURITY_HTTP_QUERY_PROTECT_ENABLED = SystemSettingsUtils.isSecurityHttpQueryProtectEnabled();

    private static final Pattern SECURITY_HTTP_ACCESS_DENIED_BODY_REGEX = init(SystemSettingsUtils.getSecurityHttpBodyAccessDeniedRegex());
    private static final Pattern SECURITY_HTTP_ACCESS_GRANTED_BODY_REGEX = init(SystemSettingsUtils.getSecurityHttpBodyAccessGrantedRegex());
    public static final boolean SECURITY_HTTP_BODY_PROTECT_ENABLED = SystemSettingsUtils.isSecurityHttpBodyProtectEnabled();

    public static boolean validateHttpQuery(String query) {
        return validate(query, SECURITY_HTTP_QUERY_PROTECT_ENABLED, q -> q.length() > SECURITY_HTTP_ACCESS_GRANTED_QUERY_LIMIT,
                SECURITY_HTTP_ACCESS_DENIED_QUERY_REGEX, SECURITY_HTTP_ACCESS_GRANTED_QUERY_REGEX);

    }

    public static boolean validateHttpBody(String body) {
        return validate(body, SECURITY_HTTP_BODY_PROTECT_ENABLED, b -> false, SECURITY_HTTP_ACCESS_DENIED_BODY_REGEX,
                SECURITY_HTTP_ACCESS_GRANTED_BODY_REGEX);
    }

    private static boolean validate(String input,
                                    boolean protectEnabled,
                                    Predicate<String> beforeCheckRegex,
                                    Pattern accessDeniedRegex,
                                    Pattern accessGrantedRegex) {
        if(!protectEnabled)
            return true;

        if (input == null || input.isEmpty()) {
            return false;
        }

        if(beforeCheckRegex.test(input)) {
            return false;
        }

        if(accessDeniedRegex != null && accessDeniedRegex.matcher(input).matches()) {
            return false;
        }

        return accessGrantedRegex == null || accessGrantedRegex.matcher(input).matches();
    }

    private static Pattern init(String regex) {
        if(StringUtils.isEmpty(regex)) {
            return null;
        }
        return Pattern.compile(regex);
    }
}
