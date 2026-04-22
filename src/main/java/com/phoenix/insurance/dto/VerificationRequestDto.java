package com.phoenix.insurance.dto;

public record VerificationRequestDto(
        Long sessionId,
        String code
) {}