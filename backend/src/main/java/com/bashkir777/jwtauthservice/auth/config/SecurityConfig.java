package com.bashkir777.jwtauthservice.auth.config;

import com.bashkir777.jwtauthservice.auth.filters.JwtAccessAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAccessAuthenticationFilter jwtAccessAuthenticationFilter;
    private CorsConfigurationSource configurationSource;
    public static final List<String> openUris = List.of("/api/v1/auth/**");

    @Autowired
    @Qualifier("allowAll")
    public void setConfigurationSource(CorsConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors((cors) -> cors
                        .configurationSource(configurationSource)
                ).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> {
                    openUris.forEach(url -> authorizeRequests.requestMatchers(url).permitAll());
                    authorizeRequests.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAccessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

