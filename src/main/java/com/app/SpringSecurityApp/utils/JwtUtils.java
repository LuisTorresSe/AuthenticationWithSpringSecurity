package com.app.SpringSecurityApp.utils;

import com.app.SpringSecurityApp.persistence.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUtils {

    @Value("${security_jwt_secret_key}")
    private String secureKey;

    @Value("${security_jwt_refresh_token_expiration}")
    private Integer expiration_refreshToken;

    @Value("${security_jwt_expiration}")
    private Integer expiration;

    public String generateToken (String username, String authorities) {
        return buildToken(username, authorities, expiration);
    }

    public String generateRefreshToken (String username, String authorities) {
        return buildToken(username, authorities, expiration_refreshToken);
    }

    private String buildToken(String username, String authorities, Integer expiration) {
        return Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration( new Date(System.currentTimeMillis()+expiration))
                .signWith(getSignKey())
                .compact();
    }

    public String parseAuthorityToString (List<SimpleGrantedAuthority> authorities) {
        return authorities
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }


    private SecretKey getSignKey()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secureKey));
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public String extractAuthorities(String token) {
        return Jwts.parser().verifyWith(getSignKey())
                .build().parseSignedClaims(token)
                .getPayload().get("authorities")
                .toString();
    }

    public boolean validateToken(String token, UserEntity user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        final Date expirationDate = Jwts.parser().verifyWith(getSignKey())
                .build().parseSignedClaims(token).getPayload().getExpiration();
        return expirationDate.before(new Date());
    }


}
