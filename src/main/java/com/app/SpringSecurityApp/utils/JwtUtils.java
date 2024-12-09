package com.app.SpringSecurityApp.utils;

import com.app.SpringSecurityApp.persistence.repository.AuthRepository;
import com.app.SpringSecurityApp.persistence.entity.TokenEntity;
import com.app.SpringSecurityApp.persistence.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtUtils {
    private final AuthRepository authRepository;

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
        if (authorities == null || authorities.isEmpty()) {
            return "";
        }
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

        final boolean compareUsernameWithUserOfToken = username.equals(user.getEmail());

      //  validateDatabaseToken(token);

        if(isTokenExpired(token) || !compareUsernameWithUserOfToken) {
            revokeAllTokens(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        return true;
    }

    private void validateDatabaseToken(String token) {
        final TokenEntity findTokenInDatabase = authRepository.findByToken(token).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found"));

        final boolean tokenExpiredInDatabase = findTokenInDatabase.isExpired();
        final boolean tokenRevokedInDatabase = findTokenInDatabase.isRevoked();

        if( tokenRevokedInDatabase) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token revoked");
        }

        if(tokenExpiredInDatabase) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
        }
    }


    public boolean isTokenExpired(String token) {

        final Date expirationDate = Jwts.parser().verifyWith(getSignKey())
                .build().parseSignedClaims(token).getPayload().getExpiration();

        return  expirationDate.before(new Date());
    }


    public void revokeAllTokens(UserEntity user) {

        final List<TokenEntity> tokensUserValid = authRepository.findAllValidToken(user.getId()).orElseThrow();

        if(!tokensUserValid.isEmpty()) {
            tokensUserValid.forEach(t -> {
                t.setRevoked(true);
                t.setExpired(true);
            });

            authRepository.saveAll(tokensUserValid);
        }

    }

}
