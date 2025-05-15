package com.popoletos.ggauth.controller.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class ErrorsHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledExceptions(Exception ex) {
        log.error("Exception caught {}", ex.getMessage(), ex);

        var responseStatusValues = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatusValues != null) {
            log.error("Exception is annotated with ResponseStatus, honoring its values", ex);
            return new ResponseEntity<>(
                    ErrorResponse.builder()
                            .errorCode(responseStatusValues.code().toString())
                            .errorMessage(responseStatusValues.reason())
                            .build(),
                    responseStatusValues.code());
        }

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .errorCode("unknown_error")
                        .errorMessage(ex.getMessage())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
