package com.bashkir777.jwtauthservice.auth.security.services;

import com.bashkir777.jwtauthservice.data.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
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
    private final static int WEEK_IN_SECONDS = 7 * 24 * 60 * 60;
    private final static int FIVE_MINUTES_IN_SECONDS = 5 * 60;

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public Claims extractAllClaimsAndValidateToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(jwt).getBody();
    }

    public TokenType parseTokenType(String jwt) {
        return parseTokenType(extractAllClaimsAndValidateToken(jwt));
    }

    public TokenType parseTokenType(Claims claims) {
        return TokenType.valueOf((String) claims.get("type"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaimsAndValidateToken(token);
        return claimResolver.apply(claims);
    }

    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }



    public String generateToken(UserDetails userDetails, TokenType type, @Nullable Map<String, Object> extraClaims) {
        if(extraClaims == null) extraClaims = new HashMap<>();
        extraClaims.put("type", type.name());
        int timeAliveSeconds = type.equals(TokenType.REFRESH) ? FIVE_MINUTES_IN_SECONDS : WEEK_IN_SECONDS;
        return Jwts.builder().addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + timeAliveSeconds * 1000L))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
