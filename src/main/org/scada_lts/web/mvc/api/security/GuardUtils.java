package org.scada_lts.web.mvc.api.security;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiPredicate;

public final class GuardUtils {

    private static final Log LOG = LogFactory.getLog(GuardUtils.class);

    public static final String ARG_IS_XID_IS_NOT_SUPPORTED = "- arg isXid is not supported.";

    private GuardUtils() {}

    public static boolean doHasPermission(HttpServletRequest request,
                                          BiPredicate<User, Integer> permissionById,
                                          BiPredicate<User, String> permissionByXid,
                                          String identifier,
                                          boolean isXid) {
        if(StringUtils.isEmpty(identifier))
            return false;
        if(identifier.contains(",")) {
            String[] identifiers = identifier.split(",");
            for(String id: identifiers) {
                if(!doHas(request, permissionById, permissionByXid, id, isXid))
                    return false;
            }
            return true;
        }
        return doHas(request, permissionById, permissionByXid, identifier, isXid);
    }

    private static boolean doHas(HttpServletRequest request,
                                BiPredicate<User, Integer> permissionById,
                                BiPredicate<User, String> permissionByXid,
                                String identifier, boolean isXid) {
        User user = Common.getUser(request);
        if(isXid) {
            return permissionByXid.test(user, identifier);
        } else {
            int conv = converter(identifier);
            if (conv != Common.NEW_ID) {
                return permissionById.test(user, conv);
            }
        }
        return false;
    }

    private static int converter(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            LOG.warn("Trying to convert the value of " + value + " to int, failed. Exception: " + ex.getMessage(), ex);
            return Common.NEW_ID;
        }
    }
}
