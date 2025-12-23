package org.scada_lts.web.mvc.api.exceptions;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ScadaExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Converts a ScadaApiException into an HTTP response containing its ScadaErrorMessage.
     *
     * @param ex      the ScadaApiException carrying the ScadaErrorMessage
     * @param request the current web request
     * @return        a ResponseEntity whose body is the exception's ScadaErrorMessage and whose HTTP status is taken from that message's status
     */
    @ExceptionHandler({ScadaApiException.class})
    public ResponseEntity<ScadaErrorMessage> handleScadaApiException(ScadaApiException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getErrorMessage(), new HttpHeaders(), HttpStatus.valueOf(ex.getErrorMessage().getStatus()));
    }

    /**
     * Handle controller method argument validation failures and produce a structured ScadaErrorMessage response.
     *
     * @param ex      the exception containing validation results and field errors
     * @param request the current web request (used to populate the error instance/context)
     * @return a ResponseEntity whose body is a ScadaErrorMessage summarizing validation field errors; the HTTP status reflects the error message's status
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        if(bindingResult == null || bindingResult.getFieldErrors() == null) {
            return super.handleMethodArgumentNotValid(ex, headers, status, request);
        }
        StringBuilder detail = new StringBuilder();
        for(FieldError fieldError: bindingResult.getFieldErrors()) {
            detail.append(fieldError.getDefaultMessage()).append("; ");
        }
        ScadaErrorMessage scadaErrorMessage = ScadaErrorMessage.builder(HttpStatus.BAD_REQUEST)
                .type("/api/exceptions/spring/" + MethodArgumentNotValidException.class.getSimpleName())
                .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(detail.toString())
                .instance(request.getContextPath())
                .build();
        return new ResponseEntity<>(scadaErrorMessage, new HttpHeaders(), HttpStatus.valueOf(scadaErrorMessage.getStatus()));
    }
}