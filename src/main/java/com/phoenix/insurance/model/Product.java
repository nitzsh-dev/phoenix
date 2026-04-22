package com.phoenix.insurance.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}