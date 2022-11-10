package org.scada_lts.web.mvc.api.security;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiPredicate;

public final class GuardUtils {

    private static final Log LOG = LogFactory.getLog(GuardUtils.class);

    private GuardUtils() {}

    public static boolean doHasPermission(HttpServletRequest request,
                                           BiPredicate<User, Integer> permissionById,
                                           BiPredicate<User, String> permissionByXid,
                                           String identifier, boolean isXid) {
        User user = Common.getUser(request);
        if(isXid) {
            if(permissionByXid == null) {
                LOG.warn("permissionByXid is null");
                return false;
            }
            return permissionByXid.test(user, identifier);
        } else {
            int conv = converter(identifier);
            if (conv != Common.NEW_ID) {
                return permissionById.test(user, conv);
            }
        }
        return false;
    }

    public static int converter(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return Common.NEW_ID;
        }
    }

    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
        return mapper;
    }
}
