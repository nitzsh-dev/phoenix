package com.phoenix.insurance.controller;

import com.phoenix.insurance.dto.AuthResponseDto;
import com.phoenix.insurance.dto.LoginRequestDto;
import com.phoenix.insurance.dto.UserRegistrationDto;
import com.phoenix.insurance.model.ContactMethod;
import com.phoenix.insurance.model.User;
import com.phoenix.insurance.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/users") // The base URL for all endpoints in this controller
public class UserController {

    private final UserService userService;

    // Spring injects the UserService automatically
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Updated register endpoint using DTOs
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto registrationDto) {
        if (registrationDto.id() == null || registrationDto.id().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "חובה להקליד תעודת זהות");
        }

        if (registrationDto.email() == null || registrationDto.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "חובה להקליד כתובת מייל");
        }

        if (registrationDto.phone() == null || registrationDto.phone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "חובה להקליד מספר טלפון");
        }

        Long sessionId = userService.registerUser(registrationDto);
        return ResponseEntity.ok("הרישום בוצע בהצלחה. אנא אמת כתובת מייל בעזרת קוד שנשלח למייל. קוד הסשן הוא " + sessionId);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto) {
        // ולידציה בסיסית על הקלט
        if (loginDto.id() == null || loginDto.id().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "חובה להזין תעודת זהות");
        }

        ContactMethod method = loginDto.preferredMethod();
        // קריאה ללוגיקה של הלוגין
        String message = userService.login(loginDto.id(), method);
        return ResponseEntity.ok(message);
    }

    // Endpoint to purchase a product
    @PostMapping("/{id}/purchase/{productId}")
    public ResponseEntity<String> purchase(@RequestHeader("X-OTP-Session-Id") Long sessionId, @PathVariable Long productId) {
        userService.purchaseProduct(sessionId, productId);
        return ResponseEntity.ok("Product purchased successfully!");
    }

    // Endpoint to see all purchased product IDs
    @GetMapping("/my-products")
    public ResponseEntity<Set<Long>> getMyProducts(@RequestHeader("X-OTP-Session-Id") Long sessionId) {
        Set<Long> products = userService.getUserProductsBySession(sessionId);
        return ResponseEntity.ok(products);
    }
}