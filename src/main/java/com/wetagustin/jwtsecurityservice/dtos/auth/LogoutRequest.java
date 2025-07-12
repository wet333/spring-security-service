package com.wetagustin.jwtsecurityservice.dtos.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String token;
    private String refreshToken;
}