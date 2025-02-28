package com.example.noweat.domain.user.enums;

import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;

public enum UserRole {
    OWNER, USER;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestStateException("유효하지 않은 UerRole"));
    }
}
