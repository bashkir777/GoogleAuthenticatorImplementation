package com.bashkir777.jwtauthservice.auth.controllers;

import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.security.exceptions.InvalidTokenException;
import com.bashkir777.jwtauthservice.auth.security.services.AuthenticationService;
import com.bashkir777.jwtauthservice.auth.security.services.JwtService;
import com.bashkir777.jwtauthservice.data.entities.User;
import com.bashkir777.jwtauthservice.data.enums.TokenType;
import com.bashkir777.jwtauthservice.data.exceptions.UserCreationException;
import com.bashkir777.jwtauthservice.data.repositories.TokenRepository;
import com.bashkir777.jwtauthservice.data.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        try{
            AuthenticationResponse authenticationResponse = authenticationService.register(registerRequest);
            return ResponseEntity.ok(authenticationResponse);
        }catch (DataIntegrityViolationException dataIntegrityViolationException){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST).body(dataIntegrityViolationException.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
        try{
            AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
            return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
        }catch (DataIntegrityViolationException | AuthenticationException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
        }
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
