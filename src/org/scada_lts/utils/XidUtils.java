package org.scada_lts.utils;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

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
			errors.put("eventDetector" + ped.getId() + "ErrorMessage", LocalizableMessage.getMessage(resourceBundle, "validate.ped.xidMissing"));
			return true;
		}
		if (ped.getXid().length() > 50) {
			errors.put("eventDetector" + ped.getId() + "ErrorMessage", LocalizableMessage.getMessage(resourceBundle, "validate.ped.xidTooLong"));
			return true;
		}
		return false;
	}
	public static void validateXid(Map<String, String> errors, String xid, ResourceBundle resourceBundle) {
		validateXid(errors::put,
				LocalizableMessage.getMessage(resourceBundle,"validate.required"),
				LocalizableMessage.getMessage(resourceBundle,"validate.notLongerThan", 50),
				null,
				(a,b) -> true, xid, -1);
	}
	public static void validateXid(DwrResponseI18n response, BiPredicate<String, Integer> isUnique, String xid, int id) {
		validateXid(response::addMessage,
				new LocalizableMessage("validate.required"),
				new LocalizableMessage("validate.notLongerThan", 50),
				new LocalizableMessage("validate.xidUsed"),
				isUnique, xid, id);
	}

    private static <T> void validateXid(BiConsumer<String, T> ifNotValidate, T ifEmpty, T ifLengthGreater, T ifUsed,
                                        BiPredicate<String, Integer> isUnique, String xid, int id) {
        if (StringUtils.isEmpty(xid))
            ifNotValidate.accept("xid", ifEmpty);
        else if (StringUtils.isLengthGreaterThan(xid, 50))
            ifNotValidate.accept("xid", ifLengthGreater);
        else if (isUnique.negate().test(xid, id))
            ifNotValidate.accept("xid", ifUsed);
    }
}
