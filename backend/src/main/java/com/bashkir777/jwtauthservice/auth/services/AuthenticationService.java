package com.bashkir777.jwtauthservice.auth.services;

import com.bashkir777.jwtauthservice.auth.dto.*;
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

    public AuthenticationResponse register(RegisterRequest registerRequest) throws DataIntegrityViolationException{
        var user = User.builder().firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        AuthenticationResponse authenticationResponse = generateTokenPair(user);
        tokenRepository.save(RefreshToken.builder()
                .token(authenticationResponse.getRefreshToken()).user(getCurrentUser()).build());
        return authenticationResponse;
    }

    public AuthenticationResponse login(@NonNull AuthenticationRequest authenticationRequest)
            throws DataIntegrityViolationException, AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()
        ));
        AuthenticationResponse authenticationResponse;

        var user = userRepository
                .getUserByUsername(authenticationRequest.getUsername());
        authenticationResponse = generateTokenPair(user);
        tokenRepository.save(RefreshToken.builder()
                .token(authenticationResponse.getRefreshToken()).user(getCurrentUser()).build());

        return authenticationResponse;
    }

    public AccessToken refresh(@NonNull RefreshTokenDTO refreshTokenDTO) throws InvalidTokenException {
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
        var refreshTokenDb = tokenRepository.findRefreshTokenByUser(user);
        if (refreshTokenDb.isEmpty() ||
                refreshTokenDb.get().getToken().equals(refreshTokenDTO.getRefreshToken())) {
            throw new InvalidTokenException();
        }

        String jwtAccess = jwtService.
                generateToken(getCurrentUserDetails(), TokenType.ACCESS, null);

        return AccessToken.builder()
                .accessToken(jwtAccess).build();
    }


}
