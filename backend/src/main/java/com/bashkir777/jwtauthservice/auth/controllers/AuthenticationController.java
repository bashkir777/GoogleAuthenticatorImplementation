package com.bashkir777.jwtauthservice.auth.controllers;

import com.bashkir777.jwtauthservice.auth.dto.AuthenticationRequest;
import com.bashkir777.jwtauthservice.auth.dto.AuthenticationResponse;
import com.bashkir777.jwtauthservice.auth.dto.RegisterRequest;
import com.bashkir777.jwtauthservice.auth.security.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest registerRequest){
        return ResponseEntity.ok(authenticationService.login(registerRequest));
    }

}
