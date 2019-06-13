package org.scada_lts.serorepl.utils;

public class ObjectUtil {

    public ObjectUtil() {
    }
    public static boolean isEqual(Object o1, Object o2) {
        return o1 == o2 || o1 != null && o2 != null && o1.equals(o2);
    }
}
