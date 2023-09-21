package org.scada_lts.utils;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

public class XidUtils {

    public static String generateXid(String prefix) {
        return prefix + generateRandomString(6, "0123456789");
    }

    public static String generateRandomString(final int length, final String charSet) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            Random random = new Random();
            sb.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        return sb.toString();
    }

    public static boolean validateXid(Map<String, String> errors, ResourceBundle resourceBundle, PointEventDetectorVO ped) {
        if (StringUtils.isEmpty(ped.getXid())) {
            errors.put("eventDetector" + ped.getId() + "ErrorMessage", LocalizableMessage.getMessage(resourceBundle,"validate.ped.xidMissing"));
            return true;
        }
        if (ped.getXid().length() > 50) {
            errors.put("eventDetector" + ped.getId() + "ErrorMessage", LocalizableMessage.getMessage(resourceBundle,"validate.ped.xidTooLong"));
            return true;
        }
        return false;
    }
    public static void validateXid(DwrResponseI18n response, String xid) {
        if (StringUtils.isEmpty(xid))
            response.addContextualMessage("xid", "validate.required");
        else if (StringUtils.isLengthGreaterThan(xid, 50))
            response.addMessage("xid", new LocalizableMessage("validate.notLongerThan", 50));
    }

}
