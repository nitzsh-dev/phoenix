package com.phoenix.insurance.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class IdentificationSession {
    private Long id;
    private String userId;
    private ContactMethod methodType;
    private String methodValue;
    private String otpCode;
    private SessionStatus status = SessionStatus.OPEN;
    private int retryCount = 0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiryTime = createdAt.plusMinutes(5);
}