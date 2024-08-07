package com.bashkir777.jwtauthservice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String secret;
    private String otp;
    private Boolean tfaEnabled;
}
