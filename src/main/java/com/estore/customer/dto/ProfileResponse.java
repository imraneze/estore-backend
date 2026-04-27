package com.estore.customer.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;
    private String phone;
    private String address;
    private String city;
    private String country;
    private Long userId;
}