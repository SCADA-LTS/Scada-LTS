package org.scada_lts.web.mvc.api.exceptions;


import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ScadaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ScadaApiException.class})
    public ResponseEntity<ScadaErrorMessage> handleScadaApiException(ScadaApiException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getErrorMessage(), new HttpHeaders(), ex.getErrorMessage().getStatus());
    }
}
