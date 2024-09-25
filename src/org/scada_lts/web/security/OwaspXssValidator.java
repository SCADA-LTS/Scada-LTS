package org.scada_lts.web.security;

import org.scada_lts.web.beans.validation.ScadaValidator;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class OwaspXssValidator implements ScadaValidator<String> {

    private final PolicyFactory policyFactory;

    public OwaspXssValidator() {
        this.policyFactory = createPolicyFactory();
    }

    @Override
    public void validate(String input) throws XssValidatorException {
        try {
            validateXss(input);
        } catch (Exception e) {
            throw new XssValidatorException("XSS validation failed: " + e.getMessage(), e);
        }
    }

    void validateXss(String input) throws XssValidatorException {
        String sanitized = policyFactory.sanitize(input);
        if (!sanitized.equals(input)) {
            throw new XssValidatorException("Potential XSS attack detected");
        }
    }

    private PolicyFactory createPolicyFactory() {
        return new HtmlPolicyBuilder()
                .allowElements("b", "i", "u", "strong", "em", "p", "ul", "ol", "li")
                .toFactory();
    }
}
