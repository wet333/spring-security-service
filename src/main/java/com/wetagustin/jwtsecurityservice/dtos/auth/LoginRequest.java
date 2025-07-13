package com.wetagustin.jwtsecurityservice.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request containing user credentials")
public class LoginRequest {
    
    @Schema(
            description = "Username for authentication",
            example = "john.doe",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
    
    @Schema(
            description = "Password for authentication",
            example = "mySecurePassword123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}