package org.scada_lts.serorepl.utils;

import com.serotonin.util.IpAddressUtils;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class IpAdresUtilsTest {
    @Test
    public void ipWhiteListCheck() throws Exception {
        String correctIp1 = "154.198.63.74";
        String correctIp2 = "121.0.0.1";
        String incorrectIp = "121.88.0.1";
        String[] allowedIps = new String[]{
                "192.164.16.1",
                correctIp1,
                "158.694.6.6",
                "173.69.11.4-16",
                "145.99.8.1-20",
                correctIp2
        };
        assertEquals(IpAddressUtils.ipWhiteListCheck(allowedIps, correctIp1) , IpAdresUtils.ipWhiteListCheck(allowedIps, correctIp1));
        assertEquals(IpAddressUtils.ipWhiteListCheck(allowedIps, correctIp2) , IpAdresUtils.ipWhiteListCheck(allowedIps, correctIp2));

        assertEquals(IpAddressUtils.ipWhiteListCheck(allowedIps, incorrectIp) , IpAdresUtils.ipWhiteListCheck(allowedIps, incorrectIp));
    }

    @Test
    public void checkIpMask() throws Exception {
        String ip1 = "0.255.255.0";
        String ip2 = "121.0.0.1";
        String ip3 = "192.16.10.5-16";
        String ip4 = "300.16.10.5";
        String ip5 = "102.250.11.5-16";


//        System.out.println(IpAddressUtils.checkIpMask(ip1));
//        System.out.println(IpAddressUtils.checkIpMask(ip2));
//        System.out.println(IpAddressUtils.checkIpMask(ip3));
//        System.out.println(IpAddressUtils.checkIpMask(ip4));
//        System.out.println(IpAddressUtils.checkIpMask(ip5));


        assertEquals(IpAddressUtils.checkIpMask(ip1) , IpAdresUtils.checkIpMask(ip1));
        assertEquals(IpAddressUtils.checkIpMask(ip2) , IpAdresUtils.checkIpMask(ip2));
        assertEquals(IpAddressUtils.checkIpMask(ip3) , IpAdresUtils.checkIpMask(ip3));
        assertEquals(IpAddressUtils.checkIpMask(ip5) , IpAdresUtils.checkIpMask(ip5));
        //   assertEquals(IpAddressUtils.checkIpMask(ip4) , IpAdresUtils.checkIpMask(ip4));

        assertEquals(IpAdresUtils.checkIpMask(ip4),"Incorrect value in IP address" );
    }

    @Test
    public void toIpAddress() throws Exception {

        String ip1 = "0.255.255.0";
        String ip2 = "121.0.0.1";
        String ip3 = "192.16.10.5";

        //IpAdresUtils.toIpAddress(ip1);
        System.out.println("Ip1 serp "+Arrays.toString(IpAddressUtils.toIpAddress(ip1)));
        System.out.println("Ip2 serp "+Arrays.toString(IpAddressUtils.toIpAddress(ip2)));
        System.out.println("Ip3 serp "+Arrays.toString(IpAddressUtils.toIpAddress(ip3)));
        System.out.println("");

        System.out.println("Ip1 scad "+Arrays.toString(IpAdresUtils.toIpAddress(ip1)));
        System.out.println("Ip2 scad "+Arrays.toString(IpAdresUtils.toIpAddress(ip2)));
        System.out.println("Ip3 scad "+Arrays.toString(IpAdresUtils.toIpAddress(ip3)));

        assertEquals(Arrays.toString(IpAdresUtils.toIpAddress(ip1)), Arrays.toString(IpAddressUtils.toIpAddress(ip1)));
        assertEquals(Arrays.toString(IpAdresUtils.toIpAddress(ip2)), Arrays.toString(IpAddressUtils.toIpAddress(ip2)));
        assertEquals(Arrays.toString(IpAdresUtils.toIpAddress(ip3)), Arrays.toString(IpAddressUtils.toIpAddress(ip3)));

    }

    @Test(expected = IllegalArgumentException.class)
    public void toIpAddressShouldThrowWhenInvalidArgumentGiven() throws Exception{
        String ip4 = "192.16.10.5-16";

        System.out.println("Ip4 scad "+Arrays.toString(IpAdresUtils.toIpAddress(ip4)));
    }

}