package com.app.SpringSecurityApp.controllers;

import com.app.SpringSecurityApp.persistence.dto.UserProfileResponse;
import com.app.SpringSecurityApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile() {
        return new ResponseEntity<>(userService.getProfile(),HttpStatus.OK);
    }


}
