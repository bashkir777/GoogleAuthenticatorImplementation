package com.bashkir777.jwtauthservice.auth.controllers;

import com.bashkir777.jwtauthservice.app.data.exceptions.NoSuchUserException;
import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidCode;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidTokenException;
import com.bashkir777.jwtauthservice.auth.exceptions.TFAIsNotEnabled;
import com.bashkir777.jwtauthservice.auth.services.AuthenticationService;
import com.bashkir777.jwtauthservice.auth.services.TwoFactorAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and returns refresh and access tokens. Optionally enables two-factor authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful registration",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid one time password",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Database already contains user with provided username",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error. Appears in case if the NTP server used for " +
                            "two-factor authentication is currently unavailable",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest)
            throws DataIntegrityViolationException, UnknownHostException, InvalidCode {
        AuthenticationResponse authenticationResponse = authenticationService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationResponse);
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns refresh and access tokens."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Could be invalid username/password/one time password." +
                            " This status also can appear in case if user who has two-factor authentication enabled" +
                            " hasn't provided an otp",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error. Appears in case if the NTP server used for " +
                            "two-factor authentication is currently unavailable",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest)
            throws BadCredentialsException, InvalidCode {
        AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
    }

    @Operation(
            summary = "Create new access token",
            description = "Create new access token if refresh token provided by user is valid"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Refresh token is valid. Access token created and send",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccessToken.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Refresh token provided by user either expired or forged",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            ),
    })
    @PostMapping("/refresh")
    public ResponseEntity<AccessToken> refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException {
        AccessToken accessToken = authenticationService.refresh(refreshTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body(accessToken);
    }

    @Operation(
            summary = "Check if user has enabled two-factor authentication",
            description = "Check if user has enabled two-factor authentication based on username"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Username provided by user is correct. Answer wrapped with JSON",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TFAEnabled.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Username provided by user is incorrect",
                    content = @Content(
                        mediaType = "plain/text"
                    )
            ),
    })
    @GetMapping("/tfa-enabled/{username}")
    public ResponseEntity<TFAEnabled> isTfaEnabled(@PathVariable String username) throws BadCredentialsException {
        return ResponseEntity.ok(new TFAEnabled(authenticationService.tfaEnabled(username)));
    }

    @Operation(
            summary = "User logout",
            description = "JWT authentication doesn't support instant logout." +
                    " This endpoint removes provided refresh token from whitelist so that it can't be used any more"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Refresh token provided by user is correct and whitelist contains it." +
                            " Refresh token successfully removed from whitelist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Success.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Either refresh token expired/forged/incorrect " +
                            "or account of user who has created it has been deleted from database",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            ),
    })
    @PostMapping("/logout")
    public ResponseEntity<Success> logout(@RequestBody RefreshTokenDTO refreshTokenDTO)
            throws InvalidTokenException {
        authenticationService.logout(refreshTokenDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new Success(true, "you have successfully logged out"));
    }

    @Operation(
            summary = "Generate qr code containing secret key",
            description = "Generate qr code containing secret key which will be used for two-factor authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns URL supported by Google Authenticator wrapped in JSON." +
                            "This URL contains secret key, username etc.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = QRCode.class)
                    )
            )
    })
    @GetMapping("/generate-secret-qr-url/{username}")
    public ResponseEntity<QRCode> generateQr(@PathVariable String username) {
        String secret = twoFactorAuthenticationService.generateNewSecret();
        QRCode qrCode = new QRCode(twoFactorAuthenticationService.formatQRCodeImageURL(secret, username));
        return ResponseEntity.ok(qrCode);
    }

    @Operation(
            summary = "Check if username provided by user is available",
            description = "Check if username provided by user is available"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Answer wrapped with JSON",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IsFree.class)
                    )
            ),
    })
    @GetMapping("/is-username-free/{username}")
    public ResponseEntity<IsFree> isUsernameFree(@PathVariable String username) {
        return ResponseEntity.ok(authenticationService.isFree(username));
    }

    @Operation(
            summary = "Reset password",
            description = "Reset password operation" +
                    ". Available only for users who has enabled two-factor authentication"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Username, new password and one time password provided by user is correct.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Username or one time password provided by user is incorrect",
                    content = @Content(
                            mediaType = "plain/text"
                    )
            ),
    })
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
