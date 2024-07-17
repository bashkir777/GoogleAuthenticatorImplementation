package com.bashkir777.jwtauthservice.auth.controllers;

import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.security.exceptions.InvalidTokenException;
import com.bashkir777.jwtauthservice.auth.security.services.AuthenticationService;
import com.bashkir777.jwtauthservice.auth.security.services.JwtService;
import com.bashkir777.jwtauthservice.data.entities.User;
import com.bashkir777.jwtauthservice.data.enums.TokenType;
import com.bashkir777.jwtauthservice.data.repositories.TokenRepository;
import com.bashkir777.jwtauthservice.data.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(authenticationService.login(authenticationRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO){
        try{
            AccessToken accessToken = authenticationService.refresh(refreshTokenDTO);
            return ResponseEntity.status(HttpStatus.OK).body(accessToken);
        }catch (InvalidTokenException invalidTokenException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(invalidTokenException.getMessage());
        }
    }

}
