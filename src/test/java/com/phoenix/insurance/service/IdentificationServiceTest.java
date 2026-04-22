package com.phoenix.insurance.service;

import com.phoenix.insurance.model.*;
import com.phoenix.insurance.repository.IdentificationSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentificationServiceTest {

    @Mock
    private IdentificationSessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private IdentificationService identificationService;

    private User mockUser;
    private String userId = "123456789";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail(new EmailAddress("test@phoenix.co.il"));
        mockUser.setPhone(new PhoneNumber("0501234567"));
    }

    @Test
    @DisplayName("Should initiate identification and generate 6-digit OTP")
    void testInitiateIdentification_Success() {
        // Arrange
        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(sessionRepository.save(any(IdentificationSession.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        IdentificationSession session = identificationService.initiateIdentification(userId, ContactMethod.EMAIL);

        // Assert
        assertNotNull(session);
        assertEquals(userId, session.getUserId());
        assertEquals(6, session.getOtpCode().length());
        assertTrue(session.getExpiryTime().isAfter(LocalDateTime.now()));
        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should verify OTP successfully and change status to IDENTIFIED")
    void testVerifyOtp_Success() {
        // Arrange
        Long sessionId = 1L;
        IdentificationSession session = new IdentificationSession();
        session.setOtpCode("123456");
        session.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        session.setStatus(SessionStatus.OPEN);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act
        identificationService.verifyOtp(sessionId, "123456");

        // Assert
        assertEquals(SessionStatus.IDENTIFIED, session.getStatus());
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("Should throw exception and increment retry count on wrong OTP")
    void testVerifyOtp_WrongCode() {
        // Arrange
        Long sessionId = 1L;
        IdentificationSession session = new IdentificationSession();
        session.setOtpCode("123456");
        session.setRetryCount(0);
        session.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () ->
                identificationService.verifyOtp(sessionId, "000000")
        );
        assertEquals(1, session.getRetryCount());
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("Should block session after 3 failed attempts")
    void testVerifyOtp_BlockAfterThreeAttempts() {
        // Arrange
        Long sessionId = 1L;
        IdentificationSession session = new IdentificationSession();
        session.setOtpCode("123456");
        session.setRetryCount(2); // ניסיון שלישי בדרך
        session.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () ->
                identificationService.verifyOtp(sessionId, "000000")
        );
        assertEquals(SessionStatus.BLOCKED, session.getStatus());
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("Should throw exception if session is expired")
    void testVerifyOtp_ExpiredSession() {
        // Arrange
        Long sessionId = 1L;
        IdentificationSession session = new IdentificationSession();
        session.setExpiryTime(LocalDateTime.now().minusMinutes(1)); // פג תוקף לפני דקה

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () ->
                identificationService.verifyOtp(sessionId, "123456")
        );
    }
}