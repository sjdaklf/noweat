package com.example.noweat.global.argumentResolver;

import com.example.noweat.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthUser {
    private Long id;
    private String email;
    private UserRole userRole;

    @Builder
    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}
