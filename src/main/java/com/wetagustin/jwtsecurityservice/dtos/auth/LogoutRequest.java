package com.wetagustin.jwtsecurityservice.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Logout request containing tokens to be revoked")
public class LogoutRequest {
    
    @Schema(
            description = "Access token to be revoked",
            example = "eyJhbGciOiJSUzI1NiIsInR5cCI...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String token;
    
    @Schema(
            description = "Refresh token to be revoked",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;
}