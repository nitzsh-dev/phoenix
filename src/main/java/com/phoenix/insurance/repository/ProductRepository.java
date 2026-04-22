package com.phoenix.insurance.repository;

import com.phoenix.insurance.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    public Product save(Product product) {
        if (product.getId() != null) {
            products.put(product.getId(), product);
        }
        return product;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Collection<Product> findAll() {
        return products.values();
    }
}