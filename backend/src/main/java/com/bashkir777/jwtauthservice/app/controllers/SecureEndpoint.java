package com.bashkir777.jwtauthservice.app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/secure")
public class SecureEndpoint {
    @GetMapping("/echo/{string}")
    public String secureEndpointExample(@PathVariable String string){
        return string;
    }
}
