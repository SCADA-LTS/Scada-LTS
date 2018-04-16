package org.scada_lts.serorepl.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IpAdresUtils{

    private static final String INVALID_IP_ADDRESS = "Invalid IP address: ";
    private static final String INVALID_REMOTE_ADDRESS = "Invalid remote address ";
    private static final String MUST_HAVE_4_PARTS = "IP address must have 4 parts";
    private static final String INCORRECT_VALUE_IN_IP = "Incorrect value in IP address";
    private static final String TO_VALUE_OUT_OF_RANGE = "'To' value out of range in '";
    private static final String FROM_VALUE_OUT_OF_RANGE = "'From' value out of range in '";
    private static final String FROM_IS_GREATER_THAN_TO = "'From' value is greater than 'To' value in '";
    private static final String INT_PARSE_ERROR = "Integer parsing error";
    private static final String DOT_SPLIT = "\\.";

    public IpAdresUtils(){}

    public static boolean ipWhiteListCheck(String[] allowedIps, String remoteIp) throws IpWhiteListException {

        String [] remoteIpAsAprts = remoteIp.split(DOT_SPLIT);
        if (remoteIpAsAprts.length != 4)throw new IpWhiteListException(INVALID_IP_ADDRESS+ remoteIp);
        else{
            for (String allowedAddress: allowedIps) {
                if (ipWhiteListCheckImpl(allowedAddress, remoteIp, remoteIpAsAprts)) {
                    return true;
                }
            }
        }
        return  false;
    }

    public static boolean ipWhiteListCheckImpl(String allowedIp, String remoteIp, String[] remoteIpAsAprts) throws IpWhiteListException {
        String[] allowedAsParts = allowedIp.split(DOT_SPLIT);
        if (allowedAsParts.length != 4){
            throw new IpWhiteListException(INVALID_REMOTE_ADDRESS + remoteIp);
        }else
        {
            return  validate(allowedAsParts[0], remoteIpAsAprts[0], allowedIp, remoteIp) &&
                    validate(allowedAsParts[1], remoteIpAsAprts[1], allowedIp, remoteIp) &&
                    validate(allowedAsParts[2], remoteIpAsAprts[2], allowedIp, remoteIp) &&
                    validate(allowedAsParts[3], remoteIpAsAprts[3], allowedIp, remoteIp);
        }

    }

    private static boolean validate(String allowed, String remote, String allowedIp, String remoteIp){
        Predicate<String> isAsterisk = s -> s.equals("*");
        if (Stream.of(allowed).anyMatch(isAsterisk)){
            return true;
        }else{
            int separator = allowed.indexOf("-");

            if(separator == -1){
                return Integer.parseInt(allowed) == Integer.parseInt(remote);
            }else{
                int from = Integer.parseInt(allowed.substring(0, separator));
                int to = Integer.parseInt(allowed.substring(separator + 1));
                int rem = Integer.parseInt(remote);
                return from <= rem && rem <= to;
            }
        }
    }

    public static String checkIpMask(String ip){

        String[] parts = ip.split(DOT_SPLIT);

        if (parts.length != 4){return MUST_HAVE_4_PARTS;}
        Predicate<String> isIncorrectRange = s -> Integer.parseInt(s) < 0 || Integer.parseInt(s) > 255;
        Predicate<Integer> isIncorrectRangeInt = s -> s < 0 || s > 255;
        Predicate<String> isAsterisk = s -> s.equals("*");
        if(Stream.of(parts).anyMatch(isAsterisk)){
            return null;
        }


        if (parts[3].contains("-")){
            if (Stream.of(parts[0], parts[1], parts[2]).anyMatch(isIncorrectRange)){ // if any part is outside [0,255] interval
                return INCORRECT_VALUE_IN_IP;
            }
            int dash = parts[3].indexOf('-');
            int from = Integer.parseInt(parts[3].substring(0, dash));
            int to = Integer.parseInt(parts[3].substring(dash + 1));
            if (Stream.of(to).allMatch(isIncorrectRangeInt)){
                return TO_VALUE_OUT_OF_RANGE + parts[3] + "'";

            }if (Stream.of(from).allMatch(isIncorrectRangeInt)){
                return FROM_VALUE_OUT_OF_RANGE + parts[3] + "'";
            }
            if (from > to) {
                return FROM_IS_GREATER_THAN_TO + parts[3] + "'";
            }
        }
        else {
            if (Stream.of(parts).anyMatch(isIncorrectRange)){ // if any part is outside [0,255] interval
                return INCORRECT_VALUE_IN_IP;
            }else{
                return null;  // when all ok
            }
        }
        return null;
    }

    public static byte[] toIpAddress(String address) throws IllegalArgumentException{

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(address);
            byte[] bytes = ip.getAddress();
            return bytes;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(INT_PARSE_ERROR);

        }

     //return  null;
    }


}
