package com.bashkir777.jwtauthservice.auth.security.filters;

import com.bashkir777.jwtauthservice.auth.security.services.JwtService;
import com.bashkir777.jwtauthservice.app.data.enums.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAccessAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("BEARER ")) {
            // Access token is absent or not in the correct format
            // go user+password authentication
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.split(" ")[1];

        //if token is expired or forged send Error
        Claims claims;
        try{
            claims = jwtService.extractAllClaimsAndValidateToken(jwt);
        }catch (RuntimeException invalidToken){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(invalidToken.getMessage());
            response.getWriter().flush();
            return;
        }


        String username = jwtService.extractUsername(claims);
        TokenType type = jwtService.parseTokenType(claims);

        if (type.equals(TokenType.ACCESS)) {
            if (username != null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("refresh token cant be used as an access token");
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
