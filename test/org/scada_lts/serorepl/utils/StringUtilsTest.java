package org.scada_lts.serorepl.utils;

import com.serotonin.util.StringUtils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {
    @Test
    public void isEqual() throws Exception {
        assertTrue(StringUtils.isEqual(null,null));
        assertFalse(StringUtils.isEqual(null,new Object()));
        assertFalse(StringUtils.isEqual(new Object(),null));
        Object object = new Object();
        assertTrue(StringUtils.isEqual(object,object));
        assertFalse(StringUtils.isEqual(object,21));
        assertFalse(StringUtils.isEqual("String",object));
    }

    @Test
    public void pad() throws Exception {
        //StringUtils.pad("RANDOM", 'X', 7);
        //org.scada_lts.serorepl.utils.StringUtils.pad("RANDOM", 'X', 7);

    //    assertEquals(StringUtils.pad("RANDOM", 'X', 7)  ,
      //          org.scada_lts.serorepl.utils.StringUtils.pad("RANDOM", 'X', 7));

    }
/*
    @Test
    public void isEqualIgnoreCase() throws Exception {
    }

    @Test
    public void isEmpty() throws Exception {
    }

    @Test
    public void trimWhitespace() throws Exception {
    }

    @Test
    public void isEmpty1() throws Exception {
    }

    @Test
    public void isEmpty2() throws Exception {
    }

    @Test
    public void isLengthBetween() throws Exception {
    }

    @Test
    public void isLengthLessThan() throws Exception {
    }

    @Test
    public void isLengthGreaterThan() throws Exception {
    }

    @Test
    public void containsUppercase() throws Exception {
    }

    @Test
    public void containsLowercase() throws Exception {
    }

    @Test
    public void containsDigit() throws Exception {
    }

    @Test
    public void isBetweenInc() throws Exception {
    }

    @Test
    public void mask() throws Exception {
    }

    @Test
    public void generatePassword() throws Exception {
    }

    @Test
    public void generatePassword1() throws Exception {
    }

    @Test
    public void generateRandomString() throws Exception {
    }

    @Test
    public void isOneOf() throws Exception {
    }

    @Test
    public void isOneOf1() throws Exception {
    }

    @Test
    public void escapeLT() throws Exception {
    }

    @Test
    public void globWhiteListMatchIgnoreCase() throws Exception {
    }

    @Test
    public void replaceMacros() throws Exception {
    }

    @Test
    public void replaceMacro() throws Exception {
    }

    @Test
    public void replaceMacro1() throws Exception {
    }

    @Test
    public void getMacroContent() throws Exception {
    }

    @Test
    public void truncate() throws Exception {
    }

    @Test
    public void truncate1() throws Exception {
    }

    @Test
    public void findGroup() throws Exception {
    }

    @Test
    public void findGroup1() throws Exception {
    }

    @Test
    public void findAllGroup() throws Exception {
    }

    @Test
    public void findAllGroup1() throws Exception {
    }

    @Test
    public void in() throws Exception {
    }

    @Test
    public void parseInt() throws Exception {
    }

    @Test
    public void durationToString() throws Exception {
    }

    @Test
    public void capitalize() throws Exception {
    }

    @Test
    public void startsWith() throws Exception {
    }

    @Test
    public void compareStrings() throws Exception {
    }
    */
}