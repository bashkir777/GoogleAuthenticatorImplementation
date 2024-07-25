package com.bashkir777.jwtauthservice.auth.services;

import com.bashkir777.jwtauthservice.app.data.exceptions.NoSuchUserException;
import com.bashkir777.jwtauthservice.auth.dto.*;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidCode;
import com.bashkir777.jwtauthservice.auth.exceptions.InvalidTokenException;
import com.bashkir777.jwtauthservice.app.data.entities.RefreshToken;
import com.bashkir777.jwtauthservice.app.data.repositories.TokenRepository;
import com.bashkir777.jwtauthservice.app.data.repositories.UserRepository;
import com.bashkir777.jwtauthservice.app.data.entities.User;
import com.bashkir777.jwtauthservice.app.data.enums.Role;
import com.bashkir777.jwtauthservice.app.data.enums.TokenType;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthenticationService tfaService;

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            return userRepository.getUserByUsername((String) principal);
        }
        UserDetails userDetails = (UserDetails) principal;
        return userRepository.getUserByUsername(userDetails.getUsername());
    }

    public UserDetails getCurrentUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private AuthenticationResponse generateTokenPair(User user) {
        var accessToken = jwtService.generateToken(user, TokenType.ACCESS, null);
        var refreshToken = jwtService.generateToken(user, TokenType.REFRESH, null);
        return AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public boolean tfaEnabled(String username) throws BadCredentialsException {
        var user = userRepository
                .getUserByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("No such user");
        }
        return user.isTwoFactorAuthenticationEnabled();
    }

    public RegisterResponse register(RegisterRequest registerRequest)
            throws DataIntegrityViolationException, InvalidCode, UnknownHostException {

        if (registerRequest.getTfaEnabled()
                && !tfaService.isOTPValid(registerRequest.getSecret(), registerRequest.getOtp())) {
            throw new InvalidCode();
        }
        var user = User.builder().firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .secretKey(registerRequest.getSecret())
                .twoFactorAuthenticationEnabled(registerRequest.getTfaEnabled())
                .role(Role.USER).build();
        userRepository.save(user);
        var tokenPair = generateTokenPair(user);
        var registerResponse = RegisterResponse.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .build();
        tokenRepository.save(RefreshToken.builder()
                .token(registerResponse.getRefreshToken())
                .user(user).build());
        return registerResponse;
    }

    public AuthenticationResponse login(@NonNull AuthenticationRequest authenticationRequest)
            throws DataIntegrityViolationException, AuthenticationException, InvalidCode {
        var user = userRepository
                .getUserByUsername(authenticationRequest.getUsername());
        if (user == null) {
            throw new BadCredentialsException("No such user");
        }
        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        if (user.isTwoFactorAuthenticationEnabled()) {
            if (!tfaService.isOTPValid(user.getSecretKey(), authenticationRequest.getOtp())) {
                throw new InvalidCode();
            }
        }
        AuthenticationResponse authenticationResponse;
        authenticationResponse = generateTokenPair(user);
        tokenRepository.save(RefreshToken.builder()
                .token(authenticationResponse.getRefreshToken()).user(user).build());
        return authenticationResponse;
    }

    public AuthenticationResponse resetPassword(@NonNull ResetPassword resetPassword) throws InvalidCode, BadCredentialsException {
        var user = userRepository
                .getUserByUsername(resetPassword.getUsername());
        if (user == null) {
            throw new BadCredentialsException("No such user");
        }
        if (user.isTwoFactorAuthenticationEnabled()) {
            if (!tfaService.isOTPValid(user.getSecretKey(), resetPassword.getOtp())) {
                throw new InvalidCode();
            }
            userRepository.resetPassword(user.getUsername()
                    , passwordEncoder.encode(resetPassword.getNewPassword()));
            AuthenticationResponse authenticationResponse;
            authenticationResponse = generateTokenPair(user);
            tokenRepository.save(RefreshToken.builder()
                    .token(authenticationResponse.getRefreshToken()).user(user).build());
            return authenticationResponse;
        } else {
            throw new BadCredentialsException("User hasn't enabled TFA. Password can't be changed");
        }
    }

    private User checkIfTokenIsValidAndRefreshTypeAndReturnUser(RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException {
        String username;
        TokenType type;
        try {
            Claims claims = jwtService.extractAllClaimsAndValidateToken(refreshTokenDTO.getRefreshToken());
            username = claims.getSubject();
            type = TokenType.valueOf((String) claims.get("type"));
        } catch (RuntimeException exception) {
            throw new InvalidTokenException();
        }
        User user = userRepository.getUserByUsername(username);
        if (user == null || !type.equals(TokenType.REFRESH)) {
            throw new InvalidTokenException();
        }
        return user;
    }

    public void logout(@NonNull RefreshTokenDTO refreshTokenDTO)
            throws InvalidTokenException, DataIntegrityViolationException {
        User user = checkIfTokenIsValidAndRefreshTypeAndReturnUser(refreshTokenDTO);
        tokenRepository.deleteRefreshTokenByUser(user);
    }

    public AccessToken refresh(@NonNull RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException {
        User user = checkIfTokenIsValidAndRefreshTypeAndReturnUser(refreshTokenDTO);
        var refreshTokenDb = tokenRepository.findRefreshTokenByUser(user);
        if (refreshTokenDb.isEmpty() ||
                refreshTokenDb.get().getToken().equals(refreshTokenDTO.getRefreshToken())) {
            throw new InvalidTokenException();
        }
        var jwtAccess = jwtService.
                generateToken(user, TokenType.ACCESS, null);
        return AccessToken.builder()
                .accessToken(jwtAccess).build();
    }

}
