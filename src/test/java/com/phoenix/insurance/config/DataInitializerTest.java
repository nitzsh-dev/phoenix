package com.phoenix.insurance.config;

import com.phoenix.insurance.model.User;
import com.phoenix.insurance.model.UserRole;
import com.phoenix.insurance.repository.ProductRepository;
import com.phoenix.insurance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataInitializerTest {

    private ProductRepository productRepository;
    private UserRepository userRepository;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
        userRepository = new UserRepository();
        dataInitializer = new DataInitializer(productRepository, userRepository);
    }

    @Test
    @DisplayName("DataInitializer - Should seed products and admin user on startup")
    void testDataInitialization() throws Exception {
        // Act
        dataInitializer.run();

        // Assert - Products
        assertFalse(productRepository.findAll().isEmpty(), "Products should be initialized");
        assertTrue(productRepository.findById(100L).isPresent(), "Car Insurance should exist");

        // Assert - Admin User
        User admin = userRepository.findById("111111111").orElse(null);
        assertNotNull(admin, "Admin user should be created");
        assertEquals(UserRole.ADMIN, admin.getRole());
    }
}