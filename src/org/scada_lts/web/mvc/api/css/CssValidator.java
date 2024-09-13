package org.scada_lts.web.mvc.api.css;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CssValidator implements ConstraintValidator<CssValid, CssStyle> {

    private static final String VALIDATOR_API_URL = "https://jigsaw.w3.org/css-validator/validator?profile=css3&output=json&text=";

    @Override
    public void initialize(CssValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(CssStyle value, ConstraintValidatorContext context) {
        if (value == null || value.getContent().trim().isEmpty()) {
            return false;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String cssContent = value.getContent();
            String encodedCssContent = java.net.URLEncoder.encode(cssContent, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VALIDATOR_API_URL + encodedCssContent))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();


            if (responseBody.contains("\"errorcount\"   : 0")) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("CSS contains validation errors").addConstraintViolation();
                return false;
            }

        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Error validating CSS: " + e.getMessage()).addConstraintViolation();
            return false;
        }
    }
}