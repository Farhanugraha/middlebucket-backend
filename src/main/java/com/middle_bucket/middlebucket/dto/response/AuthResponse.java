package com.middle_bucket.middlebucket.dto.response;

import com.middle_bucket.middlebucket.entity.Role;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private Role role;

    public AuthResponse(String token, Long userId, String name, String email, Role role) {
        this.token = token;
        this.tokenType = "bearer";
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
