package com.bashkir777.jwtauthservice.security.services;

import com.bashkir777.jwtauthservice.auth.dto.AuthenticationRequest;
import com.bashkir777.jwtauthservice.auth.dto.AuthenticationResponse;
import com.bashkir777.jwtauthservice.auth.dto.RegisterRequest;
import com.bashkir777.jwtauthservice.data.UserRepository;
import com.bashkir777.jwtauthservice.data.entities.User;
import com.bashkir777.jwtauthservice.data.enums.Role;
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

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder().firstName(registerRequest.getFirstname())
                .lastName(registerRequest.getLastname())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest registerRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                registerRequest.getUsername(), registerRequest.getPassword()
        ));
        var user = userRepository
                .getUserByUsername(registerRequest.getUsername());
        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }
}
