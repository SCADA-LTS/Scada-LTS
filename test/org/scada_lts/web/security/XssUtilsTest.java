package org.scada_lts.web.security;

import org.junit.Test;
import static org.junit.Assert.*;

public class XssUtilsTest {

    @Test
    public void testValidate_NullValue() {
        assertTrue("Null value should be considered valid.", XssUtils.validate(null));
    }

    @Test
    public void testValidate_ValidString() {
        assertTrue("A valid string should be considered valid.", XssUtils.validate("ValidString123"));
        assertTrue("A valid string with spaces should be considered valid.", XssUtils.validate("Simple sentence with spaces"));
        assertTrue("A valid string with alphanumeric characters and allowed symbols should be considered valid.", XssUtils.validate("A1.b2 c3"));
    }

    @Test
    public void testValidate_ExceedsMaxLength() {
        String longString = new String(new char[1025]).replace("\0", "a");
        assertFalse("A string longer than 1024 characters should be considered invalid.", XssUtils.validate(longString));
    }

    @Test
    public void testValidate_ContainsForbiddenPatterns() {
        assertFalse("A string containing 'javascript:' should be considered invalid.", XssUtils.validate("This contains javascript: in the middle"));
        assertFalse("A string containing 'onerror=' should be considered invalid.", XssUtils.validate("This contains onerror= in the middle"));
        assertFalse("A string containing 'onload=' should be considered invalid.", XssUtils.validate("This contains onload= in the middle"));
        assertFalse("A string containing 'onmouseover=' should be considered invalid.", XssUtils.validate("This contains onmouseover= in the middle"));
    }

    @Test
    public void testValidate_InvalidCharacters() {
        assertFalse("A string containing '<' and '>' should be considered invalid.", XssUtils.validate("<script>alert('XSS')</script>"));
        assertFalse("A string containing '\"' should be considered invalid.", XssUtils.validate("Invalid\"Character"));
        assertFalse("A string containing ''' should be considered invalid.", XssUtils.validate("Invalid'Character"));
        assertFalse("A string containing '(' and ')' should be considered invalid.", XssUtils.validate("Invalid(Character)"));
    }

    @Test
    public void testValidate_ValidWithEdgeCases() {
        assertTrue("A single dot should be considered valid.", XssUtils.validate("."));
        String edgeCaseString = "A string with exactly 1024 characters" + new String(new char[1024 - 37]).replace("\0", "a");
        assertTrue("A string with exactly 1024 characters should be considered valid.", XssUtils.validate(edgeCaseString));
    }

    @Test
    public void testValidate_EmptyString() {
        assertTrue("An empty string should be considered valid.", XssUtils.validate(""));
    }
}
