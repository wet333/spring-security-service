package com.wetagustin.jwtsecurityservice.dtos.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response containing error details")
public class ErrorResponse {
    
    @Schema(description = "Error message describing what went wrong", example = "Invalid credentials provided")
    private String errorMsg;
}