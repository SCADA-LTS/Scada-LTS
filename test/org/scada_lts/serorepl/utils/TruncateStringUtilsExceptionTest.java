package org.scada_lts.serorepl.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static br.org.scadabr.db.utils.TestUtils.*;

@RunWith(Parameterized.class)
public class TruncateStringUtilsExceptionTest {

    @Parameterized.Parameters(name = "{index}: truncate(word: {0}, suffix: {1}, length: {2})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {

                {null, null, 6},
                {null, "", 6},
                {null, ".", 6},
                {null, "....", 6},

                {STRING_LENGTH_7, null, 3},
                {STRING_LENGTH_8, null, 3},
                {STRING_LENGTH_9, null, 3},

                {STRING_LENGTH_7, null, 2},
                {STRING_LENGTH_8, null, 2},
                {STRING_LENGTH_9, null, 2},

                {STRING_LENGTH_7, null, 1},
                {STRING_LENGTH_8, null, 1},
                {STRING_LENGTH_9, null, 1},

                {STRING_LENGTH_7, null, 0},
                {STRING_LENGTH_8, null, 0},
                {STRING_LENGTH_9, null, 0},

                {STRING_LENGTH_7, "", 0},
                {STRING_LENGTH_8, "", 0},
                {STRING_LENGTH_9, "", 0},

                {STRING_LENGTH_7, ".", 0},
                {STRING_LENGTH_8, ".", 0},
                {STRING_LENGTH_9, ".", 0},

                {STRING_LENGTH_7, "..", 0},
                {STRING_LENGTH_8, "..", 0},
                {STRING_LENGTH_9, "..", 0},

                {STRING_LENGTH_7, null, -1},
                {STRING_LENGTH_8, null, -1},
                {STRING_LENGTH_9, null, -1},


                {STRING_LENGTH_9, "1", 1},
                {STRING_LENGTH_9, "12", 2},
                {STRING_LENGTH_9, "123", 3},

                {STRING_LENGTH_9, "1234", 4},
                {STRING_LENGTH_9, "12345", 5},
                {STRING_LENGTH_9, "123456", 6},

                {STRING_LENGTH_9, "12345", 5},
                {STRING_LENGTH_9, "123456", 6},
                {STRING_LENGTH_9, "1234567", 7},


                {STRING_LENGTH_9, "1", 0},
                {STRING_LENGTH_9, "12", 1},
                {STRING_LENGTH_9, "123", 2},

                {STRING_LENGTH_9, "1234", 3},
                {STRING_LENGTH_9, "12345", 4},
                {STRING_LENGTH_9, "123456", 5},

                {STRING_LENGTH_9, "123456", 6},
                {STRING_LENGTH_9, "1234567", 7},
                {STRING_LENGTH_9, "12345678", 8},


                {STRING_LENGTH_9, "1", 0},
                {STRING_LENGTH_9, "12", 0},
                {STRING_LENGTH_9, "123", 1},

                {STRING_LENGTH_9, "1234", 2},
                {STRING_LENGTH_9, "12345", 3},
                {STRING_LENGTH_9, "123456", 4},

                {STRING_LENGTH_9, "12345", 5},
                {STRING_LENGTH_9, "123456", 6},
                {STRING_LENGTH_9, "1234567", 7},

        });
    }

    private final String word;
    private final String suffix;
    private final int length;

    public TruncateStringUtilsExceptionTest(String word, String suffix, int length) {
        this.word = word;
        this.suffix = suffix;
        this.length = length;
    }

    @Test(expected = IllegalArgumentException.class)
    public void when_truncateExpectException() {
        StringUtils.truncate(word, suffix, length);
    }
}