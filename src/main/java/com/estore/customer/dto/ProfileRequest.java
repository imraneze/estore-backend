package com.estore.customer.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProfileRequest {
    private String phone;
    private String address;
    private String city;
    private String country;
}