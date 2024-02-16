package org.scada_lts.serorepl.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static br.org.scadabr.db.utils.TestUtils.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TruncateStringUtilsTest {

    @Parameterized.Parameters(name = "{index}: truncate(word: {0}, truncateSuffix: {1}, length: {2}, expectedWord: {3})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {

                {STRING_LENGTH_13, null, 10, "ds_1 - ...", 10},
                {STRING_LENGTH_13, "", 10, "ds_1 - 13c", 10},
                {STRING_LENGTH_13, ".", 10, "ds_1 - 13.", 10},
                {STRING_LENGTH_13, "..", 10, "ds_1 - 2..", 10},

                {STRING_LENGTH_12, null, 10, "ds_1 - ...", 10},
                {STRING_LENGTH_12, "", 10, "ds_1 - 12c", 10},
                {STRING_LENGTH_12, ".", 10, "ds_1 - 12.", 10},
                {STRING_LENGTH_12, "..", 10, "ds_1 - 1..", 10},

                {STRING_LENGTH_11, null, 10, "ds_1 - ...", 10},
                {STRING_LENGTH_11, "", 10, "ds_1 - 11c", 10},
                {STRING_LENGTH_11, ".", 10, "ds_1 - 11.", 10},
                {STRING_LENGTH_11, "..", 10, "ds_1 - 1..", 10},

                {STRING_LENGTH_10, null, 10, "ds_1 - 10c", 10},
                {STRING_LENGTH_10, "", 10, "ds_1 - 10c", 10},
                {STRING_LENGTH_10, ".", 10, "ds_1 - 10c", 10},
                {STRING_LENGTH_10, "..", 10, "ds_1 - 10c", 10},

                {STRING_LENGTH_9, null, 10, "ds_1 - 9c", 9},
                {STRING_LENGTH_9, "", 10, "ds_1 - 9c", 9},
                {STRING_LENGTH_9, ".", 10, "ds_1 - 9c", 9},
                {STRING_LENGTH_9, "..", 10, "ds_1 - 9c", 9},

                {STRING_LENGTH_8, null, 10, "ds_1 - 8", 8},
                {STRING_LENGTH_8, "", 10, "ds_1 - 8", 8},
                {STRING_LENGTH_8, ".", 10, "ds_1 - 8", 8},
                {STRING_LENGTH_8, "..", 10, "ds_1 - 8", 8},


                {STRING_LENGTH_14, null, 11, "ds_1 - 1...", 11},
                {STRING_LENGTH_14, "", 11, "ds_1 - 14ch", 11},
                {STRING_LENGTH_14, ".", 11, "ds_1 - 14c.", 11},
                {STRING_LENGTH_14, "..", 11, "ds_1 - 14..", 11},

                {STRING_LENGTH_13, null, 11, "ds_1 - 1...", 11},
                {STRING_LENGTH_13, "", 11, "ds_1 - 13ch", 11},
                {STRING_LENGTH_13, ".", 11, "ds_1 - 13c.", 11},
                {STRING_LENGTH_13, "..", 11, "ds_1 - 13..", 11},

                {STRING_LENGTH_12, null, 11, "ds_1 - 1...", 11},
                {STRING_LENGTH_12, "", 11, "ds_1 - 12ch", 11},
                {STRING_LENGTH_12, ".", 11, "ds_1 - 12c.", 11},
                {STRING_LENGTH_12, "..", 11, "ds_1 - 12..", 11},


                {STRING_LENGTH_10, null, 11, "ds_1 - 10c", 10},
                {STRING_LENGTH_10, "", 11, "ds_1 - 10c", 10},
                {STRING_LENGTH_10, ".", 11, "ds_1 - 10c", 10},
                {STRING_LENGTH_10, "..", 11, "ds_1 - 10c", 10},

                {STRING_LENGTH_9, null, 11, "ds_1 - 9c", 9},
                {STRING_LENGTH_9, "", 11, "ds_1 - 9c", 9},
                {STRING_LENGTH_9, ".", 11, "ds_1 - 9c", 9},
                {STRING_LENGTH_9, "..", 11, "ds_1 - 9c", 9},

                {STRING_LENGTH_8, null, 11, "ds_1 - 8", 8},
                {STRING_LENGTH_8, "", 11, "ds_1 - 8", 8},
                {STRING_LENGTH_8, ".", 11, "ds_1 - 8", 8},
                {STRING_LENGTH_8, "..", 11, "ds_1 - 8", 8},


                {STRING_LENGTH_10, null, 4, "d...", 4},
                {STRING_LENGTH_10, "", 1, "d", 1},
                {STRING_LENGTH_10, ".", 2, "d.", 2},
                {STRING_LENGTH_10, "..", 3, "d..", 3},

                {STRING_LENGTH_9, null, 4, "d...", 4},
                {STRING_LENGTH_9, "", 1, "d", 1},
                {STRING_LENGTH_9, ".", 2, "d.", 2},
                {STRING_LENGTH_9, "..", 3, "d..", 3},

                {STRING_LENGTH_8, null, 4, "d...", 4},
                {STRING_LENGTH_8, "", 1, "d", 1},
                {STRING_LENGTH_8, ".", 2, "d.", 2},
                {STRING_LENGTH_8, "..", 3, "d..", 3},

        });
    }

    private final String word;
    private final String suffix;
    private final int length;
    private final String expectedWord;
    private final int expectedLength;

    public TruncateStringUtilsTest(String word, String suffix, int length, String expectedWord, int expectedLength) {
        this.word = word;
        this.suffix = suffix;
        this.length = length;
        this.expectedWord = expectedWord;
        this.expectedLength = expectedLength;
    }


    @Test
    public void when_truncate_then_word() {
        //when:
        String actualWord = StringUtils.truncate(word, suffix, length);

        //than:
        assertEquals(expectedWord, actualWord);
    }

    @Test
    public void when_truncate_then_length() {
        //when:
        String actualWord = StringUtils.truncate(word, suffix, length);

        //than:
        assertEquals(expectedLength, actualWord.length());
    }
}