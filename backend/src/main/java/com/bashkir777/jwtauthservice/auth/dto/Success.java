package com.bashkir777.jwtauthservice.auth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Success {
    private Boolean success;
    private String description;
}
