package com.bashkir777.jwtauthservice.auth.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
    private String otp;
}
