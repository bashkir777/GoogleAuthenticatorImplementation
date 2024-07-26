package com.bashkir777.jwtauthservice.app.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/secure")
@SecurityRequirement(name = "bearerAuth")
public class SecureEndpoint {
    @GetMapping("/echo")
    public String secureEndpointExample(){
        return "This information received from secured endpoint";
    }
}
