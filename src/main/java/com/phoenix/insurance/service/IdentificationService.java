package com.phoenix.insurance.service;

import com.phoenix.insurance.model.*;
import com.phoenix.insurance.repository.IdentificationSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdentificationService {

    private final IdentificationSessionRepository sessionRepository;
    private final UserService userService;
    private final AtomicLong idCounter = new AtomicLong(1);

    public IdentificationService(IdentificationSessionRepository sessionRepository, UserService userService) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
    }

    // Step 1: Create a session and "send" OTP
    public IdentificationSession initiateIdentification(String userId, ContactMethod method) {
        User user = userService.getUserById(userId);

        IdentificationSession session = new IdentificationSession();
        session.setId(idCounter.getAndIncrement());
        session.setUserId(userId);
        session.setMethodType(method);

        // Get the value based on the chosen method (Email or Phone)
        String contactValue = (method == ContactMethod.EMAIL)
                ? user.getEmail().value()
                : user.getPhone().value();

        session.setMethodValue(contactValue);

        // Generate a random 6-digit code
        String code = String.format("%06d", new Random().nextInt(1000000));
        session.setOtpCode(code);

        // Set expiry to 5 minutes from now
        session.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        // In a real app, we would call an SMS/Email gateway here
        System.out.println("DEBUG: Sending OTP " + code + " to " + contactValue);

        return sessionRepository.save(session);
    }

    // Step 2: Verify the OTP code provided by the user
    public void verifyOtp(Long sessionId, String userInputCode) {
        IdentificationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "סשן האימות לא נמצא"));

        if (session.getStatus() == SessionStatus.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "סשן זה חסום עקב יותר מדי ניסיונות כושלים");
        }
        if (session.getStatus() == SessionStatus.EXPIRED) {
            throw new ResponseStatusException(HttpStatus.GONE, "פג תוקף הקוד, עליך לבצע התחברות מחדש");
        }

        if (java.time.LocalDateTime.now().isAfter(session.getExpiryTime())) {
            session.setStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new ResponseStatusException(HttpStatus.GONE, "פג תוקף הקוד, עליך לבצע התחברות מחדש");
        }

        if (!session.getOtpCode().equals(userInputCode)) {
            session.setRetryCount(session.getRetryCount() + 1);

            if (session.getRetryCount() >= 3) {
                session.setStatus(SessionStatus.BLOCKED);
                sessionRepository.save(session);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "סשן נחסם - יותר מדי ניסיונות שגויים");
            }

            sessionRepository.save(session);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "קוד שגוי, נסה שנית (ניסיון " + session.getRetryCount() + "/3)");
        }

        session.setStatus(SessionStatus.IDENTIFIED);
        sessionRepository.save(session);
    }
}