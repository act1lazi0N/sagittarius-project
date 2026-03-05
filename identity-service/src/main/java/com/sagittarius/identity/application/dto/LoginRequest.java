package com.sagittarius.identity.application.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
