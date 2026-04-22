package com.phoenix.insurance.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record PhoneNumber(String value) {
    public PhoneNumber {
        if (value == null || !value.matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone number must be exactly 10 digits");
        }
    }
}