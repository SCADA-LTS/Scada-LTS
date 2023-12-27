package org.scada_lts.utils;

import java.util.Random;

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
}
