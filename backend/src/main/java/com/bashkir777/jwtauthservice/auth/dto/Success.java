package com.bashkir777.jwtauthservice.auth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Success {
    private Boolean success;
    private String description;
}
