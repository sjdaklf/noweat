package com.example.noweat.domain.user;

import com.example.noweat.domain.store.StoreCategory;
import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.enums.ErrorCode;
import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;
import java.util.Optional;

public enum UserRole {
    OWNER, USER;

    public static UserRole of(String userRole) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(userRole))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_USER_ROLE));
    }
}
