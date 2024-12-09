package com.app.SpringSecurityApp.services;

import com.app.SpringSecurityApp.persistence.entity.UserEntity;
import com.app.SpringSecurityApp.persistence.dto.UserProfileResponse;
import com.app.SpringSecurityApp.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile() {


        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email = principal.toString();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario con email " + email + " no encontrado"));

        return UserProfileResponse.builder()
                .email(user.getEmail())
                .ci(user.getCi())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .fullName(user.getFullName())
                .nationality(user.getNationality())
                .status(user.getStatusAccount())
                .build();
    }
}
