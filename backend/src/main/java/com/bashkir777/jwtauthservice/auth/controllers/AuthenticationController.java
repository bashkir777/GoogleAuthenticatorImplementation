package com.bashkir777.jwtauthservice.auth.controllers;

import com.bashkir777.jwtauthservice.app.data.exceptions.NoSuchUserException;
import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidCode;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidTokenException;
import com.bashkir777.jwtauthservice.auth.exceptions.TFAIsNotEnabled;
import com.bashkir777.jwtauthservice.auth.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest)
            throws DataIntegrityViolationException{
        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest)
            throws DataIntegrityViolationException, AuthenticationException{
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessToken> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException {
        AccessToken accessToken = authenticationService.refresh(refreshTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenDTO refreshTokenDTO)
            throws InvalidTokenException, DataIntegrityViolationException {
        authenticationService.logout(refreshTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body("You have successfully logged out of the system");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthenticationResponse> verifyCode(@RequestBody VerificationRequest verificationRequest)
            throws InvalidCode, NoSuchUserException, TFAIsNotEnabled {
       return ResponseEntity.ok(authenticationService.verifyCode(verificationRequest));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, NoSuchUserException.class})
    public ResponseEntity<String> handleDataIntegrityViolationException(Exception ex) {
        return new ResponseEntity<>("Data integrity violation: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({InvalidTokenException.class, InvalidCode.class, TFAIsNotEnabled.class})
    public ResponseEntity<String> handleIAuthExceptions(Exception ex) {
        return new ResponseEntity<>("Authentication/Authorization failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
