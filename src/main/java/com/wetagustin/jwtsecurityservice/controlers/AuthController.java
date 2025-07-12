package com.wetagustin.jwtsecurityservice.controlers;

import com.wetagustin.jwtsecurityservice.dtos.auth.LoginRequest;
import com.wetagustin.jwtsecurityservice.dtos.auth.LoginResponse;
import com.wetagustin.jwtsecurityservice.dtos.auth.LogoutRequest;
import com.wetagustin.jwtsecurityservice.dtos.common.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${keycloak.endpoints.base}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest loginRequest) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("username", loginRequest.getUsername());
            formData.add("password", loginRequest.getPassword());
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);

            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                    tokenUrl, entity, LoginResponse.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", logoutRequest.getToken());
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            String revokeUrl = String.format("%s/realms/%s/protocol/openid-connect/revoke", keycloakUrl, realm);

            restTemplate.postForEntity(revokeUrl, entity, String.class);

            if (logoutRequest.getRefreshToken() != null && !logoutRequest.getRefreshToken().isEmpty()) {
                MultiValueMap<String, String> refreshFormData = new LinkedMultiValueMap<>();
                refreshFormData.add("token", logoutRequest.getRefreshToken());
                refreshFormData.add("client_id", clientId);
                refreshFormData.add("client_secret", clientSecret);

                HttpEntity<MultiValueMap<String, String>> refreshEntity = new HttpEntity<>(refreshFormData, headers);
                restTemplate.postForEntity(revokeUrl, refreshEntity, String.class);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to logout"));
        }
    }

}
