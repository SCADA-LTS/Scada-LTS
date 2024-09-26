package org.scada_lts.web.beans.validation.xss;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.Sanitizers;
import org.scada_lts.web.beans.validation.ScadaValidator;
import org.owasp.html.PolicyFactory;

public class OwaspXssValidator implements ScadaValidator<String> {

    private final PolicyFactory policyFactory;

    public OwaspXssValidator() {
        PolicyFactory basePolicy = Sanitizers.FORMATTING
                .and(Sanitizers.LINKS)
                .and(Sanitizers.STYLES)
                .and(Sanitizers.IMAGES);

        this.policyFactory = new HtmlPolicyBuilder()
                .allowCommonInlineFormattingElements()
                .allowCommonBlockElements()
                .allowStandardUrlProtocols()
                .allowStyling()
                .toFactory()
                .and(basePolicy);
    }

    @Override
    public void validate(String input) throws XssValidatorException {
        System.out.println("input: " + input);
        String sanitized = policyFactory.sanitize(input);
        System.out.println("Sanitized: " + sanitized);
        if (!sanitized.equals(input)) {
            throw new XssValidatorException("Potential XSS attack detected");
        }
    }
}
