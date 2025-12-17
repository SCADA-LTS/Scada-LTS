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

    @ExceptionHandler({ScadaApiException.class})
    public ResponseEntity<ScadaErrorMessage> handleScadaApiException(ScadaApiException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getErrorMessage(), new HttpHeaders(), HttpStatus.valueOf(ex.getErrorMessage().getStatus()));
    }

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
