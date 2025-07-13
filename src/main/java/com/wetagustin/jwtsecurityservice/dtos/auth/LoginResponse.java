package com.wetagustin.jwtsecurityservice.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login response containing authentication tokens and metadata")
public class LoginResponse {

    @JsonProperty("access_token")
    @Schema(description = "JWT access token for API authentication", example = "eyJhbGciOiJSUzI1NiIsInR5cCI...")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(description = "Refresh token for obtaining new access tokens", example = "eyJhbGciOiJIUzI1NiIsInR5cCI...")
    private String refreshToken;

    @JsonProperty("token_type")
    @Schema(description = "Type of the token", example = "Bearer")
    private String tokenType;

    @JsonProperty("expires_in")
    @Schema(description = "Token expiration time in seconds", example = "3600")
    private int expiresIn;
}