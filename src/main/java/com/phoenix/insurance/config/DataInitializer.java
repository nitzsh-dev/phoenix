package com.phoenix.insurance.config;

import com.phoenix.insurance.model.*;
import com.phoenix.insurance.repository.ProductRepository;
import com.phoenix.insurance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DataInitializer(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Create sample products
        Product carInsurance = new Product();
        carInsurance.setId(100L);
        carInsurance.setName("Car Insurance Platinum");
        carInsurance.setPrice(new BigDecimal("1200.00"));

        Product homeInsurance = new Product();
        homeInsurance.setId(101L);
        homeInsurance.setName("Home Structure Insurance");
        homeInsurance.setPrice(new BigDecimal("850.50"));

        productRepository.save(carInsurance);
        productRepository.save(homeInsurance);

        // Create admin user
        User user = new User();
        user.setId("111111111");
        user.setEmail(new EmailAddress("admin@gmail.com"));
        user.setPhone(new PhoneNumber("0522222222"));
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        System.out.println("DEBUG: Sample products and admin user initialized in memory.");
    }
}