package com.bashkir777.jwtauthservice.auth.controllers;

import com.bashkir777.jwtauthservice.app.data.exceptions.NoSuchUserException;
import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidCode;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidTokenException;
import com.bashkir777.jwtauthservice.auth.exceptions.TFAIsNotEnabled;
import com.bashkir777.jwtauthservice.auth.services.AuthenticationService;
import com.bashkir777.jwtauthservice.auth.services.TwoFactorAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest)
            throws DataIntegrityViolationException, UnknownHostException, InvalidCode {
        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
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
            throws InvalidCode, NoSuchUserException, TFAIsNotEnabled, UnknownHostException {
       return ResponseEntity.ok(authenticationService.verifyCode(verificationRequest));
    }
    @GetMapping("/generate-secret-qr-url/{username}")
    public ResponseEntity<QRCode> generateQr(@PathVariable String username) {
        String secret = twoFactorAuthenticationService.generateNewSecret();
        QRCode qrCode = new QRCode(twoFactorAuthenticationService.formatQRCodeImageURL(secret, username));
        return ResponseEntity.ok(qrCode);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, NoSuchUserException.class})
    public ResponseEntity<String> handleDataIntegrityViolationException(Exception ex) {
        return new ResponseEntity<>("Data integrity violation: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UnknownHostException.class})
    public ResponseEntity<String> internalServerError(Exception ex) {
        return new ResponseEntity<>("Exception on our side", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvalidTokenException.class, InvalidCode.class, TFAIsNotEnabled.class})
    public ResponseEntity<String> handleIAuthExceptions(Exception ex) {
        return new ResponseEntity<>("Authentication/Authorization failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
