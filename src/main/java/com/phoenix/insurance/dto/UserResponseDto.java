package com.phoenix.insurance.dto;

import com.phoenix.insurance.model.UserRole;

public record UserResponseDto(
        String id,
        String email,
        UserRole role
) {}