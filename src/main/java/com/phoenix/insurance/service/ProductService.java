package com.phoenix.insurance.service;

import com.phoenix.insurance.dto.ProductResponseDto;
import com.phoenix.insurance.model.*;
import com.phoenix.insurance.repository.IdentificationSessionRepository;
import com.phoenix.insurance.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final IdentificationSessionRepository identificationSessionRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, IdentificationSessionRepository identificationSessionRepository, UserService userService) {
        this.productRepository = productRepository;
        this.identificationSessionRepository = identificationSessionRepository;
        this.userService = userService;
    }

    // Only Admin can update products
    public void updateProductWithSession(Product updatedProduct, Long sessionId) {
        IdentificationSession session = identificationSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session not found"));

        if (session.getStatus() != SessionStatus.IDENTIFIED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "סשן לא מאומת - חובה לבצע OTP");
        }

        User user = userService.getUserById(session.getUserId());
        if (user == null || user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "אין הרשאות אדמין לפעולה זו");
        }

        Optional<Product> productOpt = productRepository.findById(updatedProduct.getId());
        if (productOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "המוצר המבוקש אינו נמצא");
        }

        Product existingProduct = productOpt.get();
        if (updatedProduct.getName() != null && !updatedProduct.getName().isBlank())
            existingProduct.setName(updatedProduct.getName());
        if (updatedProduct.getPrice() != null)
            existingProduct.setPrice(updatedProduct.getPrice());

        productRepository.save(existingProduct);
    }

    // Get all available products in the catalog
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponseDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice()
                ))
                .collect(Collectors.toList());
    }
}