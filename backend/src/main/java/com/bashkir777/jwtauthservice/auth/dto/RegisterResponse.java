package com.bashkir777.jwtauthservice.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegisterResponse {
    private String refreshToken;
    private String accessToken;
    private String secretImgURI;
}