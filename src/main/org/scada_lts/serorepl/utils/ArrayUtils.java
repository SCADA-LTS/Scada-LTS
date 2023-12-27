package org.scada_lts.serorepl.utils;

import java.util.Arrays;
import java.util.stream.IntStream;

public class ArrayUtils {

    private static final String EMPTY_ARRAY = "[]";
    private static final String OPEN_ARRAY_BRACKET = "[";
    private static final String CLOSE_ARRAY_BRACKET= "]";
    private static final char COLON = ',';

    public ArrayUtils() {
    }

    public static String toHexString(byte[] bytes){
        if (bytes.length == 0)
            return EMPTY_ARRAY;
        else{
            StringBuilder hexString = new StringBuilder();
            hexString.append(OPEN_ARRAY_BRACKET).append(Integer.toHexString(bytes[0] & 255));

            for (int i = 1; i < bytes.length; i++) {
                hexString.append(COLON);
                hexString.append(Integer.toHexString(bytes[i] & 255));
            }
            hexString.append(CLOSE_ARRAY_BRACKET);
            return hexString.toString();
        }
    }

    public static boolean contains(int[] values, int value){
        return IntStream.of(values).anyMatch(i -> i == value);
    }

    public static boolean contains(String[] values, String value){
        if (values != null)
            return Arrays.asList(values).contains(value);
        else
            return false;
    }

    public static int indexOf(String[] values, String value) {
        return Arrays.asList(values).indexOf(value);
    }
}
