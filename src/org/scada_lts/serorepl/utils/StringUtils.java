package org.scada_lts.serorepl.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtils {

    private static final Random RANDOM = new Random();

    private static final String LESS_THAN_SING = "<";
    private static final String LESS_THAN_EQUIVALENT = "&lt;";

    private static final char SPACE = ' ';
    private static final char UNDERLINE = '_';

    private static final int CHARACTER_NO_FOUND = -1;
    private static final String DOLLAR_OPEN_BRACKET = "${";
    private static final String CLOSE_BRACKET =  "}";
    private static int FIRST_GROUP = 1;


    public static String capitalize(String stringToCapitalize){
        if (stringToCapitalize != null){
            return stringToCapitalize.toUpperCase().replace(SPACE, UNDERLINE);
        }
        else return null;
    }

    public static String escapeLT(String s){
           return s.replaceAll(LESS_THAN_SING, LESS_THAN_EQUIVALENT);
    }

    public static String generateRandomString(int length, String charSet){
        StringBuffer randomString = new StringBuffer();

        for(int i = 0; i < length; ++i) {
            randomString.append(charSet.charAt(RANDOM.nextInt(charSet.length())));
        }

        return randomString.toString();
    }

    public static boolean globWhiteListMatchIgnoreCase(String[] values, String value){

        if (values == null || values.length == 0 || value == null) {
            return false;
        }
        int occurence = 0;
        for (String element : values){
            occurence = element.indexOf("*");
            if (occurence != CHARACTER_NO_FOUND){
                if (element.equalsIgnoreCase(value)){
                    return true;
                }else if (value.length() >= occurence && element.substring(0, occurence).equalsIgnoreCase(value.substring(0,occurence))){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBetweenInc(int value, int min, int max){
        return min <= value && value <= max;
    }

    public static boolean isEmpty(int[] value){
        return value == null || value.length == 0;
    }

    public static boolean isEmpty(String string){
        return string == null || string.trim().length() == 0;
    }

    public static boolean isEqual(Object object1, Object object2){
        if( object1 != null) return object1.equals(object2);
        else return false;
    }

    public static boolean isLengthGreaterThan(String value, int max){
        if (value != null){
            return value.length() > max;
        }
        return false;
    }
    /**
    * When s.lenght is less than given length, method append padChar (len - s.string)
     * times at the beginning of the String s
    */
    public static String pad(String s, char padChar, int len){
        if(s.length() >= len){
            return s;
        }
        else{
            String tmp = String.valueOf(padChar);
            String padCharRepated = IntStream.range(0, len-s.length()).mapToObj(i -> tmp).collect(Collectors.joining(""));
            StringBuffer buffer = new StringBuffer();
            return buffer.append(padCharRepated).append(s).toString();
        }
    }

    public static int parseInt(String string, int defaultValue){
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException var3) {
            return defaultValue;
        }
    }

    public static String replaceMacro(String string, String name, String replacement){
        return string.replaceAll(Pattern.quote(DOLLAR_OPEN_BRACKET + name + CLOSE_BRACKET), replacement);
    }

    public static String replaceMacros(String s, Properties properties){
        String regex = "\\$\\{(.*?)\\}";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(s);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group(FIRST_GROUP);
            matcher.appendReplacement(result, Matcher.quoteReplacement(ObjectUtils.toString(properties.get(group))));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static String trimWhitespace(String string){
        return string.trim();
    }

    public static String truncate(String string, int length){
        return truncate(string, length, (String)null);
    }

    public static String truncate(String string, int length, String truncateSuffix){
        if (string == null) {
            return null;
        }
        if (string.length() <= length) {
            return string;
        }
        if (truncateSuffix != null){
            return string.substring(0,length) + truncateSuffix;
        }
        return string.substring(0,length);
    }
}
