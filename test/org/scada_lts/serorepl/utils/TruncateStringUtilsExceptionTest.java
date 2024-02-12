package org.scada_lts.serorepl.utils;

import com.serotonin.mango.vo.report.ReportChartCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TruncateStringUtilsExceptionTest {

    @Parameterized.Parameters(name = "{index}: truncate(word: {0}, truncateSuffix: {1}, length: {2})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"ds - datap", "longsuffix", 6},
                {"ds - dp", "suffix", 6},
                {"2c", ".", 1},
                {"ds - datapointnameplaceholder1", "longsuffix", 9},
                {"ds-dp", ".", 3},
                {"ds-dp", null, 3}
        });
    }

    private final String word;
    private final String truncateSuffix;
    private final int length;

    public TruncateStringUtilsExceptionTest(String word, String truncateSuffix, int length) {
        this.word = word;
        this.truncateSuffix = truncateSuffix;
        this.length = length;
    }


    @Test(expected = IllegalArgumentException.class)
    public void when_truncateExpectException() {
        StringUtils.truncate(word, truncateSuffix, length);
    }
}