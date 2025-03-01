package com.example.noweat.domain.user;

import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;
import java.util.Optional;

public enum UserRole {
    OWNER, USER;

    public static Optional<UserRole> of(String role) {
        for(UserRole userRole : UserRole.values()){
            if(userRole.name().equals(role)){
                return Optional.of(userRole);
            }
        }
        return Optional.empty();
    }
}
