package com.wetagustin.jwtsecurityservice.controlers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "jwt-security-service");
        status.put("status", "running");
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("auth", Map.of(
            "login", "POST /api/auth/login",
            "logout", "POST /api/auth/logout"
        ));
        endpoints.put("public", Map.of(
            "status", "GET /api/status",
            "home", "GET /api/home"
        ));
        endpoints.put("protected", Map.of(
            "home", "GET /api/protected/home",
            "userData", "GET /api/protected/userData"
        ));
        
        status.put("endpoints", endpoints);
        return status;
    }

    @GetMapping("/home")
    public String hello() {
        return "Unprotected main Endpoint";
    }

    @GetMapping("/protected/home")
    public String protectedHello() {
        return "Protected main Endpoint";
    }

    @GetMapping("/protected/userData")
    public String protectedEndpoint(@AuthenticationPrincipal Jwt user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream().map(Objects::toString).toList();
        Map<String, Object> attributes = user.getClaims();
        return "User Data: \n" + attributes.toString() + "\n\n" + roles.toString();
    }
}
