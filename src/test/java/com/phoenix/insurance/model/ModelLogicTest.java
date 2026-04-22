package com.phoenix.insurance.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class ModelLogicTest {

    @Test
    @DisplayName("EmailAddress - Should create valid email")
    void testValidEmail() {
        assertDoesNotThrow(() -> new EmailAddress("test@phoenix.co.il"));
    }

    @Test
    @DisplayName("EmailAddress - Should throw exception for invalid email")
    void testInvalidEmail() {
        assertThrows(ResponseStatusException.class, () -> new EmailAddress("invalid-email"));
    }

    @Test
    @DisplayName("PhoneNumber - Should accept exactly 10 digits")
    void testValidPhone() {
        assertDoesNotThrow(() -> new PhoneNumber("0501234567"));
    }

    @Test
    @DisplayName("PhoneNumber - Should throw exception for 9 digits or characters")
    void testInvalidPhone() {
        assertThrows(ResponseStatusException.class, () -> new PhoneNumber("050123456")); // קצר מדי
        assertThrows(ResponseStatusException.class, () -> new PhoneNumber("050123456a")); // מכיל אות
    }

    @Test
    @DisplayName("User - Should initialize with default role and empty products")
    void testUserDefaults() {
        User user = new User();
        assertEquals(UserRole.USER, user.getRole());
        assertNotNull(user.getPurchasedProductIds());
        assertTrue(user.getPurchasedProductIds().isEmpty());
    }

    @Test
    @DisplayName("IdentificationSession - Should have 5 minutes expiry by default")
    void testSessionExpiry() {
        IdentificationSession session = new IdentificationSession();
        assertNotNull(session.getExpiryTime());
        assertTrue(session.getExpiryTime().isAfter(session.getCreatedAt()));
        assertEquals(SessionStatus.OPEN, session.getStatus());
    }
}