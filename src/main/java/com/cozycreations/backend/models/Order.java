package com.cozycreations.backend.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Order {
    private String id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String paymentMethod;
    private Double total;
    private Double deliveryFee;
    private Double platformFee;
    private String paymentId;
    private String paymentOrderId;
    private Address shippingAddress;
    private List<OrderItem> items;
    private String status;
    private Map<String, Object> metadata;
}
