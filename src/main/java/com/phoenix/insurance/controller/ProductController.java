package com.phoenix.insurance.controller;

import com.phoenix.insurance.dto.ProductResponseDto;
import com.phoenix.insurance.model.Product;
import com.phoenix.insurance.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(
            @RequestBody Product product,
            @RequestHeader("X-OTP-Session-Id") Long sessionId) {

        productService.updateProductWithSession(product, sessionId);
        return ResponseEntity.ok("Product updated successfully");
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}