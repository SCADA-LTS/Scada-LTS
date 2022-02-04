package org.scada_lts.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class UrlValidatorUtilsTest {

    @Parameterized.Parameters(name = "[{index}] valid ''{0}'' is {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {

                { "http://abc.com:9000", true},
                { "http://localhost:8080", true},
                { "http://machine:8080", true},
                { "http://127.0.0.1:8080", true},
                { "http://127.0.0.1:8080/path1/path2", true},
                { "https://127.0.0.1:8080", true},
                { "ftp://127.0.0.1:8080", true},
                { "http://127.255.255.1:8080", true},
                { "http://255.255.255.255:8080", true},
                { "http://127.0.0.1:9999", true},
                { "http://127.0.0.1:80", true},
                { "http://127.0.0.1:2", true},
                { "http://127.0.0.1:0", true},
                { "http://255.255.255.0:8080", true},
                { "http://255.255.0.255:8080", true},
                { "http://255.0.255.255:8080", true},
                { "http://0.255.255.255:8080", true},
                { "http://0.0.0.0:8080", true},
                { "http://127.0.0.1:65535", true},
                { "http://abc.eu", true},
                { "http://abc.pl", true},
                { "http://abc.com:", true},
                { "http://abc.abc", true},
                { "http://127.0.0.1:8080/path1?param1=123&param2=abc", true},
                { "http://127.0.0.1:8080/path1?param1=123&param2abc", true},
                { "http://127.0.0.1:8080/path1param1=123&param2=abc", true},
                { "http://127.0.0.1:8080/path1?param1=123param2=abc", true},
                { "http://www.abc.com", true},
                { "http://abc.abc.abc", true},
                { "http://abc1.abc", true},

                { "http://abc.abc1", false},
                { "http://abc.ab1c", false},
                { "http://abc.1abc", false},
                { "www.abc.com", false},
                { "ftps://127.0.0.1:8080", false},
                { "ws://127.0.0.1:8080", false},
                { "wss://127.0.0.1:8080", false},
                { "ssh://127.0.0.1:8080", false},
                { "sftp://127.0.0.1:8080", false},
                { "", false},
                { "abc", false},
                { "/abc", false},
                { "123", false},
                { "://127.0.0.1:8080", false},
                { "127.0.0.1:8080", false},
                { "127.0.0.1", false},
                { "http://:8080", false},
                { "http:/127.0.0.1:8080", false},
                { "http:127.0.0.1:8080", false},
                { "http://127.0.0.1:65536", false},
                { "htttp://127.0.0.1:8080", false},
                { "httpw://127.0.0.1:8080", false},
                { "abc://127.0.0.1:8080", false},
                { "http://127.0.0.:8080", false},
                { "http://127.0..1:8080", false},
                { "http://127..0.1:8080", false},
                { "http://.0.0.1:8080", false},
                { "http://255.255.255.256:8080", false},
                { "http://255.255.256.255:8080", false},
                { "http://255.256.255.255:8080", false},
                { "http://256.255.255.255:8080", false},
                { "http://255.255.255.-1:8080", false},
                { "http://255.255.-1.255:8080", false},
                { "http://255.-1.255.255:8080", false},
                { "http://-1.255.255.255:8080", false},
                { "http://255.255.255.00:8080", false},
                { "http://255.255.00.255:8080", false},
                { "http://255.00.255.255:8080", false},
                { "http://00.255.255.255:8080", false},
                { "http://127.0.0.1:abc", false},
                { "http://abc.com:abc", false},
                { null, false},
        });
    }


    public final String address;
    public final boolean valid;

    public UrlValidatorUtilsTest(String address, boolean valid) {
        this.address = address;
        this.valid = valid;
    }

    @Test
    public void when_isValid_then() {
        Assert.assertEquals(valid, UrlValidatorUtils.isValid(address));
    }
}