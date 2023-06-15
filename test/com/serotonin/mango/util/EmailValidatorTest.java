package com.serotonin.mango.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class EmailValidatorTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                new Object[] {"email@com.com", true},
                new Object[] {"email@com.co", true},
                new Object[] {"email@com.c", true},
                new Object[] {"email@com.", false},
                new Object[] {"email@co.com", true},
                new Object[] {"email@c.com", true},
                new Object[] {"email@.com", false},
                new Object[] {"emailcom.com", false},
                new Object[] {"email@@.cm", false},
                new Object[] {"e@mail@com.com", false},
                new Object[] {"@email@com.com", false},
                new Object[] {"emai@l@com.com", false},
                new Object[] {"email@com.c@om", false},
                new Object[] {"email@com.@com", false},
                new Object[] {"email@com@.com", false},
                new Object[] {"email@com.com@", false},
                new Object[] {"email@dewa.c", true},
                new Object[] {"email@@@a.om", false},
                new Object[] {"email@ac.cm", true},
                new Object[] {"ertyu@ac.cm", true},
                new Object[] {"e@ac.cm", true},
                new Object[] {"@ac.cm", false},
                new Object[] {"ac.cm", false},
                new Object[] {"ac.@cm", false},
        };
    }

    private final String email;
    private final boolean isvalid;

    public EmailValidatorTest(String email, boolean isvalid) {
        this.email = email;
        this.isvalid = isvalid;
    }

    @Test
    public void when_isValidEmail() {
        //given:
        //when:
        boolean result = EmailValidator.isValidEmail(email);
        //then:
        Assert.assertEquals(isvalid, result);
    }
}