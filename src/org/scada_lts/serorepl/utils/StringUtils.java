package org.scada_lts.serorepl.utils;

import br.org.scadabr.api.da.WriteDataOptions;
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


    public static String capitalize(String s){
        if (s != null){
            return s.toUpperCase().replace(SPACE, UNDERLINE);
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

    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }

    public static boolean isEqual(Object o1, Object o2){
        if( o1 != null) return o1.equals(o2);
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

    public static int parseInt(String s, int defaultValue){
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException var3) {
            return defaultValue;
        }
    }

    public static String replaceMacro(String s, String name, String replacement){
        return s.replaceAll(Pattern.quote(DOLLAR_OPEN_BRACKET + name + CLOSE_BRACKET), replacement);
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

    public static String trimWhitespace(String s){
        return s.trim();
    }
	@Deprecated
	public static String truncate(String s, int length){
		return truncate(s, length, (String)null);
	}
	@Deprecated
	public static String truncate(String s, int length, String truncateSuffix){
		if (s == null) {
			return null;
		}
		if (s.length() <= length) {
			return s;
		}
		if (truncateSuffix != null){
			return s.substring(0,length) + truncateSuffix;
		}
		return s.substring(0,length);
	}
    public static String abbreviateMiddle(String s, int length) {
        return abbreviateMiddle(s, length, (String)null);
    }

    public static String abbreviateMiddle(String s, int length, String truncateSuffix) {
        if(truncateSuffix == null) {
            truncateSuffix = "...";
        }
        return org.apache.commons.lang3.StringUtils.abbreviateMiddle(s, truncateSuffix, length);
    }

}
