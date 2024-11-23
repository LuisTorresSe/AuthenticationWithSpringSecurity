package com.app.SpringSecurityApp.controllers;


import com.app.SpringSecurityApp.persistence.AuthResponse;
import com.app.SpringSecurityApp.persistence.LoginRequest;
import com.app.SpringSecurityApp.persistence.RegisterRequest;
import com.app.SpringSecurityApp.services.AuthService;

import com.app.SpringSecurityApp.services.CookieService;
import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);

        Cookie refreshToken = cookieService.createCookieToken("refreshToken",authResponse.refresh_token());
        response.addCookie(refreshToken);

        return new ResponseEntity<>( authResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest)
    {
        String registerResponse  = authService.register(registerRequest);
        return new ResponseEntity<>( registerResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader
    ,HttpServletResponse response)
    {

        AuthResponse authResponse = authService.refreshToken(authHeader);

        Cookie refreshToken = cookieService.createCookieToken("refreshToken",authResponse.refresh_token());

        response.addCookie(refreshToken);
        return new ResponseEntity<>( authResponse, HttpStatus.OK);

    }

}
