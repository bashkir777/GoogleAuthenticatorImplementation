package com.bashkir777.jwtauthservice.auth.security.services;

import com.bashkir777.jwtauthservice.auth.dto.AuthenticationRequest;
import com.bashkir777.jwtauthservice.auth.dto.AuthenticationResponse;
import com.bashkir777.jwtauthservice.auth.dto.RegisterRequest;
import com.bashkir777.jwtauthservice.data.UserRepository;
import com.bashkir777.jwtauthservice.data.entities.User;
import com.bashkir777.jwtauthservice.data.enums.Role;
import com.bashkir777.jwtauthservice.data.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private AuthenticationResponse generateTokenPair(User user) {
        var accessToken = jwtService.generateToken(user, TokenType.ACCESS, null);
        var refreshToken = jwtService.generateToken(user, TokenType.REFRESH, null);
        return AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder().firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        return generateTokenPair(user);
    }

    public AuthenticationResponse login(AuthenticationRequest registerRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                registerRequest.getUsername(), registerRequest.getPassword()
        ));
        var user = userRepository
                .getUserByUsername(registerRequest.getUsername());
        return generateTokenPair(user);
    }

}
