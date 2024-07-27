package com.bashkir777.jwtauthservice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TFAEnabled {
    private boolean tfaEnabled;
}
