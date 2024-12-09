package com.app.SpringSecurityApp.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token")
        String access_token,
        @JsonProperty("refresh_token")
        String refresh_token

) {
}
