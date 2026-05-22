package com.cozycreations.backend.models;

import lombok.Data;

@Data
public class Address {
    private String fullName;
    private String name;
    private String phone;
    private String houseNo;
    private String street;
    private String area;
    private String landmark;
    private String city;
    private String state;
    private String pincode;
}
