package org.scada_lts.utils.security;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class SafeZipFileUtilsTest {

    @Parameterized.Parameters(name = "{index}: path: {0}, valid: {1}")
    public static Object[][] data() {
        return new Object[][] {
                new Object[] {"./test/path/", false},
                new Object[] {"./test/path", false},
                new Object[] {"./test/path/abc123", false},
                new Object[] {"./test/path/abc.tmp", false},
                new Object[] {"./test/abc.tmp", false},
                new Object[] {"../path/abc.tmp", false},
                new Object[] {"/../path/abc.tmp", false},
                new Object[] {"abc.tmp", true},
                new Object[] {"abc.", false},
                new Object[] {"abc", false},
                new Object[] {"test/org/scada_lts/utils/security/path/abc.tmp", true},
                new Object[] {"./test/org/scada_lts/utils/security/path/abc.tmp", false},
                new Object[] {"../test/org/scada_lts/utils/security/path/abc.tmp", false},
                new Object[] {"/../test/org/scada_lts/utils/security/path/abc.tmp", false},
        };
    }

    private final String path;
    private final boolean valid;

    public SafeZipFileUtilsTest(String path, boolean valid) {
        this.path = path;
        this.valid = valid;
    }

    @Test
    public void valid() {
        //when:
        boolean result = SafeZipFileUtils.valid(path);

        //then:
        Assert.assertEquals(valid, result);
    }
}