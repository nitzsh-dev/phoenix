package com.phoenix.insurance.controller;

import com.phoenix.insurance.dto.VerificationRequestDto;
import com.phoenix.insurance.service.IdentificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class IdentificationController {

    private final IdentificationService identificationService;

    public IdentificationController(IdentificationService identificationService) {
        this.identificationService = identificationService;
    }

    // Verify the code provided by user
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerificationRequestDto request) {
        identificationService.verifyOtp(request.sessionId(), request.code());
        return ResponseEntity.ok("אימות עבר בהצלחה! הנך רשאי לבצע רכישות.");
    }
}