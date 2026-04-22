package com.phoenix.insurance.dto;

import com.phoenix.insurance.model.ContactMethod;

public record AuthResponseDto(
        UserResponseDto user,
        Long otpSessionId,
        ContactMethod methodType, // EMAIL or PHONE
        String destination,       // Method value
        String message
) {}