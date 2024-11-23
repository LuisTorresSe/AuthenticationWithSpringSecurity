package com.app.SpringSecurityApp.services;

import com.app.SpringSecurityApp.persistence.*;
import com.app.SpringSecurityApp.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtUtils jwtUtils;
    private final AuthRepository authRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    public String register(RegisterRequest registerRequest)
    {
        String username = registerRequest.username();
        String password = registerRequest.password();

        UserEntity user = createUser(username, password);
        usersRepository.save(user);

        return "User created";
    }

    public AuthResponse refreshToken(String refreshToken){
        String token = extractTokenOfHeader(refreshToken);

        if(token.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        String username = jwtUtils.extractUsername(token);

        if(username.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        final UserEntity user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        final boolean isTokenValid = jwtUtils.validateToken(token, user);

        if(!isTokenValid){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }

        return getAuthResponse(user);
    }

    private String extractTokenOfHeader(String authHeader)
    {
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return authHeader.substring(7).trim();
    }

    public UserEntity createUser(String username, String password)
    {
        return UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }


    public AuthResponse login(LoginRequest loginRequest){
        String username = loginRequest.username();
        String password = loginRequest.password();


        Authentication authentication = userDetailsServiceImpl.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity findUser = usersRepository.findByUsername(authentication.getPrincipal().toString()).orElseThrow();

        return getAuthResponse(findUser);
    }

    private AuthResponse getAuthResponse(UserEntity findUser) {
        String parseauthoritiesToString = jwtUtils.parseAuthorityToString(userDetailsServiceImpl.getAuthorities(findUser));

        String accessToken = jwtUtils.generateToken(findUser.getUsername(), parseauthoritiesToString);
        String refreshToken = jwtUtils.generateRefreshToken(findUser.getUsername(), parseauthoritiesToString);

        jwtUtils.revokeAllTokens(findUser);

        saveTokenUser(refreshToken, findUser);

        return new AuthResponse(accessToken,refreshToken);
    }



    private void saveTokenUser(String token, UserEntity user) {
        TokenEntity newToken = TokenEntity.builder()
                .type("Bearer")
                .expired(false)
                .revoked(false)
                .token(token)
                .user(user)
                .build();

        authRepository.save(newToken);
    }





}
