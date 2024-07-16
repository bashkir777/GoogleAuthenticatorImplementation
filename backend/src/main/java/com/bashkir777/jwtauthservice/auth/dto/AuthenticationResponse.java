package com.bashkir777.jwtauthservice.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private String refreshToken;
    private String accessToken;
}
