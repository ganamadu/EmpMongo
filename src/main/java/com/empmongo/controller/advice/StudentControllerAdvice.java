package com.empmongo.controller.advice;

import com.empmongo.exception.EntityNotFoundException;
import com.empmongo.exception.StandardError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StudentControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(StudentControllerAdvice.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public StandardError entityNotFoundErrorHandling(EntityNotFoundException entityNotFoundException) {
        StandardError standardError = new StandardError();
        standardError.setErrorCode(String.valueOf(entityNotFoundException.getStatusCode().value()));
        standardError.setErrorMessage(entityNotFoundException.getReason());
        return standardError;
    }

}
