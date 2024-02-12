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
public class TruncateStringUtilsTest {

    @Parameterized.Parameters(name = "{index}: truncate(word: {0}, truncateSuffix: {1}, length: {2}, expectedWord: {3})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"ds - datapointnameDP", "...", 10, "ds - da..."},
                {"ds - datapointnameDP", "", 10, "ds - datap"},
                {"ds - datapointnameDP", null, 10, "ds - da..."},
                {"ds - datap", "12345", 7, "ds12345"},
                {"ds - data", " - datapoint", 14, "ds - datapoint"},
                {"ds - wrongname ", " - datapoint", 14, "ds - datapoint"}
        });
    }

    private final String word;
    private final String truncateSuffix;
    private final int length;
    private final String expectedWord;

    public TruncateStringUtilsTest(String word, String truncateSuffix, int length, String expectedWord) {
        this.word = word;
        this.truncateSuffix = truncateSuffix;
        this.length = length;
        this.expectedWord = expectedWord;
    }


    @Test
    public void truncate() {
        //when:
        String actualWord = StringUtils.truncate(word, truncateSuffix, length);

        //than:
        assertEquals(expectedWord, actualWord);
    }
}