package com.wetagustin.jwtsecurityservice.controlers;

import com.wetagustin.jwtsecurityservice.dtos.auth.LoginRequest;
import com.wetagustin.jwtsecurityservice.dtos.auth.LoginResponse;
import com.wetagustin.jwtsecurityservice.dtos.auth.LogoutRequest;
import com.wetagustin.jwtsecurityservice.dtos.common.ApiResponse;
import com.wetagustin.jwtsecurityservice.services.OAuth2KeycloakService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuth2KeycloakService oAuth2KeycloakService;

    public AuthController(OAuth2KeycloakService oAuth2KeycloakService) {
        this.oAuth2KeycloakService = oAuth2KeycloakService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> getToken(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.of("Username is required"));
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.of("Password is required"));
            }

            String username = loginRequest.getUsername().trim();
            String password = loginRequest.getPassword().trim();

            ResponseEntity<LoginResponse> response = oAuth2KeycloakService.authenticateUser(username, password);
            return ResponseEntity.ok(ApiResponse.of("Login successful", response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of("Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest logoutRequest) {
        try {
            if (logoutRequest.getToken() == null || logoutRequest.getToken().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.of("Token is required"));
            }
            if (logoutRequest.getRefreshToken() == null || logoutRequest.getRefreshToken().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.of("Refresh token is required"));
            }

            String token = logoutRequest.getToken();
            String refreshToken = logoutRequest.getRefreshToken();

            // Primary approach: Use logout endpoint to terminate the entire session
            boolean sessionLogoutSuccess = oAuth2KeycloakService.performSessionLogout(refreshToken);
            if (sessionLogoutSuccess) {
                return ResponseEntity.ok(ApiResponse.of("Logout successful"));
            }

            // Fallback approach: Revoke tokens individually
            boolean tokenRevocationSuccess = oAuth2KeycloakService.performTokenRevocation(token, refreshToken);
            if (tokenRevocationSuccess) {
                return ResponseEntity.ok(ApiResponse.of("Logout successful"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.of("Failed to logout"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.of("Failed to logout: " + e.getMessage())
            );
        }
    }
}
