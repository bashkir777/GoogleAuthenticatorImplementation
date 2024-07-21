package com.bashkir777.jwtauthservice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TFAVerificationRequest {
    private String username;
    private String password;
    private String otp;
}
