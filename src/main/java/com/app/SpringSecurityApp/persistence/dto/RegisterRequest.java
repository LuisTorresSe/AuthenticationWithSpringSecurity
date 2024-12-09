package com.app.SpringSecurityApp.persistence.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record RegisterRequest(
        @NotNull String email,
        @NotNull String password,
        @NotNull String fullName,
        @NotNull Date dateOfBirth,
        @NotNull String nationality,
        @NotNull String ci,
        @NotNull String phone
 ) {
}
