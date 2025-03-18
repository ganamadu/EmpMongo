package com.empmongo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {
    private static final Logger logger = LoggerFactory.getLogger(EntityNotFoundException.class);
    private static final String REASON = "Student Entity was not found";

    public EntityNotFoundException() {
        super(HttpStatus.NOT_FOUND, REASON);
    }

}
