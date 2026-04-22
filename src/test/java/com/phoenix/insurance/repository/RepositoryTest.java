package com.phoenix.insurance.repository;

import com.phoenix.insurance.model.IdentificationSession;
import com.phoenix.insurance.model.Product;
import com.phoenix.insurance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private UserRepository userRepository;
    private ProductRepository productRepository;
    private IdentificationSessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        productRepository = new ProductRepository();
        sessionRepository = new IdentificationSessionRepository();
    }

    @Test
    @DisplayName("UserRepository - Should save and find user")
    void testUserRepo_SaveAndFind() {
        User user = new User();
        user.setId("12345");

        userRepository.save(user);
        Optional<User> found = userRepository.findById("12345");

        assertTrue(found.isPresent());
        assertEquals("12345", found.get().getId());
    }

    @Test
    @DisplayName("ProductRepository - Should find all saved products")
    void testProductRepo_FindAll() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Insurance A");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Insurance B");

        productRepository.save(p1);
        productRepository.save(p2);

        Collection<Product> all = productRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("IdentificationSessionRepository - Should update existing session")
    void testSessionRepo_Update() {
        IdentificationSession session = new IdentificationSession();
        session.setId(100L);
        session.setOtpCode("111111");

        sessionRepository.save(session);

        // Update the code
        session.setOtpCode("222222");
        sessionRepository.save(session);

        Optional<IdentificationSession> found = sessionRepository.findById(100L);
        assertEquals("222222", found.get().getOtpCode());
    }
}