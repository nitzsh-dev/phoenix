package com.phoenix.insurance.dto;

import com.phoenix.insurance.model.ContactMethod;

public record LoginRequestDto(
        String id,
        ContactMethod preferredMethod
) {}