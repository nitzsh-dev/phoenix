package com.phoenix.insurance.repository;

import com.phoenix.insurance.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    // Using ConcurrentHashMap to ensure thread-safety for our in-memory storage
    private final Map<String, User> users = new ConcurrentHashMap<>();

    // Save or update a user in memory
    public User save(User user) {
        if (user.getId() != null) {
            users.put(user.getId(), user);
        }
        return user;
    }

    // Find a user by their unique ID
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
}