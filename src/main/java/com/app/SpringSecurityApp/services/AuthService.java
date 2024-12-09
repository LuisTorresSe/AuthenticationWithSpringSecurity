package com.app.SpringSecurityApp.services;

import com.app.SpringSecurityApp.persistence.dto.AuthResponse;
import com.app.SpringSecurityApp.persistence.dto.LoginRequest;
import com.app.SpringSecurityApp.persistence.dto.RegisterRequest;
import com.app.SpringSecurityApp.persistence.entity.TokenEntity;
import com.app.SpringSecurityApp.persistence.entity.UserEntity;
import com.app.SpringSecurityApp.persistence.repository.AuthRepository;
import com.app.SpringSecurityApp.persistence.repository.UserRepository;
import com.app.SpringSecurityApp.utils.JwtUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class AuthService
{
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtUtils jwtUtils;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String register(RegisterRequest registerRequest)
    {
        UserEntity user = createUser(registerRequest);
        userRepository.save(user);

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

        final UserEntity user = userRepository.findByEmail(username)
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

    public UserEntity createUser(RegisterRequest registerRequest)
    {
        String email = registerRequest.email();
        String password = registerRequest.password();
        String fullName = registerRequest.fullName();
        Date dateOfBirth = registerRequest.dateOfBirth();
        String ci = registerRequest.ci();
        String nationality = registerRequest.nationality();
        String phone = registerRequest.phone();


        return UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .dateOfBirth(dateOfBirth)
                .nationality(nationality)
                .ci(ci)
                .phone(phone)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }


    public AuthResponse login(LoginRequest loginRequest){
        String email = loginRequest.email();
        String password = loginRequest.password();

        Authentication authentication = userDetailsServiceImpl.authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity findUser = userRepository.findByEmail(authentication.getPrincipal().toString()).orElseThrow();

        return getAuthResponse(findUser);
    }

    private AuthResponse getAuthResponse(UserEntity findUser) {
        String parseauthoritiesToString = jwtUtils.parseAuthorityToString(userDetailsServiceImpl.getAuthorities(findUser));

        String accessToken = jwtUtils.generateToken(findUser.getEmail(), parseauthoritiesToString);
        String refreshToken = jwtUtils.generateRefreshToken(findUser.getEmail(), parseauthoritiesToString);

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
