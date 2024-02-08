package org.scada_lts.dao.impl;

public class CharTo {

    /**
     * Method utility needed because of mango compatibility.
     * @param s
     * @return
     */
    public static boolean charToBool(String s) {
        return "Y".equals(s);
    }

    /**
     *
     * @param b
     * @return
     */
    public static String boolToChar(boolean b) {
        return b ? "Y" : "N";
    }


}
