package com.phoenix.insurance.dto;

import com.phoenix.insurance.model.ContactMethod;
import com.phoenix.insurance.model.UserRole;

public record UserRegistrationDto(
        String id,
        String email,
        String phone
) {}