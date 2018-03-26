package org.scada_lts.serorepl.utils;

import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtils {

    public static final Random RANDOM = new Random();

    public static String capitalize(String s){
        // nic się nie da zmienić
        return s == null ? null : s.toUpperCase().replace(' ', '_');
    }

    public static String escapeLT(String s){
        // nic się nie da zmienic
        return s == null ? null : s.replaceAll("<", "&lt;");
    }

    public static String generateRandomString(int length, String charSet){
        // nic się nie da zmienic

        StringBuffer randomString = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            randomString.append(charSet.charAt(RANDOM.nextInt(charSet.length())));
        }

        return randomString.toString();
    }

    public static boolean globWhiteListMatchIgnoreCase(String[] values, String value){
        return true;
    }

    public static boolean isBetweenInc(int value, int min, int max){
        return min <= value && value <= max;
    }

    public static boolean isEmpty(int[] value){
        // nie da się za wiele zmienic
        return value == null || value.length == 0;
    }

    public static boolean isEmpty(String s){
        // nie da się za wiele zmienic
        return s == null || s.trim().length() == 0;
    }


    public static boolean isEqual(Object o1, Object o2){
   //     stara implementacja

//        if (o1 == null && o2 == null) {
//            return true;
//        } else if (o1 == null) {
//            return false;
//        } else {
//            return o2 == null ? false : o1.equals(o2);
//        }
    // nowa implementacja

        if( o1 != null) return o1.equals(o2);
        else return false;

    }

    public static boolean isLengthGreaterThan(String value, int max){
        if (value != null){
            return value.length() > max;
        }
        return false;
    }

    public static String pad(String s, char padChar, int len){
       // gdy dlugosc s jest mniejsza niz len, dokleja przed stringa tyle liter padChar aby dlugosc s+(n*padchar) = len

        if(s.length() >= len){
            return s;
        }
        else{
            String padCharRepated = IntStream.range(0, len-s.length()).mapToObj(i -> s).collect(Collectors.joining(""));
            StringBuffer buffer = new StringBuffer();
            return buffer.append(padCharRepated).append(s).toString();
        }
    }

    public static int parseInt(String s, int defaultValue){
        // nie da się za wiele zmienic
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException var3) {
            return defaultValue;
        }
    }

    public static String replaceMacro(String s, String name, String replacement){
        return "";
    }

    public static String replaceMacros(String s, Properties properties){
        return "";
    }

    public static String trimWhitespace(String s){   // com seratonin mangi web dwr
        return "";
    }

    public static String truncate(String s, int length){
        return "";
    }

    public static String truncate(String s, int length, String truncateSuffix){
        return "";
    }


    /*                           METODY NIEZYWANE
    public static boolean isEqualIgnoreCase(String s1, String s2){  // nie ma użyć
        return true;
    }
    public static boolean isEmpty(Collection<?> value){
        return true;
    }
    public static boolean isLengthBetween(String value, int min, int max){
        return true;
    }

    public static boolean isLengthLessThan(String value, int min){
        return true;
    }

    public static boolean containsUppercase(String s){
        return true;
    }

    public static boolean containsLowercase(String s){
        return true;
    }
    public static boolean containsDigit(String s){
        return true;
    }

    public static String mask(String s, char maskChar, int unmaskedLength){
        return "";
    }

    public static String generatePassword(){
        int passwordLength = ThreadLocalRandom.current().nextInt(7, 12 + 1);
        byte[] array = new byte[passwordLength];

        StringBuffer password = new StringBuffer();
        String PASSWORD_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";


        for(int i = 0; i<passwordLength; i++){
            password.append(PASSWORD_CHARSET.charAt(RANDOM.nextInt(PASSWORD_CHARSET.length())));
        }
        return password.toString();

    }

    // niepotrzene, funkcja bez argumentu robi wyszstko co potrzeba

    public static String generatePassword(int length){
        return "";
    }

    public static boolean isOneOf(int value, int[] validValues){
        return true;
    }

    public static boolean isOneOf(String value, String[] validValues){
        return true;
    }

    public static String replaceMacro(String s, String name, String content, String replacement){
        return "";
    }

    public static String getMacroContent(String s, String name) {
        return "";
    }

    public static String findGroup(Pattern pattern, String s){
        return "";
    }

    public static String findGroup(Pattern pattern, String s, int group){
        return "";
    }

    public static String[] findAllGroup(Pattern pattern, String s){
        return new String[5];
    }

    public static String[] findAllGroup(Pattern pattern, String s, int group){
        return new String[5];
    }

    public static boolean in(String s, String... list){
        return true;
    }

    public static String durationToString(long duration){
        return "";
    }

    public static boolean startsWith(String haystack, String needle){
        return true;
    }

    public static int compareStrings(String s1, String s2, boolean ignoreCase){
        return 1;
    }
    */
}
