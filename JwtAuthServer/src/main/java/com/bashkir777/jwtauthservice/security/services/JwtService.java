package com.bashkir777.jwtauthservice.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value(value = "${JWTAuthService.secret-key}")
    private static String SECRET_KEY;
    public String extractUsername(String jwt){
        return extractClaim(jwt, Claims::getSubject);
    }

    public Claims extractAllClaims(String jwt){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(jwt).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder().addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1_000 * 64 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken( UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return !isExpired(token) && (username.equals(userDetails.getUsername()));
    }

    public boolean isExpired(String token){
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
