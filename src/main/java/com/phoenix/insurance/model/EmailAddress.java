package com.phoenix.insurance.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || !value.contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must contain @");
        }
    }
}