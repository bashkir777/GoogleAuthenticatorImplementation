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
import com.bashkir777.jwtauthservice.auth.exceptions.TFAIsNotEnabled;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if(principal instanceof String){
            return userRepository.getUserByUsername((String)principal);
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

    public RegisterResponse register(RegisterRequest registerRequest) throws DataIntegrityViolationException{
        var userBuilder = User.builder().firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .twoFactorAuthenticationEnabled(registerRequest.getTfaEnabled())
                .role(Role.USER);
        var registerResponseBuilder = RegisterResponse.builder();
        if(registerRequest.getTfaEnabled()){
            String secretKey = tfaService.generateNewSecret();
            userBuilder.secretKey(secretKey);
            registerResponseBuilder
                    .secretImgURI(tfaService.generateQRCodeImageURI(secretKey));
        }
        var user = userBuilder.build();
        userRepository.save(user);
        var tokenPair = generateTokenPair(user);
        var registerResponse = registerResponseBuilder
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .build();

        tokenRepository.save(RefreshToken.builder()
                .token(registerResponse.getRefreshToken())
                .user(user).build());

        return registerResponse;
    }

    public AuthenticationResponse login(@NonNull AuthenticationRequest authenticationRequest)
            throws DataIntegrityViolationException, AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()
        ));
        AuthenticationResponse authenticationResponse;

        var user = userRepository
                .getUserByUsername(authenticationRequest.getUsername());
        if(user.isTwoFactorAuthenticationEnabled()){
            return AuthenticationResponse.builder().tfaEnabled(true).build();
        }
        authenticationResponse = generateTokenPair(user);
        authenticationResponse.setTfaEnabled(false);
        tokenRepository.save(RefreshToken.builder()
                .token(authenticationResponse.getRefreshToken()).user(user).build());
        return authenticationResponse;
    }

    private User checkIfTokenIsValidAndRefreshTypeAndReturnUser(RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException{
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
            throws InvalidTokenException, DataIntegrityViolationException{
        User user =  checkIfTokenIsValidAndRefreshTypeAndReturnUser(refreshTokenDTO);
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

    public AuthenticationResponse verifyCode(@NonNull VerificationRequest verificationRequest)
            throws InvalidCode, TFAIsNotEnabled, NoSuchUserException {
        var user = userRepository.getUserByUsername(verificationRequest.getUsername());
        if(user == null){
            throw new NoSuchUserException();
        }
        if(!user.isTwoFactorAuthenticationEnabled()){
            throw new TFAIsNotEnabled();
        }
        var secret = user.getSecretKey();
        boolean codeIsValid = tfaService.isOTPValid(secret, verificationRequest.getCode());
        if(codeIsValid){
            return generateTokenPair(user);
        }else{
            throw new InvalidCode();
        }
    }


}
