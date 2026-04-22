package com.phoenix.insurance.repository;

import com.phoenix.insurance.model.IdentificationSession;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class IdentificationSessionRepository {

    // In-memory storage for OTP sessions
    private final Map<Long, IdentificationSession> sessions = new ConcurrentHashMap<>();

    // Save or update a session
    public IdentificationSession save(IdentificationSession session) {
        if (session.getId() != null) {
            sessions.put(session.getId(), session);
        }
        return session;
    }

    // Find session by ID
    public Optional<IdentificationSession> findById(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }
}