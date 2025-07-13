package com.wetagustin.jwtsecurityservice.services;

import com.wetagustin.jwtsecurityservice.dtos.auth.LoginRequest;
import com.wetagustin.jwtsecurityservice.dtos.auth.LoginResponse;
import com.wetagustin.jwtsecurityservice.dtos.auth.LogoutRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2KeycloakService {

    // Request keys
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";
    public static final String GRANT_TYPE_KEY = "grant_type";
    public static final String TOKEN_KEY = "token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String TOKEN_TYPE_HINT_KEY = "token_type_hint";
    // Grant types
    public static final String GRANT_TYPE_PASSWORD = "password";
    // Token types
    public static final String TOKEN_TYPE_HINT_ACCESS = "access_token";
    public static final String TOKEN_TYPE_HINT_REFRESH = "refresh_token";

    @Value("${keycloak.endpoints.base}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<LoginResponse> authenticateUser(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(GRANT_TYPE_KEY, GRANT_TYPE_PASSWORD);
        formData.add(USERNAME_KEY, username);
        formData.add(PASSWORD_KEY, password);
        formData.add(CLIENT_ID_KEY, clientId);
        formData.add(CLIENT_SECRET_KEY, clientSecret);

        HttpHeaders headers = createFormHeaders();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);
        return restTemplate.postForEntity(tokenUrl, entity, LoginResponse.class);
    }

    public boolean performSessionLogout(String refreshToken) {
        MultiValueMap<String, String> logoutData = new LinkedMultiValueMap<>();
        logoutData.add(REFRESH_TOKEN_KEY, refreshToken);
        logoutData.add(CLIENT_ID_KEY, clientId);
        logoutData.add(CLIENT_SECRET_KEY, clientSecret);

        HttpHeaders headers = createFormHeaders();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(logoutData, headers);
        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout", keycloakUrl, realm);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(logoutUrl, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean performTokenRevocation(String token, String refreshToken) {
        String revokeUrl = String.format("%s/realms/%s/protocol/openid-connect/revoke", keycloakUrl, realm);
        boolean accessTokenRevoked = false;
        boolean refreshTokenRevoked = false;

        accessTokenRevoked = revokeToken(token, TOKEN_TYPE_HINT_ACCESS, revokeUrl);
        refreshTokenRevoked = revokeToken(refreshToken, TOKEN_TYPE_HINT_REFRESH, revokeUrl);

        return accessTokenRevoked || refreshTokenRevoked;
    }

    public boolean revokeToken(String token, String tokenTypeHint, String revokeUrl) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(TOKEN_KEY, token);
        formData.add(TOKEN_TYPE_HINT_KEY, tokenTypeHint);
        formData.add(CLIENT_ID_KEY, clientId);
        formData.add(CLIENT_SECRET_KEY, clientSecret);

        HttpHeaders headers = createFormHeaders();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private HttpHeaders createFormHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}