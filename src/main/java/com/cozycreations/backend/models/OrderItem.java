package com.cozycreations.backend.models;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private String name;
    private Integer quantity;
    private Integer quantityPack;
    private Double price;
    private Double weightGrams;
    private String dimensions;
}
