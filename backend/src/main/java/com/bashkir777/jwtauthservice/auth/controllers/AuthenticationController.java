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
import org.springframework.security.authentication.BadCredentialsException;
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
            throws DataIntegrityViolationException, AuthenticationException, InvalidCode {
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessToken> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException {
        AccessToken accessToken = authenticationService.refresh(refreshTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @GetMapping("/tfa-enabled/{username}")
    public ResponseEntity<TFAEnabled> isTfaEnabled(@PathVariable String username) throws BadCredentialsException{
        return ResponseEntity.ok(new TFAEnabled(authenticationService.tfaEnabled(username)));
    }


    @PostMapping("/logout")
    public ResponseEntity<Success> logout(@RequestBody RefreshTokenDTO refreshTokenDTO)
            throws InvalidTokenException, DataIntegrityViolationException {
        authenticationService.logout(refreshTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new Success(true, "you have successfully logged out"));
    }

    @GetMapping("/generate-secret-qr-url/{username}")
    public ResponseEntity<QRCode> generateQr(@PathVariable String username) {
        String secret = twoFactorAuthenticationService.generateNewSecret();
        QRCode qrCode = new QRCode(twoFactorAuthenticationService.formatQRCodeImageURL(secret, username));
        return ResponseEntity.ok(qrCode);
    }

    @GetMapping("/is-username-free/{username}")
    public ResponseEntity<IsFree> isUsernameFree(@PathVariable String username) {
        return ResponseEntity.ok(authenticationService.isFree(username));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponse> resetPassword(@RequestBody ResetPassword resetPassword)
            throws InvalidCode, BadCredentialsException {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.resetPassword(resetPassword));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, NoSuchUserException.class})
    public ResponseEntity<String> handleDataIntegrityViolationException(Exception ex) {
        return new ResponseEntity<>("Data integrity violation: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UnknownHostException.class})
    public ResponseEntity<String> internalServerError(Exception ex) {
        return new ResponseEntity<>("Exception on our side", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvalidTokenException.class, BadCredentialsException.class
            , InvalidCode.class, TFAIsNotEnabled.class})
    public ResponseEntity<String> handleIAuthExceptions(Exception ex) {
        return new ResponseEntity<>("Authentication/Authorization failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
