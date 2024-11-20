package com.app.SpringSecurityApp.persistence;

import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
 @NotNull  String username,
 @NotNull String password
) {
}
