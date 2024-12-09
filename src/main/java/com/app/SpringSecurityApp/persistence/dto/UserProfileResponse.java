package com.app.SpringSecurityApp.persistence.dto;

import com.app.SpringSecurityApp.persistence.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Date;


@Builder
public record UserProfileResponse
        (
                @JsonProperty("email")
                String email,
                @JsonProperty("fullName")
                String fullName,
                @JsonProperty("nationality")
                String nationality,
                @JsonProperty("status")
                UserStatus status,
                @JsonProperty("ci")
                String ci,
                @JsonProperty("phone")
                String phone,
                @JsonProperty("dateOfBirth")
                Date dateOfBirth
        )
{
}
