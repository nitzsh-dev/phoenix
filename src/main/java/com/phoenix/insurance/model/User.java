package com.phoenix.insurance.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String id;
    private EmailAddress email;
    private PhoneNumber phone;
    private UserRole role = UserRole.USER;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Set<Long> purchasedProductIds = new HashSet<>();
}